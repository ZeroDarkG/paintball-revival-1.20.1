package com.zerokg2004.paintball.network;

import com.zerokg2004.paintball.item.RemoteItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class C4DetonatePacket {

    public C4DetonatePacket() {}

    public C4DetonatePacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                ItemStack held = player.getMainHandItem();
                if (held.getItem() instanceof RemoteItem remote) {
                    remote.tryRemoteDetonation(player);
                }
            }
        });
        ctx.get().setPacketHandled(true);
        return true;
    }
}