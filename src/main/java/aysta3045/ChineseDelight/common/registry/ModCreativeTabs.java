package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ChineseDelight.MODID);

    public static final RegistryObject<CreativeModeTab> CHINESE_DELIGHT_TAB = CREATIVE_TABS.register("chinesedelight_tab",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup." + ChineseDelight.MODID + ".chinesedelight_tab"))
                    .icon(() -> new ItemStack(Items.COOKED_CHICKEN))
                    .displayItems((parameters, output) -> {
                        ModItems.ITEMS.getEntries().forEach(item -> {
                            output.accept(item.get());
                        });
                    }).build());
}