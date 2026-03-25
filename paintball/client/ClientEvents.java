package com.zerokg2004.paintball.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.client.model.*;
import com.zerokg2004.paintball.client.renderer.*;
import com.zerokg2004.paintball.item.RemoteItem;
import com.zerokg2004.paintball.item.gun.GunItem;
import com.zerokg2004.paintball.network.C4DetonatePacket;
import com.zerokg2004.paintball.network.NetworkHandler;
import com.zerokg2004.paintball.network.ReloadGunPacket;
import com.zerokg2004.paintball.registry.ModBlockEntities;
import com.zerokg2004.paintball.registry.ModEntityTypes;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(
        modid = PaintballMod.MODID,
        value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.MOD
)
public class ClientEvents {

    public static final KeyMapping RELOAD_KEY = new KeyMapping(
            "key.paintball.reload",
            GLFW.GLFW_KEY_R,
            "key.categories.paintball"
    );

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> GunItem.setClientFireHandler((hand, ads) -> NetworkHandler.sendFireGun(hand, ads)));
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(RELOAD_KEY);
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        BlockEntityRenderers.register(ModBlockEntities.C4_BLOCK_ENTITY.get(), C4BlockRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.CLAYMORE_BLOCK_ENTITY.get(), ClaymoreBlockRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.FLAG_BLOCK_ENTITY.get(), FlagBlockRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.MEDKIT_BLOCK_ENTITY.get(), MedKitBlockRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.POD_BLOCK_ENTITY.get(), PodBlockRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.ROULETTE_BLOCK_ENTITY.get(), DecisionRouletteBlockRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.GEAR_RACK_BLOCK_ENTITY.get(), GearRackRenderer::new);
        BlockEntityRenderers.register(ModBlockEntities.WEAPON_RACK_BLOCK_ENTITY.get(), WeaponRackRenderer::new);

        EntityRenderers.register(ModEntityTypes.RED_GRENADE.get(), ctx -> new GrenadeRenderer(ctx, "red"));
        EntityRenderers.register(ModEntityTypes.BLUE_GRENADE.get(), ctx -> new GrenadeRenderer(ctx, "blue"));
        EntityRenderers.register(ModEntityTypes.GREEN_GRENADE.get(), ctx -> new GrenadeRenderer(ctx, "green"));
        EntityRenderers.register(ModEntityTypes.YELLOW_GRENADE.get(), ctx -> new GrenadeRenderer(ctx, "yellow"));
        EntityRenderers.register(ModEntityTypes.ORANGE_GRENADE.get(), ctx -> new GrenadeRenderer(ctx, "orange"));
        EntityRenderers.register(ModEntityTypes.PURPLE_GRENADE.get(), ctx -> new GrenadeRenderer(ctx, "purple"));

        EntityRenderers.register(ModEntityTypes.RED_PELLET.get(), ctx -> new PelletRenderer(ctx, "red"));
        EntityRenderers.register(ModEntityTypes.BLUE_PELLET.get(), ctx -> new PelletRenderer(ctx, "blue"));
        EntityRenderers.register(ModEntityTypes.GREEN_PELLET.get(), ctx -> new PelletRenderer(ctx, "green"));
        EntityRenderers.register(ModEntityTypes.YELLOW_PELLET.get(), ctx -> new PelletRenderer(ctx, "yellow"));
        EntityRenderers.register(ModEntityTypes.ORANGE_PELLET.get(), ctx -> new PelletRenderer(ctx, "orange"));
        EntityRenderers.register(ModEntityTypes.PURPLE_PELLET.get(), ctx -> new PelletRenderer(ctx, "purple"));
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.C4, ModelC4::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.CLAYMORE, ModelClaymore::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.FLAG, ModelFlag::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.MEDKIT, ModelMedKit::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.POD, ModelPod::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.ROULETTE, ModelDecisionRoulette::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.GEAR_RACK, ModelGearRack::createBodyLayer);
        event.registerLayerDefinition(ModModelLayers.WEAPON_RACK, ModelWeaponRack::createBodyLayer);
    }

    public static boolean isZooming() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;

        ItemStack held = mc.player.getMainHandItem();
        if (!(held.getItem() instanceof GunItem gun)) return false;

        if (gun.isLauncherWeapon()) return GunItem.isLauncherZoomed();
        return gun.isScoped();
    }

    public static void triggerHitMarker() {
        ForgeEvents.triggerHitMarker();
    }

    @Mod.EventBusSubscriber(
            modid = PaintballMod.MODID,
            value = Dist.CLIENT,
            bus = Mod.EventBusSubscriber.Bus.FORGE
    )
    public static class ForgeEvents {

        private static final ResourceLocation HITMARKER_TEX =
                new ResourceLocation(PaintballMod.MODID, "textures/gui/hit_marker.png");
        private static int hitMarkerTicks = 0;

        private static final ResourceLocation LAUNCHER_SCOPE =
                new ResourceLocation(PaintballMod.MODID, "textures/gui/launcher_sight.png");
        private static final ResourceLocation PISTOL_SCOPE =
                new ResourceLocation(PaintballMod.MODID, "textures/gui/pistol_sight.png");
        private static final ResourceLocation SHOTGUN_SCOPE =
                new ResourceLocation(PaintballMod.MODID, "textures/gui/shotgun_sight.png");
        private static final ResourceLocation RIFLE_SCOPE =
                new ResourceLocation(PaintballMod.MODID, "textures/gui/rifle_sight.png");
        private static final ResourceLocation SNIPER_SCOPE =
                new ResourceLocation(PaintballMod.MODID, "textures/gui/sniper_sight.png");

        private static final int SCOPE_TEX_W = 1024;
        private static final int SCOPE_TEX_H = 256;

        private static boolean leftHeld = false;

        public static void triggerHitMarker() {
            hitMarkerTicks = 20;
        }

        private static boolean shouldBlockGunClient() {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.level == null) return false;
            return GunItem.isInteractableTargetClient() || GunItem.isBlockedTargetClient(mc.level);
        }

        private static float clamp01(float v) {
            return Mth.clamp(v, 0.0f, 1.0f);
        }

        // easing tipo catalejo (suave al inicio/fin)
        private static float smoothstep(float t) {
            t = clamp01(t);
            return t * t * (3f - 2f * t);
        }

        /**
         * ✅ smoothing exponencial dependiente de dt:
         * - NO da “pasitos” visibles
         * - se siente como un deslizamiento continuo
         *
         * speed: más alto = más rápido
         * dt: segundos reales del tick (aprox 0.05)
         */
        private static float expTo(float current, float target, float speed, float dt) {
            float a = 1.0f - (float) Math.exp(-speed * dt);
            return current + (target - current) * a;
        }

        @SubscribeEvent
        public static void onClientTick(ClientTickEvent event) {
            if (event.phase != ClientTickEvent.Phase.END) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.level == null || mc.player == null) return;

            LocalPlayer player = mc.player;
            if (!mc.isWindowActive()) return;
            if (!player.isAlive() || player.isSpectator()) return;

            GunItem.clientTickBlockLeftClickMiningWithGun();

            if (hitMarkerTicks > 0) hitMarkerTicks--;

            // ✅ 1) capturar prev zoom (esto es CLAVE para quitar escalones)
            GunItem.capturePrevZoom(player);

            ItemStack held = player.getMainHandItem();
            boolean blocked = shouldBlockGunClient();

            // dt del tick (20 TPS)
            final float dt = 1.0f / 20.0f;

            if (!held.isEmpty() && held.getItem() instanceof GunItem gun) {
                boolean firstPerson = mc.options.getCameraType().isFirstPerson();
                boolean rightDown = mc.options.keyUse.isDown();

                // ✅ ADS instant (overlay / hide-hand)
                boolean wantAds = !blocked && firstPerson && rightDown;

                if (gun.isLauncherWeapon()) {
                    GunItem.setLauncherZoom(wantAds);
                } else {
                    gun.setScoped(wantAds);
                }

                // ✅ zoom float SOLO FOV (suavizado continuo)
                // Ajusta “speed” a gusto (más alto = más rápido, pero sigue suave)
                final float speedInGun = 18.0f;
                final float speedOutGun = 22.0f;
                final float speedInLauncher = 16.0f;
                final float speedOutLauncher = 20.0f;

                if (gun.isLauncherWeapon()) {
                    float z = GunItem.getLauncherZoom01Client(player);
                    float speed = wantAds ? speedInLauncher : speedOutLauncher;
                    z = expTo(z, wantAds ? 1.0f : 0.0f, speed, dt);
                    GunItem.setLauncherZoom01Client(player, z);

                    // asegúrate de apagar el otro zoom
                    GunItem.setGunZoom01Client(player, 0.0f);
                } else {
                    float z = GunItem.getZoom01Client(player);
                    float speed = wantAds ? speedInGun : speedOutGun;
                    z = expTo(z, wantAds ? 1.0f : 0.0f, speed, dt);
                    GunItem.setGunZoom01Client(player, z);

                    GunItem.setLauncherZoom01Client(player, 0.0f);
                    GunItem.setLauncherZoom(false);
                }

            } else {
                // ✅ sin arma: volver a 0 suave
                float zg = GunItem.getZoom01Client(player);
                float zl = GunItem.getLauncherZoom01Client(player);

                zg = expTo(zg, 0.0f, 22.0f, dt);
                zl = expTo(zl, 0.0f, 22.0f, dt);

                GunItem.setGunZoom01Client(player, zg);
                GunItem.setLauncherZoom01Client(player, zl);
                GunItem.setLauncherZoom(false);
            }

            GunItem.clientTickAutoFireUsing(leftHeld);

            if (!held.isEmpty() && held.getItem() instanceof GunItem) {
                if (ClientEvents.RELOAD_KEY.consumeClick()) {
                    NetworkHandler.sendToServer(new ReloadGunPacket());
                }
            }

            if (!leftHeld) {
                GunItem.resetFired();
            }
        }

        @SubscribeEvent
        public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            if (!mc.options.getCameraType().isFirstPerson()) return;

            GuiGraphics gg = event.getGuiGraphics();
            int screenW = mc.getWindow().getGuiScaledWidth();
            int screenH = mc.getWindow().getGuiScaledHeight();

            if (hitMarkerTicks > 0) {
                int texW = 16;
                int texH = 16;
                int x = screenW - texW - 4;
                int y = 4;

                RenderSystem.disableDepthTest();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();

                gg.blit(HITMARKER_TEX, x, y, 0, 0, texW, texH, texW, texH);

                RenderSystem.disableBlend();
                RenderSystem.enableDepthTest();
            }

            ItemStack held = mc.player.getMainHandItem();
            if (held.isEmpty() || !(held.getItem() instanceof GunItem gun)) return;

            if (shouldBlockGunClient()) return;

            if (gun.isLauncherWeapon()) {
                if (GunItem.isLauncherZoomed()) {
                    renderScope(gg, LAUNCHER_SCOPE, screenW, screenH);
                }
                return;
            }

            if (!gun.isScoped()) return;

            ResourceLocation scopeTex = getScopeForGun(gun);
            renderScope(gg, scopeTex, screenW, screenH);
        }

        private static ResourceLocation getScopeForGun(GunItem gun) {
            int pellets = gun.getPelletsPerShot();
            if (pellets > 1) return SHOTGUN_SCOPE;

            ResourceLocation id = BuiltInRegistries.ITEM.getKey(gun);
            String path = (id != null) ? id.getPath() : "";

            if (path.contains("sniper")) return SNIPER_SCOPE;
            if (path.contains("rifle")) return RIFLE_SCOPE;

            float dmg = gun.getDamagePerPellet();
            if (dmg >= 7.5f) return SNIPER_SCOPE;

            return PISTOL_SCOPE;
        }

        private static void renderScope(GuiGraphics gui, ResourceLocation texture, int screenWidth, int screenHeight) {
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();

            int x;
            int y;

            if (texture.equals(SNIPER_SCOPE)) {
                x = (screenWidth - SCOPE_TEX_W) / 2 - 1;
                y = (screenHeight - SCOPE_TEX_H) / 2;
            } else {
                x = (screenWidth - SCOPE_TEX_W) / 2;
                y = screenHeight - SCOPE_TEX_H - 22;
            }

            gui.blit(texture, x, y, 0, 0, SCOPE_TEX_W, SCOPE_TEX_H, SCOPE_TEX_W, SCOPE_TEX_H);

            RenderSystem.disableBlend();
            RenderSystem.enableDepthTest();
        }

        @SubscribeEvent
        public static void onMouseButton(InputEvent.MouseButton.Pre event) {
            if (event.getButton() != GLFW.GLFW_MOUSE_BUTTON_LEFT) return;

            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            if (mc.screen != null) return;

            if (event.getAction() == GLFW.GLFW_PRESS) leftHeld = true;
            if (event.getAction() == GLFW.GLFW_RELEASE) leftHeld = false;

            ItemStack held = mc.player.getMainHandItem();

            if (!(held.getItem() instanceof GunItem gun)) {
                if (event.getAction() == GLFW.GLFW_PRESS && held.getItem() instanceof RemoteItem) {
                    NetworkHandler.sendToServer(new C4DetonatePacket());
                    event.setCanceled(true);
                }
                return;
            }

            if (mc.hitResult != null) {
                if (mc.hitResult.getType() == HitResult.Type.BLOCK) return;
                if (shouldBlockGunClient()) return;
            }

            if (event.getAction() == GLFW.GLFW_PRESS) {
                boolean ads;
                if (gun.isLauncherWeapon()) {
                    ads = GunItem.isLauncherZoomed();
                } else {
                    ads = mc.options.getCameraType().isFirstPerson() && mc.options.keyUse.isDown();
                }

                if (mc.player.isUsingItem() && mc.player.getUseItem() != held) {
                    mc.player.stopUsingItem();
                }

                NetworkHandler.sendFireGun(InteractionHand.MAIN_HAND, ads);
                mc.player.swing(InteractionHand.MAIN_HAND, false);
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onRenderHand(RenderHandEvent event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;
            if (!mc.options.getCameraType().isFirstPerson()) return;

            ItemStack main = mc.player.getMainHandItem();
            if (!(main.getItem() instanceof GunItem gun)) return;

            if (shouldBlockGunClient()) return;

            boolean hide = (gun.isLauncherWeapon() && GunItem.isLauncherZoomed())
                    || (!gun.isLauncherWeapon() && gun.isScoped());

            if (hide && (event.getHand() == InteractionHand.MAIN_HAND || event.getHand() == InteractionHand.OFF_HAND)) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onFovUpdate(ViewportEvent.ComputeFov event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            ItemStack held = mc.player.getMainHandItem();
            if (held.isEmpty() || !(held.getItem() instanceof GunItem gun)) return;

            if (shouldBlockGunClient()) return;

            double baseFov = event.getFOV();

            // ✅ interpolación por frame (esta es la magia anti-“saltitos”)
            float pt = mc.getFrameTime(); // 0..1
            float z;

            if (gun.isLauncherWeapon()) {
                float prev = GunItem.getLauncherZoom01ClientPrev(mc.player);
                float cur  = GunItem.getLauncherZoom01Client(mc.player);
                z = Mth.lerp(pt, prev, cur);
            } else {
                float prev = GunItem.getZoom01ClientPrev(mc.player);
                float cur  = GunItem.getZoom01Client(mc.player);
                z = Mth.lerp(pt, prev, cur);
            }

            // easing tipo catalejo
            double t = smoothstep(z);

            if (gun.isLauncherWeapon()) {
                double targetFactor = 0.35D;
                double factorNow = Mth.lerp(t, 1.0D, targetFactor);
                event.setFOV(baseFov * factorNow);
                return;
            }

            double targetFactor;
            int pellets = gun.getPelletsPerShot();
            float dmg = gun.getDamagePerPellet();

            if (pellets > 1) targetFactor = 0.85D;
            else if (dmg >= 7.5f) targetFactor = 0.10D;
            else targetFactor = 0.75D;

            double factorNow = Mth.lerp(t, 1.0D, targetFactor);
            event.setFOV(baseFov * factorNow);
        }

        @SubscribeEvent
        public static void onBlockHighlight(RenderHighlightEvent.Block event) {
        }
    }
}