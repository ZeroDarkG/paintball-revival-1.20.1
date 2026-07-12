package com.zerokg2004.paintball.entity.projectile;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.registry.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class RedPelletEntity extends BasePelletEntity {
    public RedPelletEntity(EntityType<?> type, Level level) {
        super((EntityType<? extends BasePelletEntity>) type, level);
    }
    public RedPelletEntity(Level level, LivingEntity shooter) {
        super(ModEntityTypes.RED_PELLET.get(), shooter, level);
    }
    @Override
    protected Item getDefaultItem() { return ModItems.RED_PELLETS.get(); }
    @Override
    protected DyeColor getPelletColor() { return DyeColor.RED; }
}