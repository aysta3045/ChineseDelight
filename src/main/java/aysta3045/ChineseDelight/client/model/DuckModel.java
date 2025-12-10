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

public class DuckModel extends EntityModel<ChineseDelightDuck> {
    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(ChineseDelight.MODID, "chinese_delight_duck"), "main");
    public static final ModelLayerLocation BABY_LAYER_LOCATION =
            new ModelLayerLocation(new ResourceLocation(ChineseDelight.MODID, "chinese_delight_duck_baby"), "main");

    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart comb;
    private final ModelPart beak;
    private final ModelPart leg0;
    private final ModelPart leg1;
    private final ModelPart wing0;
    private final ModelPart wing1;
    private final boolean isBaby;

    public DuckModel(ModelPart root) {
        this(root, false);
    }

    public DuckModel(ModelPart root, boolean isBaby) {
        this.isBaby = isBaby;

        if (isBaby) {
            // 幼年鸭模型
            this.body = root.getChild("body");
            this.head = this.body.getChild("head");
            this.comb = this.head.getChild("comb");
            this.beak = this.head.getChild("beak");
            this.leg0 = root.getChild("leg0");
            this.leg1 = root.getChild("leg1");
            this.wing0 = root.getChild("wing0");
            this.wing1 = root.getChild("wing1");
        } else {
            // 成年鸭模型
            this.body = root.getChild("body");
            this.head = root.getChild("head");
            this.comb = this.head.getChild("comb");
            this.beak = this.head.getChild("beak");
            this.leg0 = root.getChild("leg0");
            this.leg1 = root.getChild("leg1");
            this.wing0 = root.getChild("wing0");
            this.wing1 = root.getChild("wing1");
        }
    }

    // 创建成年鸭模型层
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

    // 创建幼年鸭模型层
    public static LayerDefinition createBabyBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 9).addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 16.0F, 0.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 6.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -4.0F, -3.0F));

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
        if (isBaby) {

            this.head.yRot = netHeadYaw * ((float)Math.PI / 180F) * 0.5F;
            this.head.xRot = headPitch * ((float)Math.PI / 180F) * 0.5F;

            this.leg0.xRot = Mth.cos(limbSwing * 0.6662F) * 0.7F * limbSwingAmount;
            this.leg1.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 0.7F * limbSwingAmount;

            if (!entity.onGround()) {
                float wingFlap = Mth.sin(ageInTicks * 2.0F) * 0.8F;
                this.wing0.zRot = wingFlap;
                this.wing1.zRot = -wingFlap;
            } else {
                this.wing0.zRot = 0.0F;
                this.wing1.zRot = 0.0F;
            }
        } else {
            this.head.yRot = netHeadYaw * ((float)Math.PI / 180F);
            this.head.xRot = headPitch * ((float)Math.PI / 180F);

            this.leg0.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
            this.leg1.xRot = Mth.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;

            if (!entity.onGround()) {
                float wingFlap = Mth.sin(ageInTicks * 1.8F) * 1.2F;
                this.wing0.zRot = wingFlap;
                this.wing1.zRot = -wingFlap;
            } else {
                this.wing0.zRot = 0.0F;
                this.wing1.zRot = 0.0F;
            }
        }
    }

    @Override
    public void renderToBuffer(com.mojang.blaze3d.vertex.PoseStack poseStack, com.mojang.blaze3d.vertex.VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (isBaby) {

            body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            leg0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            leg1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            wing0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            wing1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);

            poseStack.popPose();
        } else {
            body.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            leg0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            leg1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            wing0.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
            wing1.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }
    }

    public static DuckModel createBabyModel(ModelPart root) {
        return new DuckModel(root, true);
    }
}