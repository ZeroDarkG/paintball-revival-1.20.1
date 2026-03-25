package com.zerokg2004.paintball.network;

import com.zerokg2004.paintball.item.gun.GunItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ReloadGunPacket {
    public ReloadGunPacket() {}
    public ReloadGunPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}
    public static void handle(ReloadGunPacket msg, Supplier<NetworkEvent.Context> ctxSup) {
        final NetworkEvent.Context ctx = ctxSup.get();
        final var sp = ctx.getSender();
        ctx.enqueueWork(() -> {
            if (sp == null) return;
            ItemStack stack = sp.getMainHandItem();
            if (!stack.isEmpty() && stack.getItem() instanceof GunItem pistolMain) {
                pistolMain.manualReload(sp);
                ctx.setPacketHandled(true);
                return;
            }
            ItemStack off = sp.getOffhandItem();
            if (!off.isEmpty() && off.getItem() instanceof GunItem pistolOff) {
                pistolOff.manualReload(sp);
            }
        });
        ctx.setPacketHandled(true);
    }
}