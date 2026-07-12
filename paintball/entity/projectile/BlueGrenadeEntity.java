package com.zerokg2004.paintball.entity.projectile;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.registry.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class BlueGrenadeEntity extends BaseGrenadeEntity {
    public BlueGrenadeEntity(EntityType<?> type, Level level) {
        super((EntityType<? extends ThrowableItemProjectile>) type, level);
    }

    public BlueGrenadeEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.BLUE_GRENADE.get(), level, owner);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BLUE_GRENADE.get();
    }

    @Override
    protected DyeColor getGrenadeColor() {
        return DyeColor.BLUE;
    }
}