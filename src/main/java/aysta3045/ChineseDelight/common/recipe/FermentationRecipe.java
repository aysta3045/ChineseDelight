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

import java.util.Optional;

public class FermentationRecipe implements Recipe<Container> {
    private final ResourceLocation id;
    private final ItemStack outputTemplate;
    private final NonNullList<IngredientEntry> ingredientEntries;
    private final Ingredient container;
    private final int fermentationTime;
    private final int outputMultiplierIndex; // -1表示没有输出乘数，否则表示哪个原料的数量决定输出数量
    private final int maxMultiplier; // 最大乘数限制

    public FermentationRecipe(ResourceLocation id, ItemStack outputTemplate,
                              NonNullList<IngredientEntry> ingredientEntries,
                              Ingredient container, int fermentationTime, int outputMultiplierIndex, int maxMultiplier) {
        this.id = id;
        this.outputTemplate = outputTemplate;
        this.ingredientEntries = ingredientEntries;
        this.container = container;
        this.fermentationTime = fermentationTime;
        this.outputMultiplierIndex = outputMultiplierIndex;
        this.maxMultiplier = maxMultiplier > 0 ? maxMultiplier : 64;
    }

    @Override
    public boolean matches(Container container, Level level) {
        if (level.isClientSide()) return false;

        // 检查容器槽至少有一个容器
        if (!this.container.test(container.getItem(9)) || container.getItem(9).getCount() < 1) {
            return false;
        }

        // 为每个槽位创建一个匹配状态数组
        boolean[] slotMatched = new boolean[9];

        // 对于每个配方原料，检查是否有足够的匹配项
        for (int i = 0; i < ingredientEntries.size(); i++) {
            IngredientEntry entry = ingredientEntries.get(i);
            int requiredCount = entry.getCount();
            int foundCount = 0;

            // 遍历所有输入槽位寻找匹配项
            for (int slot = 0; slot < 9; slot++) {
                if (!slotMatched[slot] && entry.getIngredient().test(container.getItem(slot))) {
                    ItemStack stack = container.getItem(slot);

                    if (i == outputMultiplierIndex) {
                        // 对于输出乘数原料，可以处理多个
                        int available = stack.getCount();
                        int needed = requiredCount - foundCount;

                        if (available >= needed) {
                            foundCount += needed;
                            if (foundCount >= requiredCount) {
                                break;
                            }
                        }
                    } else {
                        // 对于普通原料，每槽只能使用1个
                        if (stack.getCount() >= 1) {
                            foundCount += 1;
                            slotMatched[slot] = true;
                            if (foundCount >= requiredCount) {
                                break;
                            }
                        }
                    }
                }
            }

            // 如果没有找到足够的这个原料，配方不匹配
            if (foundCount < requiredCount) {
                return false;
            }
        }

        return true;
    }

