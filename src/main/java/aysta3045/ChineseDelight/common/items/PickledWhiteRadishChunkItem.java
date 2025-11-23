package aysta3045.ChineseDelight.common.items;

import aysta3045.ChineseDelight.common.registry.ModFoods;
import net.minecraft.world.item.Item;

public class PickledWhiteRadishChunkItem extends Item
{
    public PickledWhiteRadishChunkItem() {
        super(new Item.Properties()
                .food(ModFoods.PLCKLED_WHITE_RADISH_CHUNK)
                .stacksTo(64)
        );
    }
}