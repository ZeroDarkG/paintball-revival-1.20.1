package com.zerokg2004.paintball.item.gun;

import com.zerokg2004.paintball.entity.projectile.BaseGrenadeEntity;
import com.zerokg2004.paintball.entity.projectile.BasePelletEntity;
import com.zerokg2004.paintball.registry.SoundEventsRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.UUID;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class GunItem extends Item {

    private final boolean isLauncher;
    private final boolean isThrownGrenade;

    private static final String TAG_AMMO = "Ammo";
    private static final String TAG_SCOPED = "Scoped";
    private static final String TAG_RESERVE_AMMO = "ReserveAmmo";
    private static final String TAG_RESERVE_GRENADES = "ReserveGrenades";
    private static final int MOB_RELOAD_TICKS = 18;

    public static final String TAG_REAL_OWNER = "PBRealOwner";

    @FunctionalInterface
    public interface PelletFactory {
        BasePelletEntity create(Level level, Player shooter);
    }

    @FunctionalInterface
    public interface GrenadeSupplier {
        BaseGrenadeEntity get(Level level, Player player, float damage);
    }

    @FunctionalInterface
    public interface ClientFireHandler {
        void sendFirePacket(InteractionHand hand, boolean ads);
    }

    private static ClientFireHandler CLIENT_FIRE_HANDLER = null;

    public static void setClientFireHandler(ClientFireHandler handler) {
        CLIENT_FIRE_HANDLER = handler;
    }

    public static void sendClientFirePacket(InteractionHand hand, boolean ads) {
        if (CLIENT_FIRE_HANDLER != null) {
            CLIENT_FIRE_HANDLER.sendFirePacket(hand, ads);
        }
    }

    public static void resetFired() {
    }

    private final Supplier<? extends Item> ammoSupplier;
    private final PelletFactory pelletFactory;
    private final float velocity;
    private final float inaccuracy;
    private final int cooldownTicks;
    private final float damagePerPellet;
    private final int pelletsPerShot;
    private final int maxDistance;

    private final int magCapacity;
    private final boolean automatic;
    private final float adsPitchOffsetNonSniper;

    private final Supplier<? extends Item> launcherGrenadeItemSupplier;
    private final GrenadeSupplier launcherGrenadeSupplier;
    private final float hipVelocity;
    private final float hipInaccuracy;
    private final float scopedVelocity;
    private final float scopedInaccuracy;
    private final int launcherCooldownTicks;

    private final float grenadeDamage;
    private final GrenadeSupplier thrownGrenadeSupplier;

    public GunItem(Properties props, Supplier<? extends Item> ammoSupplier, PelletFactory pelletFactory,
                   float velocity, float inaccuracy, int cooldownTicks, float damagePerPellet,
                   int pelletsPerShot, int maxDistance, int magCapacity, boolean automatic, float adsPitchOffset) {
        super(props);
        this.isLauncher = false;
        this.isThrownGrenade = false;

        this.ammoSupplier = ammoSupplier;
        this.pelletFactory = pelletFactory;
        this.velocity = velocity;
        this.inaccuracy = inaccuracy;
        this.cooldownTicks = cooldownTicks;
        this.damagePerPellet = damagePerPellet;
        this.pelletsPerShot = Math.max(1, pelletsPerShot);
        this.maxDistance = Math.max(0, maxDistance);
        this.magCapacity = Math.max(1, magCapacity);
        this.automatic = automatic;
        this.adsPitchOffsetNonSniper = adsPitchOffset;

        this.launcherGrenadeItemSupplier = null;
        this.launcherGrenadeSupplier = null;
        this.hipVelocity = 0.0f;
        this.hipInaccuracy = 0.0f;
        this.scopedVelocity = 0.0f;
        this.scopedInaccuracy = 0.0f;
        this.launcherCooldownTicks = 0;
        this.thrownGrenadeSupplier = null;
        this.grenadeDamage = 0.0F;
    }

    public GunItem(Properties properties, Supplier<? extends Item> grenadeItemSupplier, GrenadeSupplier grenadeSupplier,
                   float hipVelocity, float hipInaccuracy, float scopedVelocity, float scopedInaccuracy,
                   int cooldownTicks, float grenadeDamage) {
        super(properties);
        this.isLauncher = true;
        this.isThrownGrenade = false;

        this.launcherGrenadeItemSupplier = grenadeItemSupplier;
        this.launcherGrenadeSupplier = grenadeSupplier;
        this.hipVelocity = hipVelocity;
        this.hipInaccuracy = hipInaccuracy;
        this.scopedVelocity = scopedVelocity;
        this.scopedInaccuracy = scopedInaccuracy;
        this.launcherCooldownTicks = cooldownTicks;
        this.grenadeDamage = grenadeDamage;

        this.ammoSupplier = null;
        this.pelletFactory = null;
        this.velocity = 0.0f;
        this.inaccuracy = 0.0f;
        this.cooldownTicks = 0;
        this.damagePerPellet = 0.0f;
        this.pelletsPerShot = 1;
        this.maxDistance = 0;
        this.magCapacity = 1;
        this.automatic = false;
        this.adsPitchOffsetNonSniper = -8.0f;
        this.thrownGrenadeSupplier = null;
    }

    public GunItem(Properties properties, GrenadeSupplier grenadeSupplier, float grenadeDamage) {
        super(properties);
        this.isLauncher = false;
        this.isThrownGrenade = true;
        this.thrownGrenadeSupplier = grenadeSupplier;
        this.grenadeDamage = grenadeDamage;

        this.ammoSupplier = null;
        this.pelletFactory = null;
        this.velocity = 0.0f;
        this.inaccuracy = 0.0f;
        this.cooldownTicks = 0;
        this.damagePerPellet = 0.0f;
        this.pelletsPerShot = 1;
        this.maxDistance = 0;
        this.magCapacity = 1;
        this.automatic = false;
        this.adsPitchOffsetNonSniper = -8.0f;
        this.launcherGrenadeItemSupplier = null;
        this.launcherGrenadeSupplier = null;
        this.hipVelocity = 0.0f;
        this.hipInaccuracy = 0.0f;
        this.scopedVelocity = 0.0f;
        this.scopedInaccuracy = 0.0f;
        this.launcherCooldownTicks = 0;
    }

    public boolean isLauncher() {
        return this.isLauncher;
    }

    public boolean isThrownGrenade() {
        return this.isThrownGrenade;
    }

    @Override
    public @Nonnull UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    public boolean isTwoHandedAnim() {
        if (this.isThrownGrenade()) return false;

        String name = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(this).getPath();
        return name.contains("rifle")
                || name.contains("sniper")
                || name.contains("shotgun")
                || name.contains("launcher")
                || name.contains("pistol");
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    private static CompoundTag tag(ItemStack stack) {
        return stack.getOrCreateTag();
    }

    private static int getAmmo(ItemStack stack) {
        return tag(stack).getInt(TAG_AMMO);
    }

    private static void setAmmo(ItemStack stack, int value) {
        tag(stack).putInt(TAG_AMMO, Math.max(0, value));
    }

    private static void ensureInitialized(ItemStack stack) {
        CompoundTag tag = tag(stack);
        if (!tag.contains(TAG_AMMO)) setAmmo(stack, 0);
        if (!tag.contains(TAG_SCOPED)) tag.putBoolean(TAG_SCOPED, false);
    }

    public void fillToMax(ItemStack stack) {
        if (isLauncher || isThrownGrenade) return;
        ensureInitialized(stack);
        setAmmo(stack, this.magCapacity);
    }

    public void setMobReserveAmmo(ItemStack stack, int reserve) {
        if (isLauncher || isThrownGrenade) return;
        ensureInitialized(stack);
        tag(stack).putInt(TAG_RESERVE_AMMO, Math.max(0, reserve));
    }

    public int getMobReserveAmmo(ItemStack stack) {
        ensureInitialized(stack);
        return tag(stack).getInt(TAG_RESERVE_AMMO);
    }

    public void clearMobReserveAmmo(ItemStack stack) {
        tag(stack).remove(TAG_RESERVE_AMMO);
    }

    public void setMobReserveGrenades(ItemStack launcherStack, int reserve) {
        if (!isLauncher) return;
        tag(launcherStack).putInt(TAG_RESERVE_GRENADES, Math.max(0, reserve));
    }

    public int getMobReserveGrenades(ItemStack launcherStack) {
        return tag(launcherStack).getInt(TAG_RESERVE_GRENADES);
    }

    public void clearMobReserveGrenades(ItemStack launcherStack) {
        tag(launcherStack).remove(TAG_RESERVE_GRENADES);
    }

    public int getAmmoPublic(ItemStack stack) {
        ensureInitialized(stack);
        return getAmmo(stack);
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        ItemStack otherStack = (hand == InteractionHand.MAIN_HAND) ? player.getOffhandItem() : player.getMainHandItem();

        if (!otherStack.isEmpty()) {
            if (!(otherStack.getItem() instanceof GunItem)) {
                return InteractionResultHolder.fail(stack);
            }

            if (!this.isThrownGrenade) {
                if (otherStack.getItem() instanceof GunItem otherGun && !otherGun.isThrownGrenade) {
                    return InteractionResultHolder.fail(stack);
                }
            }
        }

        player.startUsingItem(hand);

        if (this.isThrownGrenade) {
            player.swing(hand, true);
            return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingTicks) {
        if (entity instanceof Player player) {
            ItemStack other = (player.getUsedItemHand() == InteractionHand.MAIN_HAND) ? player.getOffhandItem() : player.getMainHandItem();

            if (!other.isEmpty()) {
                if (!(other.getItem() instanceof GunItem) || (!this.isThrownGrenade && other.getItem() instanceof GunItem g && !g.isThrownGrenade)) {
                    player.stopUsingItem();
                }
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        super.releaseUsing(stack, level, entity, timeLeft);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if (!isLauncher && !isThrownGrenade) {
            ensureInitialized(stack);
        }
        super.inventoryTick(stack, level, entity, slot, selected);
    }

    private static Entity resolveRealOwner(Level level, Player shooter) {
        if (!(shooter instanceof net.minecraftforge.common.util.FakePlayer)) return shooter;

        CompoundTag persistentData = shooter.getPersistentData();
        if (!persistentData.hasUUID(TAG_REAL_OWNER)) return shooter;

        UUID real = persistentData.getUUID(TAG_REAL_OWNER);
        if (level instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(real);
            if (entity != null && entity.isAlive() && !entity.isRemoved()) return entity;
        }
        return shooter;
    }

    public void serverFire(Player player, ItemStack stack, boolean ads) {
        if (player.level().isClientSide) return;

        InteractionHand hand = (player.getMainHandItem() == stack) ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
        ItemStack otherStack = (hand == InteractionHand.MAIN_HAND) ? player.getOffhandItem() : player.getMainHandItem();

        if (!otherStack.isEmpty()) {
            if (!(otherStack.getItem() instanceof GunItem)) {
                return;
            }

            if (!this.isThrownGrenade) {
                if (otherStack.getItem() instanceof GunItem otherGun && !otherGun.isThrownGrenade) {
                    return;
                }
            }
        }

        if (player.getCooldowns().isOnCooldown(this)) return;

        Level level = player.level();
        boolean isFake = player instanceof net.minecraftforge.common.util.FakePlayer;

        if (isThrownGrenade) {
            throwHandGrenade(stack, player, level, isFake);
            return;
        }

        if (isLauncher) {
            fireLauncher(player, level, stack, ads, isFake);
            return;
        }

        ensureInitialized(stack);
        firePellets(player, level, stack, ads);
    }

    public void serverFire(Player player, ItemStack stack) {
        serverFire(player, stack, false);
    }

    private boolean throwHandGrenade(ItemStack stack, Player player, Level level, boolean isFake) {
        if (thrownGrenadeSupplier == null) return false;

        BaseGrenadeEntity grenade = this.thrownGrenadeSupplier.get(level, player, this.grenadeDamage);
        grenade.setOwner(resolveRealOwner(level, player));

        grenade.setItem(stack);
        grenade.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2.0F, 0.7F);
        level.addFreshEntity(grenade);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEventsRegistry.GRENADE_PIN.get(),
                player.getSoundSource(),
                1.0F,
                1.0F
        );

        player.awardStat(Stats.ITEM_USED.get(this));

        if (!isFake && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        player.getCooldowns().addCooldown(this, 10);
        return true;
    }

    private boolean fireLauncher(Player player, Level level, ItemStack launcherStack, boolean ads, boolean isFake) {
        if (launcherGrenadeSupplier == null || launcherGrenadeItemSupplier == null) return false;

        Item grenadeItem = launcherGrenadeItemSupplier.get();
        ItemStack firedGrenadeStack = ItemStack.EMPTY;
        boolean isCreative = player.getAbilities().instabuild;

        if (!isFake) {
            firedGrenadeStack = takeLauncherGrenadeFromInventory(player);
        }

        if (firedGrenadeStack.isEmpty() && isCreative) {
            if (player.getInventory().contains(new ItemStack(grenadeItem))) {
                firedGrenadeStack = new ItemStack(grenadeItem, 1);
            }
        }

        if (firedGrenadeStack.isEmpty() && !isCreative) {
            CompoundTag tag = tag(launcherStack);
            if (tag.contains(TAG_RESERVE_GRENADES)) {
                int reserve = tag.getInt(TAG_RESERVE_GRENADES);
                if (reserve > 0) {
                    firedGrenadeStack = new ItemStack(grenadeItem, 1);
                    if (reserve != Integer.MAX_VALUE) {
                        tag.putInt(TAG_RESERVE_GRENADES, reserve - 1);
                    }
                }
            }
        }

        if (firedGrenadeStack.isEmpty()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEventsRegistry.EMPTY_GUN_SHOOT.get(),
                    SoundSource.PLAYERS,
                    0.8F,
                    1.0F
            );
            player.getCooldowns().addCooldown(this, 4);
            return true;
        }

        BaseGrenadeEntity grenade = launcherGrenadeSupplier.get(level, player, this.grenadeDamage);
        grenade.setItem(firedGrenadeStack);
        grenade.setOwner(resolveRealOwner(level, player));

        float velocity = ads ? scopedVelocity : hipVelocity;
        float inaccuracy = ads ? scopedInaccuracy : hipInaccuracy;
        float pitchOffset = ads ? -3.2F : 0.0F;

        grenade.shootFromRotation(player, player.getXRot() + pitchOffset, player.getYRot(), 0.0F, velocity, inaccuracy);
        level.addFreshEntity(grenade);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEventsRegistry.GRENADE_PIN.get(),
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );

        player.getCooldowns().addCooldown(this, launcherCooldownTicks);
        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }

    private ItemStack takeLauncherGrenadeFromInventory(Player player) {
        if (launcherGrenadeItemSupplier == null) return ItemStack.EMPTY;
        Item grenadeItem = launcherGrenadeItemSupplier.get();

        int slotFound = -1;
        ItemStack foundStack = ItemStack.EMPTY;

        for (int i = 0; i < player.getInventory().items.size(); i++) {
            ItemStack inventoryStack = player.getInventory().items.get(i);
            if (!inventoryStack.isEmpty() && inventoryStack.getItem() == grenadeItem) {
                slotFound = i;
                foundStack = inventoryStack;
                break;
            }
        }

        if (slotFound == -1) {
            return ItemStack.EMPTY;
        }

        ItemStack fired = foundStack.copy();
        fired.setCount(1);

        if (!player.getAbilities().instabuild) {
            foundStack.shrink(1);
            if (foundStack.getCount() <= 0) {
                player.getInventory().items.set(slotFound, ItemStack.EMPTY);
            }
        }

        return fired;
    }

    private boolean tryAutoReloadForMob(Player player, Level level, ItemStack stack) {
        if (!(player instanceof net.minecraftforge.common.util.FakePlayer)) return false;

        CompoundTag tag = tag(stack);
        if (!tag.contains(TAG_RESERVE_AMMO)) return false;

        int reserve = tag.getInt(TAG_RESERVE_AMMO);
        if (reserve <= 0) return false;

        int load = Math.min(this.magCapacity, reserve);
        if (load <= 0) return false;

        setAmmo(stack, load);

        if (reserve != Integer.MAX_VALUE) {
            tag.putInt(TAG_RESERVE_AMMO, reserve - load);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEventsRegistry.RELOAD_GUN.get(),
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );

        player.getCooldowns().addCooldown(this, MOB_RELOAD_TICKS);
        return true;
    }

    private boolean firePellets(Player player, Level level, ItemStack stack, boolean ads) {
        if (ammoSupplier == null || pelletFactory == null) return false;

        int ammo = getAmmo(stack);
        boolean isCreative = player.getAbilities().instabuild;
        boolean hasAmmoInInventory = player.getInventory().contains(new ItemStack(ammoSupplier.get()));

        if (ammo <= 0 && (!isCreative || !hasAmmoInInventory)) {
            boolean reloaded = tryAutoReloadForMob(player, level, stack);
            if (reloaded) return true;

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEventsRegistry.EMPTY_GUN_SHOOT.get(), SoundSource.PLAYERS, 0.8F, 1.0F);

            player.getCooldowns().addCooldown(this, 4);
            return false;
        }

        int pelletsToShoot = isCreative ? this.pelletsPerShot : Math.min(this.pelletsPerShot, ammo);
        Entity realOwner = resolveRealOwner(level, player);

        Vec3 look = player.getLookAngle();
        Vec3 upDir = new Vec3(0, 1, 0);
        Vec3 rightDir = look.cross(upDir).normalize();
        if (rightDir.lengthSqr() < 0.001) {
            rightDir = new Vec3(1, 0, 0);
        }
        upDir = rightDir.cross(look).normalize();

        boolean isShotgun = this.pelletsPerShot > 1;
        boolean isSniper = !isShotgun && this.damagePerPellet >= 7.5f;
        float pitchOffset = isSniper ? 0.0F : (ads ? this.adsPitchOffsetNonSniper : 0.0F);

        for (int i = 0; i < pelletsToShoot; i++) {
            BasePelletEntity pellet = pelletFactory.create(level, player);
            pellet.setOwner(realOwner);
            pellet.setPos(player.getX(), player.getEyeY() - 0.1D, player.getZ());

            double angle = level.random.nextDouble() * Math.PI * 2;
            double spreadRadius = Math.sqrt(level.random.nextDouble()) * (this.inaccuracy * 0.05);
            if (!isShotgun) spreadRadius *= 0.2;

            double xOff = Math.cos(angle) * spreadRadius;
            double yOff = Math.sin(angle) * spreadRadius;

            Vec3 direction = look.add(rightDir.scale(xOff))
                    .add(upDir.scale(yOff + (pitchOffset * -0.015)))
                    .normalize()
                    .scale(velocity);

            pellet.setDeltaMovement(direction);

            double horizontal = direction.horizontalDistance();
            pellet.setYRot((float) (Mth.atan2(direction.x, direction.z) * (180F / Math.PI)));
            pellet.setXRot((float) (Mth.atan2(direction.y, horizontal) * (180F / Math.PI)));

            pellet.setDamage(this.damagePerPellet);
            level.addFreshEntity(pellet);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEventsRegistry.GUN_SHOOT.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

        if (!isCreative) setAmmo(stack, ammo - pelletsToShoot);

        player.getCooldowns().addCooldown(this, cooldownTicks);
        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }

    public void manualReload(Player player) {
        if (isLauncher || isThrownGrenade) return;
        if (player.level().isClientSide) return;
        if (player.getAbilities().instabuild) return;

        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        ItemStack stack = ItemStack.EMPTY;

        if (main.getItem() == this) {
            stack = main;
        } else if (off.getItem() == this) {
            stack = off;
        }

        if (stack.isEmpty()) return;

        ensureInitialized(stack);

        int current = getAmmo(stack);
        if (current >= this.magCapacity) return;

        int need = this.magCapacity - current;
        int moved = moveAmmoFromInventory(player, need);
        if (moved <= 0) return;

        setAmmo(stack, current + moved);

        player.level().playSound(
                null,
                player.getX(), player.getY(), player.getZ(),
                SoundEventsRegistry.RELOAD_GUN.get(),
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );
    }

    private int moveAmmoFromInventory(Player player, int amount) {
        if (ammoSupplier == null) return 0;
        Item ammo = ammoSupplier.get();
        int toTake = amount;

        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() == ammo) {
                int remove = Math.min(stack.getCount(), toTake);
                if (remove > 0) {
                    stack.shrink(remove);
                    toTake -= remove;
                    if (toTake <= 0) break;
                }
            }
        }

        return amount - toTake;
    }

    public float getDamagePerPellet() {
        return damagePerPellet;
    }

    public int getPelletsPerShot() {
        return pelletsPerShot;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public boolean isLauncherWeapon() {
        return isLauncher;
    }

    public boolean isThrownGrenadeWeapon() {
        return isThrownGrenade;
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    public int getMagCapacity() {
        return magCapacity;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public float getGrenadeDamage() {
        return grenadeDamage;
    }
}
