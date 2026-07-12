package com.zerokg2004.paintball.client.model;

import com.zerokg2004.paintball.PaintballMod;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {

    public static final ModelLayerLocation C4 = new ModelLayerLocation(
            new ResourceLocation(PaintballMod.MODID, "c4"), "main");

    public static final ModelLayerLocation CLAYMORE = new ModelLayerLocation(
            new ResourceLocation(PaintballMod.MODID, "claymore"), "main");

    public static final ModelLayerLocation FLAG = new ModelLayerLocation(
            new ResourceLocation(PaintballMod.MODID, "flag"), "main");

    public static final ModelLayerLocation MEDKIT = new ModelLayerLocation(
            new ResourceLocation(PaintballMod.MODID, "medkit"), "main");

    public static final ModelLayerLocation POD =
            new ModelLayerLocation(new ResourceLocation(PaintballMod.MODID, "pod"), "main");

    public static final ModelLayerLocation ROULETTE =
            new ModelLayerLocation(new ResourceLocation(PaintballMod.MODID, "roulette"), "main");

    public static final ModelLayerLocation GEAR_RACK =
            new ModelLayerLocation(new ResourceLocation(PaintballMod.MODID, "gear_rack"), "main");

    public static final ModelLayerLocation WEAPON_RACK =
            new ModelLayerLocation(new ResourceLocation(PaintballMod.MODID, "weapon_rack"), "main");
}
