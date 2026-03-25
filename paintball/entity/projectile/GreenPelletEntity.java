package com.zerokg2004.paintball.entity.projectile;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.registry.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class GreenPelletEntity extends BasePelletEntity {

    public GreenPelletEntity(EntityType<? extends GreenPelletEntity> type, Level level) {
        super(type, level);
    }

    public GreenPelletEntity(Level level, LivingEntity shooter) {
        super(ModEntityTypes.GREEN_PELLET.get(), shooter, level);
    }

    public GreenPelletEntity(Level level, double x, double y, double z) {
        super(ModEntityTypes.GREEN_PELLET.get(), level);
        setPos(x, y, z);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.GREEN_PELLETS.get();
    }
}