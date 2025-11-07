package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChineseDelight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityAttributes {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        // 注册中华美食鸭的属性，使用原版鸡的属性作为基础
        event.put(ModEntityTypes.CHINESE_DELIGHT_DUCK.get(),
                Chicken.createAttributes().build());

        // 如果你想要自定义属性，可以使用以下方式：
        /*
        event.put(ModEntityTypes.CHINESE_DELIGHT_DUCK.get(),
                AttributeSupplier.builder()
                        .add(Attributes.MAX_HEALTH, 4.0D)  // 生命值
                        .add(Attributes.MOVEMENT_SPEED, 0.25D)  // 移动速度
                        .build());
        */
    }
}