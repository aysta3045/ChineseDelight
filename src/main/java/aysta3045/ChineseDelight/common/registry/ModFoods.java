package aysta3045.ChineseDelight.common.registry;

import net.minecraft.world.food.FoodProperties;

// 食物属性定义
public class ModFoods {
    // 热干面
    public static final FoodProperties HOT_DRY_NOODLE = new FoodProperties.Builder()
            .nutrition(6) // 饱食度
            .saturationMod(8f) // 饱和度
            .build();

    // 生鸭肉
    public static final FoodProperties DUCK_MEAT = new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(0.3f)
            .build();

    // 熟鸭肉
    public static final FoodProperties COOKED_DUCK_MEAT = new FoodProperties.Builder()
            .nutrition(7)
            .saturationMod(7f)
            .build();
}