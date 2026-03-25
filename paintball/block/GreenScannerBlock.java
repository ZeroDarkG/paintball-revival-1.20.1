package com.zerokg2004.paintball.block;

import com.zerokg2004.paintball.registry.ModSoundTypes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class GreenScannerBlock extends ScannerBlock {
    public GreenScannerBlock() {
        super(DyeColor.GREEN, BlockBehaviour.Properties.of()
                .strength(0.5F)
                .sound(ModSoundTypes.SCANNER)
                .noOcclusion()
        );
    }
}