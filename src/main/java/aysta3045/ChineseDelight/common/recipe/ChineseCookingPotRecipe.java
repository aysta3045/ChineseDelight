package aysta3045.ChineseDelight.common.recipe;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.registry.ModRecipes;
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
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nullable;

public class ChineseCookingPotRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final ItemStack result1;
    private final ItemStack result2;
    private final int cookingTime;
    private final float experience;

    public ChineseCookingPotRecipe(ResourceLocation id, Ingredient ingredient, ItemStack result1,
                                   ItemStack result2, int cookingTime, float experience) {
        this.id = id;
        this.ingredient = ingredient;
        this.result1 = result1;
        this.result2 = result2;
        this.cookingTime = cookingTime;
        this.experience = experience;

        ChineseDelight.LOGGER.info("Created ChineseCookingPotRecipe: {}", id);
        ChineseDelight.LOGGER.info("  Input: {}", ingredient);
        ChineseDelight.LOGGER.info("  Result1: {} x{}", result1.getItem().getDescriptionId(), result1.getCount());
        ChineseDelight.LOGGER.info("  Result2: {} x{}", result2.getItem().getDescriptionId(), result2.getCount());
        ChineseDelight.LOGGER.info("  CookingTime: {}, Experience: {}", cookingTime, experience);
    }

    // 获取输入材料
    public Ingredient getIngredient() {
        return ingredient;
    }

    // 获取第一个结果
    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return result1.copy();
    }

    public ItemStack getResultItem1() {
        return result1.copy();
    }

    // 获取第二个结果
    public ItemStack getResultItem2() {
        return result2.copy();
    }

    // 获取烹饪时间
    public int getCookingTime() {
        return cookingTime;
    }

    // 获取经验值
    public float getExperience() {
        return experience;
    }

    @Override
    public boolean matches(Container container, Level level) {
        // 检查输入槽位是否有匹配的物品
        return ingredient.test(container.getItem(0));
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        return result1.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    // 在 ChineseCookingPotRecipe 类中
    @Override
    public RecipeType<?> getType() {
        return ModRecipes.CHINESE_COOKING_TYPE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(ingredient);
        return ingredients;
    }

    // 配方类型
    public static class Type implements RecipeType<ChineseCookingPotRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "chinese_cooking";

        private Type() {}
    }

    // 序列化器
    public static class Serializer implements RecipeSerializer<ChineseCookingPotRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(ChineseDelight.MODID, "chinese_cooking");

        static {
            ChineseDelight.LOGGER.info("ChineseCookingPotRecipe Serializer static block - ID: {}", ID);
        }

        public Serializer() {
            ChineseDelight.LOGGER.info("ChineseCookingPotRecipe Serializer constructor called");
        }

        @Override
        public ChineseCookingPotRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            ChineseDelight.LOGGER.info("=== LOADING CHINESE COOKING RECIPE ===");
            ChineseDelight.LOGGER.info("Recipe ID: {}", recipeId);

            // 解析输入材料
            JsonObject inputJson = GsonHelper.getAsJsonObject(json, "input");
            Ingredient ingredient = Ingredient.fromJson(inputJson);
            ChineseDelight.LOGGER.info("Group: {}", GsonHelper.getAsString(json, "group", ""));
            ChineseDelight.LOGGER.info("Input ingredient: {}", inputJson);

            // 解析第一个结果
            JsonObject result1Json = GsonHelper.getAsJsonObject(json, "result1");
            ItemStack result1 = CraftingHelper.getItemStack(result1Json, true);
            ChineseDelight.LOGGER.info("Result1: {} x{}", result1.getItem().getDescriptionId(), result1.getCount());

            // 解析第二个结果（可选）
            ItemStack result2 = ItemStack.EMPTY;
            if (json.has("result2")) {
                JsonObject result2Json = GsonHelper.getAsJsonObject(json, "result2");
                result2 = CraftingHelper.getItemStack(result2Json, true);
                ChineseDelight.LOGGER.info("Result2: {} x{}", result2.getItem().getDescriptionId(), result2.getCount());
            } else {
                ChineseDelight.LOGGER.info("No result2 specified, using empty stack");
            }

            // 解析烹饪时间和经验值
            int cookingTime = GsonHelper.getAsInt(json, "cookingtime", 200);
            float experience = GsonHelper.getAsFloat(json, "experience", 0.0F);
            ChineseDelight.LOGGER.info("Cooking time: {}, Experience: {}", cookingTime, experience);

            ChineseDelight.LOGGER.info("=== RECIPE LOADED SUCCESSFULLY ===\n");

            return new ChineseCookingPotRecipe(recipeId, ingredient, result1, result2, cookingTime, experience);
        }

        @Nullable
        @Override
        public ChineseCookingPotRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            ItemStack result1 = buffer.readItem();
            ItemStack result2 = buffer.readItem();
            int cookingTime = buffer.readVarInt();
            float experience = buffer.readFloat();

            return new ChineseCookingPotRecipe(recipeId, ingredient, result1, result2, cookingTime, experience);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ChineseCookingPotRecipe recipe) {
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.result1);
            buffer.writeItem(recipe.result2);
            buffer.writeVarInt(recipe.cookingTime);
            buffer.writeFloat(recipe.experience);
        }
    }
}