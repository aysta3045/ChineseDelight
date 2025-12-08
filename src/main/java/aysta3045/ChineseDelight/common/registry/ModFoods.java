package aysta3045.ChineseDelight.common.registry;

import net.minecraft.world.food.FoodProperties;

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

    // 白萝卜
    public static final FoodProperties WHITE_RADISH = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(1f)
            .build();

    // 白萝卜块
    public static final FoodProperties WHITE_RADISH_CHUNK = new FoodProperties.Builder()
            .nutrition(1)
            .saturationMod(0.5f)
            .build();

    // 腌白萝卜块
    public static final FoodProperties PLCKLED_WHITE_RADISH_CHUNK = new FoodProperties.Builder()
            .nutrition(3)
            .saturationMod(4f)
            .build();
}