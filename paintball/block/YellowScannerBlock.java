package com.zerokg2004.paintball.block;

import com.zerokg2004.paintball.registry.ModSoundTypes;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class YellowScannerBlock extends ScannerBlock {
    public YellowScannerBlock() {
        super(DyeColor.YELLOW, BlockBehaviour.Properties.of()
                .strength(0.5F)
                .sound(ModSoundTypes.SCANNER) // Esto usa tu sonido de lana personalizado
                .noOcclusion()
        );
    }
}