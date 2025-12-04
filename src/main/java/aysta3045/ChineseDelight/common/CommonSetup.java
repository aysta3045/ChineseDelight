package aysta3045.ChineseDelight.common;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.registry.ModItems;
import aysta3045.ChineseDelight.common.registry.ModCreativeTabs;
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

    }

}