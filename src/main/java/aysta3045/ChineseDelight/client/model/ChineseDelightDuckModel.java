package aysta3045.ChineseDelight.client.model;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.entity.ChineseDelightDuck;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ChineseDelightDuckModel extends EntityModel<ChineseDelightDuck> {
    // 修复了LAYER_LOCATION的资源位置
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(ChineseDelight.MODID, "chinese_delight_duck"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart comb;
    private final ModelPart beak;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart wing0;
    private final ModelPart wing1;

    public ChineseDelightDuckModel(ModelPart root) {
        this.body = root.getChild("body");
        this.head = root.getChild("head");
        this.comb = this.head.getChild("comb");
        this.beak = this.head.getChild("beak");
        this.leg0 = root.getChild("leg0");
        this.leg1 = root.getChild("leg1");
        this.wing0 = root.getChild("wing0");
        this.wing1 = root.getChild("wing1");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 9).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 15.0F, -4.0F));

        PartDefinition comb = head.addOrReplaceChild("comb", CubeListBuilder.create().texOffs(14, 4).addBox(-1.0F, -2.0F, -3.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition beak = head.addOrReplaceChild("beak", CubeListBuilder.create().texOffs(14, 0).addBox(-2.0F, -4.0F, -4.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition leg0 = partdefinition.addOrReplaceChild("leg0", CubeListBuilder.create().texOffs(26, 0).addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-2.0F, 19.0F, 1.0F));

        PartDefinition leg1 = partdefinition.addOrReplaceChild("leg1", CubeListBuilder.create().texOffs(26, 0).addBox(-1.0F, 0.0F, -3.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(1.0F, 19.0F, 1.0F));

        PartDefinition wing0 = partdefinition.addOrReplaceChild("wing0", CubeListBuilder.create().texOffs(24, 13).addBox(-1.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-3.0F, 13.0F, 0.0F));

        PartDefinition wing1 = partdefinition.addOrReplaceChild("wing1", CubeListBuilder.create().texOffs(24, 13).addBox(0.0F, 0.0F, -3.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(3.0F, 13.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 32);
    }

    @Override
    public void setupAnim(ChineseDelightDuck entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        // 头部旋转动画
        this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
        this.head.xRot = headPitch * ((float)Math.PI / 180F);

        // 腿部行走动画
        this.leg0.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leg1.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

        // 翅膀拍打动画
        this.wing0.zRot = ageInTicks;
        this.wing1.zRot = -ageInTicks;

        // 更自然的翅膀动画（类似原版鸡）
        float wingMovement = Mth.sin(ageInTicks) * 0.2F;
        this.wing0.yRot = wingMovement;
        this.wing1.yRot = -wingMovement;
    }

    @Override
    public void renderToBuffer(com.mojang.blaze3d.vertex.PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leg0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        leg1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        wing0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        wing1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}