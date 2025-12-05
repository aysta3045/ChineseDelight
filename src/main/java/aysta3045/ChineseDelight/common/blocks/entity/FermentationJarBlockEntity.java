package aysta3045.ChineseDelight.common.blocks.entity;

import aysta3045.ChineseDelight.common.menu.FermentationJarMenu;
import aysta3045.ChineseDelight.common.recipe.FermentationRecipe;
import aysta3045.ChineseDelight.common.registry.ModBlockEntities;
import aysta3045.ChineseDelight.common.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FermentationJarBlockEntity extends BlockEntity implements MenuProvider { // 实现MenuProvider接口

    private final ItemStackHandler itemHandler = new ItemStackHandler(11) { // 9输入 + 1容器 + 1输出
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200; // 默认发酵时间
    private int currentRecipeTime = 200; // 当前配方的发酵时间

    public FermentationJarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FERMENTATION_JAR.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> FermentationJarBlockEntity.this.progress;
                    case 1 -> FermentationJarBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> FermentationJarBlockEntity.this.progress = value;
                    case 1 -> FermentationJarBlockEntity.this.maxProgress = value;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops(Level level, BlockPos pos) {
        if (level.isClientSide()) return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        // 使用原版的方法掉落所有物品
        net.minecraft.world.Containers.dropContents(level, pos, inventory);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("fermentation_jar.progress", progress);
        tag.putInt("fermentation_jar.maxProgress", maxProgress);
        tag.putInt("fermentation_jar.currentRecipeTime", currentRecipeTime);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        progress = tag.getInt("fermentation_jar.progress");
        maxProgress = tag.getInt("fermentation_jar.maxProgress");
        currentRecipeTime = tag.getInt("fermentation_jar.currentRecipeTime");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FermentationJarBlockEntity blockEntity) {
        if (level.isClientSide()) return;


        // 检查是否有有效配方并更新最大进度
        Optional<FermentationRecipe> currentRecipe = getCurrentRecipe(blockEntity);
        if (currentRecipe.isPresent()) {
            // 获取配方中的发酵时间
            int recipeTime = currentRecipe.get().getFermentationTime();

            // 如果配方时间与当前记录的不同，更新它
            if (blockEntity.currentRecipeTime != recipeTime) {
                blockEntity.currentRecipeTime = recipeTime;
                blockEntity.maxProgress = recipeTime;
                blockEntity.setChanged();
            }

            // 如果有配方且输出槽可以接收产物，则进行发酵
            if (canCraft(blockEntity, currentRecipe.get())) {
                blockEntity.progress++;
                setChanged(level, pos, state);

                if (blockEntity.progress >= blockEntity.maxProgress) {
                    craftItem(blockEntity);
                    blockEntity.resetProgress();
                }
            } else {
                // 配方有效但输出槽已满，停止进度但不重置
                // 这样当输出槽有空位时会继续发酵
            }
        } else {
            // 没有有效配方，重置进度
            blockEntity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    private static Optional<FermentationRecipe> getCurrentRecipe(FermentationJarBlockEntity blockEntity) {
        Level level = blockEntity.level;
        if (level == null) return Optional.empty();

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));
        }

        return level.getRecipeManager()
                .getRecipeFor(ModRecipes.FERMENTATION_TYPE.get(), inventory, level);
    }

    private static boolean canCraft(FermentationJarBlockEntity blockEntity, FermentationRecipe recipe) {
        if (recipe == null) return false;

        Level level = blockEntity.level;
        if (level == null) return false;

        // 获取RegistryAccess
        var registryAccess = level.registryAccess();
        ItemStack output = recipe.getResultItem(registryAccess);

        // 检查输出槽是否可以接收产物
        ItemStack outputSlot = blockEntity.itemHandler.getStackInSlot(10);

        if (outputSlot.isEmpty()) {
            return true; // 输出槽为空，可以接收
        } else if (outputSlot.getItem() == output.getItem() &&
                ItemStack.isSameItemSameTags(outputSlot, output)) {
            // 输出槽有相同物品，检查是否可以堆叠
            return outputSlot.getCount() + output.getCount() <= outputSlot.getMaxStackSize();
        }

        return false; // 输出槽有不同物品，无法接收
    }

    private void resetProgress() {
        this.progress = 0;
        // 重置进度时不重置maxProgress，保持当前配方的时间
        setChanged();
    }

    private static void craftItem(FermentationJarBlockEntity blockEntity) {
        Level level = blockEntity.level;
        if (level == null) return;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));
        }

        Optional<FermentationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.FERMENTATION_TYPE.get(), inventory, level);

        if (recipe.isPresent() && canCraft(blockEntity, recipe.get())) {
            // 获取RegistryAccess
            var registryAccess = level.registryAccess();
            ItemStack result = recipe.get().getResultItem(registryAccess);

            // 获取配方原料
            NonNullList<Ingredient> ingredients = recipe.get().recipeItems;

            // 找出哪些槽位被用于匹配配方原料并消耗它们
            boolean[] slotUsed = new boolean[9];

            // 为每个非空配方原料找到匹配的槽位
            for (int recipeIndex = 0; recipeIndex < 9; recipeIndex++) {
                Ingredient ingredient = ingredients.get(recipeIndex);

                if (ingredient.isEmpty()) {
                    continue;
                }

                // 查找匹配的槽位
                for (int slotIndex = 0; slotIndex < 9; slotIndex++) {
                    if (slotUsed[slotIndex]) continue;

                    if (ingredient.test(blockEntity.itemHandler.getStackInSlot(slotIndex))) {
                        slotUsed[slotIndex] = true;
                        // 消耗该槽位的物品
                        blockEntity.itemHandler.extractItem(slotIndex, 1, false);
                        break;
                    }
                }
            }

            // 消耗容器
            blockEntity.itemHandler.extractItem(9, 1, false);

            // 设置输出
            ItemStack currentOutput = blockEntity.itemHandler.getStackInSlot(10);
            if (currentOutput.isEmpty()) {
                blockEntity.itemHandler.setStackInSlot(10, result.copy());
            } else if (currentOutput.getItem() == result.getItem() &&
                    ItemStack.isSameItemSameTags(currentOutput, result)) {
                currentOutput.grow(result.getCount());
                blockEntity.itemHandler.setStackInSlot(10, currentOutput);
            }

            blockEntity.resetProgress();
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.chinesedelight.fermentation_jar");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new FermentationJarMenu(containerId, playerInventory, this, this.data);
    }
}