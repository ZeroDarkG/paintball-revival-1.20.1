package com.zerokg2004.paintball.entity.projectile;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.registry.ModEntityTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class RedPelletEntity extends BasePelletEntity {

    // Constructor usado por el motor (spawn genérico)
    public RedPelletEntity(EntityType<? extends RedPelletEntity> type, Level level) {
        super(type, level);
    }

    // Conveniencia: spawn con owner (el que dispara)
    public RedPelletEntity(Level level, LivingEntity shooter) {
        super(ModEntityTypes.RED_PELLET.get(), shooter, level);
    }

    // (Opcional) Conveniencia: spawn por coordenadas
    public RedPelletEntity(Level level, double x, double y, double z) {
        super(ModEntityTypes.RED_PELLET.get(), level);
        setPos(x, y, z);
    }

    @Override
    protected Item getDefaultItem() {
        // Asegúrate de que el nombre coincide con tu registro (RED_PELLET vs RED_PELLETS)
        return ModItems.RED_PELLETS.get();
    }
}