package com.zerokg2004.paintball.network;

import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.item.gun.GunItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public final class NetworkHandler {

    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(PaintballMod.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static boolean initialized = false;

    private NetworkHandler() {}

    public static void init() {
        if (initialized) return;
        initialized = true;

        int id = 0;

        INSTANCE.messageBuilder(C4DetonatePacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(C4DetonatePacket::new)
                .encoder(C4DetonatePacket::toBytes)
                .consumerMainThread(C4DetonatePacket::handle)
                .add();

        INSTANCE.messageBuilder(ReloadGunPacket.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(ReloadGunPacket::new)
                .encoder(ReloadGunPacket::toBytes)
                .consumerMainThread(ReloadGunPacket::handle)
                .add();

        INSTANCE.messageBuilder(PacketDamageHandler.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketDamageHandler::new)
                .encoder(PacketDamageHandler::toBytes)
                .consumerMainThread(PacketDamageHandler::handle)
                .add();

        INSTANCE.messageBuilder(FireGunMessage.class, id++, NetworkDirection.PLAY_TO_SERVER)
                .decoder(FireGunMessage::new)
                .encoder(FireGunMessage::toBytes)
                .consumerMainThread(FireGunMessage::handle)
                .add();

        INSTANCE.messageBuilder(PacketHitMarker.class, id++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(PacketHitMarker::new)
                .encoder(PacketHitMarker::toBytes)
                .consumerMainThread(PacketHitMarker::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
    }

    // NUEVO
    public static void sendFireGun(InteractionHand hand, boolean ads) {
        sendToServer(new FireGunMessage(hand, ads));
    }

    public static final class FireGunMessage {

        private final int handOrdinal;
        private final boolean ads;

        public FireGunMessage(InteractionHand hand, boolean ads) {
            this.handOrdinal = (hand == InteractionHand.OFF_HAND) ? 1 : 0;
            this.ads = ads;
        }

        public FireGunMessage(FriendlyByteBuf buf) {
            this.handOrdinal = buf.readVarInt();
            this.ads = buf.readBoolean();
        }

        public void toBytes(FriendlyByteBuf buf) {
            buf.writeVarInt(handOrdinal);
            buf.writeBoolean(ads);
        }

        public static void handle(FireGunMessage msg, Supplier<NetworkEvent.Context> ctxSup) {
            NetworkEvent.Context ctx = ctxSup.get();

            ctx.enqueueWork(() -> {
                ServerPlayer sp = ctx.getSender();
                if (sp == null) return;

                InteractionHand hand = (msg.handOrdinal == 1) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                ItemStack stack = sp.getItemInHand(hand);

                if (!stack.isEmpty()) {
                    // Lógica para armas
                    if (stack.getItem() instanceof GunItem gun) {
                        gun.serverFire(sp, stack, msg.ads);
                    }
                    // NUEVO: Lógica para el detonador remoto
                    else if (stack.getItem() instanceof com.zerokg2004.paintball.item.RemoteItem remote) {
                        remote.tryRemoteDetonation(sp);
                    }
                }
            });

            ctx.setPacketHandled(true);
        }

        public static void sendReload(InteractionHand hand) {
            sendToServer(new ReloadGunPacket(hand));
        }
    }
}