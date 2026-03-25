package com.zerokg2004.paintball.item;

import net.minecraft.world.item.DyeColor;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class PaintballArmorItem extends ArmorItem {

    private final DyeColor color;
    private final String textureName;

    public PaintballArmorItem(ArmorMaterial material, Type type, Properties properties,
                              String textureName, DyeColor color) {
        super(material, type, properties);
        this.textureName = textureName;
        this.color = color;
    }

    public boolean isArmorPieceOfColor(ItemStack stack, DyeColor color) {
        if (!(stack.getItem() instanceof PaintballArmorItem armorItem)) {
            return false;
        }
        return armorItem.getColor() == color;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity,
                                  EquipmentSlot slot, String type) {
        return "paintball:textures/models/armor/" + textureName
                + (slot == EquipmentSlot.LEGS ? "_2.png" : "_1.png");
    }

    public DyeColor getColor() {
        return this.color;
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isDamaged(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return false;
    }
}