package com.zerokg2004.paintball.block.entity;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
        RED,
        BLUE,
        ORANGE,
        GREEN,
        YELLOW,
        PURPLE
    }

    private float arrowAngle;
    private float prevArrowAngle;
    private boolean spinning;
    private float spinSpeed;
    private int spinTicks;
    private int maxSpinTicks;

    private @Nullable UUID lastPlayer;

    private final Map<UUID, TeamColor> playerTeams = new HashMap<>();

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
        if (delta > Math.PI) {
            delta -= twoPI;
        } else if (delta < -Math.PI) {
            delta += twoPI;
        }

        return a0 + delta * partialTicks;
    }

    private void startSpin(long seed) {
        RandomSource rand = RandomSource.create(seed);

        spinning = true;
        spinTicks = 0;
        maxSpinTicks = 40 + rand.nextInt(40);
        spinSpeed = 0.7F + rand.nextFloat() * 0.7F;

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

        if (playerTeams.containsKey(uuid)) {
            TeamColor team = playerTeams.get(uuid);
            player.displayClientMessage(
                    Component.literal("You already have a team: " + teamName(team) + "!")
                            .withStyle(colorStyle(team)),
                    false
            );
            return;
        }

        if (playerTeams.size() >= 12) {
            player.displayClientMessage(
                    Component.literal("There is no more space in this roulette.")
                            .withStyle(ChatFormatting.DARK_RED),
                    false
            );
            return;
        }

        if (spinning) {
            player.displayClientMessage(
                    Component.literal("The roulette is spinning... please wait until it stops.")
                            .withStyle(ChatFormatting.GRAY),
                    false
            );
            return;
        }

        lastPlayer = uuid;
        startServerSpin(seed);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, DecisionRouletteBlockEntity be) {
        if (!be.spinning) {
            be.prevArrowAngle = be.arrowAngle;
            return;
        }

        be.prevArrowAngle = be.arrowAngle;

        be.spinTicks++;
        be.arrowAngle += be.spinSpeed;

        float twoPI = (float) (Math.PI * 2.0F);
        if (be.arrowAngle >= twoPI) {
            be.arrowAngle -= twoPI;
        } else if (be.arrowAngle < 0) {
            be.arrowAngle += twoPI;
        }

        if (be.spinTicks >= be.maxSpinTicks) {
            be.spinSpeed *= 0.90F;

            if (Math.abs(be.spinSpeed) < 0.02F) {
                be.spinning = false;
                if (!level.isClientSide) {
                    be.finishSpinAndAssignTeam();
                }
            }
        }
    }

    private void finishSpinAndAssignTeam() {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (lastPlayer == null) return;

        Player player = serverLevel.getPlayerByUUID(lastPlayer);
        if (player == null) return;

        if (playerTeams.size() >= 12) {
            player.displayClientMessage(
                    Component.literal("There is no more space in this roulette.")
                            .withStyle(ChatFormatting.DARK_RED),
                    false
            );
            return;
        }

        TeamColor team = getTeamFromAngle(arrowAngle);
        playerTeams.put(player.getUUID(), team);

        giveInstaBase(player, team);

        player.displayClientMessage(
                Component.literal("Your team is " + teamName(team))
                        .withStyle(colorStyle(team)),
                false
        );

        setChanged();
        level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    private void giveInstaBase(Player player, TeamColor team) {
        Item item = getInstaBaseItem(team);
        if (item == null) return;

        ItemStack stack = new ItemStack(item, 1);

        boolean added = player.getInventory().add(stack);
        if (!added) {
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

        if (deg >= 0.0F && deg < 60.0F) return TeamColor.PURPLE;
        if (deg >= 60.0F && deg < 120.0F) return TeamColor.BLUE;
        if (deg >= 120.0F && deg < 180.0F) return TeamColor.GREEN;
        if (deg >= 180.0F && deg < 240.0F) return TeamColor.YELLOW;
        if (deg >= 240.0F && deg < 300.0F) return TeamColor.ORANGE;
        return TeamColor.RED;
    }

    private String teamName(TeamColor team) {
        return switch (team) {
            case RED -> "RED";
            case BLUE -> "BLUE";
            case ORANGE -> "ORANGE";
            case GREEN -> "GREEN";
            case YELLOW -> "YELLOW";
            case PURPLE -> "PURPLE";
        };
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