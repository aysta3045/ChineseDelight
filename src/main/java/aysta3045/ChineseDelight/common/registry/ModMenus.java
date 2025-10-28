package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.menu.ChineseCookingPotMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, ChineseDelight.MODID);

    // 烹饪锅菜单
    public static final RegistryObject<MenuType<ChineseCookingPotMenu>> CHINESE_COOKING_POT_MENU =
            MENUS.register("chinese_cooking_pot_menu",
                    () -> IForgeMenuType.create(ChineseCookingPotMenu::new));
}