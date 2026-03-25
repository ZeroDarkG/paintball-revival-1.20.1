package com.zerokg2004.paintball.entity.projectile;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.registry.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class OrangePelletEntity extends BasePelletEntity {

    public OrangePelletEntity(EntityType<? extends OrangePelletEntity> type, Level level) {
        super(type, level);
    }

    public OrangePelletEntity(Level level, LivingEntity shooter) {
        super(ModEntityTypes.ORANGE_PELLET.get(), shooter, level);
    }

    public OrangePelletEntity(Level level, double x, double y, double z) {
        super(ModEntityTypes.ORANGE_PELLET.get(), level);
        setPos(x, y, z);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.ORANGE_PELLETS.get();
    }
}