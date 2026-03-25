package com.zerokg2004.paintball.registry;

import com.zerokg2004.paintball.PaintballMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundEventsRegistry {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PaintballMod.MODID);
    public static final RegistryObject<SoundEvent> SCANNER_ON      = register("scanner_on");
    public static final RegistryObject<SoundEvent> SCANNER_OFF     = register("scanner_off");
    public static final RegistryObject<SoundEvent> SCANNER_PLACE   = register("block.scanner.place");
    public static final RegistryObject<SoundEvent> SCANNER_BREAK   = register("block.scanner.break");
    public static final RegistryObject<SoundEvent> PAINTBALL_ARMOR_EQUIPMENT =
            register("paintball_armor_equipment");
    public static final RegistryObject<SoundEvent> RELOAD_GUN      = register("reload_gun");
    public static final RegistryObject<SoundEvent> GUN_SHOOT       = register("gun_shoot");
    public static final RegistryObject<SoundEvent> GUN_SPLAT       = register("gun_splat");
    public static final RegistryObject<SoundEvent> EMPTY_GUN_SHOOT  = register("empty_gun_shoot");
    public static final RegistryObject<SoundEvent> PAINTBRUSH      = register("paintbrush");
    public static final RegistryObject<SoundEvent> GRENADE_PIN     = register("grenade_pin");
    public static final RegistryObject<SoundEvent> GRENADE_LAND    = register("grenade_land");
    public static final RegistryObject<SoundEvent> C4_PLACE        = register("c4_place");
    public static final RegistryObject<SoundEvent> CLAYMORE_PLACE  = register("claymore_place");
    public static final RegistryObject<SoundEvent> MEDKIT_HEAL     = register("medkit_heal");

    private static RegistryObject<SoundEvent> register(String name) {
        ResourceLocation id = new ResourceLocation(PaintballMod.MODID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}