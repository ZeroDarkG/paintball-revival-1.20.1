package com.zerokg2004.paintball.entity.projectile;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.registry.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class OrangePelletEntity extends BasePelletEntity {
    public OrangePelletEntity(EntityType<?> type, Level level) {
        super((EntityType<? extends BasePelletEntity>) type, level);
    }
    public OrangePelletEntity(Level level, LivingEntity shooter) {
        super(ModEntityTypes.ORANGE_PELLET.get(), shooter, level);
    }
    @Override
    protected Item getDefaultItem() { return ModItems.ORANGE_PELLETS.get(); }
    @Override
    protected DyeColor getPelletColor() { return DyeColor.ORANGE; }
}