    // 获取匹配结果，包含每个槽位消耗的数量和输出数量
    public Optional<MatchResult> getMatchResult(Container container) {
        boolean[] slotMatched = new boolean[9];
        int[] slotConsumeCount = new int[9];
        int multiplierItemTotal = 0; // 乘数物品的总数量

        // 获取容器数量和最大堆叠限制
        ItemStack containerStack = container.getItem(9);
        int containerCount = containerStack.getCount();
        int maxStackSize = outputTemplate.getMaxStackSize();

        // 先处理非乘数原料
        for (int i = 0; i < ingredientEntries.size(); i++) {
            if (i == outputMultiplierIndex) continue; // 乘数原料最后处理

            IngredientEntry entry = ingredientEntries.get(i);
            int requiredCount = entry.getCount();
            int foundCount = 0;

            // 遍历所有输入槽位寻找匹配项
            for (int slot = 0; slot < 9; slot++) {
                if (!slotMatched[slot] && entry.getIngredient().test(container.getItem(slot))) {
                    ItemStack stack = container.getItem(slot);
                    if (stack.getCount() >= 1) {
                        slotConsumeCount[slot] = 1;
                        foundCount += 1;
                        slotMatched[slot] = true;

                        if (foundCount >= requiredCount) {
                            break;
                        }
                    }
                }
            }

            // 如果没有找到足够的这个原料，返回空
            if (foundCount < requiredCount) {
                return Optional.empty();
            }
        }

        // 处理乘数原料（如果存在）
        if (outputMultiplierIndex != -1) {
            IngredientEntry multiplierEntry = ingredientEntries.get(outputMultiplierIndex);
            int requiredCount = multiplierEntry.getCount();

            // 遍历所有未匹配的槽位，计算乘数原料的总数
            for (int slot = 0; slot < 9; slot++) {
                if (!slotMatched[slot] && multiplierEntry.getIngredient().test(container.getItem(slot))) {
                    ItemStack stack = container.getItem(slot);
                    multiplierItemTotal += stack.getCount();
                }
            }

            // 确保有足够的乘数原料
            if (multiplierItemTotal < requiredCount) {
                return Optional.empty();
            }
        }

        // 计算输出数量
        int outputCount;
        if (outputMultiplierIndex != -1 && multiplierItemTotal > 0) {
            // 基础输出数量等于乘数物品的总数量
            outputCount = multiplierItemTotal;

            // 限制输出数量不超过最大乘数、最大堆叠数和容器数量
            int maxAllowed = Math.min(maxMultiplier, maxStackSize);
            maxAllowed = Math.min(maxAllowed, containerCount); // 不能超过容器数量

            if (outputCount > maxAllowed) {
                outputCount = maxAllowed;
            }
        } else {
            // 对于非乘数配方，输出数量为1，但不能超过容器数量
            outputCount = Math.min(1, containerCount);
        }

        // 如果输出数量为0，返回空
        if (outputCount <= 0) {
            return Optional.empty();
        }

        // 重新计算乘数原料的消耗数量（基于最终输出数量）
        if (outputMultiplierIndex != -1) {
            // 重置乘数原料的消耗
            for (int slot = 0; slot < 9; slot++) {
                if (slotConsumeCount[slot] > 0 && ingredientEntries.get(outputMultiplierIndex).getIngredient().test(container.getItem(slot))) {
                    slotConsumeCount[slot] = 0;
                }
            }

            // 计算需要消耗的乘数原料数量（等于输出数量）
            int multiplierToConsume = outputCount;
            IngredientEntry multiplierEntry = ingredientEntries.get(outputMultiplierIndex);

            // 分配消耗到各个槽位
            for (int slot = 0; slot < 9; slot++) {
                if (multiplierToConsume <= 0) break;

                if (!slotMatched[slot] && multiplierEntry.getIngredient().test(container.getItem(slot))) {
                    ItemStack stack = container.getItem(slot);
                    int available = stack.getCount();
                    int consume = Math.min(available, multiplierToConsume);

                    if (consume > 0) {
                        slotConsumeCount[slot] = consume;
                        slotMatched[slot] = true;
                        multiplierToConsume -= consume;
                    }
                }
            }

            // 如果乘数原料不足，返回空
            if (multiplierToConsume > 0) {
                return Optional.empty();
            }
        }

        return Optional.of(new MatchResult(slotConsumeCount, outputCount, containerCount));
    }

    public static class MatchResult {
        public final int[] slotConsumeCount; // 每个槽位消耗的数量
        public final int outputCount; // 输出的物品数量
        public final int containerCount; // 容器数量

        public MatchResult(int[] slotConsumeCount, int outputCount, int containerCount) {
            this.slotConsumeCount = slotConsumeCount;
            this.outputCount = outputCount;
            this.containerCount = containerCount;
        }
    }

    @Override
    public ItemStack assemble(Container container, RegistryAccess registryAccess) {
        // 这个方法在旧系统中使用，我们使用一个简单的实现
        return getResultItem(registryAccess);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return outputTemplate.copy();
    }

