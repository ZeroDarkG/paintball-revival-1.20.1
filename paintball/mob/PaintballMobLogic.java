package com.zerokg2004.paintball.mob;

import com.mojang.authlib.GameProfile;
import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.item.gun.GunItem;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.common.util.FakePlayerFactory;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.UUID;

public final class PaintballMobLogic {

    private static final String TAG_TEAM = "PaintballTeamColor";
    private static final String TAG_AI = "PaintballAIInjected";

    private static final DyeColor[] PAINTBALL_COLORS = new DyeColor[]{
            DyeColor.RED, DyeColor.BLUE, DyeColor.GREEN,
            DyeColor.YELLOW, DyeColor.ORANGE, DyeColor.PURPLE
    };

    private static final int SHOOT_TRY_INTERVAL_TICKS = 2;

    public static final double PLAYER_TARGET_RANGE = 48.0D;
    private static final int FOCUS_PLAYER_INTERVAL_TICKS = 10;

    private PaintballMobLogic() {}

    private static DyeColor randomPaintballColor(RandomSource r) {
        return PAINTBALL_COLORS[r.nextInt(PAINTBALL_COLORS.length)];
    }

    private static void setTeam(Mob mob, DyeColor color) {
        mob.getPersistentData().putString(TAG_TEAM, color.getName());
    }

    public static DyeColor getTeam(Mob mob) {
        String s = mob.getPersistentData().getString(TAG_TEAM);
        if (s == null || s.isEmpty()) return null;
        for (DyeColor c : DyeColor.values()) if (c.getName().equals(s)) return c;
        return null;
    }

    public static boolean isPaintballMob(Mob mob) {
        return mob != null && getTeam(mob) != null;
    }

    public static void makePaintballMob(Mob mob, Difficulty diff) {
        DyeColor color = randomPaintballColor(mob.getRandom());
        setTeam(mob, color);

        equipLoadout(mob, color, diff);
        ensurePaintballAI(mob);
    }

    public static void makePaintballMobWithFixedTeam(Mob mob, Difficulty diff, DyeColor team) {
        setTeam(mob, team);
        equipLoadout(mob, team, diff);
        ensurePaintballAI(mob);
    }

    public static void ensurePaintballAI(Mob mob) {
        if (!isPaintballMob(mob)) return;
        if (!mob.isAlive() || mob.isRemoved()) return;

        if (mob.getPersistentData().getBoolean(TAG_AI)) return;

        mob.getPersistentData().putBoolean(TAG_AI, true);

        var srv = mob.level().getServer();
        if (srv != null) {
            srv.execute(() -> applyPaintballAIUnsafe(mob));
        } else {
            applyPaintballAIUnsafe(mob);
        }
    }

    public static void forceRetargetNearestPlayer(Mob mob) {
        if (mob == null || mob.level().isClientSide) return;
        if (!mob.isAlive() || mob.isRemoved()) return;
        if (!isPaintballMob(mob)) return;
        if (!(mob.level() instanceof ServerLevel level)) return;

        mob.setTarget(null);
        mob.setLastHurtByMob(null);
        mob.setLastHurtByPlayer(null);

        mob.getNavigation().stop();

        TargetingConditions cond = TargetingConditions.forCombat()
                .range(PLAYER_TARGET_RANGE)
                .selector(p -> !p.isSpectator());

        Player nearest = level.getNearestPlayer(cond, mob, mob.getX(), mob.getEyeY(), mob.getZ());
        if (nearest == null) return;

        mob.setTarget(nearest);
        mob.getLookControl().setLookAt(nearest, 30.0F, 30.0F);

        if (mob instanceof AbstractSkeleton sk) sk.setAggressive(true);
    }

