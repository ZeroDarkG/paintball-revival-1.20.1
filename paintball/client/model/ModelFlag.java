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
import net.minecraft.util.Mth;

public class ModelFlag extends Model {

    private final ModelPart root;
    private final ModelPart flag1;
    private final ModelPart flag2;
    private final ModelPart flag3;
    private final ModelPart flag4;

    public ModelFlag(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
        this.flag1 = root.getChild("Flag1");
        this.flag2 = root.getChild("Flag2");
        this.flag3 = root.getChild("Flag3");
        this.flag4 = root.getChild("Flag4");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("Top",
                CubeListBuilder.create()
                        .texOffs(1, 20)
                        .addBox(-1.5F, -15.0F, -1.5F, 3, 1, 3),
                PartPose.offset(-2.0F, 23.0F, 0.0F));

        root.addOrReplaceChild("Pole",
                CubeListBuilder.create()
                        .texOffs(43, 1)
                        .addBox(-1.0F, -14.0F, -1.0F, 2, 13, 2),
                PartPose.offset(-2.0F, 23.0F, 0.0F));

        root.addOrReplaceChild("Flag1",
                CubeListBuilder.create()
                        .texOffs(15, 21)
                        .addBox(1.0F, -13.0F, -0.5F, 2, 8, 1),
                PartPose.offset(-2.0F, 23.0F, 0.0F));

        root.addOrReplaceChild("Flag2",
                CubeListBuilder.create()
                        .texOffs(22, 21)
                        .addBox(3.0F, -14.0F, -0.5F, 3, 8, 1),
                PartPose.offset(-2.0F, 23.0F, 0.0F));

        root.addOrReplaceChild("Flag3",
                CubeListBuilder.create()
                        .texOffs(31, 21)
                        .addBox(6.0F, -13.0F, -0.5F, 3, 8, 1),
                PartPose.offset(-2.0F, 23.0F, 0.0F));

        root.addOrReplaceChild("Flag4",
                CubeListBuilder.create()
                        .texOffs(40, 21)
                        .addBox(9.0F, -14.0F, -0.5F, 1, 8, 1),
                PartPose.offset(-2.0F, 23.0F, 0.0F));

        root.addOrReplaceChild("Base1",
                CubeListBuilder.create()
                        .texOffs(1, 13)
                        .addBox(-2.5F, -1.0F, -2.5F, 5, 1, 5),
                PartPose.offset(-2.0F, 23.0F, 0.0F));

        root.addOrReplaceChild("Base2",
                CubeListBuilder.create()
                        .texOffs(1, 1)
                        .addBox(-5.0F, 0.0F, -5.0F, 10, 1, 10),
                PartPose.offset(-2.0F, 23.0F, 0.0F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    public void setFlagAngle(float time) {
        float baseAmp = 0.30F;

        float a1 = Mth.sin(time          ) * baseAmp;
        float a2 = Mth.sin(time - 0.18F) * baseAmp;
        float a3 = Mth.sin(time - 0.36F) * baseAmp;
        float a4 = Mth.sin(time - 0.54F) * baseAmp;

        this.flag1.xRot = this.flag2.xRot = this.flag3.xRot = this.flag4.xRot = 0.0F;
        this.flag1.zRot = this.flag2.zRot = this.flag3.zRot = this.flag4.zRot = 0.0F;

        this.flag1.yRot = a1;
        this.flag2.yRot = a2;
        this.flag3.yRot = a3;
        this.flag4.yRot = a4;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}