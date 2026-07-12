package com.zerokg2004.paintball.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;

public class ModelPod extends Model {

    private final ModelPart root;
    private final ModelPart base;
    private final ModelPart pod;

    public ModelPod(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
        this.base = root.getChild("Base");
        this.pod = root.getChild("Pod");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "Base",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(
                                -2.5F, 0.0F, -2.5F,
                                5.0F, 1.0F, 5.0F
                        ),
                PartPose.offset(0.0F, 23.0F, 0.0F)
        );

        root.addOrReplaceChild(
                "Pod",
                CubeListBuilder.create()
                        .texOffs(0, 6)
                        .addBox(
                                -1.5F, -0.01F, -1.5F,
                                3.0F, 8.0F, 3.0F
                        ),
                PartPose.offset(0.0F, 15.0F, 0.0F)
        );

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            float red,
            float green,
            float blue,
            float alpha
    ) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}