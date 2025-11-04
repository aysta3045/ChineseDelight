package aysta3045.ChineseDelight.common.items;

import aysta3045.ChineseDelight.common.registry.ModFoods;
import net.minecraft.world.item.Item;

public class CookedDuckMeatItem extends Item
{
    public CookedDuckMeatItem() {
        super(new Item.Properties()
                .food(ModFoods.COOKED_DUCK_MEAT)
                .stacksTo(64)
        );
    }
}