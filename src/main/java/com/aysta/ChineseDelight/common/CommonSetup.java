package com.aysta.ChineseDelight.common;

import com.aysta.ChineseDelight.ChineseDelight;
import com.aysta.ChineseDelight.Config;
import com.aysta.ChineseDelight.common.registry.ModItems;
import com.aysta.ChineseDelight.common.registry.ModCreativeTabs;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod.EventBusSubscriber(modid = ChineseDelight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup
{
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void init(final FMLCommonSetupEvent event)
    {
        LOGGER.info("HELLO FROM COMMON SETUP");

        event.enqueueWork(() -> {
            // 这里可以放置需要在线程安全环境下运行的代码
            if (Config.logDirtBlock.get())
                LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

            LOGGER.info(Config.magicNumberIntroduction.get() + Config.magicNumber.get());
            Config.items.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
        });
    }

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        // 将物品添加到原版创造标签页（例如食物标签页）
        if (event.getTabKey() == net.minecraft.world.item.CreativeModeTabs.FOOD_AND_DRINKS) {
            event.accept(ModItems.HOT_DRY_NOODLES.get());
        }

        // 添加到自定义标签页
        if (event.getTabKey() == ModCreativeTabs.EXAMPLE_TAB.getKey()) {
            event.accept(ModItems.HOT_DRY_NOODLES.get());
        }
    }
}