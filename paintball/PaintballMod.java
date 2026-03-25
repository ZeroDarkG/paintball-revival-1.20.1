package com.zerokg2004.paintball;

import com.zerokg2004.paintball.client.ClientEvents;
import com.zerokg2004.paintball.network.NetworkHandler;
import com.zerokg2004.paintball.registry.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PaintballMod.MODID)
public class PaintballMod {
    public static final String MODID = "paintball";
    public PaintballMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        SoundEventsRegistry.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        NetworkHandler.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            setupClientEvents(modEventBus);
        });
    }
    private void setupClientEvents(IEventBus eventBus) {
        eventBus.addListener(ClientEvents::onClientSetup);
    }
}