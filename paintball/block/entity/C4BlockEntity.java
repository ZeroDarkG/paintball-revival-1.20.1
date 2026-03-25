package com.zerokg2004.paintball.block.entity;

import com.zerokg2004.paintball.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.UUID;

public class C4BlockEntity extends BlockEntity {

    private static final float EXPLOSION_STRENGTH = 15f;
    private static final int RANGE_INFLATE = 3;

    private Player owner;
    private DyeColor color;
    private UUID linkedPlayerId;
    private UUID uniqueId = UUID.randomUUID();

    private boolean armed = false;

    public C4BlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.C4_BLOCK_ENTITY.get(), pos, state);
    }

    public UUID getC4Id() {
        return uniqueId;
    }

    public UUID getLinkedPlayerId() {
        return linkedPlayerId;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player player) {
        this.owner = player;
        setChanged();
    }

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(DyeColor newColor) {
        if (this.color != newColor) {
            this.color = newColor;
            this.linkedPlayerId = null;
            setChanged();
            if (level != null) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    }

    public boolean isLinked() {
        return linkedPlayerId != null;
    }

    public boolean isLinkedTo(Player player) {
        return linkedPlayerId != null && linkedPlayerId.equals(player.getUUID());
    }

    public void unlink() {
        this.linkedPlayerId = null;
        setChanged();
    }

    public boolean tryLinkTo(Player player) {
        if (this.linkedPlayerId != null && !this.linkedPlayerId.equals(player.getUUID())) {
            return false;
        }
        this.linkedPlayerId = player.getUUID();
        setChanged();
        return true;
    }

    public boolean isArmed() {
        return armed;
    }

    public void setArmed(boolean armed) {
        this.armed = armed;
        setChanged();
    }

    public void startArming() {
        this.armed = true;
        setChanged();
    }

    public float getExplosionStrength() {
        return EXPLOSION_STRENGTH;
    }

    public int getRangeInflate() {
        return RANGE_INFLATE;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (color != null) tag.putInt("Color", color.getId());
        if (linkedPlayerId != null) tag.putUUID("LinkedPlayer", linkedPlayerId);
        tag.putUUID("C4Id", uniqueId);

        tag.putBoolean("Armed", this.armed);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.color = tag.contains("Color") ? DyeColor.byId(tag.getInt("Color")) : null;
        this.linkedPlayerId = tag.contains("LinkedPlayer") ? tag.getUUID("LinkedPlayer") : null;
        this.uniqueId = tag.hasUUID("C4Id") ? tag.getUUID("C4Id") : UUID.randomUUID();

        this.armed = tag.getBoolean("Armed");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        load(tag);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    public static void tick(Level level, BlockPos pos, BlockState state, C4BlockEntity blockEntity) {
        if (level.isClientSide) return;

    }
}