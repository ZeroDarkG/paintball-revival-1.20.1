package com.zerokg2004.paintball.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.renderer.RenderType;

public class ModelWeaponRack extends Model {

    private final ModelPart base;
    private final ModelPart rack;
    private final ModelPart display1;
    private final ModelPart display2;
    private final ModelPart display3;
    private final ModelPart display4;
    private final ModelPart bar;
    private final ModelPart shelf1;
    private final ModelPart shelf2;
    private final ModelPart shelf3;
    private final ModelPart shelf4;

    public ModelWeaponRack(ModelPart root) {
        super(RenderType::entityCutoutNoCull);

        this.base     = root.getChild("base");
        this.rack     = root.getChild("rack");
        this.display1 = root.getChild("display1");
        this.display2 = root.getChild("display2");
        this.display3 = root.getChild("display3");
        this.display4 = root.getChild("display4");
        this.bar      = root.getChild("bar");
        this.shelf1   = root.getChild("shelf1");
        this.shelf2   = root.getChild("shelf2");
        this.shelf3   = root.getChild("shelf3");
        this.shelf4   = root.getChild("shelf4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild(
                "base",
                CubeListBuilder.create()
                        .texOffs(1, 1)
                        .addBox(0.0F, 0.0F, 0.0F,
                                16.0F, 2.0F, 10.0F),
                PartPose.offset(-8.0F, 22.0F, -5.0F)
        );

        root.addOrReplaceChild(
                "rack",
                CubeListBuilder.create()
                        .texOffs(1, 14)
                        .addBox(0.0F, 0.0F, 0.0F,
                                16.0F, 11.0F, 2.0F),
                PartPose.offset(-8.0F, 11.0F, -1.0F)
        );

        root.addOrReplaceChild(
                "display1",
                CubeListBuilder.create()
                        .texOffs(38, 44)
                        .addBox(0.0F, 0.0F, 0.0F,
                                8.0F, 8.0F, 0.0F),
                PartPose.offset(0.0F, 12.0F, -3.1F)
        );

        root.addOrReplaceChild(
                "display2",
                CubeListBuilder.create()
                        .texOffs(38, 34)
                        .addBox(0.0F, 0.0F, 0.0F,
                                8.0F, 8.0F, 0.0F),
                PartPose.offset(-8.0F, 12.0F, -3.1F)
        );

        root.addOrReplaceChild(
                "display3",
                CubeListBuilder.create()
                        .texOffs(38, 14)
                        .addBox(0.0F, 0.0F, 0.0F,
                                8.0F, 8.0F, 0.0F),
                PartPose.offset(0.0F, 12.0F, 3.1F)
        );

        root.addOrReplaceChild(
                "display4",
                CubeListBuilder.create()
                        .texOffs(38, 24)
                        .addBox(0.0F, 0.0F, 0.0F,
                                8.0F, 8.0F, 0.0F),
                PartPose.offset(-8.0F, 12.0F, 3.1F)
        );

        root.addOrReplaceChild(
                "bar",
                CubeListBuilder.create()
                        .texOffs(1, 32)
                        .addBox(0.0F, 0.0F, 0.0F,
                                16.0F, 2.0F, 1.0F),
                PartPose.offset(-8.0F, 9.0F, -0.5F)
        );

        root.addOrReplaceChild(
                "shelf1",
                CubeListBuilder.create()
                        .texOffs(1, 28)
                        .addBox(0.0F, 0.0F, 0.0F,
                                16.0F, 1.0F, 2.0F),
                PartPose.offset(-8.0F, 12.0F, 1.0F)
        );

        root.addOrReplaceChild(
                "shelf2",
                CubeListBuilder.create()
                        .texOffs(1, 28)
                        .addBox(0.0F, 0.0F, 0.0F,
                                16.0F, 1.0F, 2.0F),
                PartPose.offset(-8.0F, 12.0F, -3.0F)
        );

        root.addOrReplaceChild(
                "shelf3",
                CubeListBuilder.create()
                        .texOffs(1, 28)
                        .addBox(0.0F, 0.0F, 0.0F,
                                16.0F, 1.0F, 2.0F),
                PartPose.offset(-8.0F, 19.0F, 1.0F)
        );

        root.addOrReplaceChild(
                "shelf4",
                CubeListBuilder.create()
                        .texOffs(1, 28)
                        .addBox(0.0F, 0.0F, 0.0F,
                                16.0F, 1.0F, 2.0F),
                PartPose.offset(-8.0F, 19.0F, -3.0F)
        );

        return LayerDefinition.create(mesh, 128, 64);
    }

    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer buffer,
            int packedLight,
            int packedOverlay,
            float red, float green, float blue, float alpha
    ) {
        base.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        rack.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        display1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        display2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        display3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        display4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        bar.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        shelf1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        shelf2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        shelf3.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        shelf4.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}