    private static void equipLoadout(Mob mob, DyeColor color, Difficulty diff) {
        mob.setCanPickUpLoot(false);

        mob.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        mob.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

        mob.setItemSlot(EquipmentSlot.HEAD, new ItemStack(helmet(color)));
        mob.setItemSlot(EquipmentSlot.CHEST, new ItemStack(chest(color)));
        mob.setItemSlot(EquipmentSlot.LEGS, new ItemStack(legs(color)));
        mob.setItemSlot(EquipmentSlot.FEET, new ItemStack(boots(color)));

        mob.setDropChance(EquipmentSlot.HEAD, 0.02f);
        mob.setDropChance(EquipmentSlot.CHEST, 0.02f);
        mob.setDropChance(EquipmentSlot.LEGS, 0.02f);
        mob.setDropChance(EquipmentSlot.FEET, 0.02f);

        Item weapon = randomWeapon(mob.getRandom(), color, diff);
        ItemStack weaponStack = new ItemStack(weapon);

        if (weaponStack.getItem() instanceof GunItem gun) {
            if (gun.isLauncherWeapon()) {
                gun.setMobReserveGrenades(weaponStack, Integer.MAX_VALUE);
            } else if (gun.isThrownGrenadeWeapon()) {
                weaponStack.setCount(64);
            } else {
                gun.fillToMax(weaponStack);
                gun.setMobReserveAmmo(weaponStack, Integer.MAX_VALUE);
            }
        }

        mob.setItemSlot(EquipmentSlot.MAINHAND, weaponStack);
        mob.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);

