package aysta3045.ChineseDelight.common.blocks.entity;

import aysta3045.ChineseDelight.common.menu.FermentationJarMenu;
import aysta3045.ChineseDelight.common.recipe.FermentationRecipe;
import aysta3045.ChineseDelight.common.registry.ModBlockEntities;
import aysta3045.ChineseDelight.common.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
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

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("fermentation_jar.progress", progress);
        tag.putInt("fermentation_jar.maxProgress", maxProgress);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        progress = tag.getInt("fermentation_jar.progress");
        maxProgress = tag.getInt("fermentation_jar.maxProgress");
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FermentationJarBlockEntity blockEntity) {
        if (level.isClientSide()) return;

        if (hasRecipe(blockEntity)) {
            blockEntity.progress++;
            setChanged(level, pos, state);

            if (blockEntity.progress >= blockEntity.maxProgress) {
                craftItem(blockEntity);
                blockEntity.progress = 0;
            }
        } else {
            blockEntity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    private void resetProgress() {
        this.progress = 0;
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

        if (recipe.isPresent() && hasRecipe(blockEntity)) {
            // 获取RegistryAccess
            var registryAccess = level.registryAccess();
            ItemStack result = recipe.get().getResultItem(registryAccess);

            // 消耗输入物品
            for (int i = 0; i < 9; i++) {
                blockEntity.itemHandler.extractItem(i, 1, false);
            }
            // 消耗容器
            blockEntity.itemHandler.extractItem(9, 1, false);

            // 设置输出
            ItemStack currentOutput = blockEntity.itemHandler.getStackInSlot(10);
            if (currentOutput.isEmpty()) {
                blockEntity.itemHandler.setStackInSlot(10, result.copy());
            } else if (currentOutput.getItem() == result.getItem()) {
                currentOutput.grow(result.getCount());
                blockEntity.itemHandler.setStackInSlot(10, currentOutput);
            }

            blockEntity.resetProgress();
        }
    }

    private static boolean hasRecipe(FermentationJarBlockEntity blockEntity) {
        Level level = blockEntity.level;
        if (level == null) return false;

        SimpleContainer inventory = new SimpleContainer(blockEntity.itemHandler.getSlots());
        for (int i = 0; i < blockEntity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, blockEntity.itemHandler.getStackInSlot(i));
        }

        Optional<FermentationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.FERMENTATION_TYPE.get(), inventory, level);

        if (recipe.isEmpty()) return false;

        // 获取RegistryAccess
        var registryAccess = level.registryAccess();
        ItemStack output = recipe.get().getResultItem(registryAccess);

        return canInsertAmountIntoOutputSlot(inventory, output.getCount()) &&
                canInsertItemIntoOutputSlot(inventory, output);
    }

    private static boolean canInsertItemIntoOutputSlot(Container inventory, ItemStack stack) {
        ItemStack outputSlot = inventory.getItem(10);
        return outputSlot.isEmpty() ||
                (outputSlot.getItem() == stack.getItem() &&
                        ItemStack.isSameItemSameTags(outputSlot, stack));
    }

    private static boolean canInsertAmountIntoOutputSlot(Container inventory, int amount) {
        ItemStack outputSlot = inventory.getItem(10);
        return outputSlot.isEmpty() ||
                (outputSlot.getCount() + amount <= outputSlot.getMaxStackSize());
    }

    // MenuProvider 接口实现 - 现在这些@Override是正确的
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