package com.zerokg2004.paintball.block.entity;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.block.PodBlock;
import com.zerokg2004.paintball.item.PaintballArmorItem;
import com.zerokg2004.paintball.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class PodBlockEntity extends BlockEntity {

    private static final int RECHARGE_STEP_TICKS = 20 * 4;
    public static final int MAX_STAGE = 4;

    private int chargeStage = 0;
    private int rechargeTicks = 0;

    public PodBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POD_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean tryGivePellets(Player player) {
        if (level == null || level.isClientSide) return false;
        if (player == null) return false;

        if (chargeStage >= MAX_STAGE) {
            return false;
        }

        DyeColor podColor = DyeColor.BLUE;
        BlockState st = getBlockState();
        if (st.getBlock() instanceof PodBlock podBlock) {
            podColor = podBlock.getColor();
        }

        if (!canUsePodWithArmor(player, podColor)) {
            return false;
        }

        ItemStack pellets = getPelletStackForColor(podColor);
        if (pellets.isEmpty()) {
            return false;
        }

        if (!player.addItem(pellets.copy())) {
            player.drop(pellets.copy(), false);
        }

        level.playSound(
                null,
                worldPosition,
                SoundEvents.ITEM_PICKUP,
                SoundSource.PLAYERS,
                0.25F,
                1.0F + (level.random.nextFloat() - level.random.nextFloat()) * 0.2F
        );

        chargeStage++;
        rechargeTicks = 0;

        setChanged();
        return true;
    }

    private boolean canUsePodWithArmor(Player player, DyeColor podColor) {
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        }) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof PaintballArmorItem armorItem) {
                if (armorItem.getColor() != podColor) {
                    sendTeamMessage(player,
                            "You aren't on the " + podColor.getName() + " team.",
                            podColor);
                    return false;
                }
            }
        }
        return true;
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

    private ItemStack getPelletStackForColor(DyeColor color) {
        int amount = 32;

        return switch (color) {
            case RED -> new ItemStack(ModItems.RED_PELLETS.get(), amount);
            case BLUE -> new ItemStack(ModItems.BLUE_PELLETS.get(), amount);
            case GREEN -> new ItemStack(ModItems.GREEN_PELLETS.get(), amount);
            case YELLOW -> new ItemStack(ModItems.YELLOW_PELLETS.get(), amount);
            case ORANGE -> new ItemStack(ModItems.ORANGE_PELLETS.get(), amount);
            case PURPLE -> new ItemStack(ModItems.PURPLE_PELLETS.get(), amount);
            default -> ItemStack.EMPTY;
        };
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, PodBlockEntity be) {
        if (level.isClientSide) return;

        if (be.chargeStage > 0) {
            be.rechargeTicks++;
            if (be.rechargeTicks >= RECHARGE_STEP_TICKS) {
                be.rechargeTicks = 0;
                be.chargeStage--;
                be.setChanged();
            }
        } else if (be.rechargeTicks != 0) {
            be.rechargeTicks = 0;
            be.setChanged();
        }
    }

    public int getChargeStage() {
        return chargeStage;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("ChargeStage", this.chargeStage);
        tag.putInt("RechargeTicks", this.rechargeTicks);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.chargeStage = tag.getInt("ChargeStage");
        this.rechargeTicks = tag.getInt("RechargeTicks");
    }

    public CompoundTag savePodData() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }
}