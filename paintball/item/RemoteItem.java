package com.zerokg2004.paintball.item;

import com.zerokg2004.paintball.block.entity.C4BlockEntity;
import com.zerokg2004.paintball.item.PaintballArmorItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RemoteItem extends Item {
    private static final Map<String, Map<UUID, BlockPos>> REMOTE_LINKS = new HashMap<>();
    private final DyeColor color;

    private static final float VISUAL_EXPLOSION_STRENGTH = 1.5F;
    private static final float C4_DAMAGE = 15.0F;
    private static final double DAMAGE_RADIUS = 4.0D;

    public RemoteItem(Properties properties, DyeColor color) {
        super(properties);
        this.color = color;
    }

    private void sendTeamMessage(Player player, String message, DyeColor color) {
        ChatFormatting formatting = switch (color) {
            case RED -> ChatFormatting.RED;
            case GREEN -> ChatFormatting.GREEN;
            case BLUE -> ChatFormatting.BLUE;
            case YELLOW -> ChatFormatting.YELLOW;
            case PURPLE -> ChatFormatting.LIGHT_PURPLE;
            case BLACK -> ChatFormatting.DARK_GRAY;
            case WHITE -> ChatFormatting.WHITE;
            case ORANGE -> ChatFormatting.GOLD;
            case CYAN -> ChatFormatting.AQUA;
            case GRAY -> ChatFormatting.GRAY;
            case LIGHT_BLUE -> ChatFormatting.BLUE;
            case LIME -> ChatFormatting.GREEN;
            case PINK -> ChatFormatting.LIGHT_PURPLE;
            case BROWN -> ChatFormatting.DARK_RED;
            case LIGHT_GRAY -> ChatFormatting.GRAY;
            case MAGENTA -> ChatFormatting.DARK_PURPLE;
        };
        player.sendSystemMessage(Component.literal(message).withStyle(formatting));
    }

    private String getKey(Player player) {
        return player.getUUID() + "_" + color.getName();
    }

    private boolean isWearingColorArmor(Player player, DyeColor armorColor) {
        int count = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            if (armor.getItem() instanceof PaintballArmorItem paintballArmor) {
                if (paintballArmor.isArmorPieceOfColor(armor, armorColor)) {
                    count++;
                }
            }
        }
        return count >= 4;
    }

    public boolean isFullyWearingTeamColor(Player player) {
        return isWearingColorArmor(player, this.color);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (level.isClientSide || player == null) return InteractionResult.PASS;

        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof C4BlockEntity c4)) {
            return InteractionResult.PASS;
        }

        if (!isFullyWearingTeamColor(player)) {
            sendTeamMessage(player, "You aren't on the " + this.color.getName() + " team.", this.color);
            return InteractionResult.FAIL;
        }

        if (c4.getColor() != this.color || (c4.isLinked() && !c4.isLinkedTo(player))) {
            sendTeamMessage(player, "This C4 belongs to a different team or player.", this.color);
            return InteractionResult.FAIL;
        }

        UUID c4Id = c4.getC4Id();
        if (c4Id == null) return InteractionResult.FAIL;

        String key = getKey(player);
        Map<UUID, BlockPos> linkedC4 = REMOTE_LINKS.computeIfAbsent(key, k -> new HashMap<>());

        if (linkedC4.containsKey(c4Id)) {
            linkedC4.remove(c4Id);
            c4.unlink();
            sendTeamMessage(player, "The C4 is no longer connected to the remote.", this.color);
        } else {
            if (!c4.tryLinkTo(player)) {
                return InteractionResult.FAIL;
            }
            linkedC4.put(c4Id, pos);
            sendTeamMessage(player, "The C4 is now connected to the remote.", this.color);
        }

        return InteractionResult.CONSUME_PARTIAL;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);

        if (level.isClientSide) return InteractionResultHolder.pass(itemInHand);

        // 1. Raycast para buscar un bloque de C4
        HitResult hitResult = player.pick(5.0D, 0.0F, false);
        boolean foundC4 = false;

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
            if (level.getBlockEntity(pos) instanceof C4BlockEntity) {
                foundC4 = true;
            }
        }

        // 2. SI NO ENCONTRAMOS UN C4 (ya sea mirando al aire o a otro bloque)
        if (!foundC4) {
            // Validación de equipo antes del mensaje
            if (!isFullyWearingTeamColor(player)) {
                sendTeamMessage(player, "You aren't on the " + this.color.getName() + " team.", this.color);
            } else {
                // "No hay C4 para detonar" (Porque intentabas conectar y no hay nada ahí)
                sendTeamMessage(player, "There is no C4 to detonate", this.color);
            }
            return InteractionResultHolder.fail(itemInHand);
        }

        // 3. SI ENCONTRAMOS UN C4
        // Devolvemos PASS para que Minecraft ejecute 'useOn' y gestione el vínculo
        return InteractionResultHolder.pass(itemInHand);
    }

    // ✅ IMPORTANTE: Debes añadir este método justo debajo para que el zoom no se corte
    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000; // 1 hora de duración (suficiente para mantener el clic derecho)
    }

    // ✅ TAMBIÉN ESTE: Indica que la animación de "uso" es de tipo 'none' o 'bow'
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE; // Evita que el arma se mueva hacia la cara como comida o arco
    }

    public void tryRemoteDetonation(Player player) {
        if (player.level().isClientSide) return;

        if (!isFullyWearingTeamColor(player)) {
            sendTeamMessage(player, "You aren't on the " + this.color.getName() + " team.", this.color);
            return;
        }

        String key = getKey(player);
        Map<UUID, BlockPos> linkedC4 = REMOTE_LINKS.get(key);

        if (linkedC4 == null || linkedC4.isEmpty()) {
            sendTeamMessage(player, "The remote does not have any C4 connected to it.", this.color);
            return;
        }

        handleDetonation(player);
    }

    private void handleDetonation(Player player) {
        String key = getKey(player);
        Map<UUID, BlockPos> linkedC4 = REMOTE_LINKS.get(key);
        if (linkedC4 == null || linkedC4.isEmpty()) return;

        Level level = player.level();
        List<UUID> toRemove = new ArrayList<>();

        for (Map.Entry<UUID, BlockPos> entry : linkedC4.entrySet()) {
            BlockPos pos = entry.getValue();

            if (!level.hasChunkAt(pos)) {
                toRemove.add(entry.getKey());
                continue;
            }

            BlockEntity be = level.getBlockEntity(pos);
            if (!(be instanceof C4BlockEntity c4) || c4.getColor() != this.color) {
                toRemove.add(entry.getKey());
                continue;
            }

            if (!c4.isLinkedTo(player) || !entry.getKey().equals(c4.getC4Id())) {
                continue;
            }

            DyeColor explosionColor = c4.getColor();

            // --- LÓGICA DE DETONACIÓN ---
            // --- LÓGICA DE DETONACIÓN ---
            c4.unlink();
            level.removeBlock(pos, false);

// 1. EFECTO VISUAL "FANTASMA" (Sin daño)
// Usamos potencia 0.0F para que el motor de Minecraft NO calcule daños.
            level.explode(
                    null,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    0.0F, // Potencia 0 = Cero daño del sistema
                    false,
                    Level.ExplosionInteraction.NONE
            );

// 2. GENERAR EL SONIDO Y PARTÍCULAS MANUALMENTE (Para que parezca una explosión real)
            level.addParticle(net.minecraft.core.particles.ParticleTypes.EXPLOSION_EMITTER, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.0D, 0.0D, 0.0D);
            level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE, net.minecraft.sounds.SoundSource.BLOCKS, 4.0F, (1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F) * 0.7F);

// 3. TU DAÑO MANUAL (Ahora sí, solo estos 20 se aplicarán)
            AABB area = new AABB(pos).inflate(DAMAGE_RADIUS);
            List<LivingEntity> victims = level.getEntitiesOfClass(LivingEntity.class, area);

            for (LivingEntity victim : victims) {
                if (victim instanceof Player targetPlayer && isWearingColorArmor(targetPlayer, explosionColor)) {
                    continue;
                }

                if (victim == player) continue;

                // Aquí están tus 20 puntos de daño exactos
                victim.hurt(level.damageSources().explosion(null, player), 20.0F);
                victim.setLastHurtByPlayer(player);
            }

            toRemove.add(entry.getKey());
            sendTeamMessage(player, "The connected C4 has been detonated.", this.color);
        }

        // 3. LIMPIEZA FUERA DEL BUCLE (Para evitar ConcurrentModificationException)
        for (UUID uuid : toRemove) {
            linkedC4.remove(uuid);
        }

        if (linkedC4.isEmpty()) {
            REMOTE_LINKS.remove(key);
        }
    }
    }