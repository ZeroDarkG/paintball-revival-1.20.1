package com.zerokg2004.paintball.util;

import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.function.BiFunction;

public class DispenseProjectile extends AbstractProjectileDispenseBehavior {
    private final EntityType<?> type;
    private final BiFunction<EntityType<?>, Level, Projectile> function;

    // Usamos una función limpia sin genéricos extraños
    public DispenseProjectile(EntityType<?> type, BiFunction<EntityType<?>, Level, Projectile> function) {
        this.type = type;
        this.function = function;
    }

    @Override
    protected Projectile getProjectile(Level level, Position pos, ItemStack stack) {
        Projectile projectile = function.apply(type, level);
        projectile.setPos(pos.x(), pos.y(), pos.z());

        if (projectile instanceof ThrowableItemProjectile throwable) {
            throwable.setItem(stack);
        }

        return projectile;
    }
}