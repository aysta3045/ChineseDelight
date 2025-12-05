package aysta3045.ChineseDelight.common.registry;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.recipe.*;
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

    // 发酵罐
    public static final RegistryObject<RecipeSerializer<FermentationRecipe>> FERMENTATION_SERIALIZER =
            SERIALIZERS.register("fermentation", () -> FermentationRecipe.Serializer.INSTANCE);
    public static final RegistryObject<RecipeType<FermentationRecipe>> FERMENTATION_TYPE =
            RECIPE_TYPES.register("fermentation", () -> FermentationRecipe.Type.INSTANCE);
}
