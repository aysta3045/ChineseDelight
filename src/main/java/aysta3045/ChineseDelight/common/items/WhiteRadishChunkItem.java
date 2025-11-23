package aysta3045.ChineseDelight.common.items;

import aysta3045.ChineseDelight.common.registry.ModFoods;
import net.minecraft.world.item.Item;

public class WhiteRadishChunkItem extends Item
{
    public WhiteRadishChunkItem() {
        super(new Item.Properties()
                .food(ModFoods.WHITE_RADISH_CHUNK)
                .stacksTo(64)
        );
    }
}