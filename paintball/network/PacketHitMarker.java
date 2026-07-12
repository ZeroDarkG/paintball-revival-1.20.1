package com.zerokg2004.paintball.network;

import com.zerokg2004.paintball.client.ClientHitMarker;   // <-- IMPORTA ESTA
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PacketHitMarker {

    public PacketHitMarker() {}

    public PacketHitMarker(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            System.out.println("[PAINTBALL] PacketHitMarker recibido en cliente -> triggerHitMarker()");
            ClientHitMarker.triggerHitMarker();
        });
        ctx.setPacketHandled(true);
    }
}