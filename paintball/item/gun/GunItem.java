package com.zerokg2004.paintball.item.gun;

import com.zerokg2004.paintball.entity.projectile.BaseGrenadeEntity;
import com.zerokg2004.paintball.entity.projectile.BasePelletEntity;
import com.zerokg2004.paintball.registry.SoundEventsRegistry;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;
import java.util.Map;
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

    private static final Map<UUID, Boolean> CLIENT_SCOPED = new HashMap<>();

    private static final Map<UUID, Float> CLIENT_ZOOM = new HashMap<>();

    private static final Map<UUID, Float> CLIENT_LAUNCHER_ZOOM = new HashMap<>();

    private static final Map<UUID, Float> CLIENT_ZOOM_PREV = new HashMap<>();
    private static final Map<UUID, Float> CLIENT_LAUNCHER_ZOOM_PREV = new HashMap<>();

    private static boolean getClientScoped(Player p) {
        return CLIENT_SCOPED.getOrDefault(p.getUUID(), false);
    }

    private static void setClientScoped(Player p, boolean v) {
        if (v) CLIENT_SCOPED.put(p.getUUID(), true);
        else CLIENT_SCOPED.remove(p.getUUID());
    }

    private static float clamp01(float v) {
        return Math.max(0.0f, Math.min(1.0f, v));
    }

    private static float getClientZoom(Player p) {
        return CLIENT_ZOOM.getOrDefault(p.getUUID(), 0.0f);
    }

    private static void setClientZoom(Player p, float v) {
        v = clamp01(v);
        if (v <= 0.0001f) CLIENT_ZOOM.remove(p.getUUID());
        else CLIENT_ZOOM.put(p.getUUID(), v);
    }

    private static float getClientLauncherZoom(Player p) {
        return CLIENT_LAUNCHER_ZOOM.getOrDefault(p.getUUID(), 0.0f);
    }

    private static void setClientLauncherZoom(Player p, float v) {
        v = clamp01(v);
        if (v <= 0.0001f) CLIENT_LAUNCHER_ZOOM.remove(p.getUUID());
        else CLIENT_LAUNCHER_ZOOM.put(p.getUUID(), v);
    }

    private static float getClientZoomPrev(Player p) {
        return CLIENT_ZOOM_PREV.getOrDefault(p.getUUID(), 0.0f);
    }

    private static void setClientZoomPrev(Player p, float v) {
        v = clamp01(v);
        if (v <= 0.0001f) CLIENT_ZOOM_PREV.remove(p.getUUID());
        else CLIENT_ZOOM_PREV.put(p.getUUID(), v);
    }

    private static float getClientLauncherZoomPrev(Player p) {
        return CLIENT_LAUNCHER_ZOOM_PREV.getOrDefault(p.getUUID(), 0.0f);
    }

    private static void setClientLauncherZoomPrev(Player p, float v) {
        v = clamp01(v);
        if (v <= 0.0001f) CLIENT_LAUNCHER_ZOOM_PREV.remove(p.getUUID());
        else CLIENT_LAUNCHER_ZOOM_PREV.put(p.getUUID(), v);
    }

    public static float getZoom01Client(Player p) {
        return getClientZoom(p);
    }

    public static float getLauncherZoom01Client(Player p) {
        return getClientLauncherZoom(p);
    }

    public static void setGunZoom01Client(Player p, float v) {
        setClientZoom(p, v);
    }

    public static void setLauncherZoom01Client(Player p, float v) {
        setClientLauncherZoom(p, v);
    }

    public static void resetFired() {
    }

    public static void capturePrevZoom(LocalPlayer player) {
        if (player == null) return;

        setClientZoomPrev(player, getClientZoom(player));
        setClientLauncherZoomPrev(player, getClientLauncherZoom(player));
    }

    public static float getLauncherZoom01ClientPrev(LocalPlayer player) {
        if (player == null) return 0.0f;
        return getClientLauncherZoomPrev(player);
    }

    public static float getZoom01ClientPrev(LocalPlayer player) {
        if (player == null) return 0.0f;
        return getClientZoomPrev(player);
    }

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

    private static boolean launcherZoom = false;

    private final GrenadeSupplier thrownGrenadeSupplier;

    public GunItem(Properties props, Supplier<? extends Item> ammoSupplier, PelletFactory pelletFactory) {
        this(props, ammoSupplier, pelletFactory,
                3.0f, 1.0f, 10,
                4.0f, 1, 30,
                10, false, -8.0f);
    }

    public GunItem(Properties props,
                   Supplier<? extends Item> ammoSupplier,
                   PelletFactory pelletFactory,
                   float velocity,
                   float inaccuracy,
                   int cooldownTicks,
                   float damagePerPellet,
                   int pelletsPerShot,
                   int maxDistance) {
        this(props, ammoSupplier, pelletFactory,
                velocity, inaccuracy, cooldownTicks,
                damagePerPellet, pelletsPerShot, maxDistance,
                10, false, -8.0f);
    }

    public GunItem(Properties props,
                   Supplier<? extends Item> ammoSupplier,
                   PelletFactory pelletFactory,
                   float velocity,
                   float inaccuracy,
                   int cooldownTicks,
                   float damagePerPellet,
                   int pelletsPerShot,
                   int maxDistance,
                   int magCapacity,
                   boolean automatic,
                   float adsPitchOffsetNonSniper) {
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
        this.adsPitchOffsetNonSniper = adsPitchOffsetNonSniper;

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

    public GunItem(Properties props,
                   Supplier<? extends Item> ammoSupplier,
                   PelletFactory pelletFactory,
                   float velocity,
                   float inaccuracy,
                   int cooldownTicks,
                   float damagePerPellet,
                   int pelletsPerShot,
                   int maxDistance,
                   float adsPitchOffsetNonSniper) {
        this(props, ammoSupplier, pelletFactory,
                velocity, inaccuracy, cooldownTicks,
                damagePerPellet, pelletsPerShot, maxDistance,
                10, false, adsPitchOffsetNonSniper);
    }

    public GunItem(Properties properties,
                   Supplier<? extends Item> grenadeItemSupplier,
                   GrenadeSupplier grenadeSupplier) {
        this(properties, grenadeItemSupplier, grenadeSupplier,
                4.5f, 0.01f, 6.7f, 0.0f, 20,
                15.0F);
    }

    public GunItem(Properties properties,
                   Supplier<? extends Item> grenadeItemSupplier,
                   GrenadeSupplier grenadeSupplier,
                   float hipVelocity, float hipInaccuracy,
                   float scopedVelocity, float scopedInaccuracy,
                   int cooldownTicks) {
        this(properties, grenadeItemSupplier, grenadeSupplier,
                hipVelocity, hipInaccuracy,
                scopedVelocity, scopedInaccuracy,
                cooldownTicks,
                15.0F);
    }

    public GunItem(Properties properties,
                   Supplier<? extends Item> grenadeItemSupplier,
                   GrenadeSupplier grenadeSupplier,
                   float hipVelocity, float hipInaccuracy,
                   float scopedVelocity, float scopedInaccuracy,
                   int cooldownTicks,
                   float grenadeDamage) {
        super(properties);
        this.isLauncher = true;
        this.isThrownGrenade = false;

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

        this.launcherGrenadeItemSupplier = grenadeItemSupplier;
        this.launcherGrenadeSupplier = grenadeSupplier;
        this.hipVelocity = hipVelocity;
        this.hipInaccuracy = hipInaccuracy;
        this.scopedVelocity = scopedVelocity;
        this.scopedInaccuracy = scopedInaccuracy;
        this.launcherCooldownTicks = cooldownTicks;

        this.thrownGrenadeSupplier = null;
        this.grenadeDamage = grenadeDamage;
    }

    public GunItem(Properties properties, GrenadeSupplier grenadeSupplier) {
        this(properties, grenadeSupplier, 15.0F);
    }

    public GunItem(Properties properties, GrenadeSupplier grenadeSupplier, float grenadeDamage) {
        super(properties);
        this.isLauncher = false;
        this.isThrownGrenade = true;

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

        this.thrownGrenadeSupplier = grenadeSupplier;
        this.grenadeDamage = grenadeDamage;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return (!isLauncher && !isThrownGrenade) ? UseAnim.BOW : UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return (!isLauncher && !isThrownGrenade) ? 72000 : 0;
    }

    public static boolean isBlockedTargetClient(Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.hitResult == null) return false;

        HitResult hr = mc.hitResult;

        if (hr.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) hr;
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(bhr.getBlockPos()).getBlock());
            if (id == null) return false;

            String p = id.getPath();
            return p.endsWith("_gear_rack")
                    || p.endsWith("_weapon_rack")
                    || p.endsWith("_pod")
                    || p.endsWith("_medkit");
        }

        if (hr.getType() == HitResult.Type.ENTITY) {
            EntityHitResult ehr = (EntityHitResult) hr;
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(ehr.getEntity().getType());
            if (id == null) return false;

            String p = id.getPath();
            return p.equals("item_frame") || p.equals("glow_item_frame");
        }

        return false;
    }

    public static boolean isInteractableTargetClient() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || mc.hitResult == null) return false;

        HitResult hr = mc.hitResult;

        if (hr.getType() == HitResult.Type.ENTITY) {
            return false;
        }

        if (hr.getType() == HitResult.Type.BLOCK) {
            BlockHitResult bhr = (BlockHitResult) hr;
            var pos = bhr.getBlockPos();
            var level = mc.level;
            var state = level.getBlockState(pos);

            if (state.getMenuProvider(level, pos) != null) return true;
            if (state.hasBlockEntity()) return true;

            var b = state.getBlock();
            return b instanceof net.minecraft.world.level.block.DoorBlock
                    || b instanceof net.minecraft.world.level.block.TrapDoorBlock
                    || b instanceof net.minecraft.world.level.block.ButtonBlock
                    || b instanceof net.minecraft.world.level.block.LeverBlock
                    || b instanceof net.minecraft.world.level.block.FenceGateBlock
                    || b instanceof net.minecraft.world.level.block.BedBlock
                    || b instanceof net.minecraft.world.level.block.NoteBlock
                    || b instanceof net.minecraft.world.level.block.RepeaterBlock
                    || b instanceof net.minecraft.world.level.block.ComparatorBlock;
        }

        return false;
    }

    public static void clientTickBlockLeftClickMiningWithGun() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;

        ItemStack held = mc.player.getMainHandItem();
        if (!(held.getItem() instanceof GunItem)) return;

        if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.BLOCK) {
            mc.options.keyAttack.setDown(false);
        }
    }

    public static void clientTickSmoothZoom() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;

        Player p = mc.player;
        ItemStack held = p.getMainHandItem();
        if (!(held.getItem() instanceof GunItem gun)) return;
        if (gun.isLauncherWeapon() || gun.isThrownGrenadeWeapon()) return;
    }

    private static CompoundTag tag(ItemStack stack) {
        return stack.getOrCreateTag();
    }

    private static int getAmmo(ItemStack stack) {
        return tag(stack).getInt(TAG_AMMO);
    }

    private static void setAmmo(ItemStack stack, int v) {
        tag(stack).putInt(TAG_AMMO, Math.max(0, v));
    }

    private static void ensureInitialized(ItemStack stack) {
        CompoundTag t = tag(stack);
        if (!t.contains(TAG_AMMO)) setAmmo(stack, 0);
        if (!t.contains(TAG_SCOPED)) t.putBoolean(TAG_SCOPED, false);
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

    public boolean isScoped() {
        if (isLauncher || isThrownGrenade) return false;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return false;

        ItemStack held = mc.player.getMainHandItem();
        if (held.isEmpty() || held.getItem() != this) return false;

        return getClientScoped(mc.player);
    }

    public void setScoped(boolean value) {
        if (isLauncher || isThrownGrenade) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;

        ItemStack held = mc.player.getMainHandItem();
        if (held.isEmpty() || held.getItem() != this) return;

        setClientScoped(mc.player, value);
    }

    @Override
    public @Nonnull InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            if (isInteractableTargetClient() || isBlockedTargetClient(level)) {
                return InteractionResultHolder.pass(stack);
            }
        }

        if (isLauncher || isThrownGrenade) return InteractionResultHolder.consume(stack);

        ensureInitialized(stack);
        return InteractionResultHolder.consume(stack);
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

        CompoundTag pd = shooter.getPersistentData();
        if (!pd.hasUUID(TAG_REAL_OWNER)) return shooter;

        UUID real = pd.getUUID(TAG_REAL_OWNER);
        if (level instanceof ServerLevel sl) {
            Entity e = sl.getEntity(real);
            if (e != null && e.isAlive() && !e.isRemoved()) return e;
        }
        return shooter;
    }

    public void serverFire(Player player, ItemStack stack, boolean ads) {
        if (player.level().isClientSide) return;
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

        if (!isFake) {
            firedGrenadeStack = takeLauncherGrenadeFromInventory(player);
        }

        if (firedGrenadeStack.isEmpty()) {
            CompoundTag t = tag(launcherStack);
            if (t.contains(TAG_RESERVE_GRENADES)) {
                int reserve = t.getInt(TAG_RESERVE_GRENADES);
                if (reserve > 0) {
                    firedGrenadeStack = new ItemStack(grenadeItem, 1);
                    if (reserve != Integer.MAX_VALUE) {
                        t.putInt(TAG_RESERVE_GRENADES, reserve - 1);
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

        boolean scoped = ads;

        float vel = scoped ? scopedVelocity : hipVelocity;
        float inac = scoped ? scopedInaccuracy : hipInaccuracy;
        float pitchOffset = scoped ? -3.2F : 0.0F;

        grenade.shootFromRotation(player,
                player.getXRot() + pitchOffset,
                player.getYRot(),
                0.0F,
                vel,
                inac
        );

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
            ItemStack invStack = player.getInventory().items.get(i);
            if (!invStack.isEmpty() && invStack.getItem() == grenadeItem) {
                slotFound = i;
                foundStack = invStack;
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

        CompoundTag t = tag(stack);
        if (!t.contains(TAG_RESERVE_AMMO)) return false;

        int reserve = t.getInt(TAG_RESERVE_AMMO);
        if (reserve <= 0) return false;

        int load = Math.min(this.magCapacity, reserve);
        if (load <= 0) return false;

        setAmmo(stack, load);

        if (reserve != Integer.MAX_VALUE) {
            t.putInt(TAG_RESERVE_AMMO, reserve - load);
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

        if (ammo <= 0) {
            boolean reloaded = tryAutoReloadForMob(player, level, stack);
            if (reloaded) return true;

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEventsRegistry.EMPTY_GUN_SHOOT.get(),
                    SoundSource.PLAYERS,
                    0.8F,
                    1.0F
            );

            player.getCooldowns().addCooldown(this, 4);
            return false;
        }

        int pelletsToShoot = Math.min(this.pelletsPerShot, ammo);
        Entity realOwner = resolveRealOwner(level, player);

        for (int i = 0; i < pelletsToShoot; i++) {
            BasePelletEntity pellet = pelletFactory.create(level, player);
            pellet.setPos(player.getX(), player.getEyeY() - 0.1D, player.getZ());
            pellet.setOwner(realOwner);

            float extraYaw = 0.0F;
            if (this.pelletsPerShot > 1) {
                extraYaw = (level.random.nextFloat() - 0.5F) * 12.0F;
            }

            boolean isShotgun = (this.pelletsPerShot > 1);
            boolean isSniper = (!isShotgun && this.damagePerPellet >= 7.5f);

            float pitchOffset = isSniper ? 0.0F : (ads ? this.adsPitchOffsetNonSniper : 0.0F);

            pellet.shootFromRotation(
                    player,
                    player.getXRot() + pitchOffset,
                    player.getYRot() + extraYaw,
                    0.0F,
                    velocity,
                    inaccuracy
            );

            pellet.setDamage(this.damagePerPellet);
            level.addFreshEntity(pellet);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEventsRegistry.GUN_SHOOT.get(),
                SoundSource.PLAYERS,
                1.0F,
                1.0F
        );

        setAmmo(stack, ammo - pelletsToShoot);

        player.getCooldowns().addCooldown(this, cooldownTicks);
        player.awardStat(Stats.ITEM_USED.get(this));
        return true;
    }

    public static void clientTickAutoFireUsing(boolean leftHeld) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;
        if (CLIENT_FIRE_HANDLER == null) return;
        if (!leftHeld) return;

        if (isInteractableTargetClient()) return;
        if (mc.level != null && isBlockedTargetClient(mc.level)) return;

        ItemStack held = mc.player.getMainHandItem();
        if (held.isEmpty() || !(held.getItem() instanceof GunItem gun)) return;
        if (!gun.automatic) return;

        if (mc.player.getCooldowns().isOnCooldown(held.getItem())) return;

        boolean firstPerson = mc.options.getCameraType().isFirstPerson();
        boolean ads;

        if (gun.isLauncher) {
            ads = GunItem.isLauncherZoomed();
        } else {
            ads = firstPerson && mc.options.keyUse.isDown();
        }

        CLIENT_FIRE_HANDLER.sendFirePacket(InteractionHand.MAIN_HAND, ads);
    }

    public static void clientTickLauncherZoom() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) {
            launcherZoom = false;
            return;
        }

        if (isInteractableTargetClient()) {
            launcherZoom = false;
            return;
        }

        if (isBlockedTargetClient(mc.level)) {
            launcherZoom = false;
            return;
        }

        boolean rightDown = mc.options.keyUse.isDown();
        boolean firstPerson = mc.options.getCameraType().isFirstPerson();

        ItemStack held = mc.player.getMainHandItem();
        boolean holdingLauncher = !held.isEmpty()
                && held.getItem() instanceof GunItem gun
                && gun.isLauncher;

        launcherZoom = firstPerson && holdingLauncher && rightDown;
    }

    public static boolean isLauncherZoomed() {
        return launcherZoom;
    }

    public static void setLauncherZoom(boolean zoomed) {
        launcherZoom = zoomed;
    }

    public void manualReload(Player player) {
        if (isLauncher || isThrownGrenade) return;
        if (player.level().isClientSide) return;

        ItemStack stack = player.getMainHandItem();
        ensureInitialized(stack);
        if (stack.isEmpty() || stack.getItem() != this) return;

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

        for (ItemStack s : player.getInventory().items) {
            if (s.getItem() == ammo) {
                int remove = Math.min(s.getCount(), toTake);
                if (remove > 0) {
                    s.shrink(remove);
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