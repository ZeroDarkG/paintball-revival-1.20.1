package com.zerokg2004.paintball.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zerokg2004.paintball.block.FlagBlock;
import com.zerokg2004.paintball.block.entity.FlagBlockEntity;
import com.zerokg2004.paintball.client.model.ModelFlag;
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

public class FlagBlockRenderer implements BlockEntityRenderer<FlagBlockEntity> {

    private final ModelFlag model;

    public FlagBlockRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new ModelFlag(ctx.bakeLayer(ModModelLayers.FLAG));
    }

    @Override
    public void render(FlagBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight, int packedOverlay) {

        if (be.getLevel() == null) return;

        long gameTime = be.getLevel().getGameTime();

        // ✅ SIN OFFSET: todas las banderas sincronizadas
        float time = (gameTime + partialTicks) * 0.14F;
        model.setFlagAngle(time);

        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));

        BlockState state = be.getBlockState();
        Direction dir = Direction.NORTH;

        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            dir = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }

        float yRot = dir.toYRot() + 180.0F;
        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));

        DyeColor color = DyeColor.BLUE;
        if (state.getBlock() instanceof FlagBlock flagBlock) {
            color = flagBlock.getColor();
        }

        String name = color.getName();
        ResourceLocation texture = new ResourceLocation(
                "paintball",
                "textures/entity/block/" + name + "_flag.png"
        );

        var vc = bufferSource.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(poseStack, vc, packedLight, packedOverlay,
                1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(FlagBlockEntity be) {
        return true;
    }
}