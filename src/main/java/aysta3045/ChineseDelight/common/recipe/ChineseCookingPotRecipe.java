package aysta3045.ChineseDelight.common.recipe;

import aysta3045.ChineseDelight.ChineseDelight;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ChineseCookingPotRecipe implements Recipe<SimpleContainer> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final ItemStack secondaryOutput;
    private final NonNullList<Ingredient> recipeItems;
    private final int cookingTime;

    public ChineseCookingPotRecipe(ResourceLocation id, ItemStack output, ItemStack secondaryOutput, NonNullList<Ingredient> recipeItems, int cookingTime) {
        this.id = id;
        this.output = output;
        this.secondaryOutput = secondaryOutput;
        this.recipeItems = recipeItems;
        this.cookingTime = cookingTime;
    }

    @Override
    public boolean matches(SimpleContainer pContainer, Level pLevel) {
        if (pLevel.isClientSide()) {
            return false;
        }

        return recipeItems.get(0).test(pContainer.getItem(0));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @Override
    public ItemStack assemble(SimpleContainer pContainer, RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    public ItemStack getSecondaryResultItem() {
        return secondaryOutput.copy();
    }

    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public static class Type implements RecipeType<ChineseCookingPotRecipe> {
        private Type() { }
        public static final Type INSTANCE = new Type();
        public static final String ID = "cooking_pot";
    }

    public static class Serializer implements RecipeSerializer<ChineseCookingPotRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        // 修复过时的构造函数警告
        public static final ResourceLocation ID = new ResourceLocation(ChineseDelight.MODID, "cooking_pot");

        @Override
        public ChineseCookingPotRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "output"));

            ItemStack secondaryOutput = ItemStack.EMPTY;
            if (pSerializedRecipe.has("secondary_output")) {
                secondaryOutput = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(pSerializedRecipe, "secondary_output"));
            }

            JsonArray ingredients = GsonHelper.getAsJsonArray(pSerializedRecipe, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(1, Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            int cookingTime = GsonHelper.getAsInt(pSerializedRecipe, "cooking_time", 200);

            return new ChineseCookingPotRecipe(pRecipeId, output, secondaryOutput, inputs, cookingTime);
        }

        @Override
        public @Nullable ChineseCookingPotRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(pBuffer.readInt(), Ingredient.EMPTY);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(pBuffer));
            }

            ItemStack output = pBuffer.readItem();
            ItemStack secondaryOutput = pBuffer.readItem();
            int cookingTime = pBuffer.readInt();
            return new ChineseCookingPotRecipe(pRecipeId, output, secondaryOutput, inputs, cookingTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ChineseCookingPotRecipe pRecipe) {
            pBuffer.writeInt(pRecipe.getIngredients().size());

            for (Ingredient ing : pRecipe.getIngredients()) {
                ing.toNetwork(pBuffer);
            }

            pBuffer.writeItem(pRecipe.getResultItem(RegistryAccess.EMPTY));
            pBuffer.writeItem(pRecipe.getSecondaryResultItem());
            pBuffer.writeInt(pRecipe.cookingTime);
        }
    }
}