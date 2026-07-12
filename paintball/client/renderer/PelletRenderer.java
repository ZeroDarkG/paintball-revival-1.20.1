package com.zerokg2004.paintball.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.entity.projectile.BasePelletEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class PelletRenderer extends EntityRenderer<BasePelletEntity> {

    private final String color;

    public PelletRenderer(EntityRendererProvider.Context ctx, String color) {
        super(ctx);
        this.color = color;
    }

    @Override
    public ResourceLocation getTextureLocation(BasePelletEntity e) {
        return new ResourceLocation(PaintballMod.MODID, "textures/entity/projectile/" + color + "_pellet.png");
    }

    @Override
    public void render(BasePelletEntity e, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        poseStack.pushPose();

        int light = this.getPackedLightCoords(e, partialTick);

        float yaw = Mth.lerp(partialTick, e.yRotO, e.getYRot());
        float pitch = Mth.lerp(partialTick, e.xRotO, e.getXRot());

        poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(pitch));

        float shake = e.getShakeTime() - partialTick;
        if (shake > 0.0F) {
            float shakeRot = -Mth.sin(shake * 3.0F) * shake;
            poseStack.mulPose(Axis.ZP.rotationDegrees(shakeRot));
        }

        float s = 0.05625F;
        poseStack.scale(s, s, s);
        poseStack.translate(-4.0D, 0.0D, 0.0D);

        // ✅ SIN ZOffset, SIN epsilons
        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(e)));

        // UVs 32x32 como el original
        float f6 = 0.0F;
        float f7 = 0.15625F;  // 5/32
        float f8 = 0.15625F;  // 5/32
        float f9 = 0.3125F;   // 10/32

        PoseStack.Pose last = poseStack.last();
        Matrix4f mat = last.pose();
        Matrix3f nrm = last.normal();

        // ===== CUERPO (plano X = -7) =====
        quad(mat, nrm, vc,
                -7, -2, -2,
                -7, -2,  2,
                -7,  2,  2,
                -7,  2, -2,
                f6, f8,  f7, f8,  f7, f9,  f6, f9,
                1.0F, 0.0F, 0.0F,
                light
        );

        // ===== ALETAS: SOLO 2 QUADS (evita duplicados sí o sí) =====
        float cu0 = 0.0F;
        float cu1 = 0.5F;
        float cv0 = 0.0F;
        float cv1 = 0.15625F;

        // (opcional) giro 45° para que quede “bonito”
        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));

        // Quad 1
        last = poseStack.last();
        mat = last.pose();
        nrm = last.normal();
        quad(mat, nrm, vc,
                -8, -2, 0,
                8, -2, 0,
                8,  2, 0,
                -8,  2, 0,
                cu0, cv0,  cu1, cv0,  cu1, cv1,  cu0, cv1,
                0.0F, 0.0F, 1.0F,
                light
        );

        // Quad 2 (perpendicular)
        poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        last = poseStack.last();
        mat = last.pose();
        nrm = last.normal();
        quad(mat, nrm, vc,
                -8, -2, 0,
                8, -2, 0,
                8,  2, 0,
                -8,  2, 0,
                cu0, cv0,  cu1, cv0,  cu1, cv1,  cu0, cv1,
                0.0F, 0.0F, 1.0F,
                light
        );

        poseStack.popPose();
        super.render(e, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    private static void quad(Matrix4f mat, Matrix3f nrm, VertexConsumer vc,
                             float x1, float y1, float z1,
                             float x2, float y2, float z2,
                             float x3, float y3, float z3,
                             float x4, float y4, float z4,
                             float u1, float v1,
                             float u2, float v2,
                             float u3, float v3,
                             float u4, float v4,
                             float nx, float ny, float nz,
                             int light) {

        vert(vc, mat, nrm, x1, y1, z1, u1, v1, nx, ny, nz, light);
        vert(vc, mat, nrm, x2, y2, z2, u2, v2, nx, ny, nz, light);
        vert(vc, mat, nrm, x3, y3, z3, u3, v3, nx, ny, nz, light);
        vert(vc, mat, nrm, x4, y4, z4, u4, v4, nx, ny, nz, light);
    }

    private static void vert(VertexConsumer vc, Matrix4f mat, Matrix3f nrm,
                             float x, float y, float z,
                             float u, float v,
                             float nx, float ny, float nz,
                             int light) {

        vc.vertex(mat, x, y, z)
                .color(255, 255, 255, 255)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(light)
                .normal(nrm, nx, ny, nz)
                .endVertex();
    }
}