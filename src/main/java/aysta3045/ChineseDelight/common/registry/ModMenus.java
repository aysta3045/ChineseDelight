package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.menu.*;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, ChineseDelight.MODID);

    // 发酵罐菜单
    public static final RegistryObject<MenuType<FermentationJarMenu>> FERMENTATION_JAR_MENU =
            MENUS.register("fermentation_jar_menu",
                    () -> IForgeMenuType.create(FermentationJarMenu::new));
}