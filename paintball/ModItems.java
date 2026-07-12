package com.zerokg2004.paintball;

import com.zerokg2004.paintball.entity.projectile.*;
import com.zerokg2004.paintball.item.C4Item;
import com.zerokg2004.paintball.item.PaintballArmorItem;
import com.zerokg2004.paintball.item.PaintbrushItem;
import com.zerokg2004.paintball.item.RemoteItem;
import com.zerokg2004.paintball.item.gun.GunItem;
import com.zerokg2004.paintball.registry.ModBlocks;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = PaintballMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PaintballMod.MODID);

    public static final RegistryObject<Item> DECISION_ROULETTE_BLOCK =
            ITEMS.register("decision_roulette",
                    () -> new BlockItem(ModBlocks.DECISION_ROULETTE_BLOCK.get(),
                            new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RED_PELLETS    = ITEMS.register("red_pellets",    () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> BLUE_PELLETS   = ITEMS.register("blue_pellets",   () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> GREEN_PELLETS  = ITEMS.register("green_pellets",  () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_PELLETS = ITEMS.register("yellow_pellets", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> ORANGE_PELLETS = ITEMS.register("orange_pellets", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> PURPLE_PELLETS = ITEMS.register("purple_pellets", () -> new Item(new Item.Properties()));

    private static final int MAG_PISTOL  = 18;
    private static final int MAG_RIFLE   = 25;
    private static final int MAG_SHOTGUN = 12;
    private static final int MAG_SNIPER  = 5;

    private static final float ADS_PISTOL  = -5.8f;
    private static final float ADS_RIFLE   = -6.0f;
    private static final float ADS_SHOTGUN = -5.8f;
    private static final float ADS_SNIPER  = 0.0f;

    public static final RegistryObject<Item> RED_PISTOL = ITEMS.register("red_pistol",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    RED_PELLETS,
                    (level, player) -> new RedPelletEntity(level, player),
                    3.0f, 1.0f, 10,
                    4.0f, 1, 30,
                    MAG_PISTOL, false, ADS_PISTOL
            ));

    public static final RegistryObject<Item> BLUE_PISTOL = ITEMS.register("blue_pistol",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    BLUE_PELLETS,
                    (level, player) -> new BluePelletEntity(level, player),
                    3.0f, 1.0f, 10,
                    4.0f, 1, 30,
                    MAG_PISTOL, false, ADS_PISTOL
            ));

    public static final RegistryObject<Item> GREEN_PISTOL = ITEMS.register("green_pistol",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    GREEN_PELLETS,
                    (level, player) -> new GreenPelletEntity(level, player),
                    3.0f, 1.0f, 10,
                    4.0f, 1, 30,
                    MAG_PISTOL, false, ADS_PISTOL
            ));

    public static final RegistryObject<Item> YELLOW_PISTOL = ITEMS.register("yellow_pistol",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    YELLOW_PELLETS,
                    (level, player) -> new YellowPelletEntity(level, player),
                    3.0f, 1.0f, 10,
                    4.0f, 1, 30,
                    MAG_PISTOL, false, ADS_PISTOL
            ));

    public static final RegistryObject<Item> ORANGE_PISTOL = ITEMS.register("orange_pistol",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    ORANGE_PELLETS,
                    (level, player) -> new OrangePelletEntity(level, player),
                    3.0f, 1.0f, 10,
                    4.0f, 1, 30,
                    MAG_PISTOL, false, ADS_PISTOL
            ));

    public static final RegistryObject<Item> PURPLE_PISTOL = ITEMS.register("purple_pistol",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    PURPLE_PELLETS,
                    (level, player) -> new PurplePelletEntity(level, player),
                    3.0f, 1.0f, 10,
                    4.0f, 1, 30,
                    MAG_PISTOL, false, ADS_PISTOL
            ));

    public static final RegistryObject<Item> RED_SHOTGUN = ITEMS.register("red_shotgun",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    RED_PELLETS,
                    (lvl, pl) -> new RedPelletEntity(lvl, pl),
                    3.0f, 1.2f, 15,
                    1.0f, 6, 15,
                    MAG_SHOTGUN, false, ADS_SHOTGUN
            ));

    public static final RegistryObject<Item> BLUE_SHOTGUN = ITEMS.register("blue_shotgun",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    BLUE_PELLETS,
                    (lvl, pl) -> new BluePelletEntity(lvl, pl),
                    3.0f, 1.2f, 15,
                    1.0f, 6, 15,
                    MAG_SHOTGUN, false, ADS_SHOTGUN
            ));

    public static final RegistryObject<Item> GREEN_SHOTGUN = ITEMS.register("green_shotgun",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    GREEN_PELLETS,
                    (lvl, pl) -> new GreenPelletEntity(lvl, pl),
                    3.0f, 1.2f, 15,
                    1.0f, 6, 15,
                    MAG_SHOTGUN, false, ADS_SHOTGUN
            ));

    public static final RegistryObject<Item> YELLOW_SHOTGUN = ITEMS.register("yellow_shotgun",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    YELLOW_PELLETS,
                    (lvl, pl) -> new YellowPelletEntity(lvl, pl),
                    3.0f, 1.2f, 15,
                    1.0f, 6, 15,
                    MAG_SHOTGUN, false, ADS_SHOTGUN
            ));

    public static final RegistryObject<Item> ORANGE_SHOTGUN = ITEMS.register("orange_shotgun",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    ORANGE_PELLETS,
                    (lvl, pl) -> new OrangePelletEntity(lvl, pl),
                    3.0f, 1.2f, 15,
                    1.0f, 6, 15,
                    MAG_SHOTGUN, false, ADS_SHOTGUN
            ));

    public static final RegistryObject<Item> PURPLE_SHOTGUN = ITEMS.register("purple_shotgun",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    PURPLE_PELLETS,
                    (lvl, pl) -> new PurplePelletEntity(lvl, pl),
                    3.0f, 1.2f, 15,
                    1.0f, 6, 15,
                    MAG_SHOTGUN, false, ADS_SHOTGUN
            ));

    public static final RegistryObject<Item> RED_RIFLE = ITEMS.register("red_rifle",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    RED_PELLETS,
                    (lvl, pl) -> new RedPelletEntity(lvl, pl),
                    5.0f, 0.9f, 4,
                    2.0f, 1, 40,
                    MAG_RIFLE, true, ADS_RIFLE
            ));

    public static final RegistryObject<Item> BLUE_RIFLE = ITEMS.register("blue_rifle",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    BLUE_PELLETS,
                    (lvl, pl) -> new BluePelletEntity(lvl, pl),
                    5.0f, 0.9f, 4,
                    2.0f, 1, 40,
                    MAG_RIFLE, true, ADS_RIFLE
            ));

    public static final RegistryObject<Item> GREEN_RIFLE = ITEMS.register("green_rifle",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    GREEN_PELLETS,
                    (lvl, pl) -> new GreenPelletEntity(lvl, pl),
                    5.0f, 0.9f, 4,
                    2.0f, 1, 40,
                    MAG_RIFLE, true, ADS_RIFLE
            ));

    public static final RegistryObject<Item> YELLOW_RIFLE = ITEMS.register("yellow_rifle",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    YELLOW_PELLETS,
                    (lvl, pl) -> new YellowPelletEntity(lvl, pl),
                    5.0f, 0.9f, 4,
                    2.0f, 1, 40,
                    MAG_RIFLE, true, ADS_RIFLE
            ));

    public static final RegistryObject<Item> ORANGE_RIFLE = ITEMS.register("orange_rifle",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    ORANGE_PELLETS,
                    (lvl, pl) -> new OrangePelletEntity(lvl, pl),
                    5.0f, 0.9f, 4,
                    2.0f, 1, 40,
                    MAG_RIFLE, true, ADS_RIFLE
            ));

    public static final RegistryObject<Item> PURPLE_RIFLE = ITEMS.register("purple_rifle",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    PURPLE_PELLETS,
                    (lvl, pl) -> new PurplePelletEntity(lvl, pl),
                    5.0f, 0.9f, 4,
                    2.0f, 1, 40,
                    MAG_RIFLE, true, ADS_RIFLE
            ));

    public static final RegistryObject<Item> RED_SNIPER = ITEMS.register("red_sniper",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    RED_PELLETS,
                    (lvl, pl) -> new RedPelletEntity(lvl, pl),
                    6.0f, 0.3f, 40,
                    8.0f, 1, 60,
                    MAG_SNIPER, false, ADS_SNIPER
            ));

    public static final RegistryObject<Item> BLUE_SNIPER = ITEMS.register("blue_sniper",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    BLUE_PELLETS,
                    (lvl, pl) -> new BluePelletEntity(lvl, pl),
                    6.0f, 0.3f, 40,
                    8.0f, 1, 60,
                    MAG_SNIPER, false, ADS_SNIPER
            ));

    public static final RegistryObject<Item> GREEN_SNIPER = ITEMS.register("green_sniper",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    GREEN_PELLETS,
                    (lvl, pl) -> new GreenPelletEntity(lvl, pl),
                    6.0f, 0.3f, 40,
                    8.0f, 1, 60,
                    MAG_SNIPER, false, ADS_SNIPER
            ));

    public static final RegistryObject<Item> YELLOW_SNIPER = ITEMS.register("yellow_sniper",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    YELLOW_PELLETS,
                    (lvl, pl) -> new YellowPelletEntity(lvl, pl),
                    6.0f, 0.3f, 40,
                    8.0f, 1, 60,
                    MAG_SNIPER, false, ADS_SNIPER
            ));

    public static final RegistryObject<Item> ORANGE_SNIPER = ITEMS.register("orange_sniper",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    ORANGE_PELLETS,
                    (lvl, pl) -> new OrangePelletEntity(lvl, pl),
                    6.0f, 0.3f, 40,
                    8.0f, 1, 60,
                    MAG_SNIPER, false, ADS_SNIPER
            ));

    public static final RegistryObject<Item> PURPLE_SNIPER = ITEMS.register("purple_sniper",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    PURPLE_PELLETS,
                    (lvl, pl) -> new PurplePelletEntity(lvl, pl),
                    6.0f, 0.3f, 40,
                    10.0f, 1, 60,
                    MAG_SNIPER, false, ADS_SNIPER
            ));

    // ===== Granadas de mano (stack 2) =====
    public static final RegistryObject<Item> RED_GRENADE = ITEMS.register("red_grenade",
            () -> new GunItem(new Item.Properties().stacksTo(2),
                    (level, player, dmg) -> {
                        RedGrenadeEntity g = new RedGrenadeEntity(level, player);
                        g.setDamage(dmg);
                        return g;
                    },
                    20.0F
            ));

    public static final RegistryObject<Item> BLUE_GRENADE = ITEMS.register("blue_grenade",
            () -> new GunItem(new Item.Properties().stacksTo(2),
                    (level, player, dmg) -> {
                        BlueGrenadeEntity g = new BlueGrenadeEntity(level, player);
                        g.setDamage(dmg);
                        return g;
                    },
                    20.0F
            ));

    public static final RegistryObject<Item> GREEN_GRENADE = ITEMS.register("green_grenade",
            () -> new GunItem(new Item.Properties().stacksTo(2),
                    (level, player, dmg) -> {
                        GreenGrenadeEntity g = new GreenGrenadeEntity(level, player);
                        g.setDamage(dmg);
                        return g;
                    },
                    20.0F
            ));

    public static final RegistryObject<Item> YELLOW_GRENADE = ITEMS.register("yellow_grenade",
            () -> new GunItem(new Item.Properties().stacksTo(2),
                    (level, player, dmg) -> {
                        YellowGrenadeEntity g = new YellowGrenadeEntity(level, player);
                        g.setDamage(dmg);
                        return g;
                    },
                    20.0F
            ));

    public static final RegistryObject<Item> ORANGE_GRENADE = ITEMS.register("orange_grenade",
            () -> new GunItem(new Item.Properties().stacksTo(2),
                    (level, player, dmg) -> {
                        OrangeGrenadeEntity g = new OrangeGrenadeEntity(level, player);
                        g.setDamage(dmg);
                        return g;
                    },
                    20.0F
            ));

    public static final RegistryObject<Item> PURPLE_GRENADE = ITEMS.register("purple_grenade",
            () -> new GunItem(new Item.Properties().stacksTo(2),
                    (level, player, dmg) -> {
                        PurpleGrenadeEntity g = new PurpleGrenadeEntity(level, player);
                        g.setDamage(dmg);
                        return g;
                    },
                    20.0F
            ));


    // ===== Launchers =====
    public static final RegistryObject<Item> RED_LAUNCHER = ITEMS.register("red_launcher",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    RED_GRENADE,
                    (level, player, dmg) -> {
                        RedGrenadeEntity g = new RedGrenadeEntity(level, player);
                        g.setDamage(dmg);
                        return g;
                    },
                    4.5f, 0.01f, 6.7f, 0.0f, 20,
                    15.0F // grenadeDamage
            ));

    public static final RegistryObject<Item> BLUE_LAUNCHER = ITEMS.register("blue_launcher",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    BLUE_GRENADE,
                    (level, player, dmg) -> {
                        BlueGrenadeEntity g = new BlueGrenadeEntity(level, player);
                        g.setDamage(dmg);
                        return g;
                    },
                    4.5f, 0.01f, 6.7f, 0.0f, 20,
                    15.0F
            ));

    public static final RegistryObject<Item> GREEN_LAUNCHER = ITEMS.register("green_launcher",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    GREEN_GRENADE,
                    (level, player, dmg) -> {
                        GreenGrenadeEntity g = new GreenGrenadeEntity(level, player);
                        g.setDamage(dmg);
                        return g;
                    },
                    4.5f, 0.01f, 6.7f, 0.0f, 20,
                    15.0F
            ));

    public static final RegistryObject<Item> YELLOW_LAUNCHER = ITEMS.register("yellow_launcher",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    YELLOW_GRENADE,
                    (level, player, dmg) -> {
                        YellowGrenadeEntity g = new YellowGrenadeEntity(level, player);
                        g.setDamage(dmg);
                        return g;
                    },
                    4.5f, 0.01f, 6.7f, 0.0f, 20,
                    15.0F
            ));

    public static final RegistryObject<Item> ORANGE_LAUNCHER = ITEMS.register("orange_launcher",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    ORANGE_GRENADE,
                    (level, player, dmg) -> {
                        OrangeGrenadeEntity g = new OrangeGrenadeEntity(level, player);
                        g.setDamage(dmg);
                        return g;
                    },
                    4.5f, 0.01f, 6.7f, 0.0f, 20,
                    15.0F
            ));

    public static final RegistryObject<Item> PURPLE_LAUNCHER = ITEMS.register("purple_launcher",
            () -> new GunItem(
                    new Item.Properties().stacksTo(1),
                    PURPLE_GRENADE,
                    (level, player, dmg) -> {
                        PurpleGrenadeEntity g = new PurpleGrenadeEntity(level, player);
                        g.setDamage(dmg);
                        return g;
                    },
                    4.5f, 0.01f, 6.7f, 0.0f, 20,
                    15.0F
            ));

    public static final RegistryObject<Item> RED_REMOTE = ITEMS.register("red_remote",
            () -> new RemoteItem(new Item.Properties().stacksTo(1), DyeColor.RED));
    public static final RegistryObject<Item> BLUE_REMOTE = ITEMS.register("blue_remote",
            () -> new RemoteItem(new Item.Properties().stacksTo(1), DyeColor.BLUE));
    public static final RegistryObject<Item> GREEN_REMOTE = ITEMS.register("green_remote",
            () -> new RemoteItem(new Item.Properties().stacksTo(1), DyeColor.GREEN));
    public static final RegistryObject<Item> YELLOW_REMOTE = ITEMS.register("yellow_remote",
            () -> new RemoteItem(new Item.Properties().stacksTo(1), DyeColor.YELLOW));
    public static final RegistryObject<Item> ORANGE_REMOTE = ITEMS.register("orange_remote",
            () -> new RemoteItem(new Item.Properties().stacksTo(1), DyeColor.ORANGE));
    public static final RegistryObject<Item> PURPLE_REMOTE = ITEMS.register("purple_remote",
            () -> new RemoteItem(new Item.Properties().stacksTo(1), DyeColor.PURPLE));

    public static final RegistryObject<Item> RED_C4 = ITEMS.register("red_c4",
            () -> new C4Item(DyeColor.RED, new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> BLUE_C4 = ITEMS.register("blue_c4",
            () -> new C4Item(DyeColor.BLUE, new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> GREEN_C4 = ITEMS.register("green_c4",
            () -> new C4Item(DyeColor.GREEN, new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> YELLOW_C4 = ITEMS.register("yellow_c4",
            () -> new C4Item(DyeColor.YELLOW, new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> ORANGE_C4 = ITEMS.register("orange_c4",
            () -> new C4Item(DyeColor.ORANGE, new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> PURPLE_C4 = ITEMS.register("purple_c4",
            () -> new C4Item(DyeColor.PURPLE, new Item.Properties().stacksTo(2)));

    public static final RegistryObject<Item> BLUE_PAINTBALL_HELMET = ITEMS.register("blue_paintball_helmet",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.HELMET, new Item.Properties(), "blue_armor", DyeColor.BLUE));
    public static final RegistryObject<Item> BLUE_PAINTBALL_CHEST = ITEMS.register("blue_paintball_chest",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "blue_armor", DyeColor.BLUE));
    public static final RegistryObject<Item> BLUE_PAINTBALL_PANTS = ITEMS.register("blue_paintball_pants",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.LEGGINGS, new Item.Properties(), "blue_armor", DyeColor.BLUE));
    public static final RegistryObject<Item> BLUE_PAINTBALL_SHOES = ITEMS.register("blue_paintball_shoes",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.BOOTS, new Item.Properties(), "blue_armor", DyeColor.BLUE));

    public static final RegistryObject<Item> GREEN_PAINTBALL_HELMET = ITEMS.register("green_paintball_helmet",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.HELMET, new Item.Properties(), "green_armor", DyeColor.GREEN));
    public static final RegistryObject<Item> GREEN_PAINTBALL_CHEST = ITEMS.register("green_paintball_chest",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "green_armor", DyeColor.GREEN));
    public static final RegistryObject<Item> GREEN_PAINTBALL_PANTS = ITEMS.register("green_paintball_pants",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.LEGGINGS, new Item.Properties(), "green_armor", DyeColor.GREEN));
    public static final RegistryObject<Item> GREEN_PAINTBALL_SHOES = ITEMS.register("green_paintball_shoes",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.BOOTS, new Item.Properties(), "green_armor", DyeColor.GREEN));

    public static final RegistryObject<Item> RED_PAINTBALL_HELMET = ITEMS.register("red_paintball_helmet",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.HELMET, new Item.Properties(), "red_armor", DyeColor.RED));
    public static final RegistryObject<Item> RED_PAINTBALL_CHEST = ITEMS.register("red_paintball_chest",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "red_armor", DyeColor.RED));
    public static final RegistryObject<Item> RED_PAINTBALL_PANTS = ITEMS.register("red_paintball_pants",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.LEGGINGS, new Item.Properties(), "red_armor", DyeColor.RED));
    public static final RegistryObject<Item> RED_PAINTBALL_SHOES = ITEMS.register("red_paintball_shoes",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.BOOTS, new Item.Properties(), "red_armor", DyeColor.RED));

    public static final RegistryObject<Item> ORANGE_PAINTBALL_HELMET = ITEMS.register("orange_paintball_helmet",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.HELMET, new Item.Properties(), "orange_armor", DyeColor.ORANGE));
    public static final RegistryObject<Item> ORANGE_PAINTBALL_CHEST = ITEMS.register("orange_paintball_chest",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "orange_armor", DyeColor.ORANGE));
    public static final RegistryObject<Item> ORANGE_PAINTBALL_PANTS = ITEMS.register("orange_paintball_pants",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.LEGGINGS, new Item.Properties(), "orange_armor", DyeColor.ORANGE));
    public static final RegistryObject<Item> ORANGE_PAINTBALL_SHOES = ITEMS.register("orange_paintball_shoes",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.BOOTS, new Item.Properties(), "orange_armor", DyeColor.ORANGE));

    public static final RegistryObject<Item> PURPLE_PAINTBALL_HELMET = ITEMS.register("purple_paintball_helmet",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.HELMET, new Item.Properties(), "purple_armor", DyeColor.PURPLE));
    public static final RegistryObject<Item> PURPLE_PAINTBALL_CHEST = ITEMS.register("purple_paintball_chest",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "purple_armor", DyeColor.PURPLE));
    public static final RegistryObject<Item> PURPLE_PAINTBALL_PANTS = ITEMS.register("purple_paintball_pants",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.LEGGINGS, new Item.Properties(), "purple_armor", DyeColor.PURPLE));
    public static final RegistryObject<Item> PURPLE_PAINTBALL_SHOES = ITEMS.register("purple_paintball_shoes",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.BOOTS, new Item.Properties(), "purple_armor", DyeColor.PURPLE));

    public static final RegistryObject<Item> YELLOW_PAINTBALL_HELMET = ITEMS.register("yellow_paintball_helmet",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.HELMET, new Item.Properties(), "yellow_armor", DyeColor.YELLOW));
    public static final RegistryObject<Item> YELLOW_PAINTBALL_CHEST = ITEMS.register("yellow_paintball_chest",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "yellow_armor", DyeColor.YELLOW));
    public static final RegistryObject<Item> YELLOW_PAINTBALL_PANTS = ITEMS.register("yellow_paintball_pants",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.LEGGINGS, new Item.Properties(), "yellow_armor", DyeColor.YELLOW));
    public static final RegistryObject<Item> YELLOW_PAINTBALL_SHOES = ITEMS.register("yellow_paintball_shoes",
            () -> new PaintballArmorItem(ModArmorMaterials.PAINTBALL, ArmorItem.Type.BOOTS, new Item.Properties(), "yellow_armor", DyeColor.YELLOW));

    public static final RegistryObject<Item> RED_SCANNER    = registerBlockItem("red_scanner",    ModBlocks.RED_SCANNER);
    public static final RegistryObject<Item> ORANGE_SCANNER = registerBlockItem("orange_scanner", ModBlocks.ORANGE_SCANNER);
    public static final RegistryObject<Item> YELLOW_SCANNER = registerBlockItem("yellow_scanner", ModBlocks.YELLOW_SCANNER);
    public static final RegistryObject<Item> GREEN_SCANNER  = registerBlockItem("green_scanner",  ModBlocks.GREEN_SCANNER);
    public static final RegistryObject<Item> BLUE_SCANNER   = registerBlockItem("blue_scanner",   ModBlocks.BLUE_SCANNER);
    public static final RegistryObject<Item> PURPLE_SCANNER = registerBlockItem("purple_scanner", ModBlocks.PURPLE_SCANNER);

    public static final RegistryObject<Item> RED_PAINTBRUSH = ITEMS.register("red_paintbrush",
            () -> new PaintbrushItem(DyeColor.RED, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> ORANGE_PAINTBRUSH = ITEMS.register("orange_paintbrush",
            () -> new PaintbrushItem(DyeColor.ORANGE, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> YELLOW_PAINTBRUSH = ITEMS.register("yellow_paintbrush",
            () -> new PaintbrushItem(DyeColor.YELLOW, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> GREEN_PAINTBRUSH = ITEMS.register("green_paintbrush",
            () -> new PaintbrushItem(DyeColor.GREEN, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> BLUE_PAINTBRUSH = ITEMS.register("blue_paintbrush",
            () -> new PaintbrushItem(DyeColor.BLUE, new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> PURPLE_PAINTBRUSH = ITEMS.register("purple_paintbrush",
            () -> new PaintbrushItem(DyeColor.PURPLE, new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> RED_CLAYMORE = ITEMS.register("red_claymore",
            () -> new BlockItem(ModBlocks.RED_CLAYMORE_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> BLUE_CLAYMORE = ITEMS.register("blue_claymore",
            () -> new BlockItem(ModBlocks.BLUE_CLAYMORE_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> GREEN_CLAYMORE = ITEMS.register("green_claymore",
            () -> new BlockItem(ModBlocks.GREEN_CLAYMORE_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> YELLOW_CLAYMORE = ITEMS.register("yellow_claymore",
            () -> new BlockItem(ModBlocks.YELLOW_CLAYMORE_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> ORANGE_CLAYMORE = ITEMS.register("orange_claymore",
            () -> new BlockItem(ModBlocks.ORANGE_CLAYMORE_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> PURPLE_CLAYMORE = ITEMS.register("purple_claymore",
            () -> new BlockItem(ModBlocks.PURPLE_CLAYMORE_BLOCK.get(), new Item.Properties().stacksTo(2)));

    public static final RegistryObject<Item> RED_FLAG    = ITEMS.register("red_flag",    () -> new BlockItem(ModBlocks.RED_FLAG_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> BLUE_FLAG   = ITEMS.register("blue_flag",   () -> new BlockItem(ModBlocks.BLUE_FLAG_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> GREEN_FLAG  = ITEMS.register("green_flag",  () -> new BlockItem(ModBlocks.GREEN_FLAG_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> YELLOW_FLAG = ITEMS.register("yellow_flag", () -> new BlockItem(ModBlocks.YELLOW_FLAG_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> ORANGE_FLAG = ITEMS.register("orange_flag", () -> new BlockItem(ModBlocks.ORANGE_FLAG_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> PURPLE_FLAG = ITEMS.register("purple_flag", () -> new BlockItem(ModBlocks.PURPLE_FLAG_BLOCK.get(), new Item.Properties().stacksTo(2)));

    public static final RegistryObject<Item> RED_MEDKIT = ITEMS.register("red_medkit",
            () -> new BlockItem(ModBlocks.RED_MEDKIT_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> BLUE_MEDKIT = ITEMS.register("blue_medkit",
            () -> new BlockItem(ModBlocks.BLUE_MEDKIT_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> GREEN_MEDKIT = ITEMS.register("green_medkit",
            () -> new BlockItem(ModBlocks.GREEN_MEDKIT_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> YELLOW_MEDKIT = ITEMS.register("yellow_medkit",
            () -> new BlockItem(ModBlocks.YELLOW_MEDKIT_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> ORANGE_MEDKIT = ITEMS.register("orange_medkit",
            () -> new BlockItem(ModBlocks.ORANGE_MEDKIT_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> PURPLE_MEDKIT = ITEMS.register("purple_medkit",
            () -> new BlockItem(ModBlocks.PURPLE_MEDKIT_BLOCK.get(), new Item.Properties().stacksTo(2)));

    public static final RegistryObject<Item> RED_POD = ITEMS.register("red_pod",
            () -> new BlockItem(ModBlocks.RED_POD_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> BLUE_POD = ITEMS.register("blue_pod",
            () -> new BlockItem(ModBlocks.BLUE_POD_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> GREEN_POD = ITEMS.register("green_pod",
            () -> new BlockItem(ModBlocks.GREEN_POD_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> YELLOW_POD = ITEMS.register("yellow_pod",
            () -> new BlockItem(ModBlocks.YELLOW_POD_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> ORANGE_POD = ITEMS.register("orange_pod",
            () -> new BlockItem(ModBlocks.ORANGE_POD_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> PURPLE_POD = ITEMS.register("purple_pod",
            () -> new BlockItem(ModBlocks.PURPLE_POD_BLOCK.get(), new Item.Properties().stacksTo(2)));

    public static final RegistryObject<Item> RED_GEAR_RACK = ITEMS.register("red_gear_rack",
            () -> new BlockItem(ModBlocks.RED_GEAR_RACK_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> BLUE_GEAR_RACK = ITEMS.register("blue_gear_rack",
            () -> new BlockItem(ModBlocks.BLUE_GEAR_RACK_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> GREEN_GEAR_RACK = ITEMS.register("green_gear_rack",
            () -> new BlockItem(ModBlocks.GREEN_GEAR_RACK_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> YELLOW_GEAR_RACK = ITEMS.register("yellow_gear_rack",
            () -> new BlockItem(ModBlocks.YELLOW_GEAR_RACK_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> ORANGE_GEAR_RACK = ITEMS.register("orange_gear_rack",
            () -> new BlockItem(ModBlocks.ORANGE_GEAR_RACK_BLOCK.get(), new Item.Properties().stacksTo(2)));
    public static final RegistryObject<Item> PURPLE_GEAR_RACK = ITEMS.register("purple_gear_rack",
            () -> new BlockItem(ModBlocks.PURPLE_GEAR_RACK_BLOCK.get(), new Item.Properties().stacksTo(2)));

    public static final RegistryObject<Item> RED_WEAPON_RACK = ITEMS.register("red_weapon_rack",
            () -> new BlockItem(ModBlocks.RED_WEAPON_RACK_BLOCK.get(), new Item.Properties().stacksTo(2)));

    public static final RegistryObject<Item> BLUE_WEAPON_RACK = ITEMS.register("blue_weapon_rack",
            () -> new BlockItem(ModBlocks.BLUE_WEAPON_RACK_BLOCK.get(), new Item.Properties().stacksTo(2)));

    public static final RegistryObject<Item> GREEN_WEAPON_RACK = ITEMS.register("green_weapon_rack",
            () -> new BlockItem(ModBlocks.GREEN_WEAPON_RACK_BLOCK.get(), new Item.Properties().stacksTo(2)));

    public static final RegistryObject<Item> YELLOW_WEAPON_RACK = ITEMS.register("yellow_weapon_rack",
            () -> new BlockItem(ModBlocks.YELLOW_WEAPON_RACK_BLOCK.get(), new Item.Properties().stacksTo(2)));

    public static final RegistryObject<Item> ORANGE_WEAPON_RACK = ITEMS.register("orange_weapon_rack",
            () -> new BlockItem(ModBlocks.ORANGE_WEAPON_RACK_BLOCK.get(), new Item.Properties().stacksTo(2)));

    public static final RegistryObject<Item> PURPLE_WEAPON_RACK = ITEMS.register("purple_weapon_rack",
            () -> new BlockItem(ModBlocks.PURPLE_WEAPON_RACK_BLOCK.get(), new Item.Properties().stacksTo(2)));

    public static final RegistryObject<Item> GREEN_INSTA_BASE = registerBlockItem("green_insta_base", ModBlocks.GREEN_INSTA_BASE_BLOCK);
    public static final RegistryObject<Item> BLUE_INSTA_BASE = registerBlockItem("blue_insta_base", ModBlocks.BLUE_INSTA_BASE_BLOCK);
    public static final RegistryObject<Item> RED_INSTA_BASE = registerBlockItem("red_insta_base", ModBlocks.RED_INSTA_BASE_BLOCK);
    public static final RegistryObject<Item> YELLOW_INSTA_BASE = registerBlockItem("yellow_insta_base", ModBlocks.YELLOW_INSTA_BASE_BLOCK);
    public static final RegistryObject<Item> ORANGE_INSTA_BASE = registerBlockItem("orange_insta_base", ModBlocks.ORANGE_INSTA_BASE_BLOCK);
    public static final RegistryObject<Item> PURPLE_INSTA_BASE = registerBlockItem("purple_insta_base", ModBlocks.PURPLE_INSTA_BASE_BLOCK);

    private static RegistryObject<Item> registerBlockItem(String name, RegistryObject<Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}