package aysta3045.ChineseDelight.client.renderer.entity;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.entity.ChineseDelightDuck;
import net.minecraft.client.model.ChickenModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ChineseDelightDuckRenderer extends MobRenderer<ChineseDelightDuck, ChickenModel<ChineseDelightDuck>> {
    private static final ResourceLocation CHINESE_DELIGHT_DUCK_TEXTURE =
            new ResourceLocation(ChineseDelight.MODID, "textures/entity/chinese_delight_duck.png");

    public ChineseDelightDuckRenderer(EntityRendererProvider.Context context) {
        super(context, new ChickenModel<>(context.bakeLayer(ModelLayers.CHICKEN)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(ChineseDelightDuck entity) {
        return CHINESE_DELIGHT_DUCK_TEXTURE;
    }

    @Override
    protected float getBob(ChineseDelightDuck livingBase, float partialTicks) {
        float f = Mth.lerp(partialTicks, livingBase.oFlap, livingBase.flap);
        float f1 = Mth.lerp(partialTicks, livingBase.oFlapSpeed, livingBase.flapSpeed);
        return (Mth.sin(f) + 1.0F) * f1;
    }
}