package aysta3045.ChineseDelight.client.renderer.entity;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.client.model.DuckModel;
import aysta3045.ChineseDelight.common.entity.ChineseDelightDuck;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class DuckRenderer extends MobRenderer<ChineseDelightDuck, DuckModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ChineseDelight.MODID, "textures/entity/duck.png");
    private static final ResourceLocation BABY_TEXTURE = new ResourceLocation(ChineseDelight.MODID, "textures/entity/duck_baby.png");

    public DuckRenderer(EntityRendererProvider.Context context) {
        super(context, new DuckModel(context.bakeLayer(DuckModel.LAYER_LOCATION)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(ChineseDelightDuck entity) {
        return entity.isBaby() ? BABY_TEXTURE : TEXTURE;
    }

    @Override
    protected void scale(ChineseDelightDuck entity, PoseStack poseStack, float partialTickTime) {
        if (entity.isBaby()) {
            float scale = 0.5F;
            poseStack.scale(scale, scale, scale);
        } else {
            float scale = 1.0F;
            poseStack.scale(scale, scale, scale);
        }

        this.shadowRadius = entity.isBaby() ? 0.15F : 0.3F;
    }
}