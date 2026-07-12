package com.zerokg2004.paintball.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.client.model.*;
import com.zerokg2004.paintball.client.renderer.*;
import com.zerokg2004.paintball.item.gun.GunItem;
import com.zerokg2004.paintball.item.gun.GunItemClient;
import com.zerokg2004.paintball.network.NetworkHandler;
import com.zerokg2004.paintball.network.ReloadGunPacket;
import com.zerokg2004.paintball.registry.ModBlockEntities;
import com.zerokg2004.paintball.registry.ModEntityTypes;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent.ClientTickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
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

    public static void triggerHitMarker() {
        ForgeEvents.triggerHitMarker();
    }

    @Mod.EventBusSubscriber(
            modid = PaintballMod.MODID,
            value = Dist.CLIENT,
            bus = Mod.EventBusSubscriber.Bus.FORGE
    )
    public static class ForgeEvents {

        private static final ResourceLocation HITMARKER_TEX = new ResourceLocation(PaintballMod.MODID, "textures/gui/hit_marker.png");
        private static int hitMarkerTicks = 0;

        private static final ResourceLocation LAUNCHER_SCOPE = new ResourceLocation(PaintballMod.MODID, "textures/gui/launcher_sight.png");
        private static final ResourceLocation PISTOL_SCOPE = new ResourceLocation(PaintballMod.MODID, "textures/gui/pistol_sight.png");
        private static final ResourceLocation SHOTGUN_SCOPE = new ResourceLocation(PaintballMod.MODID, "textures/gui/shotgun_sight.png");
        private static final ResourceLocation RIFLE_SCOPE = new ResourceLocation(PaintballMod.MODID, "textures/gui/rifle_sight.png");
        private static final ResourceLocation SNIPER_SCOPE = new ResourceLocation(PaintballMod.MODID, "textures/gui/sniper_sight.png");

        private static final int SCOPE_TEX_W = 1024;
        private static final int SCOPE_TEX_H = 256;
        private static boolean leftHeld = false;

        public static void triggerHitMarker() {
            hitMarkerTicks = 20;
        }

        public static boolean shouldBlockGunClient() {
            Minecraft mc = Minecraft.getInstance();
            if (mc == null || mc.level == null) return false;
            return GunItemClient.isInteractableTargetClient() || GunItemClient.isBlockedTargetClient(mc.level);
        }

        private static float smoothstep(float t) {
            t = Mth.clamp(t, 0.0f, 1.0f);
            return t * t * (3f - 2f * t);
        }

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
            if (!mc.isWindowActive() || !player.isAlive() || player.isSpectator()) return;

            GunItemClient.clientTickBlockLeftClickMiningWithGun();
            if (hitMarkerTicks > 0) hitMarkerTicks--;
            GunItemClient.capturePrevZoom(player);

            ItemStack mainStack = player.getMainHandItem();
            ItemStack offStack = player.getOffhandItem();

            InteractionHand activeHand = null;
            if (mainStack.getItem() instanceof GunItem) {
                activeHand = InteractionHand.MAIN_HAND;
            } else if (offStack.getItem() instanceof GunItem) {
                activeHand = InteractionHand.OFF_HAND;
            }

            boolean blocked = shouldBlockGunClient();
            final float dt = 1.0f / 20.0f;

            if (activeHand != null) {
                ItemStack held = player.getItemInHand(activeHand);
                GunItem gun = (GunItem) held.getItem();

                boolean isHandBlocked = false;
                ItemStack otherStack = (activeHand == InteractionHand.MAIN_HAND) ? offStack : mainStack;

                if (!otherStack.isEmpty()) {
                    if (!(otherStack.getItem() instanceof GunItem)) {
                        isHandBlocked = true;
                    } else if (!gun.isThrownGrenade()) {
                        GunItem otherGun = (GunItem) otherStack.getItem();
                        if (!otherGun.isThrownGrenade()) {
                            isHandBlocked = true;
                        }
                    }
                }

                boolean wantAds = !blocked
                        && !isHandBlocked
                        && !gun.isThrownGrenade()
                        && mc.options.getCameraType().isFirstPerson()
                        && mc.options.keyUse.isDown();

                if (wantAds && activeHand == InteractionHand.OFF_HAND && !player.isUsingItem()) {
                    mc.gameMode.useItem(player, InteractionHand.OFF_HAND);
                }

                if (gun.isLauncherWeapon()) {
                    GunItemClient.setLauncherZoom(wantAds);
                } else {
                    GunItemClient.setScoped(gun, wantAds);
                }

                float z = gun.isLauncherWeapon()
                        ? GunItemClient.getLauncherZoom01Client(player)
                        : GunItemClient.getZoom01Client(player);

                float speed = wantAds
                        ? (gun.isLauncherWeapon() ? 16.0f : 18.0f)
                        : (gun.isLauncherWeapon() ? 20.0f : 22.0f);

                z = expTo(z, wantAds ? 1.0f : 0.0f, speed, dt);

                if (gun.isLauncherWeapon()) {
                    GunItemClient.setLauncherZoom01Client(player, z);
                    GunItemClient.setGunZoom01Client(player, 0.0f);
                } else {
                    GunItemClient.setGunZoom01Client(player, z);
                    GunItemClient.setLauncherZoom01Client(player, 0.0f);
                    GunItemClient.setLauncherZoom(false);
                }
            } else {
                float zg = expTo(GunItemClient.getZoom01Client(player), 0.0f, 22.0f, dt);
                float zl = expTo(GunItemClient.getLauncherZoom01Client(player), 0.0f, 22.0f, dt);
                GunItemClient.setGunZoom01Client(player, zg);
                GunItemClient.setLauncherZoom01Client(player, zl);
                GunItemClient.setLauncherZoom(false);
            }

            GunItemClient.clientTickAutoFireUsing(leftHeld);

            if (RELOAD_KEY.consumeClick()) {
                boolean canReloadMain = mainStack.getItem() instanceof GunItem
                        && (offStack.isEmpty() || offStack.getItem() instanceof GunItem);
                boolean canReloadOff = offStack.getItem() instanceof GunItem
                        && (mainStack.isEmpty() || mainStack.getItem() instanceof GunItem);

                if (canReloadMain) {
                    NetworkHandler.sendToServer(new ReloadGunPacket(InteractionHand.MAIN_HAND));
                } else if (canReloadOff) {
                    NetworkHandler.sendToServer(new ReloadGunPacket(InteractionHand.OFF_HAND));
                }
            }

            if (!leftHeld) GunItem.resetFired();
        }

        @SubscribeEvent
        public static void onPlayerRender(RenderLivingEvent.Pre<?, ?> event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (!(event.getRenderer().getModel() instanceof PlayerModel<?> model)) return;

            ItemStack main = player.getMainHandItem();
            ItemStack off = player.getOffhandItem();

            boolean mainIsHeavy = main.getItem() instanceof GunItem g && g.isTwoHandedAnim();
            boolean offIsHeavy = off.getItem() instanceof GunItem g && g.isTwoHandedAnim();

            if (mainIsHeavy) {
                if (off.isEmpty()) {
                    model.rightArmPose = PlayerModel.ArmPose.BOW_AND_ARROW;
                } else {
                    model.rightArmPose = PlayerModel.ArmPose.ITEM;
                    model.leftArmPose = PlayerModel.ArmPose.ITEM;
                }
            } else if (offIsHeavy) {
                if (main.isEmpty()) {
                    model.leftArmPose = PlayerModel.ArmPose.BOW_AND_ARROW;
                } else {
                    model.leftArmPose = PlayerModel.ArmPose.ITEM;
                    model.rightArmPose = PlayerModel.ArmPose.ITEM;
                }
            }
        }

        @SubscribeEvent
        public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || !mc.options.getCameraType().isFirstPerson()) return;

            ItemStack main = mc.player.getMainHandItem();
            ItemStack off = mc.player.getOffhandItem();

            GunItem gun = null;
            if (main.getItem() instanceof GunItem g) gun = g;
            else if (off.getItem() instanceof GunItem g) gun = g;

            if (gun == null || shouldBlockGunClient()) return;

            float currentZoom = GunItemClient.getZoom01Client(mc.player);
            float launcherZoom = GunItemClient.getLauncherZoom01Client(mc.player);
            boolean isAttemptingAds = mc.options.keyUse.isDown();

            GuiGraphics gg = event.getGuiGraphics();
            int screenW = mc.getWindow().getGuiScaledWidth();
            int screenH = mc.getWindow().getGuiScaledHeight();

            if (gun.isLauncherWeapon()) {
                if (launcherZoom > 0.01f && isAttemptingAds) {
                    renderScope(gg, LAUNCHER_SCOPE, screenW, screenH);
                }
            } else {
                if (currentZoom > 0.01f && isAttemptingAds) {
                    renderScope(gg, getScopeForGun(gun), screenW, screenH);
                }
            }
        }

        private static ResourceLocation getScopeForGun(GunItem gun) {
            if (gun.getPelletsPerShot() > 1) return SHOTGUN_SCOPE;
            String path = BuiltInRegistries.ITEM.getKey(gun).getPath();
            if (path.contains("sniper") || gun.getDamagePerPellet() >= 7.5f) return SNIPER_SCOPE;
            return path.contains("rifle") ? RIFLE_SCOPE : PISTOL_SCOPE;
        }

        private static void renderScope(GuiGraphics gui, ResourceLocation texture, int screenWidth, int screenHeight) {
            RenderSystem.disableDepthTest();
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            int x = (screenWidth - SCOPE_TEX_W) / 2;
            int y = (screenHeight - SCOPE_TEX_H) / 2;

            if (!texture.equals(SNIPER_SCOPE)) {
                y = screenHeight - SCOPE_TEX_H - 22;
            } else {
                x -= 1;
            }

            gui.blit(texture, x, y, 0, 0, SCOPE_TEX_W, SCOPE_TEX_H, SCOPE_TEX_W, SCOPE_TEX_H);

            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        }

        @SubscribeEvent
        public static void onMouseButton(InputEvent.MouseButton.Pre event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || mc.screen != null) return;

            ItemStack mainHand = mc.player.getMainHandItem();
            ItemStack offHand = mc.player.getOffhandItem();

            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                leftHeld = (event.getAction() == GLFW.GLFW_PRESS);

                if (offHand.getItem() instanceof GunItem offGun) {
                    if (!mainHand.isEmpty()) {
                        if (!(mainHand.getItem() instanceof GunItem) || (!offGun.isThrownGrenade() && mainHand.getItem() instanceof GunItem mg && !mg.isThrownGrenade())) {
                            return;
                        }
                    }

                    if (event.getAction() == GLFW.GLFW_PRESS) {
                        boolean ads = offGun.isLauncherWeapon()
                                ? GunItemClient.isLauncherZoomed()
                                : GunItemClient.isScoped(offGun);
                        NetworkHandler.sendFireGun(InteractionHand.OFF_HAND, ads);

                        if (offGun.isThrownGrenade()) {
                            mc.player.swing(InteractionHand.OFF_HAND, false);
                        }
                    }
                    event.setCanceled(true);
                    return;
                } else if (mainHand.getItem() instanceof GunItem mainGun) {
                    if (!offHand.isEmpty()) {
                        if (!(offHand.getItem() instanceof GunItem) || (!mainGun.isThrownGrenade() && offHand.getItem() instanceof GunItem og && !og.isThrownGrenade())) {
                            return;
                        }
                    }

                    if (event.getAction() == GLFW.GLFW_PRESS) {
                        boolean ads = mainGun.isLauncherWeapon()
                                ? GunItemClient.isLauncherZoomed()
                                : GunItemClient.isScoped(mainGun);
                        NetworkHandler.sendFireGun(InteractionHand.MAIN_HAND, ads);

                        if (mainGun.isThrownGrenade()) {
                            mc.player.swing(InteractionHand.MAIN_HAND, false);
                        }
                    }
                    event.setCanceled(true);
                }

                String mainID = BuiltInRegistries.ITEM.getKey(mainHand.getItem()).getPath();
                String offID = BuiltInRegistries.ITEM.getKey(offHand.getItem()).getPath();

                if (mainID.contains("remote") || offID.contains("remote")) {
                    if (event.getAction() == GLFW.GLFW_PRESS) {
                        InteractionHand hand = mainID.contains("remote") ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                        NetworkHandler.sendFireGun(hand, false);
                    }
                    event.setCanceled(true);
                    return;
                }
            }

            if (event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                if (offHand.getItem() instanceof GunItem) {
                    if (!mainHand.isEmpty() && !(mainHand.getItem() instanceof GunItem)) {
                        event.setCanceled(true);
                        return;
                    }

                    if (event.getAction() == GLFW.GLFW_PRESS && !(mainHand.getItem() instanceof GunItem)) {
                        mc.gameMode.useItem(mc.player, InteractionHand.OFF_HAND);
                        event.setCanceled(true);
                    }
                } else if (mainHand.getItem() instanceof GunItem) {
                    if (!offHand.isEmpty() && !(offHand.getItem() instanceof GunItem)) {
                        event.setCanceled(true);
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onRenderHand(RenderHandEvent event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || !mc.options.getCameraType().isFirstPerson() || shouldBlockGunClient()) return;

            ItemStack main = mc.player.getMainHandItem();
            ItemStack off = mc.player.getOffhandItem();
            GunItem gun = (main.getItem() instanceof GunItem g) ? g : (off.getItem() instanceof GunItem g ? g : null);

            if (gun != null && ((gun.isLauncherWeapon() && GunItemClient.isLauncherZoomed()) || GunItemClient.isScoped(gun))) {
                event.setCanceled(true);
            }
        }

        @SubscribeEvent
        public static void onItemTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            if (stack.isEmpty()) return;

            Player player = event.getEntity();
            if (player == null || !player.getInventory().contains(stack)) {
                return;
            }

            Item item = stack.getItem();

            if (item instanceof GunItem gun) {
                if (!gun.isThrownGrenade() && !gun.isLauncher()) {
                    event.getToolTip().add(Component.literal("Paintball Revival")
                            .withStyle(ChatFormatting.BLUE));
                }
            }
        }

        @SubscribeEvent
        public static void onFovUpdate(ViewportEvent.ComputeFov event) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null || shouldBlockGunClient()) return;

            ItemStack main = mc.player.getMainHandItem();
            ItemStack off = mc.player.getOffhandItem();
            GunItem gun = (main.getItem() instanceof GunItem g) ? g : (off.getItem() instanceof GunItem g ? g : null);

            if (gun != null) {
                float pt = mc.getFrameTime();
                float z = gun.isLauncherWeapon()
                        ? Mth.lerp(pt, GunItemClient.getLauncherZoom01ClientPrev(mc.player), GunItemClient.getLauncherZoom01Client(mc.player))
                        : Mth.lerp(pt, GunItemClient.getZoom01ClientPrev(mc.player), GunItemClient.getZoom01Client(mc.player));

                double targetFactor = gun.isLauncherWeapon()
                        ? 0.35D
                        : (gun.getPelletsPerShot() > 1 ? 0.85D : (gun.getDamagePerPellet() >= 7.5f ? 0.10D : 0.75D));

                event.setFOV(event.getFOV() * Mth.lerp(smoothstep(z), 1.0D, targetFactor));
            }
        }

        @SubscribeEvent
        public static void onBlockHighlight(RenderHighlightEvent.Block event) {
        }
    }
}
