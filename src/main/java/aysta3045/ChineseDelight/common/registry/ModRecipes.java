package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.recipe.OilCookingPotRecipe;
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

    public static final RegistryObject<RecipeSerializer<OilCookingPotRecipe>> OIL_COOKING_SERIALIZER =
            SERIALIZERS.register("oil_cooking", () -> OilCookingPotRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeType<OilCookingPotRecipe>> OIL_COOKING_TYPE =
            RECIPE_TYPES.register("oil_cooking",
                    () -> RecipeType.simple(new ResourceLocation(ChineseDelight.MODID, "oil_cooking")));

    static {
        ChineseDelight.LOGGER.info("ModRecipes static initializer - Type: {}, Serializer: {}",
                OIL_COOKING_TYPE.getId(), OIL_COOKING_SERIALIZER.getId());
    }
}