package aysta3045.ChineseDelight.common.items;

import aysta3045.ChineseDelight.common.registry.ModFoods;
import net.minecraft.world.item.Item;

public class DuckMeatItem extends Item
{
    public DuckMeatItem() {
        super(new Item.Properties()
                .food(ModFoods.DUCK_MEAT)
                .stacksTo(64)
        );
    }
}