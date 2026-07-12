package com.zerokg2004.paintball.item;

import com.zerokg2004.paintball.registry.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class C4Item extends BlockItem {
    private final DyeColor color;

    public C4Item(DyeColor color, Properties properties) {
        super(getC4BlockForColor(color), properties);
        this.color = color;
    }

    private static Block getC4BlockForColor(DyeColor color) {
        return switch (color) {
            case RED -> ModBlocks.RED_C4_BLOCK.get();
            case BLUE -> ModBlocks.BLUE_C4_BLOCK.get();
            case GREEN -> ModBlocks.GREEN_C4_BLOCK.get();
            case YELLOW -> ModBlocks.YELLOW_C4_BLOCK.get();
            case ORANGE -> ModBlocks.ORANGE_C4_BLOCK.get();
            case PURPLE -> ModBlocks.PURPLE_C4_BLOCK.get();
            default -> ModBlocks.RED_C4_BLOCK.get();
        };
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return super.placeBlock(context, state);
    }

    public DyeColor getColor() {
        return color;
    }
}