    // 获取带有数量的输出物品
    public ItemStack getResultItemWithCount(RegistryAccess registryAccess, int count) {
        ItemStack result = outputTemplate.copy();
        if (count > 0) {
            result.setCount(Math.min(count, result.getMaxStackSize()));
        }
        return result;
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

    // 实现Recipe接口要求的getIngredients方法
    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> ingredients = NonNullList.create();

        // 将IngredientEntry转换为Ingredient列表
        for (IngredientEntry entry : ingredientEntries) {
            // 根据数量重复添加Ingredient
            for (int i = 0; i < entry.getCount(); i++) {
                ingredients.add(entry.getIngredient());
            }
        }

        return ingredients;
    }

    // 我们自己的方法，获取带有数量信息的原料
    public NonNullList<IngredientEntry> getIngredientEntries() {
        return ingredientEntries;
    }

    public Ingredient getContainer() {
        return container;
    }

    public int getFermentationTime() {
        return fermentationTime;
    }

    public int getOutputMultiplierIndex() {
        return outputMultiplierIndex;
    }

    // 获取最大输出数量（基于乘数物品的最大堆叠）
    public int getMaxOutputCount() {
        return outputTemplate.getMaxStackSize();
    }

    // 获取最大乘数限制
    public int getMaxMultiplier() {
        return maxMultiplier;
    }

    public static class Type implements RecipeType<FermentationRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
        public static final ResourceLocation ID = new ResourceLocation("chinesedelight", "fermentation");

        @Override
        public String toString() {
            return ID.toString();
        }
    }

    public static class Serializer implements RecipeSerializer<FermentationRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation("chinesedelight", "fermentation");

        @Override
        public FermentationRecipe fromJson(ResourceLocation id, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "output"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");
            NonNullList<IngredientEntry> inputs = NonNullList.create();

            for (int i = 0; i < ingredients.size(); i++) {
                JsonObject ingredientObj = ingredients.get(i).getAsJsonObject();
                Ingredient ingredient = Ingredient.fromJson(ingredientObj.get("ingredient"));
                int count = GsonHelper.getAsInt(ingredientObj, "count", 1);
                inputs.add(new IngredientEntry(ingredient, count));
            }

            Ingredient container = Ingredient.fromJson(GsonHelper.getAsJsonObject(json, "container"));
            int fermentationTime = GsonHelper.getAsInt(json, "fermentation_time", 200);

            // 解析输出乘数索引，-1表示没有乘数
            int outputMultiplierIndex = GsonHelper.getAsInt(json, "output_multiplier_index", -1);

            // 解析最大乘数限制
            int maxMultiplier = GsonHelper.getAsInt(json, "max_multiplier", 64);

            return new FermentationRecipe(id, output, inputs, container, fermentationTime, outputMultiplierIndex, maxMultiplier);
        }

        @Override
        public @Nullable FermentationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            int ingredientCount = buf.readVarInt();
            NonNullList<IngredientEntry> inputs = NonNullList.create();

            for (int i = 0; i < ingredientCount; i++) {
                Ingredient ingredient = Ingredient.fromNetwork(buf);
                int count = buf.readVarInt();
                inputs.add(new IngredientEntry(ingredient, count));
            }

            Ingredient container = Ingredient.fromNetwork(buf);
            ItemStack output = buf.readItem();
            int fermentationTime = buf.readInt();
            int outputMultiplierIndex = buf.readInt();
            int maxMultiplier = buf.readInt(); // 读取最大乘数

            return new FermentationRecipe(id, output, inputs, container, fermentationTime, outputMultiplierIndex, maxMultiplier);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, FermentationRecipe recipe) {
            buf.writeVarInt(recipe.ingredientEntries.size());
            for (IngredientEntry entry : recipe.ingredientEntries) {
                entry.getIngredient().toNetwork(buf);
                buf.writeVarInt(entry.getCount());
            }
            recipe.container.toNetwork(buf);
            buf.writeItem(recipe.outputTemplate);
            buf.writeInt(recipe.fermentationTime);
            buf.writeInt(recipe.outputMultiplierIndex);
            buf.writeInt(recipe.maxMultiplier); // 写入最大乘数
        }
    }
}