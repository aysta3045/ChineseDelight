package aysta3045.ChineseDelight.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.food.FoodProperties;

import javax.annotation.Nullable;
import java.util.List;

public class ALargeBottleOfIcedBlackTeaItem extends Item
{
    // 定义食物属性
    private static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .nutrition(2) // 饱食度
            .saturationMod(2.4f) // 饱和度
            .build();

    public ALargeBottleOfIcedBlackTeaItem() {
        super(new Item.Properties()
                .food(FOOD_PROPERTIES)
                .rarity(Rarity.RARE)
                .stacksTo(16)
        );
    }

    // 添加物品详细信息
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.chinesedelight.a_large_bottle_of_iced_black_tea.description"));
    }
}