        mob.setDropChance(EquipmentSlot.MAINHAND, 0.05f);
        mob.setDropChance(EquipmentSlot.OFFHAND, 0.0f);
    }

    private static Item helmet(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_PAINTBALL_HELMET.get();
            case BLUE -> ModItems.BLUE_PAINTBALL_HELMET.get();
            case GREEN -> ModItems.GREEN_PAINTBALL_HELMET.get();
            case YELLOW -> ModItems.YELLOW_PAINTBALL_HELMET.get();
            case ORANGE -> ModItems.ORANGE_PAINTBALL_HELMET.get();
            case PURPLE -> ModItems.PURPLE_PAINTBALL_HELMET.get();
            default -> ModItems.RED_PAINTBALL_HELMET.get();
        };
    }

    private static Item chest(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_PAINTBALL_CHEST.get();
            case BLUE -> ModItems.BLUE_PAINTBALL_CHEST.get();
            case GREEN -> ModItems.GREEN_PAINTBALL_CHEST.get();
            case YELLOW -> ModItems.YELLOW_PAINTBALL_CHEST.get();
            case ORANGE -> ModItems.ORANGE_PAINTBALL_CHEST.get();
            case PURPLE -> ModItems.PURPLE_PAINTBALL_CHEST.get();
            default -> ModItems.RED_PAINTBALL_CHEST.get();
        };
    }

    private static Item legs(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_PAINTBALL_PANTS.get();
            case BLUE -> ModItems.BLUE_PAINTBALL_PANTS.get();
            case GREEN -> ModItems.GREEN_PAINTBALL_PANTS.get();
            case YELLOW -> ModItems.YELLOW_PAINTBALL_PANTS.get();
            case ORANGE -> ModItems.ORANGE_PAINTBALL_PANTS.get();
            case PURPLE -> ModItems.PURPLE_PAINTBALL_PANTS.get();
            default -> ModItems.RED_PAINTBALL_PANTS.get();
        };
    }

    private static Item boots(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_PAINTBALL_SHOES.get();
            case BLUE -> ModItems.BLUE_PAINTBALL_SHOES.get();
            case GREEN -> ModItems.GREEN_PAINTBALL_SHOES.get();
            case YELLOW -> ModItems.YELLOW_PAINTBALL_SHOES.get();
            case ORANGE -> ModItems.ORANGE_PAINTBALL_SHOES.get();
            case PURPLE -> ModItems.PURPLE_PAINTBALL_SHOES.get();
            default -> ModItems.RED_PAINTBALL_SHOES.get();
        };
    }

    private static Item randomWeapon(RandomSource r, DyeColor c, Difficulty diff) {
        ArrayList<Item> pool = new ArrayList<>();

        if (diff == Difficulty.NORMAL) {
            boolean advanced = r.nextFloat() < 0.50f;
            if (!advanced) addPistolAndShotgun(pool, c);
            else addRifleAndGrenade(pool, c);
        } else {
            addAllSix(pool, c);
        }

        return pool.get(r.nextInt(pool.size()));
    }

    private static void addPistolAndShotgun(ArrayList<Item> pool, DyeColor c) {
        switch (c) {
            case RED -> { pool.add(ModItems.RED_PISTOL.get()); pool.add(ModItems.RED_SHOTGUN.get()); }
            case BLUE -> { pool.add(ModItems.BLUE_PISTOL.get()); pool.add(ModItems.BLUE_SHOTGUN.get()); }
            case GREEN -> { pool.add(ModItems.GREEN_PISTOL.get()); pool.add(ModItems.GREEN_SHOTGUN.get()); }
            case YELLOW -> { pool.add(ModItems.YELLOW_PISTOL.get()); pool.add(ModItems.YELLOW_SHOTGUN.get()); }
            case ORANGE -> { pool.add(ModItems.ORANGE_PISTOL.get()); pool.add(ModItems.ORANGE_SHOTGUN.get()); }
            case PURPLE -> { pool.add(ModItems.PURPLE_PISTOL.get()); pool.add(ModItems.PURPLE_SHOTGUN.get()); }
            default -> pool.add(ModItems.RED_PISTOL.get());
        }
    }

    private static void addRifleAndGrenade(ArrayList<Item> pool, DyeColor c) {
        switch (c) {
            case RED -> { pool.add(ModItems.RED_RIFLE.get()); pool.add(ModItems.RED_GRENADE.get()); }
            case BLUE -> { pool.add(ModItems.BLUE_RIFLE.get()); pool.add(ModItems.BLUE_GRENADE.get()); }
            case GREEN -> { pool.add(ModItems.GREEN_RIFLE.get()); pool.add(ModItems.GREEN_GRENADE.get()); }
            case YELLOW -> { pool.add(ModItems.YELLOW_RIFLE.get()); pool.add(ModItems.YELLOW_GRENADE.get()); }
            case ORANGE -> { pool.add(ModItems.ORANGE_RIFLE.get()); pool.add(ModItems.ORANGE_GRENADE.get()); }
            case PURPLE -> { pool.add(ModItems.PURPLE_RIFLE.get()); pool.add(ModItems.PURPLE_GRENADE.get()); }
            default -> pool.add(ModItems.RED_RIFLE.get());
        }
    }

    private static void addAllSix(ArrayList<Item> pool, DyeColor c) {
        switch (c) {
            case RED -> {
                pool.add(ModItems.RED_PISTOL.get());
                pool.add(ModItems.RED_SHOTGUN.get());
                pool.add(ModItems.RED_RIFLE.get());
                pool.add(ModItems.RED_SNIPER.get());
                pool.add(ModItems.RED_GRENADE.get());
                pool.add(ModItems.RED_LAUNCHER.get());
            }
            case BLUE -> {
                pool.add(ModItems.BLUE_PISTOL.get());
                pool.add(ModItems.BLUE_SHOTGUN.get());
                pool.add(ModItems.BLUE_RIFLE.get());
                pool.add(ModItems.BLUE_SNIPER.get());
                pool.add(ModItems.BLUE_GRENADE.get());
                pool.add(ModItems.BLUE_LAUNCHER.get());
            }
            case GREEN -> {
                pool.add(ModItems.GREEN_PISTOL.get());
                pool.add(ModItems.GREEN_SHOTGUN.get());
                pool.add(ModItems.GREEN_RIFLE.get());
                pool.add(ModItems.GREEN_SNIPER.get());
                pool.add(ModItems.GREEN_GRENADE.get());
                pool.add(ModItems.GREEN_LAUNCHER.get());
            }
            case YELLOW -> {
                pool.add(ModItems.YELLOW_PISTOL.get());
                pool.add(ModItems.YELLOW_SHOTGUN.get());
                pool.add(ModItems.YELLOW_RIFLE.get());
                pool.add(ModItems.YELLOW_SNIPER.get());
                pool.add(ModItems.YELLOW_GRENADE.get());
                pool.add(ModItems.YELLOW_LAUNCHER.get());
            }
            case ORANGE -> {
                pool.add(ModItems.ORANGE_PISTOL.get());
                pool.add(ModItems.ORANGE_SHOTGUN.get());
                pool.add(ModItems.ORANGE_RIFLE.get());
                pool.add(ModItems.ORANGE_SNIPER.get());
                pool.add(ModItems.ORANGE_GRENADE.get());
                pool.add(ModItems.ORANGE_LAUNCHER.get());
            }
            case PURPLE -> {
                pool.add(ModItems.PURPLE_PISTOL.get());
                pool.add(ModItems.PURPLE_SHOTGUN.get());
                pool.add(ModItems.PURPLE_RIFLE.get());
                pool.add(ModItems.PURPLE_SNIPER.get());
                pool.add(ModItems.PURPLE_GRENADE.get());
                pool.add(ModItems.PURPLE_LAUNCHER.get());
            }
            default -> pool.add(ModItems.RED_RIFLE.get());
        }
    }

    private static void applyPaintballAIUnsafe(Mob mob) {
        if (!mob.isAlive() || mob.isRemoved()) return;
        if (!isPaintballMob(mob)) return;

        mob.goalSelector.getAvailableGoals().removeIf(w ->
                w.getGoal() instanceof MeleeAttackGoal
                        || w.getGoal() instanceof RangedBowAttackGoal
                        || w.getGoal() instanceof RangedAttackGoal
                        || w.getGoal() instanceof PaintballShootGoal
                        || w.getGoal() instanceof FocusPlayerGoal
        );

        mob.targetSelector.getAvailableGoals().removeIf(w -> w.getGoal() instanceof TargetGoal);

        if (mob instanceof PathfinderMob pm) {
            boolean hasHurtBy = mob.targetSelector.getAvailableGoals().stream()
                    .anyMatch(w -> w.getGoal() instanceof HurtByTargetGoal);
            if (!hasHurtBy) mob.targetSelector.addGoal(1, new HurtByTargetGoal(pm));
        }

        mob.goalSelector.addGoal(1, new FocusPlayerGoal(mob, PLAYER_TARGET_RANGE, FOCUS_PLAYER_INTERVAL_TICKS));

        mob.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(mob, Player.class, false));

        mob.goalSelector.addGoal(2, new PaintballShootGoal(mob, SHOOT_TRY_INTERVAL_TICKS, 40));

        if (mob instanceof AbstractSkeleton sk) sk.setAggressive(false);
    }

    private static class FocusPlayerGoal extends Goal {
        private final Mob mob;
        private final int interval;
        private int ticker = 0;
        private final TargetingConditions cond;

        FocusPlayerGoal(Mob mob, double range, int intervalTicks) {
            this.mob = mob;
            this.interval = Math.max(1, intervalTicks);
            this.cond = TargetingConditions.forCombat()
                    .range(range)
                    .selector(p -> !p.isSpectator());
        }

        @Override public boolean canUse() { return !mob.level().isClientSide; }
        @Override public boolean canContinueToUse() { return true; }

        @Override
        public void tick() {
            if (!(mob.level() instanceof ServerLevel level)) return;

            ticker++;
            if (ticker < interval) return;
            ticker = 0;

            LivingEntity t = mob.getTarget();

            boolean bad = (t == null) || !t.isAlive() || t.isRemoved() || !(t instanceof Player);
            if (!bad) return;

            mob.setTarget(null);
            mob.setLastHurtByMob(null);
            mob.setLastHurtByPlayer(null);

            Player nearest = level.getNearestPlayer(cond, mob, mob.getX(), mob.getEyeY(), mob.getZ());
            if (nearest == null) return;

            mob.setTarget(nearest);
            mob.getNavigation().stop();
            mob.getLookControl().setLookAt(nearest, 30.0F, 30.0F);
        }
    }

    private static class PaintballShootGoal extends Goal {
        private final Mob mob;
        private final int tryIntervalTicks;
        private final double maxRange;
        private int cooldown = 0;

        PaintballShootGoal(Mob mob, int tryIntervalTicks, double maxRange) {
            this.mob = mob;
            this.tryIntervalTicks = Math.max(1, tryIntervalTicks);
            this.maxRange = maxRange;
            this.setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            if (mob.level().isClientSide) return false;

            LivingEntity t = mob.getTarget();
            if (!(t instanceof Player pl)) return false;
            if (!pl.isAlive() || pl.isRemoved() || pl.isSpectator()) return false;

            return mob.getMainHandItem().getItem() instanceof GunItem;
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void tick() {
            LivingEntity raw = mob.getTarget();
            if (!(raw instanceof Player pl) || !pl.isAlive() || pl.isRemoved() || pl.isSpectator()) {
                mob.setTarget(null);
                if (mob instanceof AbstractSkeleton sk) sk.setAggressive(false);
                return;
            }

            if (mob instanceof AbstractSkeleton sk) sk.setAggressive(true);

            double d2 = mob.distanceToSqr(pl);
            boolean canSee = mob.getSensing().hasLineOfSight(pl);

            if (!canSee || d2 > (maxRange * maxRange)) mob.getNavigation().moveTo(pl, 1.15);
            else mob.getNavigation().moveTo(pl, 1.00);

            mob.getLookControl().setLookAt(pl, 30.0F, 30.0F);

            if (cooldown > 0) {
                cooldown--;
                return;
            }

            if (!canSee) {
                cooldown = tryIntervalTicks;
                return;
            }

            if (!(mob.level() instanceof ServerLevel level)) return;

            ItemStack stack = mob.getMainHandItem();
            if (!(stack.getItem() instanceof GunItem gun)) return;

            GameProfile profile = new GameProfile(fakeUuidForMob(mob.getUUID()), "PB_" + mob.getId());
            FakePlayer fp = FakePlayerFactory.get(level, profile);

            fp.getCooldowns().tick();
            fp.moveTo(mob.getX(), mob.getY(), mob.getZ(), mob.getYRot(), mob.getXRot());

            aimAtTarget(fp, mob, pl);

            if (fp.getCooldowns().isOnCooldown(stack.getItem())) {
                cooldown = tryIntervalTicks;
                return;
            }

            mob.swing(net.minecraft.world.InteractionHand.MAIN_HAND, true);
            gun.serverFire(fp, stack, false);

            cooldown = tryIntervalTicks;
        }

        private static UUID fakeUuidForMob(UUID mobUuid) {
            return new UUID(~mobUuid.getMostSignificantBits(), ~mobUuid.getLeastSignificantBits());
        }

        private static void aimAtTarget(FakePlayer fp, Mob shooter, LivingEntity target) {
            double sx = shooter.getX();
            double sy = shooter.getEyeY();
            double sz = shooter.getZ();

            double tx = target.getX();
            double ty = target.getEyeY();
            double tz = target.getZ();

            double dx = tx - sx;
            double dy = ty - sy;
            double dz = tz - sz;

            double distXZ = Math.sqrt(dx * dx + dz * dz);

            float yaw = (float) (Math.toDegrees(Math.atan2(dz, dx)) - 90.0);
            float pitch = (float) (-Math.toDegrees(Math.atan2(dy, distXZ)));

            fp.setYRot(yaw);
            fp.setXRot(pitch);
            fp.setYHeadRot(yaw);
            fp.setYBodyRot(yaw);

            shooter.setYRot(yaw);
            shooter.setXRot(pitch);
            shooter.setYHeadRot(yaw);
            shooter.yBodyRot = yaw;
        }
    }
}