package com.zerokg2004.paintball.entity.projectile;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.registry.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class BluePelletEntity extends BasePelletEntity {

    public BluePelletEntity(EntityType<? extends BluePelletEntity> type, Level level) {
        super(type, level);
    }

    public BluePelletEntity(Level level, LivingEntity shooter) {
        super(ModEntityTypes.BLUE_PELLET.get(), shooter, level);
    }

    public BluePelletEntity(Level level, double x, double y, double z) {
        super(ModEntityTypes.BLUE_PELLET.get(), level);
        setPos(x, y, z);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BLUE_PELLETS.get();
    }
}