package aysta3045.ChineseDelight.common.blocks.entity;

import aysta3045.ChineseDelight.common.recipe.FermentationRecipe;
import aysta3045.ChineseDelight.common.registry.ModBlockEntities;
import aysta3045.ChineseDelight.common.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
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

public class FermentationJarBlockEntity extends BlockEntity {
    private final ItemStackHandler itemHandler = new ItemStackHandler(1) { // 只有一个槽位
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            }
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private int fermentationProgress = 0;
    private int fermentationTime = 1200; // 60秒 (20 ticks/秒)

    public FermentationJarBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FERMENTATION_JAR.get(), pos, state);
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

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("fermentation.progress", fermentationProgress);
        tag.putInt("fermentation.time", fermentationTime);
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        fermentationProgress = tag.getInt("fermentation.progress");
        fermentationTime = tag.getInt("fermentation.time");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, FermentationJarBlockEntity pEntity) {
        if (level.isClientSide()) return;

        if (hasRecipe(pEntity)) {
            pEntity.fermentationProgress++;
            setChanged(level, pos, state);

            if (pEntity.fermentationProgress >= pEntity.fermentationTime) {
                craftItem(pEntity);
                // 播放完成音效
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 0.5f, 1.0f);
            }
        } else {
            pEntity.resetProgress();
            setChanged(level, pos, state);
        }
    }

    private static boolean hasRecipe(FermentationJarBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        // 修复：使用注册的配方类型
        Optional<FermentationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.FERMENTATION_TYPE.get(), inventory, level);

        return recipe.isPresent() && !inventory.getItem(0).isEmpty();
    }

    private static void craftItem(FermentationJarBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        // 修复：使用注册的配方类型
        Optional<FermentationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.FERMENTATION_TYPE.get(), inventory, level);

        if (recipe.isPresent()) {
            ItemStack result = recipe.get().getResultItem(level.registryAccess()).copy();
            entity.itemHandler.setStackInSlot(0, result);
            entity.resetProgress();
        }
    }

    private void resetProgress() {
        this.fermentationProgress = 0;
    }

    // 玩家交互方法
    public boolean interact(Player player, ItemStack heldItem) {
        ItemStack currentItem = itemHandler.getStackInSlot(0);

        if (currentItem.isEmpty()) {
            // 罐子为空，可以放入物品
            if (!heldItem.isEmpty() && hasValidRecipe(heldItem)) {
                // 复制物品并放入罐子
                ItemStack toInsert = heldItem.copy();
                toInsert.setCount(1);
                itemHandler.setStackInSlot(0, toInsert);

                // 减少玩家手中的物品
                heldItem.shrink(1);

                // 播放放入音效
                level.playSound(null, worldPosition, SoundEvents.WOOD_PLACE, SoundSource.BLOCKS, 0.5f, 1.0f);
                return true;
            }
        } else {
            // 罐子有物品，可以取出
            if (heldItem.isEmpty()) {
                // 玩家空手，取出物品
                player.getInventory().add(itemHandler.getStackInSlot(0).copy());
                itemHandler.setStackInSlot(0, ItemStack.EMPTY);
                resetProgress();

                // 播放取出音效
                level.playSound(null, worldPosition, SoundEvents.WOOD_HIT, SoundSource.BLOCKS, 0.5f, 1.0f);
                return true;
            }
        }

        return false;
    }

    private boolean hasValidRecipe(ItemStack item) {
        Level level = this.level;
        SimpleContainer testInventory = new SimpleContainer(item);
        // 修复：使用注册的配方类型
        Optional<FermentationRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ModRecipes.FERMENTATION_TYPE.get(), testInventory, level);
        return recipe.isPresent();
    }

    public ItemStack getDisplayItem() {
        return itemHandler.getStackInSlot(0);
    }

    public boolean isFermenting() {
        return fermentationProgress > 0 && fermentationProgress < fermentationTime;
    }

    public float getFermentationProgress() {
        return (float) fermentationProgress / fermentationTime;
    }
}