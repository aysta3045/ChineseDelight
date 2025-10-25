package com.aysta.ChineseDelight.common.registry;

import com.aysta.ChineseDelight.ChineseDelight;
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
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .title(Component.translatable("itemGroup." + ChineseDelight.MODID + ".chinesedelight_tab"))
                    .icon(() -> new ItemStack(Items.COOKED_CHICKEN))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.HOT_DRY_NOODLES.get());
                    }).build());
}