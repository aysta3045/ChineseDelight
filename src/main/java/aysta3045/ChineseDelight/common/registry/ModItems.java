package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.items.HotDryNoodlesItem;
import aysta3045.ChineseDelight.common.items.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ChineseDelight.MODID);
    // 注册物品

    // 热干面
    public static final RegistryObject<Item> HOT_DRY_NOODLES = ITEMS.register("hot_dry_noodles",
            HotDryNoodlesItem::new);
}