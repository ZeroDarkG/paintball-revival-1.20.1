package com.zerokg2004.paintball.client.renderer;

import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.block.C4Block;
import net.minecraft.core.Direction;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zerokg2004.paintball.block.entity.C4BlockEntity;
import com.zerokg2004.paintball.client.model.ModelC4;
import com.zerokg2004.paintball.client.model.ModModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class C4BlockRenderer implements BlockEntityRenderer<C4BlockEntity> {
    private final ModelC4 model;
    private static final String TEXTURE_PATH = "textures/entity/block/%s_c4.png";

    public C4BlockRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new ModelC4(context.bakeLayer(ModModelLayers.C4));
    }

    @Override
    public void render(C4BlockEntity blockEntity, float partialTicks, PoseStack poseStack,
                      MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5D, 1.5D, 0.5D);
        Direction direction = blockEntity.getBlockState().getValue(C4Block.FACING);
        float angle = switch (direction) {
            case NORTH -> 0f;
            case EAST -> 90f;
            case SOUTH -> 180f;
            case WEST -> 270f;
            default -> 0f;
        };
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(angle));
        poseStack.scale(1.0F, -1.0F, -1.0F);
        DyeColor color = blockEntity.getColor();
        String colorName = color != null ? color.getName() : "blue"; // Color por defecto si es null
        ResourceLocation texture = new ResourceLocation(PaintballMod.MODID,
            String.format(TEXTURE_PATH, colorName));
        model.renderToBuffer(poseStack, buffer.getBuffer(model.renderType(texture)),
                packedLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }
}