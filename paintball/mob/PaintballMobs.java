package com.zerokg2004.paintball.mob;

import com.zerokg2004.paintball.ModItems;
import com.zerokg2004.paintball.PaintballMod;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Iterator;

@Mod.EventBusSubscriber(modid = PaintballMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class PaintballMobs {

    private static final String TAG_CHECKED = "PaintballSpawnChecked";

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onEntityJoinLevel(EntityJoinLevelEvent e) {
        if (e.getLevel().isClientSide) return;
        if (!(e.getLevel() instanceof ServerLevel level)) return;
        if (!(e.getEntity() instanceof Mob mob)) return;

        if (PaintballMobLogic.isPaintballMob(mob)) {
            PaintballMobLogic.ensurePaintballAI(mob);
            return;
        }

        if (!isSupportedMob(mob)) return;

        var data = mob.getPersistentData();
        if (data.getBoolean(TAG_CHECKED)) return;
        data.putBoolean(TAG_CHECKED, true);

        Difficulty diff = level.getDifficulty();
        float chance = switch (diff) {
            case NORMAL -> 0.05f;
            case HARD -> 0.10f;
            default -> 0.0f;
        };
        if (chance <= 0.0f) return;
        if (level.random.nextFloat() > chance) return;

        PaintballMobLogic.makePaintballMob(mob, diff);
    }

    @SubscribeEvent
    public static void onLivingConversion(LivingConversionEvent.Post e) {
        if (!(e.getEntity().level() instanceof ServerLevel level)) return;

        if (!(e.getEntity() instanceof Mob oldMob)) return;
        if (!(e.getOutcome() instanceof Mob newMob)) return;

        DyeColor team = PaintballMobLogic.getTeam(oldMob);
        if (team == null) return;

        PaintballMobLogic.makePaintballMobWithFixedTeam(newMob, level.getDifficulty(), team);
    }

    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent e) {
        LivingEntity ent = e.getEntity();
        if (ent.level().isClientSide) return;
        if (!(ent instanceof Mob mob)) return;

        DyeColor team = PaintballMobLogic.getTeam(mob);
        if (team == null) return;

        filterVanillaDrops(mob, e);

        if (mob.getRandom().nextFloat() < 0.50f) {
            int amount = 4 + mob.getRandom().nextInt(10);
            Item pellets = pelletsItem(team);
            if (pellets != null) {
                ItemStack drop = new ItemStack(pellets, amount);
                ItemEntity it = new ItemEntity(mob.level(), mob.getX(), mob.getY() + 0.5, mob.getZ(), drop);
                e.getDrops().add(it);
            }
        }
    }

    private static boolean isSupportedMob(Mob mob) {
        return mob instanceof Zombie || mob instanceof Husk || mob instanceof Drowned
                || mob instanceof Skeleton || mob instanceof Stray || mob instanceof WitherSkeleton;
    }

    private static void filterVanillaDrops(Mob mob, LivingDropsEvent e) {
        Iterator<ItemEntity> it = e.getDrops().iterator();
        while (it.hasNext()) {
            ItemEntity ie = it.next();
            ItemStack stack = ie.getItem();
            if (stack.isEmpty()) continue;

            Item item = stack.getItem();

            if (mob instanceof Skeleton || mob instanceof Stray) {
                if (item == Items.ARROW || item == Items.TIPPED_ARROW || item == Items.SPECTRAL_ARROW || item == Items.BOW) {
                    it.remove();
                }
            }

            if (mob instanceof WitherSkeleton) {
                if (item == Items.STONE_SWORD) {
                    it.remove();
                }
            }
        }
    }

    private static Item pelletsItem(DyeColor c) {
        return switch (c) {
            case RED -> ModItems.RED_PELLETS.get();
            case BLUE -> ModItems.BLUE_PELLETS.get();
            case GREEN -> ModItems.GREEN_PELLETS.get();
            case YELLOW -> ModItems.YELLOW_PELLETS.get();
            case ORANGE -> ModItems.ORANGE_PELLETS.get();
            case PURPLE -> ModItems.PURPLE_PELLETS.get();
            default -> null;
        };
    }

    private PaintballMobs() {}
}