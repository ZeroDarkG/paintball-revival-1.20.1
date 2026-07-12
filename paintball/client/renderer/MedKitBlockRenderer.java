package com.zerokg2004.paintball.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zerokg2004.paintball.block.MedKitBlock;
import com.zerokg2004.paintball.block.entity.MedKitBlockEntity;
import com.zerokg2004.paintball.client.model.ModelMedKit;
import com.zerokg2004.paintball.client.model.ModModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.state.BlockState;

public class MedKitBlockRenderer implements BlockEntityRenderer<MedKitBlockEntity> {

    private final ModelMedKit model;

    public MedKitBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new ModelMedKit(ctx.bakeLayer(ModModelLayers.MEDKIT));
    }

    @Override
    public void render(MedKitBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        if (be.getLevel() == null) return;

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        BlockState state = be.getBlockState();
        float yRot = 0.0F;
        if (state.getBlock() instanceof MedKitBlock) {
            yRot = state.getValue(MedKitBlock.FACING).toYRot();
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
        DyeColor color = DyeColor.BLUE;
        if (state.getBlock() instanceof MedKitBlock medkitBlock) {
            color = medkitBlock.getColor();
        }
        String colorName = color.getName();
        int stage = be.getChargeStage();

        String suffix = switch (stage) {
            case 0 -> "1";
            case 1 -> "2";
            default -> "3";
        };

        ResourceLocation texture = new ResourceLocation(
                "paintball",
                "textures/entity/block/" + colorName + "_medkit" + suffix + ".png"
        );

        var vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(poseStack, vc, packedLight, packedOverlay,
                1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(MedKitBlockEntity be) {
        return true;
    }
}