package com.zerokg2004.paintball.block;

import com.zerokg2004.paintball.registry.ModSoundTypes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class PurpleScannerBlock extends ScannerBlock {
    public PurpleScannerBlock() {
        super(DyeColor.PURPLE, BlockBehaviour.Properties.of()
                .strength(0.5F)
                .sound(ModSoundTypes.SCANNER)
                .noOcclusion()
        );
    }
}