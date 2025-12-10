package aysta3045.ChineseDelight.common.items;

import aysta3045.ChineseDelight.common.registry.ModFoods;
import net.minecraft.world.item.Item;

public class RiceWineItem extends Item
{
    public RiceWineItem() {
        super(new Item.Properties()
                .food(ModFoods.RICE_WINE)
                .stacksTo(16)
        );
    }
}