package com.zerokg2004.paintball.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.entity.projectile.BaseGrenadeEntity;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class GrenadeRenderer extends EntityRenderer<BaseGrenadeEntity> {

    private final String color;

    // Separación mínima entre planos para evitar z-fighting interno
    private static final float CROSS_EPS = 0.01F;

    // === PIVOT REAL DEL SPRITE EN LA TEXTURA ===
    // Tu green_grenade.png es 8x10.
    // Según tu línea roja (centro real del cuerpo), el pivot está en el pixel (3,4) aprox.
    // Si quieres afinar:
    // - mueve PIVOT_PX_X (0..7)
    // - mueve PIVOT_PX_Y (0..9)
    private static final float TEX_W = 8.0F;
    private static final float TEX_H = 10.0F;
    private static final float PIVOT_PX_X = 3.0F; // <-- ajusta aquí si hace falta
    private static final float PIVOT_PX_Y = 4.0F; // <-- ajusta aquí si hace falta

    // Convertimos a UV (centro del pixel)
    private static final float PIVOT_U = (PIVOT_PX_X + 0.5F) / TEX_W; // 0..1
    private static final float PIVOT_V = (PIVOT_PX_Y + 0.5F) / TEX_H; // 0..1

    public GrenadeRenderer(EntityRendererProvider.Context context, String color) {
        super(context);
        this.color = color;
        this.shadowRadius = 0.15F;
    }

    @Override
    public ResourceLocation getTextureLocation(BaseGrenadeEntity entity) {
        return new ResourceLocation(
                PaintballMod.MODID,
                "textures/entity/projectile/" + color + "_grenade.png"
        );
    }

    @Override
    public void render(BaseGrenadeEntity grenade, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        if (grenade.isStuck()) {
            int stuckLight = computeStuckLight(grenade);
            renderStuckGrenade(grenade, partialTicks, poseStack, buffer, stuckLight);
        } else {
            // tu offset SOLO en vuelo
            poseStack.translate(0, 0.15D, 0);
            renderFlyingGrenade(grenade, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }

        poseStack.popPose();
        super.render(grenade, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private int computeStuckLight(BaseGrenadeEntity e) {
        Direction f = e.getStuckFace();

        double eps = 0.001D;
        double sx = e.getX() - (f == null ? 0 : f.getStepX() * eps);
        double sy = e.getY() - (f == null ? 0 : f.getStepY() * eps);
        double sz = e.getZ() - (f == null ? 0 : f.getStepZ() * eps);

        BlockPos samplePos = BlockPos.containing(sx, sy, sz);
        return LevelRenderer.getLightColor(e.level(), samplePos);
    }

    private void renderStuckGrenade(BaseGrenadeEntity grenade, float partialTicks,
                                    PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        float scale = 0.25F; // tu tamaño clavada
        poseStack.scale(scale, scale, scale);

        // clavado tipo flecha (yaw/pitch reales)
        float yaw = Mth.lerp(partialTicks, grenade.yRotO, grenade.getYRot());
        float pitch = Mth.lerp(partialTicks, grenade.xRotO, grenade.getXRot());

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));

        // shake como pellet
        float shake = grenade.getShakeTime() - partialTicks;
        if (shake > 0.0F) {
            float shakeRot = -Mth.sin(shake * 3.0F) * shake;
            poseStack.mulPose(Axis.ZP.rotationDegrees(shakeRot));
        }

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutout(getTextureLocation(grenade)));

        // Cruz REAL centrada en el pivot (0,0) del sprite
        renderCross(vc, poseStack, 1.0F, 1.0F, packedLight);
    }

    private void renderFlyingGrenade(BaseGrenadeEntity grenade, float entityYaw, float partialTicks,
                                     PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        float scale = 0.3F; // tu tamaño en vuelo
        poseStack.scale(scale, scale, scale);

        float time = grenade.tickCount + partialTicks;
        float startFactor = Math.min(time / 5.0F, 1.0F);

        // tu rotación realista original
        poseStack.mulPose(Axis.YP.rotationDegrees(entityYaw + (time * 4.0F * startFactor)));
        poseStack.mulPose(Axis.XP.rotationDegrees(time * 6.0F * startFactor));
        poseStack.mulPose(Axis.ZP.rotationDegrees(time * 5.0F * startFactor));

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutout(getTextureLocation(grenade)));

        // En vuelo también cruz, pero centrada en el pivot real del sprite
        renderCross(vc, poseStack, 1.0F, 1.0F, packedLight);
    }

    private void renderCross(VertexConsumer vc, PoseStack poseStack,
                             float size, float heightRatio, int light) {

        // Plano 1 (con un pelín de separación)
        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, CROSS_EPS);
        renderPlanePivot(vc, poseStack, size, heightRatio, light);
        poseStack.popPose();

        // Plano 2 (rotado 90°) con separación contraria
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        poseStack.translate(0.0D, 0.0D, -CROSS_EPS);
        renderPlanePivot(vc, poseStack, size, heightRatio, light);
        poseStack.popPose();
    }

    /**
     * Dibuja un quad usando TODO el UV (0..1) pero con geometría ASIMÉTRICA
     * para que el origen (0,0) coincida con el pivot real del sprite dentro del PNG.
     */
    private void renderPlanePivot(VertexConsumer vc, PoseStack poseStack,
                                  float size, float heightRatio, int light) {

        float h = size * heightRatio;

        // Queremos que x=0 => u=PIVOT_U y y=0 => v=PIVOT_V (con v invertida como lo estás dibujando)
        float left  = 2.0F * size * PIVOT_U;        // cuánto hay hacia la izquierda
        float right = 2.0F * size * (1.0F - PIVOT_U);

        float up    = 2.0F * h * PIVOT_V;           // hacia arriba (y positivo en tu quad)
        float down  = 2.0F * h * (1.0F - PIVOT_V);  // hacia abajo

        // UV completo
        float u0 = 0.0F, u1 = 1.0F;
        float vTop = 0.0F, vBot = 1.0F;

        // OJO: tú estás usando y=-h con v=1 y y=+h con v=0 (v invertida). Respetamos eso.
        vertex(vc, poseStack, -left,  -down, 0, u0, vBot, light);
        vertex(vc, poseStack,  right, -down, 0, u1, vBot, light);
        vertex(vc, poseStack,  right,  up,   0, u1, vTop, light);
        vertex(vc, poseStack, -left,   up,   0, u0, vTop, light);

        // backface
        vertex(vc, poseStack, -left,   up,   0, u0, vTop, light);
        vertex(vc, poseStack,  right,  up,   0, u1, vTop, light);
        vertex(vc, poseStack,  right, -down, 0, u1, vBot, light);
        vertex(vc, poseStack, -left,  -down, 0, u0, vBot, light);
    }

    private void vertex(VertexConsumer vc, PoseStack poseStack,
                        float x, float y, float z,
                        float u, float v, int light) {

        PoseStack.Pose pose = poseStack.last();
        vc.vertex(pose.pose(), x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(pose.normal(), 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}