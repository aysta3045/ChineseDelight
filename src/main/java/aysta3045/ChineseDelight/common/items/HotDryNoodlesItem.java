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

public class HotDryNoodlesItem extends Item
{
    // 定义食物属性
    private static final FoodProperties FOOD_PROPERTIES = new FoodProperties.Builder()
            .nutrition(6)
            .saturationMod(8f)
            .build();

    public HotDryNoodlesItem() {
        super(new Item.Properties()
                .food(FOOD_PROPERTIES)
                .rarity(Rarity.RARE)
                .stacksTo(16)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        tooltip.add(Component.translatable("item.chinesedelight.hot_dry_noodles.tooltip"));
    }
}