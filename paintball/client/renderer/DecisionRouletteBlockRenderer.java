package com.zerokg2004.paintball.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.block.DecisionRouletteBlock;
import com.zerokg2004.paintball.block.entity.DecisionRouletteBlockEntity;
import com.zerokg2004.paintball.client.model.ModelDecisionRoulette;
import com.zerokg2004.paintball.client.model.ModModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class DecisionRouletteBlockRenderer implements BlockEntityRenderer<DecisionRouletteBlockEntity> {

    private final ModelDecisionRoulette model;
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(PaintballMod.MODID, "textures/entity/block/roulette.png");

    public DecisionRouletteBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new ModelDecisionRoulette(ctx.bakeLayer(ModModelLayers.ROULETTE));
    }

    @Override
    public void render(DecisionRouletteBlockEntity be,
                       float partialTicks,
                       PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight,
                       int packedOverlay) {

        if (be.getLevel() == null) return;

        BlockState state = be.getBlockState();
        Direction facing = state.getValue(DecisionRouletteBlock.FACING);

        poseStack.pushPose();
        poseStack.translate(0.5D, 0.0D, 0.5D);
        float rotY = -facing.toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotY));
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        float scale = 2.1F;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(0.0D, -1.50D, 0.0D);
        float angle = be.getArrowAngle(partialTicks);
        model.setArrowAngle(angle);

        var vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        model.renderToBuffer(poseStack, vc, packedLight, packedOverlay,
                1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(DecisionRouletteBlockEntity be) {
        return true;
    }
}