package com.aysta.ChineseDelight.common.registry;

import com.aysta.ChineseDelight.ChineseDelight;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.food.FoodProperties;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nullable;
import java.util.List;

public class ModItems
{
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ChineseDelight.MODID);

    // 热干面物品
    public static final RegistryObject<Item> HOT_DRY_NOODLES = ITEMS.register("hot_dry_noodles",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(8)
                    .saturationMod(0.8f)
                    .build())) {

                // 添加工具提示
                @Override
                public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
                    super.appendHoverText(stack, level, tooltip, flag);

                    // 基础工具提示
                    tooltip.add(Component.translatable("item.chinesedelight.hot_dry_noodles.tooltip"));

                    // 如果按住Shift显示详细信息
                    if (net.minecraft.client.gui.screens.Screen.hasShiftDown()) {
                        tooltip.add(Component.translatable("item.chinesedelight.hot_dry_noodles.tooltip.details"));
                    } else {
                        tooltip.add(Component.translatable("item.chinesedelight.hot_dry_noodles.tooltip.shift"));
                    }
                }
            });
}