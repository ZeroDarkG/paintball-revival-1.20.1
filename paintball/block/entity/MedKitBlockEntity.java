package com.zerokg2004.paintball.block.entity;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.block.MedKitBlock;
import com.zerokg2004.paintball.item.PaintballArmorItem;
import com.zerokg2004.paintball.registry.ModBlockEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MedKitBlockEntity extends BlockEntity {

    private static final int RECHARGE_STEP_TICKS = 20 * 30;

    private int chargeStage = 0;
    private int rechargeTicks = 0;

    public MedKitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MEDKIT_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean tryHeal(Player player) {
        return tryUse(player);
    }

    public boolean tryUse(Player player) {
        if (level == null || level.isClientSide) return false;

        boolean isCreative = player.getAbilities().instabuild;

        // 1. Obtener color del bloque
        DyeColor medkitColor = DyeColor.BLUE;
        BlockState state = getBlockState();
        if (state.getBlock() instanceof MedKitBlock mkBlock) {
            medkitColor = mkBlock.getColor();
        }

        // 2. BLOQUEO DE EQUIPO: Obligatorio para Survival y Creativo
        if (!isFullyWearingTeamColor(player, medkitColor)) {
            return false;
        }

        // 3. BLOQUEOS DE SURVIVAL
        if (!isCreative) {
            // No usar si está recargando
            if (this.chargeStage != 0) return false;

            // No desperdiciar si tiene la vida llena
            if (player.getHealth() >= player.getMaxHealth()) return false;
        }

        // Curación
        player.heal(16.0F);

        // 4. ACTIVAR RECARGA: Solo si NO es creativo
        if (!isCreative) {
            this.chargeStage = 2; // Asegúrate que MAX_STAGE sea coherente con tus texturas
            this.rechargeTicks = 0;
            setChanged();
        }

        return true;
    }

    // Nueva lógica de validación de equipo (Consistente con los Racks)
    private boolean isFullyWearingTeamColor(Player player, DyeColor teamColor) {
        int count = 0;
        for (ItemStack armor : player.getArmorSlots()) {
            if (!armor.isEmpty() && armor.getItem() instanceof PaintballArmorItem paintballArmor) {
                if (paintballArmor.isArmorPieceOfColor(armor, teamColor)) {
                    count++;
                }
            }
        }
        if (count < 4) {
            sendTeamMessage(player, "You aren't on the " + teamColor.getName() + " team.", teamColor);
            return false;
        }
        return true;
    }

    private boolean canUseMedkitWithArmor(Player player, DyeColor medkitColor) {
        for (var armorStack : player.getArmorSlots()) {
            if (armorStack.isEmpty()) continue;

            Item item = armorStack.getItem();

            if (isAnyPaintballArmor(item)) {
                if (!isPaintballArmorOfColor(item, medkitColor)) {
                    sendTeamMessage(player,
                            "You aren't on the " + medkitColor.getName() + " team.",
                            medkitColor);
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAnyPaintballArmor(Item item) {
        return item == ModItems.BLUE_PAINTBALL_HELMET.get()
                || item == ModItems.BLUE_PAINTBALL_CHEST.get()
                || item == ModItems.BLUE_PAINTBALL_PANTS.get()
                || item == ModItems.BLUE_PAINTBALL_SHOES.get()
                || item == ModItems.RED_PAINTBALL_HELMET.get()
                || item == ModItems.RED_PAINTBALL_CHEST.get()
                || item == ModItems.RED_PAINTBALL_PANTS.get()
                || item == ModItems.RED_PAINTBALL_SHOES.get()
                || item == ModItems.GREEN_PAINTBALL_HELMET.get()
                || item == ModItems.GREEN_PAINTBALL_CHEST.get()
                || item == ModItems.GREEN_PAINTBALL_PANTS.get()
                || item == ModItems.GREEN_PAINTBALL_SHOES.get()
                || item == ModItems.YELLOW_PAINTBALL_HELMET.get()
                || item == ModItems.YELLOW_PAINTBALL_CHEST.get()
                || item == ModItems.YELLOW_PAINTBALL_PANTS.get()
                || item == ModItems.YELLOW_PAINTBALL_SHOES.get()
                || item == ModItems.ORANGE_PAINTBALL_HELMET.get()
                || item == ModItems.ORANGE_PAINTBALL_CHEST.get()
                || item == ModItems.ORANGE_PAINTBALL_PANTS.get()
                || item == ModItems.ORANGE_PAINTBALL_SHOES.get()
                || item == ModItems.PURPLE_PAINTBALL_HELMET.get()
                || item == ModItems.PURPLE_PAINTBALL_CHEST.get()
                || item == ModItems.PURPLE_PAINTBALL_PANTS.get()
                || item == ModItems.PURPLE_PAINTBALL_SHOES.get();
    }

    private boolean isPaintballArmorOfColor(Item item, DyeColor color) {
        return switch (color) {
            case BLUE -> item == ModItems.BLUE_PAINTBALL_HELMET.get()
                    || item == ModItems.BLUE_PAINTBALL_CHEST.get()
                    || item == ModItems.BLUE_PAINTBALL_PANTS.get()
                    || item == ModItems.BLUE_PAINTBALL_SHOES.get();
            case RED -> item == ModItems.RED_PAINTBALL_HELMET.get()
                    || item == ModItems.RED_PAINTBALL_CHEST.get()
                    || item == ModItems.RED_PAINTBALL_PANTS.get()
                    || item == ModItems.RED_PAINTBALL_SHOES.get();
            case GREEN -> item == ModItems.GREEN_PAINTBALL_HELMET.get()
                    || item == ModItems.GREEN_PAINTBALL_CHEST.get()
                    || item == ModItems.GREEN_PAINTBALL_PANTS.get()
                    || item == ModItems.GREEN_PAINTBALL_SHOES.get();
            case YELLOW -> item == ModItems.YELLOW_PAINTBALL_HELMET.get()
                    || item == ModItems.YELLOW_PAINTBALL_CHEST.get()
                    || item == ModItems.YELLOW_PAINTBALL_PANTS.get()
                    || item == ModItems.YELLOW_PAINTBALL_SHOES.get();
            case ORANGE -> item == ModItems.ORANGE_PAINTBALL_HELMET.get()
                    || item == ModItems.ORANGE_PAINTBALL_CHEST.get()
                    || item == ModItems.ORANGE_PAINTBALL_PANTS.get()
                    || item == ModItems.ORANGE_PAINTBALL_SHOES.get();
            case PURPLE -> item == ModItems.PURPLE_PAINTBALL_HELMET.get()
                    || item == ModItems.PURPLE_PAINTBALL_CHEST.get()
                    || item == ModItems.PURPLE_PAINTBALL_PANTS.get()
                    || item == ModItems.PURPLE_PAINTBALL_SHOES.get();
            default -> false;
        };
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

    public static void serverTick(Level level, BlockPos pos, BlockState state, MedKitBlockEntity be) {
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

    public CompoundTag saveMedkitData() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }
}