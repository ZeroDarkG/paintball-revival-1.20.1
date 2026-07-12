package com.zerokg2004.paintball.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ModelClaymore extends Model {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(new ResourceLocation("paintball", "claymore"), "main");

    private final ModelPart root;

    public ModelClaymore(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("body",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, 0.0F, -1.0F, 8, 5, 2),
                PartPose.offset(0.0F, 17.5F, 0.0F));

        root.addOrReplaceChild("leg1",
                CubeListBuilder.create()
                        .texOffs(0, 7)
                        .addBox(-3.5F, 4.5F, -0.5F, 1, 2, 1),
                PartPose.offset(0.0F, 17.5F, 0.0F));

        root.addOrReplaceChild("leg2",
                CubeListBuilder.create()
                        .texOffs(0, 7)
                        .addBox(2.5F, 4.5F, -0.5F, 1, 2, 1),
                PartPose.offset(0.0F, 17.5F, 0.0F));

        root.addOrReplaceChild("handle1",
                CubeListBuilder.create()
                        .texOffs(4, 7)
                        .addBox(-0.1F, -2.0F, -0.5F, 3, 1, 1),
                PartPose.offsetAndRotation(0.0F, 17.5F, 0.0F, 0.0F, 0.0F, 0.59341F));

        root.addOrReplaceChild("handle2",
                CubeListBuilder.create()
                        .texOffs(4, 7)
                        .addBox(-2.9F, -2.0F, -0.5F, 3, 1, 1),
                PartPose.offsetAndRotation(0.0F, 17.5F, 0.0F, 0.0F, 0.0F, -0.59341F));

        root.addOrReplaceChild("handle3",
                CubeListBuilder.create()
                        .texOffs(12, 7)
                        .addBox(-1.0F, -1.73F, -0.5F, 2, 1, 1),
                PartPose.offset(0.0F, 17.5F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int light, int overlay,
                               float r, float g, float b, float a) {
        this.root.render(poseStack, buffer, light, overlay);
    }
}