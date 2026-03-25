package com.zerokg2004.paintball.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.world.entity.Entity;

public class ModelMedKit extends HierarchicalModel<Entity> {

    public static final String MAIN = "main";
    public static final String HANDLE1 = "handle1";
    public static final String HANDLE2 = "handle2";
    public static final String HANDLE3 = "handle3";

    private final ModelPart root;
    private final ModelPart main;
    private final ModelPart handle1;
    private final ModelPart handle2;
    private final ModelPart handle3;

    public ModelMedKit(ModelPart root) {
        this.root = root;
        this.main = root.getChild(MAIN);
        this.handle1 = root.getChild(HANDLE1);
        this.handle2 = root.getChild(HANDLE2);
        this.handle3 = root.getChild(HANDLE3);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        var root = mesh.getRoot();

        root.addOrReplaceChild(
                MAIN,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                4.0F, 8.0F, 12.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        -6.0F, 16.0F, 2.0F,
                        0.0F, (float) Math.PI / 2F, 0.0F
                )
        );

        root.addOrReplaceChild(
                HANDLE1,
                CubeListBuilder.create()
                        .texOffs(15, 22)
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                1.0F, 3.0F, 1.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        2.0F, 13.0F, 0.5F,
                        0.0F, (float) Math.PI / 2F, 0.0F
                )
        );

        root.addOrReplaceChild(
                HANDLE2,
                CubeListBuilder.create()
                        .texOffs(9, 22)
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                1.0F, 3.0F, 1.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        -3.0F, 13.0F, 0.5F,
                        0.0F, (float) Math.PI / 2F, 0.0F
                )
        );

        root.addOrReplaceChild(
                HANDLE3,
                CubeListBuilder.create()
                        .texOffs(17, 24)
                        .addBox(
                                0.0F, 0.0F, 0.0F,
                                1.0F, 1.0F, 6.0F,
                                new CubeDeformation(0.0F)
                        ),
                PartPose.offsetAndRotation(
                        -3.0F, 13.0F, 0.5F,
                        0.0F, (float) Math.PI / 2F, 0.0F
                )
        );

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void setupAnim(
            Entity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
    }

    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer vc,
            int packedLight,
            int packedOverlay,
            float r,
            float g,
            float b,
            float a
    ) {
        root.render(poseStack, vc, packedLight, packedOverlay, r, g, b, a);
    }

    @Override
    public ModelPart root() {
        return root;
    }
}