package aysta3045.ChineseDelight.client;

import aysta3045.ChineseDelight.common.registry.ModEntityTypes;
import aysta3045.ChineseDelight.client.renderer.entity.*;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "chinesedelight", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class EntityRendererRegistry {

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {

            // 鸭实体渲染器
            EntityRenderers.register(ModEntityTypes.CHINESE_DELIGHT_DUCK.get(), DuckRenderer::new);

            // 鸭蛋实体渲染器
            EntityRenderers.register(ModEntityTypes.DUCK_EGG.get(), ThrownItemRenderer::new);
        });
    }
}