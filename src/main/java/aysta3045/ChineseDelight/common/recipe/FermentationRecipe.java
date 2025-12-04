package aysta3045.ChineseDelight.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class FermentationRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final ItemStack output;
    private final NonNullList<Ingredient> recipeItems;
    private final Ingredient container;
    private final int fermentationTime;

    public FermentationRecipe(ResourceLocation id, ItemStack output,
                              NonNullList<Ingredient> recipeItems, Ingredient container, int fermentationTime) {
        this.id = id;
        this.output = output;
        this.recipeItems = recipeItems;
        this.container = container;
        this.fermentationTime = fermentationTime;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if (level.isClientSide()) return false;

        // 检查容器
        if (!this.container.test(container.getItem(9))) return false;

        // 检查9个输入槽
        for (int i = 0; i < 9; i++) {
            if (!recipeItems.get(i).test(container.getItem(i))) return false;
        }

        return true;
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
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

    public int getFermentationTime() {
        return fermentationTime;
    }

    public static class Type implements RecipeType<FermentationRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final String ID = "fermentation";
    }

    public static class Serializer implements RecipeSerializer<FermentationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation("chinesedelight", "fermentation");

        @Override
        public FermentationRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<Ingredient> inputs = NonNullList.withSize(9, Ingredient.EMPTY);

            for (int i = 0; i < Math.min(ingredients.size(), 9); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            Ingredient container = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "container"));
            int fermentationTime = GsonHelper.getAsInt(json, "fermentation_time", 200);

            return new FermentationRecipe(id, output, inputs, container, fermentationTime);
        }

        @Override
        public @Nullable FermentationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(buf.readInt(), Ingredient.EMPTY);
            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(buf));
            }

            Ingredient container = Ingredient.fromNetwork(buf);
            ItemStack output = buf.readItem();
            int fermentationTime = buf.readInt();

            return new FermentationRecipe(id, output, inputs, container, fermentationTime);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FermentationRecipe recipe) {
            buf.writeInt(recipe.recipeItems.size());
            for (Ingredient ing : recipe.recipeItems) {
                ing.toNetwork(buf);
            }
            recipe.container.toNetwork(buf);
            buf.writeItem(recipe.output);
            buf.writeInt(recipe.fermentationTime);
        }
    }
}