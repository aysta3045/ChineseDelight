package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.common.items.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, aysta3045.ChineseDelight.ChineseDelight.MODID);
    // 注册物品

    // 热干面
    public static final RegistryObject<Item> HOT_DRY_NOODLES = ITEMS.register("hot_dry_noodles",
            HotDryNoodlesItem::new);

    // 芝麻
    public static final RegistryObject<Item> SESAME = ITEMS.register("sesame",
            () -> new SesameItem(ModBlocks.SESAME_CROP.get(), new Item.Properties()));

    // 芝麻穗
    public static final RegistryObject<Item> SESAME_SPIKE = ITEMS.register("sesame_spike",
            () -> new SesameSpikeItem(new Item.Properties()));

    // 一捆芝麻穗
    public static final RegistryObject<Item> SESAME_BALE = ITEMS.register("sesame_bale",
            () -> new BlockItem(ModBlocks.SESAME_BALE.get(), new Item.Properties()));

    // 牛油
    public static final RegistryObject<Item> BEEF_TALLOW = ITEMS.register("beef_tallow",
            () -> new BeefTallowItem(new Item.Properties()));

    // 熬油锅
    public static final RegistryObject<Item> OIL_COOKING_POT = ITEMS.register("oil_cooking_pot",
            () -> new BlockItem(ModBlocks.OIL_COOKING_POT.get(), new Item.Properties()));

    // 牛油块
    public static final RegistryObject<Item> BEEF_TALLOW_BLOCK = ITEMS.register("beef_tallow_block",
            () -> new BlockItem(ModBlocks.BEEF_TALLOW_BLOCK.get(), new Item.Properties()));

    // 生鸭肉
    public static final RegistryObject<Item> DUCK_MEAT = ITEMS.register("duck_meat",
            DuckMeatItem::new);

    // 熟鸭肉
    public static final RegistryObject<Item> COOKED_DUCK_MEAT = ITEMS.register("cooked_duck_meat",
            CookedDuckMeatItem::new);

    // 鸭毛
    public static final RegistryObject<Item> DUCK_FEATHER = ITEMS.register("duck_feather",
            () -> new DuckEggItem(new Item.Properties()));

    // 发酵罐
    public static final RegistryObject<Item> FERMENTATION_JAR = ITEMS.register("fermentation_jar",
            () -> new BlockItem(ModBlocks.FERMENTATION_JAR.get(), new Item.Properties()));

    // 鸭刷怪蛋
    public static final RegistryObject<Item> CHINESE_DELIGHT_DUCK_SPAWN_EGG = ITEMS.register("chinese_delight_duck_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntityTypes.CHINESE_DELIGHT_DUCK,
                    0xA0522D, 0xFFD700, // 棕色身体，金色喙
                    new Item.Properties()));

    // 鸭蛋
    public static final RegistryObject<Item> DUCK_EGG = ITEMS.register("duck_egg",
            () -> new DuckEggItem(new Item.Properties()));
}