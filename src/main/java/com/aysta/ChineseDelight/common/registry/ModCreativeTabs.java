package com.aysta.ChineseDelight.common.registry;

import com.aysta.ChineseDelight.ChineseDelight;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeTabs
{
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ChineseDelight.MODID);

    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_TABS.register("example_tab",
            () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .title(Component.translatable("itemGroup." + ChineseDelight.MODID + ".example_tab"))
                    .icon(() -> ModItems.HOT_DRY_NOODLES.get().getDefaultInstance())  // 使用热干面作为标签页图标
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.HOT_DRY_NOODLES.get());  // 只显示热干面
                    }).build());
}