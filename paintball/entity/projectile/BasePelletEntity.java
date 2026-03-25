package com.zerokg2004.paintball.entity.projectile;

import com.zerokg2004.paintball.network.NetworkHandler;
import com.zerokg2004.paintball.network.PacketHitMarker;
import com.zerokg2004.paintball.registry.SoundEventsRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

public abstract class BasePelletEntity extends ThrowableItemProjectile {

    private static final EntityDataAccessor<Boolean> DATA_IN_GROUND =
            SynchedEntityData.defineId(BasePelletEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> DATA_IN_GROUND_FACE =
            SynchedEntityData.defineId(BasePelletEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Integer> DATA_SHAKE =
            SynchedEntityData.defineId(BasePelletEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Float> DATA_STUCK_YAW =
            SynchedEntityData.defineId(BasePelletEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> DATA_STUCK_PITCH =
            SynchedEntityData.defineId(BasePelletEntity.class, EntityDataSerializers.FLOAT);

    private static final int MAX_TICKS_IN_GROUND = 1200;

    // Solo servidor (y NBT). No confíes en esto en cliente al spawnear desde red.
    private BlockPos inGroundPos;
    private BlockState inGroundState;

    private int ticksInGround = 0;
    private int ticksInAir = 0;

    private Vec3 startPos;
    private int maxDistanceBlocks = 0;

    private float damage = 2.0F;

    protected BasePelletEntity(EntityType<? extends BasePelletEntity> type, Level level) {
        super(type, level);
    }

    protected BasePelletEntity(EntityType<? extends BasePelletEntity> type, LivingEntity shooter, Level level) {
        super(type, shooter, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        entityData.define(DATA_IN_GROUND, false);
        entityData.define(DATA_IN_GROUND_FACE, -1);
        entityData.define(DATA_SHAKE, 0);
        entityData.define(DATA_STUCK_YAW, 0.0F);
        entityData.define(DATA_STUCK_PITCH, 0.0F);
    }

    // ===== Helpers =====
    public boolean isInGround() {
        return entityData.get(DATA_IN_GROUND);
    }

    public Direction getInGroundFace() {
        int idx = entityData.get(DATA_IN_GROUND_FACE);
        return (idx >= 0 && idx < Direction.values().length) ? Direction.values()[idx] : null;
    }

    public float getShakeTime() {
        return entityData.get(DATA_SHAKE);
    }

    public float getStuckYaw() {
        return entityData.get(DATA_STUCK_YAW);
    }

    public float getStuckPitch() {
        return entityData.get(DATA_STUCK_PITCH);
    }

    private void setStuckRot(float yaw, float pitch) {
        entityData.set(DATA_STUCK_YAW, yaw);
        entityData.set(DATA_STUCK_PITCH, pitch);
    }

    private void setInGround(boolean v, Direction face) {
        entityData.set(DATA_IN_GROUND, v);
        entityData.set(DATA_IN_GROUND_FACE, face == null ? -1 : face.ordinal());

        if (!v) {
            inGroundPos = null;
            inGroundState = null;
            setNoGravity(false);
            noPhysics = false;
            setOnGround(false);
        }
    }

    private void setShakeTime(int ticks) {
        entityData.set(DATA_SHAKE, Math.max(0, ticks));
    }

    public void setPelletDamage(float dmg) {
        this.damage = dmg;
    }

    protected float getPelletDamage() {
        return damage;
    }

    public void setMaxDistanceBlocks(int blocks) {
        this.maxDistanceBlocks = blocks;
    }

    // IMPORTANTE: cuando se sincronice que está clavado o cambie la rot clavada,
    // aplicamos la rotación en cliente para que nunca “salte” al recargar/chunk-load.
    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> key) {
        super.onSyncedDataUpdated(key);

        if (level().isClientSide) {
            if (DATA_IN_GROUND.equals(key) || DATA_STUCK_YAW.equals(key) || DATA_STUCK_PITCH.equals(key)) {
                if (isInGround()) {
                    applyStuckRotationClient();
                    freezeInGroundClient();
                }
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

    private void freezeInGroundClient() {
        // En cliente NO validamos bloque/pos (no están sincronizados): solo congelamos.
        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
        noPhysics = true;
        setOnGround(true);
    }

    @Override
    public void tick() {
        if (startPos == null) startPos = position();

        int shake = entityData.get(DATA_SHAKE);
        if (shake > 0) entityData.set(DATA_SHAKE, shake - 1);

        if (!level().isClientSide && maxDistanceBlocks > 0) {
            if (position().distanceTo(startPos) >= (double) maxDistanceBlocks) {
                discard();
                return;
            }
        }

        if (isInGround()) {
            // ✅ CLAVE: cliente no puede “desclavarlo” por falta de inGroundPos/state
            if (level().isClientSide) {
                applyStuckRotationClient();
                freezeInGroundClient();
                // no llames super.tick() para que no intente física/movimiento
                return;
            }

            tickInGroundServer();
            return;
        }

        ticksInAir++;
        super.tick();
    }

    private void tickInGroundServer() {
        if (inGroundPos == null || inGroundState == null) {
            // Si por algún motivo servidor no lo sabe, lo soltamos.
            setInGround(false, null);
            super.tick();
            return;
        }

        BlockState current = level().getBlockState(inGroundPos);

        // ✅ FIX: NO uses "!=" (referencia). Compara por bloque (estable).
        // Si quieres ser más estricto, puedes comparar también propiedades, pero esto ya evita el bug random.
        boolean sameBlock = current.is(inGroundState.getBlock());

        if (!sameBlock || current.isAir()) {
            setInGround(false, null);

            Vec3 v = getDeltaMovement();
            setDeltaMovement(
                    v.x * (random.nextFloat() * 0.2F),
                    v.y * (random.nextFloat() * 0.2F),
                    v.z * (random.nextFloat() * 0.2F)
            );

            ticksInGround = 0;
            ticksInAir = 0;
            super.tick();
            return;
        }

        ticksInGround++;

        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
        noPhysics = true;
        setOnGround(true);

        // Mantén rotación clavada también en servidor (por consistencia si se vuelve a guardar/cargar)
        float yaw = getStuckYaw();
        float pitch = getStuckPitch();
        setYRot(yaw);
        setXRot(pitch);
        yRotO = yaw;
        xRotO = pitch;

        if (ticksInGround >= MAX_TICKS_IN_GROUND) {
            discard();
        }
    }

    @Override
    protected float getGravity() {
        if (damage >= 7.5F) return 0.005F;
        return 0.03F;
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.BLOCK) {
            onHitBlock((BlockHitResult) result);
        } else if (result.getType() == HitResult.Type.ENTITY) {
            onHitEntity((EntityHitResult) result);
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (isInGround()) return;

        Direction face = result.getDirection();
        BlockPos hitPos = result.getBlockPos();
        BlockState state = level().getBlockState(hitPos);

        Vec3 hit = result.getLocation();

        Vec3 v = getDeltaMovement();
        double len = v.length();

        double back = 0.05D;
        Vec3 backVec = (len > 1.0E-6)
                ? v.scale(1.0D / len).scale(back)
                : new Vec3(face.getStepX(), face.getStepY(), face.getStepZ()).scale(back);

        double out = 0.0025D;
        Vec3 faceOut = new Vec3(face.getStepX(), face.getStepY(), face.getStepZ()).scale(out);

        Vec3 finalPos = hit.subtract(backVec).add(faceOut);
        setPos(finalPos.x, finalPos.y, finalPos.z);

        double horiz = Math.sqrt(v.x * v.x + v.z * v.z);
        float yaw = (float) (Mth.atan2(v.x, v.z) * (180F / (float) Math.PI));
        float pitch = (float) (Mth.atan2(v.y, horiz) * (180F / (float) Math.PI));

        setYRot(yaw);
        setXRot(pitch);
        yRotO = yaw;
        xRotO = pitch;

        // ✅ rot clavada sincronizada
        setStuckRot(yaw, pitch);

        inGroundPos = hitPos.immutable();
        inGroundState = state;

        ticksInGround = 0;
        ticksInAir = 0;
        setInGround(true, face);

        setDeltaMovement(Vec3.ZERO);
        setNoGravity(true);
        noPhysics = true;
        setOnGround(true);

        setShakeTime(7);

        if (!level().isClientSide) {
            level().playSound(
                    null, getX(), getY(), getZ(),
                    SoundEventsRegistry.GUN_SPLAT.get(),
                    SoundSource.PLAYERS,
                    0.9F, 1.2F
            );
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (level().isClientSide) return;

        Entity target = result.getEntity();
        if (!(target instanceof LivingEntity living)) {
            discard();
            return;
        }

        Entity owner = getOwner();
        float dmg = getPelletDamage();

        if (owner instanceof ServerPlayer shooter) {
            boolean didDamage = living.hurt(level().damageSources().thrown(this, shooter), dmg);
            if (didDamage) {
                NetworkHandler.INSTANCE.send(
                        PacketDistributor.PLAYER.with(() -> shooter),
                        new PacketHitMarker()
                );
            }
        } else if (owner instanceof LivingEntity livingOwner) {
            living.hurt(level().damageSources().thrown(this, livingOwner), dmg);
        } else {
            living.hurt(level().damageSources().thrown(this, null), dmg);
        }

        level().playSound(
                null, getX(), getY(), getZ(),
                SoundEventsRegistry.GUN_SPLAT.get(),
                SoundSource.PLAYERS,
                0.9F, 1.0F
        );

        discard();
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    // ==== NBT ====
    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);

        tag.putBoolean("InGround", isInGround());
        tag.putInt("TicksInGround", ticksInGround);
        tag.putInt("TicksInAir", ticksInAir);
        tag.putInt("Shake", entityData.get(DATA_SHAKE));
        tag.putFloat("Damage", damage);

        tag.putFloat("StuckYaw", getStuckYaw());
        tag.putFloat("StuckPitch", getStuckPitch());

        if (startPos != null) {
            tag.putDouble("StartX", startPos.x);
            tag.putDouble("StartY", startPos.y);
            tag.putDouble("StartZ", startPos.z);
        }
        tag.putInt("MaxDistance", maxDistanceBlocks);

        if (isInGround() && inGroundPos != null) {
            tag.putInt("InGroundX", inGroundPos.getX());
            tag.putInt("InGroundY", inGroundPos.getY());
            tag.putInt("InGroundZ", inGroundPos.getZ());
            Direction f = getInGroundFace();
            if (f != null) tag.putInt("InGroundFace", f.ordinal());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);

        damage = tag.getFloat("Damage");
        ticksInGround = tag.getInt("TicksInGround");
        ticksInAir = tag.getInt("TicksInAir");
        maxDistanceBlocks = tag.getInt("MaxDistance");
        setShakeTime(tag.getInt("Shake"));

        setStuckRot(tag.getFloat("StuckYaw"), tag.getFloat("StuckPitch"));

        if (tag.contains("StartX")) {
            startPos = new Vec3(tag.getDouble("StartX"), tag.getDouble("StartY"), tag.getDouble("StartZ"));
        }

        boolean ig = tag.getBoolean("InGround");
        if (ig && tag.contains("InGroundX")) {
            inGroundPos = new BlockPos(tag.getInt("InGroundX"), tag.getInt("InGroundY"), tag.getInt("InGroundZ"));
            inGroundState = level().getBlockState(inGroundPos);

            int faceIdx = tag.contains("InGroundFace") ? tag.getInt("InGroundFace") : -1;
            Direction face = (faceIdx >= 0 && faceIdx < Direction.values().length) ? Direction.values()[faceIdx] : null;

            setInGround(true, face);

            // ✅ Restaura rotación clavada y flags al cargar chunk (servidor)
            float yaw = getStuckYaw();
            float pitch = getStuckPitch();
            setYRot(yaw);
            setXRot(pitch);
            yRotO = yaw;
            xRotO = pitch;

            setDeltaMovement(Vec3.ZERO);
            setNoGravity(true);
            noPhysics = true;
            setOnGround(true);
        } else {
            setInGround(false, null);
        }
    }

    @Override
    protected abstract Item getDefaultItem();

    public void setDamage(float damagePerPellet) {
        setPelletDamage(damagePerPellet);
    }
}