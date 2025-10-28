package aysta3045.ChineseDelight.common.block.entity;

import aysta3045.ChineseDelight.common.menu.ChineseCookingPotMenu;
import aysta3045.ChineseDelight.common.registry.ModBlockEntities;
import aysta3045.ChineseDelight.common.recipe.ChineseCookingPotRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
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

public class ChineseCookingPotBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };

    private static final int INPUT_SLOT = 0;
    private static final int FUEL_SLOT = 1;
    private static final int OUTPUT_SLOT_1 = 2;
    private static final int OUTPUT_SLOT_2 = 3;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 200;
    private int fuelTime = 0;
    private int maxFuelTime = 0;

    public ChineseCookingPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHINESE_COOKING_POT.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> ChineseCookingPotBlockEntity.this.progress;
                    case 1 -> ChineseCookingPotBlockEntity.this.maxProgress;
                    case 2 -> ChineseCookingPotBlockEntity.this.fuelTime;
                    case 3 -> ChineseCookingPotBlockEntity.this.maxFuelTime;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> ChineseCookingPotBlockEntity.this.progress = value;
                    case 1 -> ChineseCookingPotBlockEntity.this.maxProgress = value;
                    case 2 -> ChineseCookingPotBlockEntity.this.fuelTime = value;
                    case 3 -> ChineseCookingPotBlockEntity.this.maxFuelTime = value;
                }
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.chinesedelight.chinese_cooking_pot");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new ChineseCookingPotMenu(id, inventory, this, this.data);
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
    protected void saveAdditional(CompoundTag nbt) {
        nbt.put("inventory", itemHandler.serializeNBT());
        nbt.putInt("chinese_cooking_pot.progress", progress);
        nbt.putInt("chinese_cooking_pot.fuel_time", fuelTime);
        nbt.putInt("chinese_cooking_pot.max_fuel_time", maxFuelTime);
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        progress = nbt.getInt("chinese_cooking_pot.progress");
        fuelTime = nbt.getInt("chinese_cooking_pot.fuel_time");
        maxFuelTime = nbt.getInt("chinese_cooking_pot.max_fuel_time");
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, ChineseCookingPotBlockEntity entity) {
        if (level.isClientSide()) {
            return;
        }

        // 检查燃料
        if (entity.fuelTime <= 0) {
            ItemStack fuelStack = entity.itemHandler.getStackInSlot(FUEL_SLOT);
            if (isFuel(fuelStack)) {
                entity.maxFuelTime = getFuelTime(fuelStack);
                entity.fuelTime = entity.maxFuelTime;
                fuelStack.shrink(1);
                setChanged(level, pos, state);
            } else {
                if (entity.progress > 0) {
                    entity.progress = 0;
                    setChanged(level, pos, state);
                }
                return;
            }
        }

        // 检查是否可以制作
        if (hasRecipe(entity)) {
            entity.fuelTime--;
            entity.progress++;
            setChanged(level, pos, state);

            if (entity.progress >= entity.maxProgress) {
                craftItem(entity);
            }
        } else {
            if (entity.progress > 0) {
                entity.progress = 0;
                setChanged(level, pos, state);
            }
        }
    }

    private static boolean hasRecipe(ChineseCookingPotBlockEntity entity) {
        Level level = entity.level;
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<ChineseCookingPotRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ChineseCookingPotRecipe.Type.INSTANCE, inventory, level);

        return recipe.isPresent() && canInsertOutput(inventory, recipe.get().getResultItem(level.registryAccess()))
                && canInsertSecondaryOutput(inventory, recipe.get().getSecondaryResultItem());
    }

    private static void craftItem(ChineseCookingPotBlockEntity entity) {
        Level level = entity.level;
        RegistryAccess registryAccess = level.registryAccess();
        SimpleContainer inventory = new SimpleContainer(entity.itemHandler.getSlots());
        for (int i = 0; i < entity.itemHandler.getSlots(); i++) {
            inventory.setItem(i, entity.itemHandler.getStackInSlot(i));
        }

        Optional<ChineseCookingPotRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(ChineseCookingPotRecipe.Type.INSTANCE, inventory, level);

        if (hasRecipe(entity)) {
            // 移除输入
            entity.itemHandler.extractItem(INPUT_SLOT, 1, false);

            // 添加输出
            ItemStack resultItem = recipe.get().getResultItem(registryAccess);
            ItemStack currentOutput = entity.itemHandler.getStackInSlot(OUTPUT_SLOT_1);
            if (currentOutput.isEmpty()) {
                entity.itemHandler.setStackInSlot(OUTPUT_SLOT_1, resultItem.copy());
            } else if (currentOutput.is(resultItem.getItem())) {
                currentOutput.grow(resultItem.getCount());
            }

            ItemStack secondaryResult = recipe.get().getSecondaryResultItem();
            if (!secondaryResult.isEmpty()) {
                ItemStack currentSecondary = entity.itemHandler.getStackInSlot(OUTPUT_SLOT_2);
                if (currentSecondary.isEmpty()) {
                    entity.itemHandler.setStackInSlot(OUTPUT_SLOT_2, secondaryResult.copy());
                } else if (currentSecondary.is(secondaryResult.getItem())) {
                    currentSecondary.grow(secondaryResult.getCount());
                }
            }

            entity.progress = 0;
        }
    }

    private static boolean canInsertOutput(SimpleContainer inventory, ItemStack output) {
        ItemStack outputSlot = inventory.getItem(OUTPUT_SLOT_1);
        return outputSlot.isEmpty() ||
                (outputSlot.is(output.getItem()) &&
                        outputSlot.getCount() + output.getCount() <= outputSlot.getMaxStackSize());
    }

    private static boolean canInsertSecondaryOutput(SimpleContainer inventory, ItemStack output) {
        if (output.isEmpty()) return true;

        ItemStack outputSlot = inventory.getItem(OUTPUT_SLOT_2);
        return outputSlot.isEmpty() ||
                (outputSlot.is(output.getItem()) &&
                        outputSlot.getCount() + output.getCount() <= outputSlot.getMaxStackSize());
    }

    private static boolean isFuel(ItemStack stack) {
        return getFuelTime(stack) > 0;
    }

    private static int getFuelTime(ItemStack stack) {
        return net.minecraftforge.common.ForgeHooks.getBurnTime(stack, null);
    }
}