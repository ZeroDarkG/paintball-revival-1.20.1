package com.zerokg2004.paintball.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;

public class ScannerUtils {

    public static boolean playerMatchesColor(Player player, DyeColor color) {

        ItemStack mainHand = player.getMainHandItem();
        if (mainHand.is(Items.RED_WOOL) && color == DyeColor.RED) return true;
        if (mainHand.is(Items.BLUE_WOOL) && color == DyeColor.BLUE) return true;
        if (mainHand.is(Items.YELLOW_WOOL) && color == DyeColor.YELLOW) return true;
        if (mainHand.is(Items.GREEN_WOOL) && color == DyeColor.GREEN) return true;
        if (mainHand.is(Items.ORANGE_WOOL) && color == DyeColor.ORANGE) return true;
        if (mainHand.is(Items.PURPLE_WOOL) && color == DyeColor.PURPLE) return true;

        return false;
    }
}