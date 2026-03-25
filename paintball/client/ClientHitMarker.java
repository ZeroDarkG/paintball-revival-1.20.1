package com.zerokg2004.paintball.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zerokg2004.paintball.PaintballMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = PaintballMod.MODID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public class ClientHitMarker {

    private static final ResourceLocation HITMARKER_TEX =
            new ResourceLocation(PaintballMod.MODID, "textures/gui/hit_marker.png");

    private static int hitMarkerTicks = 0;

    private ClientHitMarker() {
    }

    public static void triggerHitMarker() {
        hitMarkerTicks = 20;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent event) {
        if (event.phase != ClientTickEvent.Phase.END) return;
        if (hitMarkerTicks > 0) hitMarkerTicks--;
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (hitMarkerTicks <= 0) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;
        if (!mc.options.getCameraType().isFirstPerson()) return;

        GuiGraphics gg = event.getGuiGraphics();
        int screenW = mc.getWindow().getGuiScaledWidth();

        int drawW = 20;
        int drawH = 20;
        int u = 727;
        int v = 6;

        int margin = 6;
        int x = screenW - drawW - margin;
        int y = margin;

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        gg.blit(
                HITMARKER_TEX,
                x, y,
                (float) u, (float) v,
                drawW, drawH,
                1024, 256
        );

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }
}