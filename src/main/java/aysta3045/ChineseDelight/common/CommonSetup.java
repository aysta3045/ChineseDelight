package aysta3045.ChineseDelight.common;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.Config;
import aysta3045.ChineseDelight.common.registry.ModItems;
import aysta3045.ChineseDelight.common.registry.ModCreativeTabs;
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
            if (Config.logDirtBlock.get())
                LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));

            LOGGER.info(Config.magicNumberIntroduction.get() + Config.magicNumber.get());
            Config.items.get().forEach((item) -> LOGGER.info("ITEM >> {}", item));
        });
    }

    @SubscribeEvent
    public static void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        // 添加到中式乐事标签页
        if (event.getTabKey() == ModCreativeTabs.CHINESE_DELIGHT_TAB.getKey()) {
            event.accept(ModItems.HOT_DRY_NOODLES.get());
        }
    }
}