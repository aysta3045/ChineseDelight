package aysta3045.ChineseDelight.client.renderer.entity;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.client.model.ChineseDelightDuckModel;
import aysta3045.ChineseDelight.common.entity.ChineseDelightDuck;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ChineseDelightDuckRenderer extends MobRenderer<ChineseDelightDuck, ChineseDelightDuckModel> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ChineseDelight.MODID, "textures/entity/duck.png");

    public ChineseDelightDuckRenderer(EntityRendererProvider.Context context) {
        super(context, new ChineseDelightDuckModel(context.bakeLayer(ChineseDelightDuckModel.LAYER_LOCATION)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(ChineseDelightDuck entity) {
        return TEXTURE;
    }
}