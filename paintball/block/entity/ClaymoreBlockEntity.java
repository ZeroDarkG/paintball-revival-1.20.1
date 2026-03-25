package com.zerokg2004.paintball.block.entity;

import com.zerokg2004.paintball.block.ClaymoreBlock;
import com.zerokg2004.paintball.item.PaintballArmorItem;
import com.zerokg2004.paintball.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ClaymoreBlockEntity extends BlockEntity {

    private static final int ARM_DELAY_TICKS = 20;
    private static final float VISUAL_EXPLOSION_STRENGTH = 1.5F;
    private static final float CLAYMORE_DAMAGE = 15.0F;
    private static final int RANGE_INFLATE = 3;
    private UUID ownerId;
    private long armAtGameTime;
    private boolean armed;

    public ClaymoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CLAYMORE_BLOCK_ENTITY.get(), pos, state);
    }

    public void onPlacedBy(UUID owner) {
        this.ownerId = owner;
        if (this.level != null) {
            this.armAtGameTime = this.level.getGameTime() + ARM_DELAY_TICKS;
        }
        setChanged();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, ClaymoreBlockEntity be) {
        if (level.isClientSide) return;

        if (!be.armed) {
            if (level.getGameTime() >= be.armAtGameTime) {
                be.armed = true;
                be.setChanged();
            } else {
                return;
            }
        }

        DyeColor clayColor = ((ClaymoreBlock) state.getBlock()).getColor();
        AABB box = new AABB(pos).inflate(RANGE_INFLATE);
        List<LivingEntity> living = level.getEntitiesOfClass(
                LivingEntity.class,
                box,
                e -> e.isAlive() && !(e instanceof Player p && (p.isSpectator() || p.isCreative()))
        );

        if (living.isEmpty()) return;
        boolean shouldDetonate = false;
        for (LivingEntity e : living) {
            if (e instanceof Player p) {
                if (isWearingFullArmorOfColor(p, clayColor)) continue;
                shouldDetonate = true;
                break;
            } else {
                shouldDetonate = true;
                break;
            }
        }

        if (!shouldDetonate) return;
        level.explode(
                null,
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                VISUAL_EXPLOSION_STRENGTH,
                false,
                Level.ExplosionInteraction.NONE
        );

        for (LivingEntity e : living) {
            if (e instanceof Player p && isWearingFullArmorOfColor(p, clayColor)) {
                continue;
            }
            e.hurt(level.damageSources().explosion(null), CLAYMORE_DAMAGE);
        }

        level.removeBlock(pos, false);
    }

    private static boolean isWearingFullArmorOfColor(Player player, DyeColor expectedColor) {
        for (EquipmentSlot slot : new EquipmentSlot[]{
                EquipmentSlot.HEAD,
                EquipmentSlot.CHEST,
                EquipmentSlot.LEGS,
                EquipmentSlot.FEET
        }) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!(stack.getItem() instanceof PaintballArmorItem armorItem) ||
                    armorItem.getColor() != expectedColor) {
                return false;
            }
        }
        return true;
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
        return saveWithoutMetadata();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition).inflate(0.25D);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (ownerId != null) tag.putUUID("Owner", ownerId);
        tag.putLong("ArmAt", armAtGameTime);
        tag.putBoolean("Armed", armed);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        ownerId = tag.hasUUID("Owner") ? tag.getUUID("Owner") : null;
        armAtGameTime = tag.getLong("ArmAt");
        armed = tag.getBoolean("Armed");
    }
}