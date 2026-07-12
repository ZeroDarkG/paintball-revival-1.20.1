package com.zerokg2004.paintball.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zerokg2004.paintball.PaintballMod;
import com.zerokg2004.paintball.block.WeaponRackBlock;
import com.zerokg2004.paintball.block.entity.WeaponRackBlockEntity;
import com.zerokg2004.paintball.client.model.ModelWeaponRack;
import com.zerokg2004.paintball.client.model.ModModelLayers;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

public class WeaponRackRenderer implements BlockEntityRenderer<WeaponRackBlockEntity> {

    public static final ModelLayerLocation LAYER = ModModelLayers.WEAPON_RACK;

    private final ModelWeaponRack model;

    public WeaponRackRenderer(BlockEntityRendererProvider.Context ctx) {
        this.model = new ModelWeaponRack(ctx.bakeLayer(LAYER));
    }

    @Override
    public void render(WeaponRackBlockEntity be, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {

        if (be.getLevel() == null) return;

        poseStack.pushPose();

        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180.0F));

        Direction dir = be.getBlockState().getValue(WeaponRackBlock.FACING);
        float yRot = switch (dir) {
            case NORTH -> 180.0F;
            case SOUTH -> 0.0F;
            case WEST  -> 90.0F;
            case EAST  -> -90.0F;
            default    -> 0.0F;
        };

        yRot += 180.0F;

        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));

        DyeColor color = DyeColor.BLUE;
        if (be.getBlockState().getBlock() instanceof WeaponRackBlock rackBlock) {
            color = rackBlock.getColor();
        }

        int texIndex = be.getTextureIndex();
        if (texIndex < 1 || texIndex > 6) texIndex = 1;

        String colorName = color.getName().toLowerCase();
        ResourceLocation texture = new ResourceLocation(
                PaintballMod.MODID,
                "textures/entity/block/" + colorName + "_weapon_rack" + texIndex + ".png"
        );

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(poseStack, vc, packedLight, packedOverlay, 1F, 1F, 1F, 1F);

        poseStack.popPose();
    }
}