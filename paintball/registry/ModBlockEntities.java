package com.zerokg2004.paintball.registry;

import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PaintballMod.MODID);

    public static final RegistryObject<BlockEntityType<C4BlockEntity>> C4_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("c4",
                    () -> BlockEntityType.Builder.of(C4BlockEntity::new,
                            ModBlocks.RED_C4_BLOCK.get(),
                            ModBlocks.BLUE_C4_BLOCK.get(),
                            ModBlocks.GREEN_C4_BLOCK.get(),
                            ModBlocks.YELLOW_C4_BLOCK.get(),
                            ModBlocks.ORANGE_C4_BLOCK.get(),
                            ModBlocks.PURPLE_C4_BLOCK.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<ClaymoreBlockEntity>> CLAYMORE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("claymore",
                    () -> BlockEntityType.Builder.of(ClaymoreBlockEntity::new,
                            ModBlocks.RED_CLAYMORE_BLOCK.get(),
                            ModBlocks.BLUE_CLAYMORE_BLOCK.get(),
                            ModBlocks.GREEN_CLAYMORE_BLOCK.get(),
                            ModBlocks.YELLOW_CLAYMORE_BLOCK.get(),
                            ModBlocks.ORANGE_CLAYMORE_BLOCK.get(),
                            ModBlocks.PURPLE_CLAYMORE_BLOCK.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<FlagBlockEntity>> FLAG_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("flag",
                    () -> BlockEntityType.Builder.of(FlagBlockEntity::new,
                            ModBlocks.RED_FLAG_BLOCK.get(),
                            ModBlocks.BLUE_FLAG_BLOCK.get(),
                            ModBlocks.GREEN_FLAG_BLOCK.get(),
                            ModBlocks.YELLOW_FLAG_BLOCK.get(),
                            ModBlocks.ORANGE_FLAG_BLOCK.get(),
                            ModBlocks.PURPLE_FLAG_BLOCK.get()
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<MedKitBlockEntity>> MEDKIT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("medkit",
                    () -> BlockEntityType.Builder.of(MedKitBlockEntity::new,
                            ModBlocks.RED_MEDKIT_BLOCK.get(),
                            ModBlocks.BLUE_MEDKIT_BLOCK.get(),
                            ModBlocks.GREEN_MEDKIT_BLOCK.get(),
                            ModBlocks.YELLOW_MEDKIT_BLOCK.get(),
                            ModBlocks.ORANGE_MEDKIT_BLOCK.get(),
                            ModBlocks.PURPLE_MEDKIT_BLOCK.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<PodBlockEntity>> POD_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("pod",
                    () -> BlockEntityType.Builder.of(PodBlockEntity::new,
                            ModBlocks.RED_POD_BLOCK.get(),
                            ModBlocks.BLUE_POD_BLOCK.get(),
                            ModBlocks.GREEN_POD_BLOCK.get(),
                            ModBlocks.YELLOW_POD_BLOCK.get(),
                            ModBlocks.ORANGE_POD_BLOCK.get(),
                            ModBlocks.PURPLE_POD_BLOCK.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<DecisionRouletteBlockEntity>> ROULETTE_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("roulette",
                    () -> BlockEntityType.Builder.of(DecisionRouletteBlockEntity::new,
                            ModBlocks.DECISION_ROULETTE_BLOCK.get()   // <- aquí
                    ).build(null)
            );

    public static final RegistryObject<BlockEntityType<GearRackBlockEntity>> GEAR_RACK_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("gear_rack",
                    () -> BlockEntityType.Builder.of(GearRackBlockEntity::new,
                            ModBlocks.RED_GEAR_RACK_BLOCK.get(),
                            ModBlocks.BLUE_GEAR_RACK_BLOCK.get(),
                            ModBlocks.GREEN_GEAR_RACK_BLOCK.get(),
                            ModBlocks.YELLOW_GEAR_RACK_BLOCK.get(),
                            ModBlocks.ORANGE_GEAR_RACK_BLOCK.get(),
                            ModBlocks.PURPLE_GEAR_RACK_BLOCK.get()
                    ).build(null));

    public static final RegistryObject<BlockEntityType<WeaponRackBlockEntity>> WEAPON_RACK_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("weapon_rack",
                    () -> BlockEntityType.Builder.of(
                            WeaponRackBlockEntity::new,
                            ModBlocks.RED_WEAPON_RACK_BLOCK.get(),
                            ModBlocks.BLUE_WEAPON_RACK_BLOCK.get(),
                            ModBlocks.GREEN_WEAPON_RACK_BLOCK.get(),
                            ModBlocks.YELLOW_WEAPON_RACK_BLOCK.get(),
                            ModBlocks.ORANGE_WEAPON_RACK_BLOCK.get(),
                            ModBlocks.PURPLE_WEAPON_RACK_BLOCK.get()
                    ).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}