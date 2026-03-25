package com.zerokg2004.paintball;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = PaintballMod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PaintballMod.MODID);

    public static final RegistryObject<CreativeModeTab> PAINTBALL_TAB = CREATIVE_MODE_TABS.register(
            "paintball_revival",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("Paintball Revival"))
                    .icon(() -> new ItemStack(ModItems.RED_RIFLE.get()))
                    .displayItems((params, output) -> {

                        output.accept(ModItems.DECISION_ROULETTE_BLOCK.get());

                        // ===== RED =====
                        output.accept(ModItems.RED_SCANNER.get());
                        output.accept(ModItems.RED_INSTA_BASE.get());
                        output.accept(ModItems.RED_PAINTBALL_HELMET.get());
                        output.accept(ModItems.RED_PAINTBALL_CHEST.get());
                        output.accept(ModItems.RED_PAINTBALL_PANTS.get());
                        output.accept(ModItems.RED_PAINTBALL_SHOES.get());
                        output.accept(ModItems.RED_REMOTE.get());
                        output.accept(ModItems.RED_PAINTBRUSH.get());
                        output.accept(ModItems.RED_PELLETS.get());
                        output.accept(ModItems.RED_PISTOL.get());
                        output.accept(ModItems.RED_SHOTGUN.get());
                        output.accept(ModItems.RED_RIFLE.get());
                        output.accept(ModItems.RED_SNIPER.get());
                        output.accept(ModItems.RED_LAUNCHER.get());
                        output.accept(ModItems.RED_GRENADE.get());
                        addGravityGunIfLoaded(output, "red");
                        output.accept(ModItems.RED_CLAYMORE.get());
                        output.accept(ModItems.RED_C4.get());
                        output.accept(ModItems.RED_FLAG.get());
                        output.accept(ModItems.RED_POD.get());
                        output.accept(ModItems.RED_WEAPON_RACK.get());
                        output.accept(ModItems.RED_GEAR_RACK.get());
                        output.accept(ModItems.RED_MEDKIT.get());

                        // ===== ORANGE =====
                        output.accept(ModItems.ORANGE_SCANNER.get());
                        output.accept(ModItems.ORANGE_INSTA_BASE.get());
                        output.accept(ModItems.ORANGE_PAINTBALL_HELMET.get());
                        output.accept(ModItems.ORANGE_PAINTBALL_CHEST.get());
                        output.accept(ModItems.ORANGE_PAINTBALL_PANTS.get());
                        output.accept(ModItems.ORANGE_PAINTBALL_SHOES.get());
                        output.accept(ModItems.ORANGE_REMOTE.get());
                        output.accept(ModItems.ORANGE_PAINTBRUSH.get());
                        output.accept(ModItems.ORANGE_PELLETS.get());
                        output.accept(ModItems.ORANGE_PISTOL.get());
                        output.accept(ModItems.ORANGE_SHOTGUN.get());
                        output.accept(ModItems.ORANGE_RIFLE.get());
                        output.accept(ModItems.ORANGE_SNIPER.get());
                        output.accept(ModItems.ORANGE_LAUNCHER.get());
                        output.accept(ModItems.ORANGE_GRENADE.get());
                        addGravityGunIfLoaded(output, "orange");
                        output.accept(ModItems.ORANGE_CLAYMORE.get());
                        output.accept(ModItems.ORANGE_C4.get());
                        output.accept(ModItems.ORANGE_FLAG.get());
                        output.accept(ModItems.ORANGE_POD.get());
                        output.accept(ModItems.ORANGE_WEAPON_RACK.get());
                        output.accept(ModItems.ORANGE_GEAR_RACK.get());
                        output.accept(ModItems.ORANGE_MEDKIT.get());

                        // ===== YELLOW =====
                        output.accept(ModItems.YELLOW_SCANNER.get());
                        output.accept(ModItems.YELLOW_INSTA_BASE.get());
                        output.accept(ModItems.YELLOW_PAINTBALL_HELMET.get());
                        output.accept(ModItems.YELLOW_PAINTBALL_CHEST.get());
                        output.accept(ModItems.YELLOW_PAINTBALL_PANTS.get());
                        output.accept(ModItems.YELLOW_PAINTBALL_SHOES.get());
                        output.accept(ModItems.YELLOW_REMOTE.get());
                        output.accept(ModItems.YELLOW_PAINTBRUSH.get());
                        output.accept(ModItems.YELLOW_PELLETS.get());
                        output.accept(ModItems.YELLOW_PISTOL.get());
                        output.accept(ModItems.YELLOW_SHOTGUN.get());
                        output.accept(ModItems.YELLOW_RIFLE.get());
                        output.accept(ModItems.YELLOW_SNIPER.get());
                        output.accept(ModItems.YELLOW_LAUNCHER.get());
                        output.accept(ModItems.YELLOW_GRENADE.get());
                        addGravityGunIfLoaded(output, "yellow");
                        output.accept(ModItems.YELLOW_CLAYMORE.get());
                        output.accept(ModItems.YELLOW_C4.get());
                        output.accept(ModItems.YELLOW_FLAG.get());
                        output.accept(ModItems.YELLOW_POD.get());
                        output.accept(ModItems.YELLOW_WEAPON_RACK.get());
                        output.accept(ModItems.YELLOW_GEAR_RACK.get());
                        output.accept(ModItems.YELLOW_MEDKIT.get());

                        // ===== GREEN =====
                        output.accept(ModItems.GREEN_SCANNER.get());
                        output.accept(ModItems.GREEN_INSTA_BASE.get());
                        output.accept(ModItems.GREEN_PAINTBALL_HELMET.get());
                        output.accept(ModItems.GREEN_PAINTBALL_CHEST.get());
                        output.accept(ModItems.GREEN_PAINTBALL_PANTS.get());
                        output.accept(ModItems.GREEN_PAINTBALL_SHOES.get());
                        output.accept(ModItems.GREEN_REMOTE.get());
                        output.accept(ModItems.GREEN_PAINTBRUSH.get());
                        output.accept(ModItems.GREEN_PELLETS.get());
                        output.accept(ModItems.GREEN_PISTOL.get());
                        output.accept(ModItems.GREEN_SHOTGUN.get());
                        output.accept(ModItems.GREEN_RIFLE.get());
                        output.accept(ModItems.GREEN_SNIPER.get());
                        output.accept(ModItems.GREEN_LAUNCHER.get());
                        output.accept(ModItems.GREEN_GRENADE.get());
                        addGravityGunIfLoaded(output, "green");
                        output.accept(ModItems.GREEN_CLAYMORE.get());
                        output.accept(ModItems.GREEN_C4.get());
                        output.accept(ModItems.GREEN_FLAG.get());
                        output.accept(ModItems.GREEN_POD.get());
                        output.accept(ModItems.GREEN_WEAPON_RACK.get());
                        output.accept(ModItems.GREEN_GEAR_RACK.get());
                        output.accept(ModItems.GREEN_MEDKIT.get());

                        // ===== BLUE =====
                        output.accept(ModItems.BLUE_SCANNER.get());
                        output.accept(ModItems.BLUE_INSTA_BASE.get());
                        output.accept(ModItems.BLUE_PAINTBALL_HELMET.get());
                        output.accept(ModItems.BLUE_PAINTBALL_CHEST.get());
                        output.accept(ModItems.BLUE_PAINTBALL_PANTS.get());
                        output.accept(ModItems.BLUE_PAINTBALL_SHOES.get());
                        output.accept(ModItems.BLUE_REMOTE.get());
                        output.accept(ModItems.BLUE_PAINTBRUSH.get());
                        output.accept(ModItems.BLUE_PELLETS.get());
                        output.accept(ModItems.BLUE_PISTOL.get());
                        output.accept(ModItems.BLUE_SHOTGUN.get());
                        output.accept(ModItems.BLUE_RIFLE.get());
                        output.accept(ModItems.BLUE_SNIPER.get());
                        output.accept(ModItems.BLUE_LAUNCHER.get());
                        output.accept(ModItems.BLUE_GRENADE.get());
                        addGravityGunIfLoaded(output, "blue");
                        output.accept(ModItems.BLUE_CLAYMORE.get());
                        output.accept(ModItems.BLUE_C4.get());
                        output.accept(ModItems.BLUE_FLAG.get());
                        output.accept(ModItems.BLUE_POD.get());
                        output.accept(ModItems.BLUE_WEAPON_RACK.get());
                        output.accept(ModItems.BLUE_GEAR_RACK.get());
                        output.accept(ModItems.BLUE_MEDKIT.get());

                        // ===== PURPLE =====
                        output.accept(ModItems.PURPLE_SCANNER.get());
                        output.accept(ModItems.PURPLE_INSTA_BASE.get());
                        output.accept(ModItems.PURPLE_PAINTBALL_HELMET.get());
                        output.accept(ModItems.PURPLE_PAINTBALL_CHEST.get());
                        output.accept(ModItems.PURPLE_PAINTBALL_PANTS.get());
                        output.accept(ModItems.PURPLE_PAINTBALL_SHOES.get());
                        output.accept(ModItems.PURPLE_REMOTE.get());
                        output.accept(ModItems.PURPLE_PAINTBRUSH.get());
                        output.accept(ModItems.PURPLE_PELLETS.get());
                        output.accept(ModItems.PURPLE_PISTOL.get());
                        output.accept(ModItems.PURPLE_SHOTGUN.get());
                        output.accept(ModItems.PURPLE_RIFLE.get());
                        output.accept(ModItems.PURPLE_SNIPER.get());
                        output.accept(ModItems.PURPLE_LAUNCHER.get());
                        output.accept(ModItems.PURPLE_GRENADE.get());
                        addGravityGunIfLoaded(output, "purple");
                        output.accept(ModItems.PURPLE_CLAYMORE.get());
                        output.accept(ModItems.PURPLE_C4.get());
                        output.accept(ModItems.PURPLE_FLAG.get());
                        output.accept(ModItems.PURPLE_POD.get());
                        output.accept(ModItems.PURPLE_WEAPON_RACK.get());
                        output.accept(ModItems.PURPLE_GEAR_RACK.get());
                        output.accept(ModItems.PURPLE_MEDKIT.get());
                    })
                    .build()
    );

    private static void addGravityGunIfLoaded(CreativeModeTab.Output output, String color) {
        if (!ModList.get().isLoaded("gravitygun")) return;

        ResourceLocation id = new ResourceLocation("gravitygun", color + "_gravity_gun");
        Item item = ForgeRegistries.ITEMS.getValue(id);
        if (item != null) {
            output.accept(item);
        }
    }

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}