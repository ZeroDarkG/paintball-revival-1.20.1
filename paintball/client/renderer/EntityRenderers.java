package com.zerokg2004.paintball.client.renderer;

import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.registry.ModEntityTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = PaintballMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EntityRenderers {
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        // Grenades
        event.registerEntityRenderer(ModEntityTypes.RED_GRENADE.get(),    ctx -> new GrenadeRenderer(ctx, "red"));
        event.registerEntityRenderer(ModEntityTypes.BLUE_GRENADE.get(),   ctx -> new GrenadeRenderer(ctx, "blue"));
        event.registerEntityRenderer(ModEntityTypes.GREEN_GRENADE.get(),  ctx -> new GrenadeRenderer(ctx, "green"));
        event.registerEntityRenderer(ModEntityTypes.YELLOW_GRENADE.get(), ctx -> new GrenadeRenderer(ctx, "yellow"));
        event.registerEntityRenderer(ModEntityTypes.ORANGE_GRENADE.get(), ctx -> new GrenadeRenderer(ctx, "orange"));
        event.registerEntityRenderer(ModEntityTypes.PURPLE_GRENADE.get(), ctx -> new GrenadeRenderer(ctx, "purple"));

        // Pellets
        event.registerEntityRenderer(ModEntityTypes.RED_PELLET.get(),     ctx -> new PelletRenderer(ctx, "red"));
        event.registerEntityRenderer(ModEntityTypes.BLUE_PELLET.get(),    ctx -> new PelletRenderer(ctx, "blue"));
        event.registerEntityRenderer(ModEntityTypes.GREEN_PELLET.get(),   ctx -> new PelletRenderer(ctx, "green"));
        event.registerEntityRenderer(ModEntityTypes.YELLOW_PELLET.get(),  ctx -> new PelletRenderer(ctx, "yellow"));
        event.registerEntityRenderer(ModEntityTypes.ORANGE_PELLET.get(),  ctx -> new PelletRenderer(ctx, "orange"));
        event.registerEntityRenderer(ModEntityTypes.PURPLE_PELLET.get(),  ctx -> new PelletRenderer(ctx, "purple"));
    }
}