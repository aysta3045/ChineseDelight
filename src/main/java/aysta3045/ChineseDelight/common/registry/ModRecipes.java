package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.recipe.ChineseCookingPotRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ChineseDelight.MODID);

    // 注册烹饪锅配方序列化器
    public static final RegistryObject<RecipeSerializer<ChineseCookingPotRecipe>> CHINESE_COOKING_POT_SERIALIZER =
            RECIPE_SERIALIZERS.register("chinese_cooking_pot",
                    () -> ChineseCookingPotRecipe.Serializer.INSTANCE);
}