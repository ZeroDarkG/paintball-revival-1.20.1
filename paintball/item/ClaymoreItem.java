package com.zerokg2004.paintball.item;

import com.zerokg2004.paintball.registry.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.BlockState;

public class ClaymoreItem extends BlockItem {
    private final DyeColor color;

    public ClaymoreItem(DyeColor color, Properties properties) {
        super(getClaymoreBlockForColor(color), properties);
        this.color = color;
    }

    private static Block getClaymoreBlockForColor(DyeColor color) {
        return switch (color) {
            case RED    -> ModBlocks.RED_CLAYMORE_BLOCK.get();
            case BLUE   -> ModBlocks.BLUE_CLAYMORE_BLOCK.get();
            case GREEN  -> ModBlocks.GREEN_CLAYMORE_BLOCK.get();
            case YELLOW -> ModBlocks.YELLOW_CLAYMORE_BLOCK.get();
            case ORANGE -> ModBlocks.ORANGE_CLAYMORE_BLOCK.get();
            case PURPLE -> ModBlocks.PURPLE_CLAYMORE_BLOCK.get();
            default -> ModBlocks.RED_CLAYMORE_BLOCK.get();
        };
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        return super.placeBlock(context, state);
    }

    public DyeColor getColor() { return color; }
}