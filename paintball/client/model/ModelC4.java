package com.zerokg2004.paintball.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.RenderType;

public class ModelC4 extends Model {
    private final ModelPart strap1;
    private final ModelPart strap2;
    private final ModelPart lump;
    private final ModelPart body;

    public ModelC4(ModelPart root) {
        super(RenderType::entitySolid);
        this.strap1 = root.getChild("strap1");
        this.strap2 = root.getChild("strap2");
        this.lump = root.getChild("lump");
        this.body = root.getChild("body");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("strap1", CubeListBuilder.create()
                .texOffs(24, 0)
                .addBox(-3.0F, 20.0F, -4.0F, 1, 4, 8),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("strap2", CubeListBuilder.create()
                .texOffs(24, 0)
                .addBox(2.0F, 20.0F, -4.0F, 1, 4, 8),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("lump", CubeListBuilder.create()
                .texOffs(0, 0)
                .addBox(-1.0F, 20.0F, -1.0F, 2, 1, 2),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
                .texOffs(0, 19)
                .addBox(-4.0F, 21.0F, -3.0F, 8, 3, 6),
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        strap1.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        strap2.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        lump.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
        body.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}