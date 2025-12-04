package aysta3045.ChineseDelight.client;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.registry.ModMenus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod.EventBusSubscriber(modid = ChineseDelight.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup
{
    public static void init(final FMLClientSetupEvent event)
    {

        event.enqueueWork(() -> {

            // 注册中式烹饪锅屏幕
            MenuScreens.register(ModMenus.OIL_COOKING_POT_MENU.get(), OilCookingPotScreen::new);

        });
    }
}