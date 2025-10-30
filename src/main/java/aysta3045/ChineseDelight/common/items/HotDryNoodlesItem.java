package aysta3045.ChineseDelight.common.items;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

import static aysta3045.ChineseDelight.common.registry.ModFoods.FOOD_PROPERTIES;

public class HotDryNoodlesItem extends Item
{
    public HotDryNoodlesItem() {
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
        tooltip.add(Component.translatable("item.chinesedelight.hot_dry_noodles.description"));
    }
}