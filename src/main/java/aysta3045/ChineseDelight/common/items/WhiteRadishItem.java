package aysta3045.ChineseDelight.common.items;

import aysta3045.ChineseDelight.common.registry.ModFoods;
import net.minecraft.world.item.Item;

public class WhiteRadishItem extends Item
{
    public WhiteRadishItem() {
        super(new Item.Properties()
                .food(ModFoods.WHITE_RADISH)
                .stacksTo(64)
        );
    }
}