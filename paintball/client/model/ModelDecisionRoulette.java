package com.zerokg2004.paintball.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public class ModelDecisionRoulette extends Model {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation("paintball", "decision_roulette"), "main");

    private final ModelPart root;
    private final ModelPart arrow;

    public ModelDecisionRoulette(ModelPart root) {
        super(RenderType::entityCutoutNoCull);
        this.root = root;
        this.arrow = root.getChild("roulette_arrow");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        PartDefinition roulette = partdefinition.addOrReplaceChild("roulette",
                CubeListBuilder.create()
                        .texOffs(0, 0).addBox(-10.0F, -22.0F, -1.0F, 20.0F, 20.0F, 2.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 28).addBox(4.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition roulette_arrow = partdefinition.addOrReplaceChild("roulette_arrow",
                CubeListBuilder.create()
                        .texOffs(34, 23).addBox(-2.0F, -10.0F, -2.0F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                        .texOffs(44, 24).addBox(-1.0F, -8.0F, -2.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
    public void setArrowAngle(float angleRad) {
        this.arrow.zRot = angleRad;   // eje Z en vez de Y
    }
    @Override
    public void renderToBuffer(
            PoseStack poseStack,
            VertexConsumer vertexConsumer,
            int packedLight,
            int packedOverlay,
            float red, float green, float blue, float alpha
    ) {
        root.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}