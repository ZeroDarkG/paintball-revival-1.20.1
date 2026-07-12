package com.zerokg2004.paintball.registry;

import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.block.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PaintballMod.MODID);

    public static final RegistryObject<Block> RED_SCANNER = registerScanner("red_scanner", DyeColor.RED);
    public static final RegistryObject<Block> BLUE_SCANNER = registerScanner("blue_scanner", DyeColor.BLUE);
    public static final RegistryObject<Block> ORANGE_SCANNER = registerScanner("orange_scanner", DyeColor.ORANGE);
    public static final RegistryObject<Block> YELLOW_SCANNER = registerScanner("yellow_scanner", DyeColor.YELLOW);
    public static final RegistryObject<Block> GREEN_SCANNER = registerScanner("green_scanner", DyeColor.GREEN);
    public static final RegistryObject<Block> PURPLE_SCANNER = registerScanner("purple_scanner", DyeColor.PURPLE);

    public static final RegistryObject<Block> RED_C4_BLOCK = registerC4("red_c4", DyeColor.RED);
    public static final RegistryObject<Block> BLUE_C4_BLOCK = registerC4("blue_c4", DyeColor.BLUE);
    public static final RegistryObject<Block> GREEN_C4_BLOCK = registerC4("green_c4", DyeColor.GREEN);
    public static final RegistryObject<Block> YELLOW_C4_BLOCK = registerC4("yellow_c4", DyeColor.YELLOW);
    public static final RegistryObject<Block> ORANGE_C4_BLOCK = registerC4("orange_c4", DyeColor.ORANGE);
    public static final RegistryObject<Block> PURPLE_C4_BLOCK = registerC4("purple_c4", DyeColor.PURPLE);

    public static final RegistryObject<Block> RED_CLAYMORE_BLOCK = registerClaymore("red_claymore", DyeColor.RED);
    public static final RegistryObject<Block> BLUE_CLAYMORE_BLOCK = registerClaymore("blue_claymore", DyeColor.BLUE);
    public static final RegistryObject<Block> GREEN_CLAYMORE_BLOCK = registerClaymore("green_claymore", DyeColor.GREEN);
    public static final RegistryObject<Block> YELLOW_CLAYMORE_BLOCK = registerClaymore("yellow_claymore", DyeColor.YELLOW);
    public static final RegistryObject<Block> ORANGE_CLAYMORE_BLOCK = registerClaymore("orange_claymore", DyeColor.ORANGE);
    public static final RegistryObject<Block> PURPLE_CLAYMORE_BLOCK = registerClaymore("purple_claymore", DyeColor.PURPLE);

    public static final RegistryObject<Block> RED_FLAG_BLOCK = registerFlag("red_flag", DyeColor.RED);
    public static final RegistryObject<Block> BLUE_FLAG_BLOCK = registerFlag("blue_flag", DyeColor.BLUE);
    public static final RegistryObject<Block> GREEN_FLAG_BLOCK = registerFlag("green_flag", DyeColor.GREEN);
    public static final RegistryObject<Block> YELLOW_FLAG_BLOCK = registerFlag("yellow_flag", DyeColor.YELLOW);
    public static final RegistryObject<Block> ORANGE_FLAG_BLOCK = registerFlag("orange_flag", DyeColor.ORANGE);
    public static final RegistryObject<Block> PURPLE_FLAG_BLOCK = registerFlag("purple_flag", DyeColor.PURPLE);

    public static final RegistryObject<Block> RED_MEDKIT_BLOCK = registerMedkit("red_medkit", DyeColor.RED);
    public static final RegistryObject<Block> BLUE_MEDKIT_BLOCK = registerMedkit("blue_medkit", DyeColor.BLUE);
    public static final RegistryObject<Block> GREEN_MEDKIT_BLOCK = registerMedkit("green_medkit", DyeColor.GREEN);
    public static final RegistryObject<Block> YELLOW_MEDKIT_BLOCK = registerMedkit("yellow_medkit", DyeColor.YELLOW);
    public static final RegistryObject<Block> ORANGE_MEDKIT_BLOCK = registerMedkit("orange_medkit", DyeColor.ORANGE);
    public static final RegistryObject<Block> PURPLE_MEDKIT_BLOCK = registerMedkit("purple_medkit", DyeColor.PURPLE);

    public static final RegistryObject<Block> RED_POD_BLOCK = registerPod("red_pod", DyeColor.RED);
    public static final RegistryObject<Block> BLUE_POD_BLOCK = registerPod("blue_pod", DyeColor.BLUE);
    public static final RegistryObject<Block> GREEN_POD_BLOCK = registerPod("green_pod", DyeColor.GREEN);
    public static final RegistryObject<Block> YELLOW_POD_BLOCK = registerPod("yellow_pod", DyeColor.YELLOW);
    public static final RegistryObject<Block> ORANGE_POD_BLOCK = registerPod("orange_pod", DyeColor.ORANGE);
    public static final RegistryObject<Block> PURPLE_POD_BLOCK = registerPod("purple_pod", DyeColor.PURPLE);

    public static final RegistryObject<Block> RED_GEAR_RACK_BLOCK = registerGearRack("red_gear_rack", DyeColor.RED);
    public static final RegistryObject<Block> BLUE_GEAR_RACK_BLOCK = registerGearRack("blue_gear_rack", DyeColor.BLUE);
    public static final RegistryObject<Block> GREEN_GEAR_RACK_BLOCK = registerGearRack("green_gear_rack", DyeColor.GREEN);
    public static final RegistryObject<Block> YELLOW_GEAR_RACK_BLOCK = registerGearRack("yellow_gear_rack", DyeColor.YELLOW);
    public static final RegistryObject<Block> ORANGE_GEAR_RACK_BLOCK = registerGearRack("orange_gear_rack", DyeColor.ORANGE);
    public static final RegistryObject<Block> PURPLE_GEAR_RACK_BLOCK = registerGearRack("purple_gear_rack", DyeColor.PURPLE);

    public static final RegistryObject<Block> RED_WEAPON_RACK_BLOCK = registerWeaponRack("red_weapon_rack", DyeColor.RED);
    public static final RegistryObject<Block> BLUE_WEAPON_RACK_BLOCK = registerWeaponRack("blue_weapon_rack", DyeColor.BLUE);
    public static final RegistryObject<Block> GREEN_WEAPON_RACK_BLOCK = registerWeaponRack("green_weapon_rack", DyeColor.GREEN);
    public static final RegistryObject<Block> YELLOW_WEAPON_RACK_BLOCK = registerWeaponRack("yellow_weapon_rack", DyeColor.YELLOW);
    public static final RegistryObject<Block> ORANGE_WEAPON_RACK_BLOCK = registerWeaponRack("orange_weapon_rack", DyeColor.ORANGE);
    public static final RegistryObject<Block> PURPLE_WEAPON_RACK_BLOCK = registerWeaponRack("purple_weapon_rack", DyeColor.PURPLE);

    public static final RegistryObject<Block> RED_INSTA_BASE_BLOCK = registerInstaBase("red_insta_base", DyeColor.RED);
    public static final RegistryObject<Block> BLUE_INSTA_BASE_BLOCK = registerInstaBase("blue_insta_base", DyeColor.BLUE);
    public static final RegistryObject<Block> GREEN_INSTA_BASE_BLOCK = registerInstaBase("green_insta_base", DyeColor.GREEN);
    public static final RegistryObject<Block> YELLOW_INSTA_BASE_BLOCK = registerInstaBase("yellow_insta_base", DyeColor.YELLOW);
    public static final RegistryObject<Block> ORANGE_INSTA_BASE_BLOCK = registerInstaBase("orange_insta_base", DyeColor.ORANGE);
    public static final RegistryObject<Block> PURPLE_INSTA_BASE_BLOCK = registerInstaBase("purple_insta_base", DyeColor.PURPLE);

    public static final RegistryObject<Block> DECISION_ROULETTE_BLOCK =
            registerRoulette("decision_roulette");

    private static RegistryObject<Block> registerScanner(String name, DyeColor color) {
        return BLOCKS.register(name, () ->
                new ScannerBlock(
                        color,
                        BlockBehaviour.Properties.of()
                                .mapColor(color.getMapColor())
                                .strength(0.5F)
                                .noCollission()
                                .sound(ModSoundTypes.SCANNER)
                ));
    }

    private static RegistryObject<Block> registerC4(String name, DyeColor color) {
        return BLOCKS.register(name, () ->
                new C4Block(
                        BlockBehaviour.Properties.of()
                                .mapColor(color.getMapColor())
                                .strength(2F)
                                .noOcclusion()
                                .dynamicShape(),
                        color
                ));
    }

    private static RegistryObject<Block> registerClaymore(String name, DyeColor color) {
        return BLOCKS.register(name, () ->
                new ClaymoreBlock(
                        BlockBehaviour.Properties.of()
                                .mapColor(color.getMapColor())
                                .strength(2F)
                                .noOcclusion()
                                .dynamicShape()
                                .sound(ModSoundTypes.CLAYMORE),
                        color
                ));
    }

    private static RegistryObject<Block> registerFlag(String name, DyeColor color) {
        return BLOCKS.register(name, () ->
                new FlagBlock(
                        color,
                        BlockBehaviour.Properties.of()
                                .mapColor(color.getMapColor())
                                .strength(0F)
                                .noOcclusion()
                                .sound(SoundType.STONE)
                )
        );
    }

    private static RegistryObject<Block> registerMedkit(String name, DyeColor color) {
        return BLOCKS.register(name, () ->
                new MedKitBlock(
                        color,
                        BlockBehaviour.Properties.of()
                                .mapColor(color.getMapColor())
                                .strength(2F)
                                .noOcclusion()
                                .sound(SoundType.METAL)
                )
        );
    }

    private static RegistryObject<Block> registerPod(String name, DyeColor color) {
        return BLOCKS.register(name, () ->
                new PodBlock(
                        color,
                        BlockBehaviour.Properties.of()
                                .mapColor(color.getMapColor())
                                .strength(2F)
                                .noOcclusion()
                                .sound(SoundType.STONE)
                )
        );
    }

    private static RegistryObject<Block> registerGearRack(String name, DyeColor color) {
        return BLOCKS.register(name, () ->
                new GearRackBlock(
                        color,
                        BlockBehaviour.Properties.of()
                                .mapColor(color.getMapColor())
                                .strength(2F)
                                .noOcclusion()
                                .sound(SoundType.STONE)
                )
        );
    }

    private static RegistryObject<Block> registerWeaponRack(String name, DyeColor color) {
        return BLOCKS.register(name, () ->
                new WeaponRackBlock(
                        color,
                        BlockBehaviour.Properties.of()
                                .strength(2F)
                                .noOcclusion()
                )
        );
    }

    private static RegistryObject<Block> registerRoulette(String name) {
        return BLOCKS.register(name, () ->
                new DecisionRouletteBlock(
                        BlockBehaviour.Properties.of()
                                .strength(0F)
                                .noOcclusion()
                                .dynamicShape()
                                .sound(SoundType.METAL)
                )
        );
    }

    private static RegistryObject<Block> registerInstaBase(String name, DyeColor color) {
        return BLOCKS.register(name, () ->
                new InstaBaseBlock(
                        BlockBehaviour.Properties.of()
                                .mapColor(color.getMapColor())
                                .strength(2.0F)
                                .sound(SoundType.WOOD)
                                .noOcclusion(),
                        color
                )
        );
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}