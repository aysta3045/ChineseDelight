package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ChineseDelight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityAttributes {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        // 注册中华美食鸭的属性，使用原版鸡的属性作为基础
        event.put(ModEntityTypes.CHINESE_DELIGHT_DUCK.get(),
                AttributeSupplier.builder()
                        .add(Attributes.MAX_HEALTH, 4.0D)           // 生命值
                        .add(Attributes.MOVEMENT_SPEED, 0.25D)      // 移动速度
                        .add(ForgeMod.ENTITY_GRAVITY.get(), 0.5D)  // 重力值
                        .add(Attributes.KNOCKBACK_RESISTANCE, 0.0D) // 受击击退属性
                        .add(Attributes.FOLLOW_RANGE, 16.0D)        // 跟随范围
                        .add(Attributes.ATTACK_DAMAGE, 0.0D)        // 攻击伤害
                        .add(Attributes.ARMOR, 0.0D)                // 护甲
                        .add(Attributes.ARMOR_TOUGHNESS, 0.0D)      // 护甲韧性
                        .build());
    }
}