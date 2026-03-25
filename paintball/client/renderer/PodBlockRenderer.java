package com.zerokg2004.paintball.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zerokg2004.paintball.block.PodBlock;
import com.zerokg2004.paintball.block.entity.PodBlockEntity;
import com.zerokg2004.paintball.client.model.ModelPod;
import com.zerokg2004.paintball.client.model.ModModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class PodBlockRenderer implements BlockEntityRenderer<PodBlockEntity> {

    private final ModelPod model;

    public PodBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new ModelPod(ctx.bakeLayer(ModModelLayers.POD));
    }

    @Override
    public void render(PodBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        if (be.getLevel() == null) return;

        BlockState state = be.getBlockState();

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        Direction dir = Direction.NORTH;
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
        float yRot = dir.toYRot();
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));

        DyeColor color = DyeColor.BLUE;
        if (state.getBlock() instanceof PodBlock podBlock) {
            color = podBlock.getColor();
        }
        String colorName = color.getName();

        int stage = be.getChargeStage();
        int texIndex = Math.min(Math.max(stage, 0), 4) + 1;

        ResourceLocation texture = new ResourceLocation(
                "paintball",
                "textures/entity/block/" + colorName + "_pod" + texIndex + ".png"
        );

        var vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(poseStack, vc, packedLight, packedOverlay,
                1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(PodBlockEntity be) {
        return true;
    }
}