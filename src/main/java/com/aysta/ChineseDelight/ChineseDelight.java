package com.aysta.ChineseDelight;

import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;
import com.aysta.ChineseDelight.client.ClientSetup;
import com.aysta.ChineseDelight.common.CommonSetup;
import com.aysta.ChineseDelight.common.registry.*;

@Mod(ChineseDelight.MODID)
public class ChineseDelight
{
    public static final String MODID = "chinesedelight";
    private static final Logger LOGGER = LogUtils.getLogger();

    public ChineseDelight()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(CommonSetup::init);
        if (FMLEnvironment.dist.isClient()) {
            modEventBus.addListener(ClientSetup::init);
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}