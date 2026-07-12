package com.zerokg2004.paintball.block.entity;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.block.GearRackBlock;
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

public class GearRackBlockEntity extends BlockEntity {

    private static final int RECHARGE_STEP_TICKS = 20 * 10;
    public static final int MAX_STAGE = 5;

    private int chargeStage = 0;
    private int rechargeTicks = 0;

    public GearRackBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GEAR_RACK_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean tryGiveArmor(Player player) {
        if (level == null || level.isClientSide) return false;
        if (player == null) return false;

        // Lógica para modo Creativo
        boolean isCreative = player.getAbilities().instabuild;

        // Si NO es creativo, aplicamos las restricciones normales
        if (!isCreative) {
            // Bloqueo: Solo permite el uso si la recarga ha terminado
            if (this.chargeStage != 0) return false;

            DyeColor rackColor = DyeColor.BLUE;
            BlockState state = getBlockState();
            if (state.getBlock() instanceof GearRackBlock rackBlock) {
                rackColor = rackBlock.getColor();
            }

            // Aquí comprueba si puede usarlo (por ejemplo, si no tiene ya el equipo puesto)
            if (!canUseGearRack(player, rackColor)) return false;
        }

        // Obtenemos el color (necesario para equipar el set incluso en creativo)
        DyeColor colorToGive = DyeColor.BLUE;
        if (getBlockState().getBlock() instanceof GearRackBlock rackBlock) {
            colorToGive = rackBlock.getColor();
        }

        // Verificación de inventario (opcional en creativo, pero recomendada)
        boolean armorFull =
                !player.getItemBySlot(EquipmentSlot.HEAD).isEmpty() &&
                        !player.getItemBySlot(EquipmentSlot.CHEST).isEmpty() &&
                        !player.getItemBySlot(EquipmentSlot.LEGS).isEmpty() &&
                        !player.getItemBySlot(EquipmentSlot.FEET).isEmpty();

        if (armorFull && player.getInventory().getFreeSlot() == -1) {
            player.sendSystemMessage(Component.literal("Your inventory is full"));
            return false;
        }

        // Entregamos el equipo
        equipFullSet(player, colorToGive);

        level.playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEvents.ITEM_PICKUP,
                SoundSource.PLAYERS,
                0.25F,
                0.9F + (level.random.nextFloat() * 0.2F)
        );

        // SOLO reinicia el ciclo de recarga si NO es creativo
        if (!isCreative) {
            chargeStage = MAX_STAGE;
            rechargeTicks = 0;
            setChanged();
        }

        return true;
    }

    private boolean canUseGearRack(Player player, DyeColor rackColor) {
        for (ItemStack stack : player.getArmorSlots()) {
            if (stack.isEmpty()) continue;

            if (stack.getItem() instanceof PaintballArmorItem armorItem) {
                if (armorItem.getColor() != rackColor) {
                    sendTeamMessage(player,
                            "You aren't on the " + rackColor.getName() + " team.",
                            rackColor);
                    return false;
                }
            }
        }
        return true;
    }

    private void equipFullSet(Player player, DyeColor color) {
        ItemStack helmet = ItemStack.EMPTY;
        ItemStack chest = ItemStack.EMPTY;
        ItemStack legs = ItemStack.EMPTY;
        ItemStack boots = ItemStack.EMPTY;

        switch (color) {
            case BLUE -> {
                helmet = new ItemStack(ModItems.BLUE_PAINTBALL_HELMET.get());
                chest  = new ItemStack(ModItems.BLUE_PAINTBALL_CHEST.get());
                legs   = new ItemStack(ModItems.BLUE_PAINTBALL_PANTS.get());
                boots  = new ItemStack(ModItems.BLUE_PAINTBALL_SHOES.get());
            }
            case RED -> {
                helmet = new ItemStack(ModItems.RED_PAINTBALL_HELMET.get());
                chest  = new ItemStack(ModItems.RED_PAINTBALL_CHEST.get());
                legs   = new ItemStack(ModItems.RED_PAINTBALL_PANTS.get());
                boots  = new ItemStack(ModItems.RED_PAINTBALL_SHOES.get());
            }
            case GREEN -> {
                helmet = new ItemStack(ModItems.GREEN_PAINTBALL_HELMET.get());
                chest  = new ItemStack(ModItems.GREEN_PAINTBALL_CHEST.get());
                legs   = new ItemStack(ModItems.GREEN_PAINTBALL_PANTS.get());
                boots  = new ItemStack(ModItems.GREEN_PAINTBALL_SHOES.get());
            }
            case YELLOW -> {
                helmet = new ItemStack(ModItems.YELLOW_PAINTBALL_HELMET.get());
                chest  = new ItemStack(ModItems.YELLOW_PAINTBALL_CHEST.get());
                legs   = new ItemStack(ModItems.YELLOW_PAINTBALL_PANTS.get());
                boots  = new ItemStack(ModItems.YELLOW_PAINTBALL_SHOES.get());
            }
            case ORANGE -> {
                helmet = new ItemStack(ModItems.ORANGE_PAINTBALL_HELMET.get());
                chest  = new ItemStack(ModItems.ORANGE_PAINTBALL_CHEST.get());
                legs   = new ItemStack(ModItems.ORANGE_PAINTBALL_PANTS.get());
                boots  = new ItemStack(ModItems.ORANGE_PAINTBALL_SHOES.get());
            }
            case PURPLE -> {
                helmet = new ItemStack(ModItems.PURPLE_PAINTBALL_HELMET.get());
                chest  = new ItemStack(ModItems.PURPLE_PAINTBALL_CHEST.get());
                legs   = new ItemStack(ModItems.PURPLE_PAINTBALL_PANTS.get());
                boots  = new ItemStack(ModItems.PURPLE_PAINTBALL_SHOES.get());
            }
            default -> { }
        }

        if (!helmet.isEmpty()) {
            moveOldArmorToInventory(player, EquipmentSlot.HEAD);
            player.setItemSlot(EquipmentSlot.HEAD, helmet);
        }
        if (!chest.isEmpty()) {
            moveOldArmorToInventory(player, EquipmentSlot.CHEST);
            player.setItemSlot(EquipmentSlot.CHEST, chest);
        }
        if (!legs.isEmpty()) {
            moveOldArmorToInventory(player, EquipmentSlot.LEGS);
            player.setItemSlot(EquipmentSlot.LEGS, legs);
        }
        if (!boots.isEmpty()) {
            moveOldArmorToInventory(player, EquipmentSlot.FEET);
            player.setItemSlot(EquipmentSlot.FEET, boots);
        }
    }

    private void moveOldArmorToInventory(Player player, EquipmentSlot slot) {
        ItemStack old = player.getItemBySlot(slot);
        if (old.isEmpty()) return;

        ItemStack copy = old.copy();
        if (!player.addItem(copy)) {
            player.drop(copy, false);
        }
    }

    private void sendTeamMessage(Player player, String message, DyeColor color) {
        ChatFormatting formatting = switch (color) {
            case RED -> ChatFormatting.RED;
            case GREEN -> ChatFormatting.GREEN;
            case BLUE -> ChatFormatting.BLUE;
            case YELLOW -> ChatFormatting.YELLOW;
            case PURPLE -> ChatFormatting.LIGHT_PURPLE;
            case ORANGE -> ChatFormatting.GOLD;
            default -> ChatFormatting.WHITE;
        };

        player.sendSystemMessage(Component.literal(message).withStyle(formatting));
    }

    public int getTextureIndex() {
        int stage = Math.max(0, Math.min(MAX_STAGE, chargeStage));
        return stage + 1;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, GearRackBlockEntity be) {
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
}