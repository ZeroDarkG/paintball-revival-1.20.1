package com.zerokg2004.paintball.item.gun;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class GunItemClient {
    private GunItemClient() {
    }

    private static final Map<UUID, Boolean> CLIENT_SCOPED = new HashMap<>();
    private static final Map<UUID, Float> CLIENT_ZOOM = new HashMap<>();
    private static final Map<UUID, Float> CLIENT_LAUNCHER_ZOOM = new HashMap<>();
    private static final Map<UUID, Float> CLIENT_ZOOM_PREV = new HashMap<>();
    private static final Map<UUID, Float> CLIENT_LAUNCHER_ZOOM_PREV = new HashMap<>();

    private static boolean launcherZoom = false;

    private static boolean getClientScoped(Player player) {
        return CLIENT_SCOPED.getOrDefault(player.getUUID(), false);
    }

    private static void setClientScoped(Player player, boolean value) {
        if (value) {
            CLIENT_SCOPED.put(player.getUUID(), true);
        } else {
            CLIENT_SCOPED.remove(player.getUUID());
        }
    }

    private static float clamp01(float value) {
        return Math.max(0.0f, Math.min(1.0f, value));
    }

    private static float getClientZoom(Player player) {
        return CLIENT_ZOOM.getOrDefault(player.getUUID(), 0.0f);
    }

    private static void setClientZoom(Player player, float value) {
        value = clamp01(value);
        if (value <= 0.0001f) {
            CLIENT_ZOOM.remove(player.getUUID());
        } else {
            CLIENT_ZOOM.put(player.getUUID(), value);
        }
    }

    private static float getClientLauncherZoom(Player player) {
        return CLIENT_LAUNCHER_ZOOM.getOrDefault(player.getUUID(), 0.0f);
    }

    private static void setClientLauncherZoom(Player player, float value) {
        value = clamp01(value);
        if (value <= 0.0001f) {
            CLIENT_LAUNCHER_ZOOM.remove(player.getUUID());
        } else {
            CLIENT_LAUNCHER_ZOOM.put(player.getUUID(), value);
        }
    }

    private static float getClientZoomPrev(Player player) {
        return CLIENT_ZOOM_PREV.getOrDefault(player.getUUID(), 0.0f);
    }

    private static void setClientZoomPrev(Player player, float value) {
        value = clamp01(value);
        if (value <= 0.0001f) {
            CLIENT_ZOOM_PREV.remove(player.getUUID());
        } else {
            CLIENT_ZOOM_PREV.put(player.getUUID(), value);
        }
    }

    private static float getClientLauncherZoomPrev(Player player) {
        return CLIENT_LAUNCHER_ZOOM_PREV.getOrDefault(player.getUUID(), 0.0f);
    }

    private static void setClientLauncherZoomPrev(Player player, float value) {
        value = clamp01(value);
        if (value <= 0.0001f) {
            CLIENT_LAUNCHER_ZOOM_PREV.remove(player.getUUID());
        } else {
            CLIENT_LAUNCHER_ZOOM_PREV.put(player.getUUID(), value);
        }
    }

    public static float getZoom01Client(Player player) {
        return getClientZoom(player);
    }

    public static float getLauncherZoom01Client(Player player) {
        return getClientLauncherZoom(player);
    }

    public static void setGunZoom01Client(Player player, float value) {
        setClientZoom(player, value);
    }

    public static void setLauncherZoom01Client(Player player, float value) {
        setClientLauncherZoom(player, value);
    }

    public static void capturePrevZoom(Player player) {
        if (player == null) return;
        setClientZoomPrev(player, getClientZoom(player));
        setClientLauncherZoomPrev(player, getClientLauncherZoom(player));
    }

    public static float getLauncherZoom01ClientPrev(Player player) {
        if (player == null) return 0.0f;
        return getClientLauncherZoomPrev(player);
    }

    public static float getZoom01ClientPrev(Player player) {
        if (player == null) return 0.0f;
        return getClientZoomPrev(player);
    }

    public static boolean isBlockedTargetClient(Level level) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.hitResult == null) return false;

        HitResult hitResult = mc.hitResult;

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            ResourceLocation id = BuiltInRegistries.BLOCK.getKey(level.getBlockState(blockHit.getBlockPos()).getBlock());
            if (id == null) return false;

            String path = id.getPath();
            return path.endsWith("_gear_rack")
                    || path.endsWith("_weapon_rack")
                    || path.endsWith("_pod")
                    || path.endsWith("_medkit");
        }

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(entityHit.getEntity().getType());
            if (id == null) return false;

            String path = id.getPath();
            return path.equals("item_frame") || path.equals("glow_item_frame");
        }

        return false;
    }

    public static boolean isInteractableTargetClient() {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.level == null || mc.hitResult == null) return false;

        HitResult hitResult = mc.hitResult;

        if (hitResult.getType() == HitResult.Type.ENTITY) {
            return false;
        }

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            var pos = blockHit.getBlockPos();
            var level = mc.level;
            var state = level.getBlockState(pos);

            if (state.getMenuProvider(level, pos) != null) return true;
            if (state.hasBlockEntity()) return true;

            var block = state.getBlock();
            return block instanceof net.minecraft.world.level.block.DoorBlock
                    || block instanceof net.minecraft.world.level.block.TrapDoorBlock
                    || block instanceof net.minecraft.world.level.block.ButtonBlock
                    || block instanceof net.minecraft.world.level.block.LeverBlock
                    || block instanceof net.minecraft.world.level.block.FenceGateBlock
                    || block instanceof net.minecraft.world.level.block.BedBlock
                    || block instanceof net.minecraft.world.level.block.NoteBlock
                    || block instanceof net.minecraft.world.level.block.RepeaterBlock
                    || block instanceof net.minecraft.world.level.block.ComparatorBlock;
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

    public static boolean isScoped(GunItem gun) {
        if (gun.isLauncherWeapon() || gun.isThrownGrenadeWeapon()) return false;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return false;

        return getClientScoped(mc.player);
    }

    public static void setScoped(GunItem gun, boolean value) {
        if (gun.isLauncherWeapon() || gun.isThrownGrenadeWeapon()) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;

        ItemStack main = mc.player.getMainHandItem();
        ItemStack off = mc.player.getOffhandItem();

        if (main.getItem() != gun && off.getItem() != gun) return;

        setClientScoped(mc.player, value);
    }

    public static void clientTickAutoFireUsing(boolean leftHeld) {
        Minecraft mc = Minecraft.getInstance();
        if (mc == null || mc.player == null) return;
        if (!leftHeld) return;

        if (isInteractableTargetClient()) return;
        if (mc.level != null && isBlockedTargetClient(mc.level)) return;

        ItemStack held = mc.player.getMainHandItem();
        if (held.isEmpty() || !(held.getItem() instanceof GunItem gun)) return;
        if (!gun.isAutomatic()) return;
        if (mc.player.getCooldowns().isOnCooldown(held.getItem())) return;

        boolean firstPerson = mc.options.getCameraType().isFirstPerson();
        boolean ads = gun.isLauncherWeapon()
                ? isLauncherZoomed()
                : firstPerson && mc.options.keyUse.isDown();

        GunItem.sendClientFirePacket(InteractionHand.MAIN_HAND, ads);
    }

    public static boolean isLauncherZoomed() {
        return launcherZoom;
    }

    public static void setLauncherZoom(boolean zoomed) {
        launcherZoom = zoomed;
    }
}
