package com.zerokg2004.paintball.entity.projectile;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.registry.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class RedGrenadeEntity extends BaseGrenadeEntity {
    public RedGrenadeEntity(EntityType<? extends RedGrenadeEntity> type, Level level) {
        super(type, level);
    }

    public RedGrenadeEntity(Level level, LivingEntity owner) {
        super(ModEntityTypes.RED_GRENADE.get(), level, owner);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.RED_GRENADE.get();
    }
}