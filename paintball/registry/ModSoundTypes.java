package com.zerokg2004.paintball.registry;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public class ModSoundTypes {

    public static final SoundType SCANNER = new SoundType(
            1.0F, 1.0F,
            SoundEventsRegistry.SCANNER_BREAK.get(),
            SoundEventsRegistry.SCANNER_ON.get(),
            SoundEventsRegistry.SCANNER_PLACE.get(),
            SoundEventsRegistry.SCANNER_OFF.get(),
            SoundEventsRegistry.SCANNER_OFF.get()
    );

    public static final SoundType C4 = new SoundType(
            1.0F, 1.0F,
            SoundEventsRegistry.C4_PLACE.get(),
            SoundEvents.WOOL_STEP,
            SoundEventsRegistry.C4_PLACE.get(),
            SoundEvents.WOOL_HIT,
            SoundEvents.WOOL_FALL
    );

    public static final SoundType CLAYMORE = new SoundType(
            1.0F, 1.0F,
            SoundEventsRegistry.CLAYMORE_PLACE.get(),
            SoundEvents.STONE_STEP,
            SoundEventsRegistry.CLAYMORE_PLACE.get(),
            SoundEvents.STONE_HIT,
            SoundEvents.STONE_FALL
    );
}