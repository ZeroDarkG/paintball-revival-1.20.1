package com.zerokg2004.paintball.network;

import com.zerokg2004.paintball.item.gun.GunItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ReloadGunPacket {
    private final InteractionHand hand;

    // Constructor para el cliente
    public ReloadGunPacket(InteractionHand hand) {
        this.hand = hand;
    }

    // Constructor para el buffer (red)
    public ReloadGunPacket(FriendlyByteBuf buf) {
        this.hand = buf.readEnum(InteractionHand.class);
    }

    // Escritura en el buffer
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(this.hand);
    }

    // ✅ MÉTODO FALTANTE: Esto soluciona el error "Cannot resolve method 'getHand'"
    public InteractionHand getHand() {
        return this.hand;
    }

    public static void handle(ReloadGunPacket msg, Supplier<NetworkEvent.Context> ctxSup) {
        final NetworkEvent.Context ctx = ctxSup.get();
        final var sp = ctx.getSender();

        ctx.enqueueWork(() -> {
            if (sp == null) return;

            // Usamos la mano que viene en el mensaje
            ItemStack stack = sp.getItemInHand(msg.getHand());

            if (!stack.isEmpty() && stack.getItem() instanceof GunItem gun) {
                // Llamamos con 1 solo argumento (sp) para evitar el error anterior
                gun.manualReload(sp);
            }
        });
        ctx.setPacketHandled(true);
    }
}