package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.items.*;
import aysta3045.ChineseDelight.common.blocks.*;
import net.minecraft.world.item.BlockItem;
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

    // 芝麻
    public static final RegistryObject<Item> SESAME_SEEDS = ITEMS.register("sesame_seeds",
            () -> new SesameSeedsItem(ModBlocks.SESAME_CROP.get(), new Item.Properties()));

    // 芝麻穗
    public static final RegistryObject<Item> SESAME_SPIKE = ITEMS.register("sesame_spike",
            () -> new SesameSpikeItem(new Item.Properties()));

    // 一捆芝麻穗
    public static final RegistryObject<Item> SESAME_BALE = ITEMS.register("sesame_bale",
            () -> new BlockItem(ModBlocks.SESAME_BALE.get(), new Item.Properties()));
}