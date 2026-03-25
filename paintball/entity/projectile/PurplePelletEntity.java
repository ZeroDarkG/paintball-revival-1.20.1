package com.zerokg2004.paintball.entity.projectile;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.registry.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class PurplePelletEntity extends BasePelletEntity {

    public PurplePelletEntity(EntityType<? extends PurplePelletEntity> type, Level level) {
        super(type, level);
    }

    public PurplePelletEntity(Level level, LivingEntity shooter) {
        super(ModEntityTypes.PURPLE_PELLET.get(), shooter, level);
    }

    public PurplePelletEntity(Level level, double x, double y, double z) {
        super(ModEntityTypes.PURPLE_PELLET.get(), level);
        setPos(x, y, z);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.PURPLE_PELLETS.get();
    }
}