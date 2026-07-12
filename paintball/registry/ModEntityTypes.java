package com.zerokg2004.paintball.registry;

import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.entity.projectile.*;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, PaintballMod.MODID);

    public static final RegistryObject<EntityType<RedGrenadeEntity>> RED_GRENADE =
            ENTITY_TYPES.register("red_grenade",
                    () -> EntityType.Builder.<RedGrenadeEntity>of(RedGrenadeEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("red_grenade"));

    public static final RegistryObject<EntityType<BlueGrenadeEntity>> BLUE_GRENADE =
            ENTITY_TYPES.register("blue_grenade",
                    () -> EntityType.Builder.<BlueGrenadeEntity>of(BlueGrenadeEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("blue_grenade"));

    public static final RegistryObject<EntityType<GreenGrenadeEntity>> GREEN_GRENADE =
            ENTITY_TYPES.register("green_grenade",
                    () -> EntityType.Builder.<GreenGrenadeEntity>of(GreenGrenadeEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("green_grenade"));

    public static final RegistryObject<EntityType<YellowGrenadeEntity>> YELLOW_GRENADE =
            ENTITY_TYPES.register("yellow_grenade",
                    () -> EntityType.Builder.<YellowGrenadeEntity>of(YellowGrenadeEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("yellow_grenade"));

    public static final RegistryObject<EntityType<OrangeGrenadeEntity>> ORANGE_GRENADE =
            ENTITY_TYPES.register("orange_grenade",
                    () -> EntityType.Builder.<OrangeGrenadeEntity>of(OrangeGrenadeEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("orange_grenade"));

    public static final RegistryObject<EntityType<PurpleGrenadeEntity>> PURPLE_GRENADE =
            ENTITY_TYPES.register("purple_grenade",
                    () -> EntityType.Builder.<PurpleGrenadeEntity>of(PurpleGrenadeEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("purple_grenade"));

    public static final RegistryObject<EntityType<RedPelletEntity>> RED_PELLET =
            ENTITY_TYPES.register("red_pellet",
                    () -> EntityType.Builder.<RedPelletEntity>of(RedPelletEntity::new, MobCategory.MISC)
                            .sized(0.125F, 0.125F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("red_pellet"));

    public static final RegistryObject<EntityType<BluePelletEntity>> BLUE_PELLET =
            ENTITY_TYPES.register("blue_pellet",
                    () -> EntityType.Builder.<BluePelletEntity>of(BluePelletEntity::new, MobCategory.MISC)
                            .sized(0.125F, 0.125F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("blue_pellet"));

    public static final RegistryObject<EntityType<GreenPelletEntity>> GREEN_PELLET =
            ENTITY_TYPES.register("green_pellet",
                    () -> EntityType.Builder.<GreenPelletEntity>of(GreenPelletEntity::new, MobCategory.MISC)
                            .sized(0.125F, 0.125F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("green_pellet"));

    public static final RegistryObject<EntityType<YellowPelletEntity>> YELLOW_PELLET =
            ENTITY_TYPES.register("yellow_pellet",
                    () -> EntityType.Builder.<YellowPelletEntity>of(YellowPelletEntity::new, MobCategory.MISC)
                            .sized(0.125F, 0.125F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("yellow_pellet"));

    public static final RegistryObject<EntityType<OrangePelletEntity>> ORANGE_PELLET =
            ENTITY_TYPES.register("orange_pellet",
                    () -> EntityType.Builder.<OrangePelletEntity>of(OrangePelletEntity::new, MobCategory.MISC)
                            .sized(0.125F, 0.125F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("orange_pellet"));

    public static final RegistryObject<EntityType<PurplePelletEntity>> PURPLE_PELLET =
            ENTITY_TYPES.register("purple_pellet",
                    () -> EntityType.Builder.<PurplePelletEntity>of(PurplePelletEntity::new, MobCategory.MISC)
                            .sized(0.125F, 0.125F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build("purple_pellet"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}