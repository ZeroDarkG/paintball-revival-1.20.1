package com.zerokg2004.paintball.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class PacketDamageHandler {

    public final int targetId;
    public final float damage;
    public final String shooterTeam;

    public PacketDamageHandler(int targetId, float damage, String shooterTeam) {
        this.targetId = targetId;
        this.damage = damage;
        this.shooterTeam = shooterTeam;
    }

    public PacketDamageHandler(FriendlyByteBuf buf) {
        this.targetId = buf.readVarInt();
        this.damage = buf.readFloat();
        this.shooterTeam = buf.readUtf(32);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(this.targetId);
        buf.writeFloat(this.damage);
        buf.writeUtf(this.shooterTeam);
    }

    public static void handle(PacketDamageHandler msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();

        context.enqueueWork(() -> {
            ServerPlayer shooter = context.getSender();
            if (shooter == null) return;

            Level level = shooter.level();

            Entity entity = level.getEntity(msg.targetId);
            if (!(entity instanceof LivingEntity living)) return;

            String st = msg.shooterTeam == null ? "" : msg.shooterTeam;
            String tt = living.getPersistentData().getString("paintball_team");

            if (!st.isEmpty() && tt != null && !tt.isEmpty() && st.equals(tt)) {
                return;
            }

            boolean didDamage = living.hurt(
                    shooter.damageSources().playerAttack(shooter),
                    msg.damage
            );

            if (didDamage) {
                NetworkHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> shooter),
                        new PacketHitMarker()
                );
            }
        });

        context.setPacketHandled(true);
    }
}