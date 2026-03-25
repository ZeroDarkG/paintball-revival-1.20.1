package com.zerokg2004.paintball.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.block.ClaymoreBlock;
import com.zerokg2004.paintball.block.entity.ClaymoreBlockEntity;
import com.zerokg2004.paintball.client.model.ModelClaymore;
import com.zerokg2004.paintball.client.model.ModModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public class ClaymoreBlockRenderer implements BlockEntityRenderer<ClaymoreBlockEntity> {

    private final ModelClaymore model;
    private static final String TEXTURE_PATH = "textures/entity/block/%s_claymore.png";

    public ClaymoreBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new ModelClaymore(ctx.bakeLayer(ModModelLayers.CLAYMORE));
    }

    private static ResourceLocation textureFor(DyeColor color) {
        return new ResourceLocation(PaintballMod.MODID, String.format(TEXTURE_PATH, color.getName()));
    }

    @Override
    public void render(ClaymoreBlockEntity be, float pt, PoseStack pose,
                       MultiBufferSource buffers, int packedLight, int packedOverlay) {

        BlockState state = be.getBlockState();
        int rotIndex = state.getValue(ClaymoreBlock.ROT);
        float yRot = rotIndex * (360f / 16f);

        final float PX = 1f / 16f;
        final float MODEL_HEIGHT = 2 * PX;
        final float NUDGE = 0.001f;
        final float EXTRA_DOWN = -14 * PX;

        pose.pushPose();
        pose.translate(0.5, 1.5, 0.5);
        pose.mulPose(Axis.XP.rotationDegrees(180f));
        pose.mulPose(Axis.YP.rotationDegrees(yRot));
        pose.translate(0, -(1.0f - MODEL_HEIGHT) - EXTRA_DOWN + NUDGE, 0);

        DyeColor color = ((ClaymoreBlock) state.getBlock()).getColor();
        ResourceLocation tex = textureFor(color);
        VertexConsumer vc = buffers.getBuffer(RenderType.entityCutoutNoCull(tex));
        model.renderToBuffer(pose, vc, packedLight, OverlayTexture.NO_OVERLAY, 1f, 1f, 1f, 1f);

        pose.popPose();
    }
}