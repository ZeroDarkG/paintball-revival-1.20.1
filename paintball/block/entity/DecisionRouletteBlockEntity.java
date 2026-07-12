package com.zerokg2004.paintball.block.entity;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DecisionRouletteBlockEntity extends BlockEntity {

    public enum TeamColor {
        RED, BLUE, ORANGE, GREEN, YELLOW, PURPLE
    }

    private float arrowAngle;
    private float prevArrowAngle;
    private boolean spinning;
    private float spinSpeed;
    private int spinTicks;
    private int maxSpinTicks;

    private final Map<UUID, TeamColor> playerTeams = new HashMap<>();

    private @Nullable UUID lastPlayer;
    // VARIABLE CLAVE: Memoria del último equipo asignado
    private @Nullable TeamColor lastAssignedTeam = null;

    public DecisionRouletteBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ROULETTE_BLOCK_ENTITY.get(), pos, state);
    }

    public float getArrowAngle() {
        return arrowAngle;
    }

    public float getArrowAngle(float partialTicks) {
        float twoPI = (float) (Math.PI * 2.0);
        float a0 = prevArrowAngle % twoPI;
        if (a0 < 0) a0 += twoPI;
        float a1 = arrowAngle % twoPI;
        if (a1 < 0) a1 += twoPI;
        float delta = a1 - a0;
        if (delta > Math.PI) delta -= twoPI;
        else if (delta < -Math.PI) delta += twoPI;
        return a0 + delta * partialTicks;
    }

    private void startSpin(long seed) {
        RandomSource rand = RandomSource.create(seed);
        spinning = true;
        spinTicks = 0;

        // Duración del giro (entre 2 y 4 segundos aprox)
        maxSpinTicks = 45 + rand.nextInt(35);
        // Velocidad inicial aleatoria
        spinSpeed = 0.6F + rand.nextFloat() * 0.5F;

        prevArrowAngle = arrowAngle;
    }

    public void startClientSpin(long seed) {
        if (spinning) return;
        startSpin(seed);
    }

    private void startServerSpin(long seed) {
        if (spinning) return;
        startSpin(seed);
    }

    public void onUse(Player player, long seed) {
        if (level == null || level.isClientSide) return;

        UUID uuid = player.getUUID();
        boolean spanish = isSpanishLang();

        // 1. Bloqueo si ya tiene equipo
        if (playerTeams.containsKey(uuid)) {
            TeamColor team = playerTeams.get(uuid);
            String msg = spanish
                    ? "¡Ya tienes equipo: " + teamName(team) + "!"
                    : "You already have a team: " + teamName(team) + "!";

            player.displayClientMessage(Component.literal(msg).withStyle(colorStyle(team)), false);
            return;
        }

        // 2. Límite de 6 personas
        if (playerTeams.size() >= 6) {
            String msg = spanish
                    ? "La ruleta ya asignó los 6 equipos disponibles."
                    : "The roulette has already assigned all 6 available teams.";

            player.displayClientMessage(Component.literal(msg).withStyle(ChatFormatting.DARK_RED), false);
            return;
        }

        // 3. Bloqueo si está girando
        if (spinning) {
            String msg = spanish
                    ? "La ruleta está girando... espera tu turno."
                    : "The roulette is spinning... please wait your turn.";

            player.displayClientMessage(Component.literal(msg).withStyle(ChatFormatting.GRAY), true);
            return;
        }

        lastPlayer = uuid;
        startServerSpin(seed);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DecisionRouletteBlockEntity be) {
        be.prevArrowAngle = be.arrowAngle;

        if (!be.spinning) return;

        be.spinTicks++;
        be.arrowAngle += be.spinSpeed;

        // Normalizar ángulo para que siempre esté entre 0 y 2PI
        float twoPI = (float) (Math.PI * 2.0F);
        be.arrowAngle %= twoPI;
        if (be.arrowAngle < 0) be.arrowAngle += twoPI;

        // Lógica de frenado progresivo
        if (be.spinTicks >= be.maxSpinTicks) {
            be.spinSpeed *= 0.94F; // Fricción

            if (be.spinSpeed < 0.01F) {
                be.spinSpeed = 0;
                be.spinning = false;
                if (!level.isClientSide) {
                    be.finishSpinAndAssignTeam();
                }
            }
        }
    }

    private void finishSpinAndAssignTeam() {
        if (!(level instanceof ServerLevel serverLevel) || lastPlayer == null) return;

        Player player = serverLevel.getPlayerByUUID(lastPlayer);
        if (player == null) return;

        java.util.Set<TeamColor> occupiedTeams = new java.util.HashSet<>(playerTeams.values());
        TeamColor team = getTeamFromAngle(arrowAngle);

        if (occupiedTeams.contains(team)) {
            TeamColor[] allColors = TeamColor.values();
            int startIndex = team.ordinal();
            for (int i = 1; i < allColors.length; i++) {
                TeamColor check = allColors[(startIndex + i) % allColors.length];
                if (!occupiedTeams.contains(check)) {
                    team = check;
                    this.arrowAngle = (float) Math.toRadians(team.ordinal() * 60.0F + 30.0F);
                    break;
                }
            }
        }

        playerTeams.put(lastPlayer, team);
        lastAssignedTeam = team;
        giveInstaBase(player, team);

        // --- LÓGICA DE IDIOMA SEGURA PARA SERVIDORES ---
        // En lugar de preguntar el idioma aquí, enviamos una "llave" de traducción
        player.displayClientMessage(
                Component.translatable("message.paintball.your_team")
                        .withStyle(ChatFormatting.WHITE)
                        .append(Component.translatable("team.paintball." + team.name().toLowerCase())
                                .withStyle(colorStyle(team))),
                false
        );

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    private void giveInstaBase(Player player, TeamColor team) {
        Item item = getInstaBaseItem(team);
        if (item == null) return;

        ItemStack stack = new ItemStack(item, 1);

        // Intentamos añadir al inventario
        if (player.getInventory().add(stack)) {
            // Suena el sonido de recoger objeto (ITEM_PICKUP)
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.ITEM_PICKUP,
                    SoundSource.PLAYERS,
                    0.2F,
                    ((level.random.nextFloat() - level.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        } else {
            // Si el inventario está lleno, lo suelta al suelo
            player.drop(stack, false);
        }
    }

    private Item getInstaBaseItem(TeamColor team) {
        return switch (team) {
            case GREEN -> ModItems.GREEN_INSTA_BASE.get();
            case BLUE -> ModItems.BLUE_INSTA_BASE.get();
            case RED -> ModItems.RED_INSTA_BASE.get();
            case YELLOW -> ModItems.YELLOW_INSTA_BASE.get();
            case ORANGE -> ModItems.ORANGE_INSTA_BASE.get();
            case PURPLE -> ModItems.PURPLE_INSTA_BASE.get();
        };
    }

    private TeamColor getTeamFromAngle(float angleRad) {
        float deg = (float) Math.toDegrees(angleRad);
        deg = deg % 360.0F;
        if (deg < 0) deg += 360.0F;

        // Sectores exactos de 60 grados para 6 equipos
        if (deg >= 0.0F && deg < 60.0F) return TeamColor.PURPLE;
        if (deg >= 60.0F && deg < 120.0F) return TeamColor.BLUE;
        if (deg >= 120.0F && deg < 180.0F) return TeamColor.GREEN;
        if (deg >= 180.0F && deg < 240.0F) return TeamColor.YELLOW;
        if (deg >= 240.0F && deg < 300.0F) return TeamColor.ORANGE;
        return TeamColor.RED;
    }

    private String teamName(TeamColor team) {
        boolean spanish = isSpanishLang();
        return switch (team) {
            case RED    -> spanish ? "ROJO" : "RED";
            case BLUE   -> spanish ? "AZUL" : "BLUE";
            case ORANGE -> spanish ? "NARANJA" : "ORANGE";
            case GREEN  -> spanish ? "VERDE" : "GREEN";
            case YELLOW -> spanish ? "AMARILLO" : "YELLOW";
            case PURPLE -> spanish ? "MORADO" : "PURPLE";
        };
    }

    private boolean isSpanishLang() {
        // Usamos la misma lógica que en tu GravityBook
        try {
            String lang = net.minecraft.client.Minecraft.getInstance().getLanguageManager().getSelected();
            return lang != null && lang.toLowerCase().startsWith("es");
        } catch (Exception e) {
            // Fallback por si se ejecuta en un servidor dedicado sin cliente
            return false;
        }
    }

    private ChatFormatting colorStyle(TeamColor team) {
        return switch (team) {
            case RED -> ChatFormatting.RED;
            case BLUE -> ChatFormatting.BLUE;
            case ORANGE -> ChatFormatting.GOLD;
            case GREEN -> ChatFormatting.GREEN;
            case YELLOW -> ChatFormatting.YELLOW;
            case PURPLE -> ChatFormatting.DARK_PURPLE;
        };
    }
}