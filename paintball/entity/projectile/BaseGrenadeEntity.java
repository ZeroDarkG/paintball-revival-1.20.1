package com.zerokg2004.paintball.entity.projectile;

import com.zerokg2004.paintball.network.NetworkHandler;
import com.zerokg2004.paintball.network.PacketHitMarker;
import com.zerokg2004.paintball.registry.SoundEventsRegistry;
import com.zerokg2004.paintball.item.PaintballArmorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public abstract class BaseGrenadeEntity extends ThrowableItemProjectile implements IEntityAdditionalSpawnData {

    private static final EntityDataAccessor<Boolean> DATA_STUCK =
            SynchedEntityData.defineId(BaseGrenadeEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> DATA_STUCK_FACE =
            SynchedEntityData.defineId(BaseGrenadeEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> DATA_STUCK_YAW =
            SynchedEntityData.defineId(BaseGrenadeEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Float> DATA_STUCK_PITCH =
            SynchedEntityData.defineId(BaseGrenadeEntity.class, EntityDataSerializers.FLOAT);

    private static final EntityDataAccessor<Integer> DATA_SHAKE =
            SynchedEntityData.defineId(BaseGrenadeEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> DATA_DAMAGE =
            SynchedEntityData.defineId(BaseGrenadeEntity.class, EntityDataSerializers.FLOAT);

    private static final int STUCK_TIME = 40;
    private static final float LAUNCH_VELOCITY_MULTIPLIER = 0.5F;
    private static final double DAMAGE_RADIUS = 2.5D;

    private BlockPos inGroundPos;
    private BlockState inGroundState;
    private int stuckTicks = 0;
    private Vec3 stuckPosition;

    public BaseGrenadeEntity(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public BaseGrenadeEntity(EntityType<? extends ThrowableItemProjectile> type, Level level, LivingEntity owner) {
        super(type, owner, level);
    }

    // ✅ MÉTODO NUEVO: Cada granada hija debe decir su color
    protected abstract DyeColor getGrenadeColor();

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_STUCK, false);
        entityData.define(DATA_STUCK_FACE, -1);
        entityData.define(DATA_STUCK_YAW, 0.0F);
        entityData.define(DATA_STUCK_PITCH, 0.0F);
        entityData.define(DATA_SHAKE, 0);
        entityData.define(DATA_DAMAGE, 15.0F);
    }

    public float getGrenadeDamage() {
        return entityData.get(DATA_DAMAGE);
    }

    public void setGrenadeDamage(float dmg) {
        entityData.set(DATA_DAMAGE, Math.max(0.0F, dmg));
    }

    public boolean isStuck() {
        return entityData.get(DATA_STUCK);
    }

    public Direction getStuckFace() {
        int idx = entityData.get(DATA_STUCK_FACE);
        return (idx >= 0 && idx < Direction.values().length) ? Direction.values()[idx] : null;
    }

    public float getStuckYaw() {
        return entityData.get(DATA_STUCK_YAW);
    }

    public float getStuckPitch() {
        return entityData.get(DATA_STUCK_PITCH);
    }

    public int getShakeTime() {
        return entityData.get(DATA_SHAKE);
    }

    private void setShakeTime(int ticks) {
        entityData.set(DATA_SHAKE, Math.max(0, ticks));
    }

    private void setStuckRot(float yaw, float pitch) {
        entityData.set(DATA_STUCK_YAW, yaw);
        entityData.set(DATA_STUCK_PITCH, pitch);
    }

    private void setStuck(boolean stuck, Direction face) {
        entityData.set(DATA_STUCK, stuck);
        entityData.set(DATA_STUCK_FACE, face == null ? -1 : face.ordinal());

        if (!stuck) {
            stuckPosition = null;
            inGroundPos = null;
            inGroundState = null;
            setNoGravity(false);
            noPhysics = false;
            setOnGround(false);
            stuckTicks = 0;
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);
        if (!level().isClientSide) return;
        if (DATA_STUCK.equals(key) || DATA_STUCK_YAW.equals(key) || DATA_STUCK_PITCH.equals(key)) {
            if (isStuck()) {
                applyStuckRotationClient();
                freezeStuckClient();
            }
        }
    }

    private void applyStuckRotationClient() {
        float yaw = getStuckYaw();
        float pitch = getStuckPitch();
        setYRot(yaw);
        setXRot(pitch);
        yRotO = yaw;
        xRotO = pitch;
    }

    private void freezeStuckClient() {
        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
        noPhysics = true;
        setOnGround(true);
    }

    @Override
    public void tick() {
        int shake = entityData.get(DATA_SHAKE);
        if (shake > 0) entityData.set(DATA_SHAKE, shake - 1);

        if (!isStuck()) {
            super.tick();
            return;
        }

        if (level().isClientSide) {
            applyStuckRotationClient();
            freezeStuckClient();
            if (stuckPosition != null) {
                setPos(stuckPosition.x, stuckPosition.y, stuckPosition.z);
            }
            return;
        }

        if (inGroundPos == null || inGroundState == null) {
            setStuck(false, null);
            super.tick();
            return;
        }

        BlockState current = level().getBlockState(inGroundPos);
        if (!current.is(inGroundState.getBlock()) || current.isAir()) {
            setStuck(false, null);
            super.tick();
            return;
        }

        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
        noPhysics = true;
        setOnGround(true);

        float yaw = getStuckYaw();
        float pitch = getStuckPitch();
        setYRot(yaw);
        setXRot(pitch);
        yRotO = yaw;
        xRotO = pitch;

        if (stuckPosition != null) {
            setPos(stuckPosition.x, stuckPosition.y, stuckPosition.z);
        }

        stuckTicks++;
        if (stuckTicks < STUCK_TIME) return;

        Entity owner = getOwner();
        final float baseDamage = getGrenadeDamage();
        DyeColor grenadeColor = getGrenadeColor();

        AABB box = new AABB(
                getX() - DAMAGE_RADIUS, getY() - DAMAGE_RADIUS, getZ() - DAMAGE_RADIUS,
                getX() + DAMAGE_RADIUS, getY() + DAMAGE_RADIUS, getZ() + DAMAGE_RADIUS
        );

        List<LivingEntity> targets = level().getEntitiesOfClass(LivingEntity.class, box, Entity::isAlive);

        // Si no hay dueño (dispensador), la granada es su propia fuente de daño
        DamageSource src = (owner instanceof LivingEntity livingOwner)
                ? level().damageSources().thrown(this, livingOwner)
                : level().damageSources().explosion(this, null);

        for (LivingEntity target : targets) {
            // Filtro de equipo: Color de armadura vs Color de granada
            if (target instanceof Player p && grenadeColor != null) {
                if (isWearingFullArmorOfColor(p, grenadeColor)) {
                    continue; // Es del equipo, saltar
                }
            }

            double dist = target.distanceTo(this);
            double factor = 1.0D - (dist / DAMAGE_RADIUS);
            if (factor <= 0.0D) continue;

            float dmg = (float) (baseDamage * factor);

            // CRÍTICO: Reset de invulnerabilidad (igual que tus pellets)
            target.invulnerableTime = 0;

            if (target.hurt(src, dmg)) {
                if (owner instanceof ServerPlayer shooter) {
                    target.setLastHurtByPlayer(shooter);
                    NetworkHandler.INSTANCE.send(
                            net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> shooter),
                            new PacketHitMarker()
                    );
                }
            }
        }

        // Explosión visual (fuerza 0 para no romper el filtro de daño de arriba)
        level().explode(owner, getX(), getY(), getZ(), 0.0F, false, Level.ExplosionInteraction.NONE);
        this.discard();
    }

    @Override
    public void writeSpawnData(net.minecraft.network.FriendlyByteBuf buffer) {
        DyeColor color = getGrenadeColor();
        buffer.writeInt(color == null ? -1 : color.ordinal());
    }

    @Override
    public void readSpawnData(net.minecraft.network.FriendlyByteBuf buffer) {
        if (buffer.isReadable()) {
            buffer.readInt();
        }
    }

    private boolean isWearingFullArmorOfColor(Player player, DyeColor expectedColor) {
        if (expectedColor == null) return false; // Seguridad
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                ItemStack stack = player.getItemBySlot(slot);
                // Añadimos comprobación de !stack.isEmpty()
                if (stack.isEmpty() || !(stack.getItem() instanceof PaintballArmorItem armorItem) ||
                        armorItem.getColor() != expectedColor) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onHit(HitResult result) {
        super.onHit(result);
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (isStuck()) return;

        Direction face = result.getDirection();
        BlockPos hitPos = result.getBlockPos();
        BlockState state = level().getBlockState(hitPos);
        Vec3 hit = result.getLocation();

        Vec3 v = getDeltaMovement();
        double vLen = v.length();
        Vec3 dir = (vLen > 1.0E-6) ? v.scale(1.0D / vLen) : hit.subtract(position()).normalize();
        Vec3 n = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ());

        if (dir.dot(n) > 0.0D) dir = dir.scale(-1.0D);

        double back = 0.06D;
        double out  = 0.0025D;
        Vec3 finalPos = hit.subtract(dir.scale(back)).add(n.scale(out));

        double horiz = Math.sqrt(dir.x * dir.x + dir.z * dir.z);
        float yaw = (float) (Mth.atan2(dir.x, dir.z) * (180F / (float) Math.PI));
        float pitch = (float) (Mth.atan2(dir.y, horiz) * (180F / (float) Math.PI));

        stuckPosition = finalPos;
        setPos(finalPos.x, finalPos.y, finalPos.z);
        setYRot(yaw);
        setXRot(pitch);
        yRotO = yaw;
        xRotO = pitch;
        setStuckRot(yaw, pitch);

        inGroundPos = hitPos.immutable();
        inGroundState = state;
        setStuck(true, face);

        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
        noPhysics = true;
        setOnGround(true);
        setShakeTime(7);

        if (!level().isClientSide) {
            level().playSound(null, getX(), getY(), getZ(), SoundEventsRegistry.GRENADE_LAND.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        super.shoot(x, y, z, velocity * LAUNCH_VELOCITY_MULTIPLIER, inaccuracy);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Stuck", isStuck());
        tag.putInt("StuckFace", entityData.get(DATA_STUCK_FACE));
        tag.putFloat("StuckYaw", getStuckYaw());
        tag.putFloat("StuckPitch", getStuckPitch());
        tag.putInt("Shake", getShakeTime());
        tag.putInt("StuckTicks", stuckTicks);
        tag.putFloat("GrenadeDamage", getGrenadeDamage());

        if (stuckPosition != null) {
            tag.putDouble("StuckX", stuckPosition.x);
            tag.putDouble("StuckY", stuckPosition.y);
            tag.putDouble("StuckZ", stuckPosition.z);
        }
        if (isStuck() && inGroundPos != null) {
            tag.putInt("InGroundX", inGroundPos.getX());
            tag.putInt("InGroundY", inGroundPos.getY());
            tag.putInt("InGroundZ", inGroundPos.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (!tag.contains("Stuck")) return;

        boolean stuck = tag.getBoolean("Stuck");
        Direction face = null;
        if (tag.contains("StuckFace")) {
            int idx = tag.getInt("StuckFace");
            if (idx >= 0 && idx < Direction.values().length) face = Direction.values()[idx];
        }
        setStuck(stuck, face);

        if (tag.contains("StuckYaw") && tag.contains("StuckPitch")) setStuckRot(tag.getFloat("StuckYaw"), tag.getFloat("StuckPitch"));
        if (tag.contains("Shake")) setShakeTime(tag.getInt("Shake"));
        if (tag.contains("StuckTicks")) stuckTicks = tag.getInt("StuckTicks");
        if (tag.contains("GrenadeDamage")) setGrenadeDamage(tag.getFloat("GrenadeDamage"));

        if (stuck && tag.contains("StuckX")) {
            stuckPosition = new Vec3(tag.getDouble("StuckX"), tag.getDouble("StuckY"), tag.getDouble("StuckZ"));
            setPos(stuckPosition.x, stuckPosition.y, stuckPosition.z);
            setDeltaMovement(Vec3.ZERO);
            setNoGravity(true);
            noPhysics = true;
            setOnGround(true);
            float yaw = getStuckYaw();
            float pitch = getStuckPitch();
            setYRot(yaw);
            setXRot(pitch);
            yRotO = yaw;
            xRotO = pitch;
        }

        if (!level().isClientSide && stuck && tag.contains("InGroundX")) {
            inGroundPos = new BlockPos(tag.getInt("InGroundX"), tag.getInt("InGroundY"), tag.getInt("InGroundZ"));
            inGroundState = level().getBlockState(inGroundPos);
        }
    }

    @Override
    protected abstract Item getDefaultItem();

    public void setDamage(float dmg) {
        setGrenadeDamage(dmg);
    }
}