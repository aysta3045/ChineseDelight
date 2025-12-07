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

    // 缓存当前匹配信息
    private FermentationRecipe currentRecipe = null;
    private FermentationRecipe.MatchResult currentMatchResult = null;

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

        // 检查是否有匹配的配方
        Optional<FermentationRecipe> recipe = findRecipe(blockEntity);
        if (recipe.isPresent()) {
            FermentationRecipe currentRecipe = recipe.get();
            SimpleContainer inventory = getContainer(blockEntity);

            // 获取匹配结果
            Optional<FermentationRecipe.MatchResult> matchResult = currentRecipe.getMatchResult(inventory);

            if (matchResult.isPresent()) {
                blockEntity.currentRecipe = currentRecipe;
                blockEntity.currentMatchResult = matchResult.get();
                blockEntity.maxProgress = currentRecipe.getFermentationTime();

                blockEntity.progress++;
                setChanged(level, pos, state);

                if (blockEntity.progress >= blockEntity.maxProgress) {
                    craftItem(blockEntity);
                    blockEntity.progress = 0;
                    blockEntity.currentRecipe = null;
                    blockEntity.currentMatchResult = null;
                }
            } else {
                blockEntity.resetProgress();
                blockEntity.currentRecipe = null;
                blockEntity.currentMatchResult = null;
                setChanged(level, pos, state);
            }
        } else {
            blockEntity.resetProgress();
            blockEntity.currentRecipe = null;
            blockEntity.currentMatchResult = null;
            setChanged(level, pos, state);
        }
    }

    private static Optional<FermentationRecipe> findRecipe(FermentationJarBlockEntity blockEntity) {
        Level level = blockEntity.level;
        if (level == null) return Optional.empty();

        SimpleContainer inventory = getContainer(blockEntity);

        return level.getRecipeManager()
                .getAllRecipesFor(ModRecipes.FERMENTATION_TYPE.get())
                .stream()
                .filter(recipe -> recipe.matches(inventory, level))
                .findFirst();
    }

    private static SimpleContainer getContainer(FermentationJarBlockEntity blockEntity) {
        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));
        }
        return inventory;
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

    private static void craftItem(FermentationJarBlockEntity blockEntity) {
        Level level = blockEntity.level;
        if (level == null || blockEntity.currentRecipe == null || blockEntity.currentMatchResult == null) return;

        var registryAccess = level.registryAccess();
        FermentationRecipe.MatchResult matchResult = blockEntity.currentMatchResult;

        // 获取输出数量和容器数量
        int outputCount = matchResult.outputCount;
        int containerCount = matchResult.containerCount;

        // 输出数量不能超过容器数量
        outputCount = Math.min(outputCount, containerCount);

        // 限制输出数量不超过配方的最大乘数
        int maxMultiplier = blockEntity.currentRecipe.getMaxMultiplier();
        if (outputCount > maxMultiplier) {
            outputCount = maxMultiplier;
        }

        // 如果输出数量为0，不执行合成
        if (outputCount <= 0) {
            return;
        }

        ItemStack result = blockEntity.currentRecipe.getResultItemWithCount(registryAccess, outputCount);

        // 根据匹配结果消耗物品
        int[] slotConsumeCount = matchResult.slotConsumeCount;
        for (int slot = 0; slot < 9; slot++) {
            if (slotConsumeCount[slot] > 0) {
                blockEntity.itemHandler.extractItem(slot, slotConsumeCount[slot], false);
            }
        }

        // 消耗容器（消耗数量等于输出数量）
        blockEntity.itemHandler.extractItem(9, outputCount, false);

        // 设置输出
        ItemStack currentOutput = blockEntity.itemHandler.getStackInSlot(10);
        if (currentOutput.isEmpty()) {
            blockEntity.itemHandler.setStackInSlot(10, result);
        } else if (currentOutput.getItem() == result.getItem() &&
                ItemStack.isSameItemSameTags(currentOutput, result)) {
            // 相同物品，合并数量
            int newCount = currentOutput.getCount() + result.getCount();
            int maxStackSize = currentOutput.getMaxStackSize();
            if (newCount <= maxStackSize) {
                currentOutput.setCount(newCount);
                blockEntity.itemHandler.setStackInSlot(10, currentOutput);
            } else {
                // 如果超过最大堆叠，只添加部分
                int canAdd = maxStackSize - currentOutput.getCount();
                if (canAdd > 0) {
                    currentOutput.setCount(currentOutput.getCount() + canAdd);
                    blockEntity.itemHandler.setStackInSlot(10, currentOutput);
                }
            }
        } else {
        }

        blockEntity.resetProgress();
    }

    private void resetProgress() {
        this.progress = 0;
        // 重置进度时不重置maxProgress，保持当前配方的时间
        setChanged();
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