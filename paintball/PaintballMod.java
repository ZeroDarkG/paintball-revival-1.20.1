package com.zerokg2004.paintball;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.client.ClientEvents;
import com.zerokg2004.paintball.entity.projectile.*;
import com.zerokg2004.paintball.network.NetworkHandler;
import com.zerokg2004.paintball.registry.*;
import com.zerokg2004.paintball.util.DispenseProjectile;
import com.zerokg2004.paintball.util.ModResourcePacks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(PaintballMod.MODID)
public class PaintballMod {
    public static final String MODID = "paintball";

    public PaintballMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Registros
        SoundEventsRegistry.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntityTypes.register(modEventBus);
        ModCreativeModeTabs.register(modEventBus);
        modEventBus.addListener(ModResourcePacks::addPackFinders);

        // Inicialización de Red
        NetworkHandler.init();

        // Registrar Common Setup (Para Dispensadores)
        modEventBus.addListener(this::commonSetup);

        // Cliente
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            setupClientEvents(modEventBus);
        });
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // --- DISPENSADORES: PELLETS ---
            DispenserBlock.registerBehavior(ModItems.BLUE_PELLETS.get(), new DispenseProjectile(ModEntityTypes.BLUE_PELLET.get(), BluePelletEntity::new));
            DispenserBlock.registerBehavior(ModItems.RED_PELLETS.get(), new DispenseProjectile(ModEntityTypes.RED_PELLET.get(), RedPelletEntity::new));
            DispenserBlock.registerBehavior(ModItems.GREEN_PELLETS.get(), new DispenseProjectile(ModEntityTypes.GREEN_PELLET.get(), GreenPelletEntity::new));
            DispenserBlock.registerBehavior(ModItems.YELLOW_PELLETS.get(), new DispenseProjectile(ModEntityTypes.YELLOW_PELLET.get(), YellowPelletEntity::new));
            DispenserBlock.registerBehavior(ModItems.ORANGE_PELLETS.get(), new DispenseProjectile(ModEntityTypes.ORANGE_PELLET.get(), OrangePelletEntity::new));
            DispenserBlock.registerBehavior(ModItems.PURPLE_PELLETS.get(), new DispenseProjectile(ModEntityTypes.PURPLE_PELLET.get(), PurplePelletEntity::new));

            // --- DISPENSADORES: GRANADAS ---
            DispenserBlock.registerBehavior(ModItems.BLUE_GRENADE.get(), new DispenseProjectile(ModEntityTypes.BLUE_GRENADE.get(), BlueGrenadeEntity::new));
            DispenserBlock.registerBehavior(ModItems.RED_GRENADE.get(), new DispenseProjectile(ModEntityTypes.RED_GRENADE.get(), RedGrenadeEntity::new));
            DispenserBlock.registerBehavior(ModItems.GREEN_GRENADE.get(), new DispenseProjectile(ModEntityTypes.GREEN_GRENADE.get(), GreenGrenadeEntity::new));
            DispenserBlock.registerBehavior(ModItems.YELLOW_GRENADE.get(), new DispenseProjectile(ModEntityTypes.YELLOW_GRENADE.get(), YellowGrenadeEntity::new));
            DispenserBlock.registerBehavior(ModItems.ORANGE_GRENADE.get(), new DispenseProjectile(ModEntityTypes.ORANGE_GRENADE.get(), OrangeGrenadeEntity::new));
            DispenserBlock.registerBehavior(ModItems.PURPLE_GRENADE.get(), new DispenseProjectile(ModEntityTypes.PURPLE_GRENADE.get(), PurpleGrenadeEntity::new));
        });
    }

    private void setupClientEvents(IEventBus eventBus) {
        eventBus.addListener(ClientEvents::onClientSetup);
    }
}