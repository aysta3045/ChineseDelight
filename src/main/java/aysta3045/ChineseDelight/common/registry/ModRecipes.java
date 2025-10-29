package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.recipe.ChineseCookingPotRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, ChineseDelight.MODID);

    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, ChineseDelight.MODID);

    public static final RegistryObject<RecipeSerializer<ChineseCookingPotRecipe>> CHINESE_COOKING_SERIALIZER =
            SERIALIZERS.register("chinese_cooking", () -> ChineseCookingPotRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<ChineseCookingPotRecipe>> CHINESE_COOKING_TYPE =
            RECIPE_TYPES.register("chinese_cooking",
                    () -> RecipeType.simple(new ResourceLocation(ChineseDelight.MODID, "chinese_cooking")));

    static {
        ChineseDelight.LOGGER.info("ModRecipes static initializer - Type: {}, Serializer: {}",
                CHINESE_COOKING_TYPE.getId(), CHINESE_COOKING_SERIALIZER.getId());
    }
}