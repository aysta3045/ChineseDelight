package aysta3045.ChineseDelight.common.blocks.entity;

import aysta3045.ChineseDelight.common.menu.OilCookingPotMenu;
import aysta3045.ChineseDelight.common.recipe.OilCookingPotRecipe;
import aysta3045.ChineseDelight.common.registry.ModBlockEntities;
import aysta3045.ChineseDelight.common.registry.ModRecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class OilCookingPotBlockEntity extends BlockEntity implements MenuProvider {
    // 槽位定义
    public static final int FUEL_SLOT = 0;
    public static final int INPUT_SLOT = 1;
    public static final int OUTPUT_SLOT_1 = 2;
    public static final int OUTPUT_SLOT_2 = 3;
    public static final int SLOT_COUNT = 4;

    // 数据跟踪索引
    public static final int DATA_COOKING_PROGRESS = 0;
    public static final int DATA_COOKING_TIME = 1;
    public static final int DATA_FUEL_TIME = 2;
    public static final int DATA_FUEL_DURATION = 3;
    public static final int DATA_COUNT = 4;

    private final ItemStackHandler itemHandler = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                // 当输入或燃料变化时，重置进度
                if (slot == INPUT_SLOT || slot == FUEL_SLOT) {
                    cookingProgress = 0;
                }
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return switch (slot) {
                case INPUT_SLOT -> true; // 任何物品都可以放入输入槽
                case FUEL_SLOT -> ForgeHooks.getBurnTime(stack, null) > 0; // 只有燃料可以放入燃料槽
                case OUTPUT_SLOT_1, OUTPUT_SLOT_2 -> false; // 不能手动放入输出槽
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    // 容器数据同步
    protected final ContainerData data;
    private int cookingProgress = 0;
    private int cookingTime = 200; // 默认烹饪时间
    private int fuelTime = 0;
    private int fuelDuration = 0;

    public OilCookingPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.OIL_COOKING_POT.get(), pos, state);
        this.data = new ContainerData() {
            @Override
            public int get(int index) {
                return switch (index) {
                    case DATA_COOKING_PROGRESS -> OilCookingPotBlockEntity.this.cookingProgress;
                    case DATA_COOKING_TIME -> OilCookingPotBlockEntity.this.cookingTime;
                    case DATA_FUEL_TIME -> OilCookingPotBlockEntity.this.fuelTime;
                    case DATA_FUEL_DURATION -> OilCookingPotBlockEntity.this.fuelDuration;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case DATA_COOKING_PROGRESS -> OilCookingPotBlockEntity.this.cookingProgress = value;
                    case DATA_COOKING_TIME -> OilCookingPotBlockEntity.this.cookingTime = value;
                    case DATA_FUEL_TIME -> OilCookingPotBlockEntity.this.fuelTime = value;
                    case DATA_FUEL_DURATION -> OilCookingPotBlockEntity.this.fuelDuration = value;
                }
            }

            @Override
            public int getCount() {
                return DATA_COUNT;
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

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.chinese_delight.oil_cooking_pot");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new OilCookingPotMenu(containerId, playerInventory, this, this.data);
    }

    // 主 tick 方法
    public static void tick(Level level, BlockPos pos, BlockState state, OilCookingPotBlockEntity blockEntity) {
        if (level.isClientSide) {
            return;
        }

        boolean wasLit = state.getValue(AbstractFurnaceBlock.LIT);
        boolean hasChanged = false;

        // 检查是否有有效配方
        boolean hasValidRecipe = blockEntity.hasRecipe();

        // 燃料处理逻辑
        if (blockEntity.fuelTime > 0) {
            // 有燃料且在燃烧
            blockEntity.fuelTime--;
            hasChanged = true;

            if (!wasLit) {
                level.setBlock(pos, state.setValue(AbstractFurnaceBlock.LIT, true), 3);
            }
        } else {
            // 燃料耗尽
            if (wasLit) {
                hasChanged = true;
                level.setBlock(pos, state.setValue(AbstractFurnaceBlock.LIT, false), 3);
            }

            // 尝试消耗新燃料
            if (hasValidRecipe && blockEntity.hasFuel()) {
                blockEntity.consumeFuel();
                hasChanged = true;

                if (!wasLit) {
                    level.setBlock(pos, state.setValue(AbstractFurnaceBlock.LIT, true), 3);
                }
            }
        }

        // 烹饪处理逻辑 - 只有在有燃料且有有效配方时才进行
        if (blockEntity.isBurning() && hasValidRecipe) {
            blockEntity.cookingProgress++;
            hasChanged = true;


            if (blockEntity.cookingProgress >= blockEntity.cookingTime) {
                blockEntity.performCooking();
                blockEntity.cookingProgress = 0;
            }
        } else if (blockEntity.cookingProgress > 0) {
            // 没有燃料或没有有效配方时，重置进度
            blockEntity.cookingProgress = 0;
            hasChanged = true;
        }

        if (hasChanged) {
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3);
        }
    }

    // 检查是否有有效配方
    private boolean hasRecipe() {
        if (level == null) {
            return false;
        }

        // 获取当前配方
        Optional<OilCookingPotRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return false;
        }

        OilCookingPotRecipe currentRecipe = recipe.get();

        // 更新烹饪时间为配方指定时间
        cookingTime = currentRecipe.getCookingTime();

        // 检查输出槽是否能容纳结果
        ItemStack result1 = currentRecipe.getResultItem1().copy();
        ItemStack result2 = currentRecipe.getResultItem2().copy();

        boolean canOutput1 = canInsertItem(OUTPUT_SLOT_1, result1);
        boolean canOutput2 = result2.isEmpty() || canInsertItem(OUTPUT_SLOT_2, result2);


        return canOutput1 && canOutput2;
    }

    // 检查是否可以插入物品到指定槽位
    private boolean canInsertItem(int slot, ItemStack stack) {
        ItemStack existing = itemHandler.getStackInSlot(slot);
        return existing.isEmpty() ||
                (ItemStack.isSameItemSameTags(existing, stack) &&
                        existing.getCount() + stack.getCount() <= existing.getMaxStackSize());
    }

    // 执行烹饪操作
    private void performCooking() {
        Optional<OilCookingPotRecipe> recipe = getCurrentRecipe();
        if (recipe.isEmpty()) {
            return;
        }

        OilCookingPotRecipe currentRecipe = recipe.get();
        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);

        // 检查输入物品是否足够
        if (input.isEmpty() || input.getCount() < 1) {
            return;
        }

        // 消耗输入物品
        input.shrink(1);

        // 获取并复制输出物品
        ItemStack result1 = currentRecipe.getResultItem1().copy();
        ItemStack result2 = currentRecipe.getResultItem2().copy();

        // 处理第一个输出
        ItemStack output1 = itemHandler.getStackInSlot(OUTPUT_SLOT_1);
        if (output1.isEmpty()) {
            itemHandler.setStackInSlot(OUTPUT_SLOT_1, result1);
        } else if (ItemStack.isSameItemSameTags(output1, result1)) {
            output1.grow(result1.getCount());
        }

        // 处理第二个输出
        if (!result2.isEmpty()) {
            ItemStack output2 = itemHandler.getStackInSlot(OUTPUT_SLOT_2);
            if (output2.isEmpty()) {
                itemHandler.setStackInSlot(OUTPUT_SLOT_2, result2);
            } else if (ItemStack.isSameItemSameTags(output2, result2)) {
                output2.grow(result2.getCount());
            }
        }
    }

    // 获取当前配方
    private Optional<OilCookingPotRecipe> getCurrentRecipe() {
        if (level == null) {
            return Optional.empty();
        }

        ItemStack input = itemHandler.getStackInSlot(INPUT_SLOT);
        if (input.isEmpty()) {
            return Optional.empty();
        }

        // 创建正确的容器用于配方匹配
        SimpleContainer inventory = new SimpleContainer(1);
        inventory.setItem(0, input);

        // 获取配方管理器
        RecipeManager recipeManager = level.getRecipeManager();

        // 使用 getAllRecipesFor 获取所有配方并手动匹配
        var recipes = recipeManager.getAllRecipesFor(ModRecipes.OIL_COOKING_TYPE.get());


        for (var recipe : recipes) {


            if (recipe.matches(inventory, level)) {
                return Optional.of(recipe);
            }
        }

        return Optional.empty();
    }

    // 检查是否有燃料
    private boolean hasFuel() {
        ItemStack fuelStack = itemHandler.getStackInSlot(FUEL_SLOT);
        if (fuelStack.isEmpty()) {
            return false;
        }

        int burnTime = getBurnDuration(fuelStack);

        return burnTime > 0;
    }

    // 消耗燃料
    private void consumeFuel() {
        ItemStack fuelStack = itemHandler.getStackInSlot(FUEL_SLOT);
        if (fuelStack.isEmpty()) {
            return;
        }

        int burnTime = getBurnDuration(fuelStack);
        if (burnTime > 0) {
            fuelTime = burnTime;
            fuelDuration = burnTime;

            // 消耗燃料，处理容器物品（如桶）
            if (fuelStack.hasCraftingRemainingItem()) {
                itemHandler.setStackInSlot(FUEL_SLOT, fuelStack.getCraftingRemainingItem());
            } else {
                fuelStack.shrink(1);
            }

        }
    }

    private int getBurnDuration(ItemStack fuel) {
        if (fuel.isEmpty()) {
            return 0;
        }
        return ForgeHooks.getBurnTime(fuel, null);
    }

    public boolean isBurning() {
        return fuelTime > 0;
    }

    // NBT数据保存和加载
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", itemHandler.serializeNBT());
        tag.putInt("cookingProgress", cookingProgress);
        tag.putInt("cookingTime", cookingTime);
        tag.putInt("fuelTime", fuelTime);
        tag.putInt("fuelDuration", fuelDuration);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
        cookingProgress = tag.getInt("cookingProgress");
        cookingTime = tag.getInt("cookingTime");
        fuelTime = tag.getInt("fuelTime");
        fuelDuration = tag.getInt("fuelDuration");
    }

    // 网络同步方法
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (pkt.getTag() != null) {
            load(pkt.getTag());
        }
    }

    // 物品掉落方法
    public void drops() {
        if (level == null) return;

        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }

        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    // Getter方法供菜单使用
    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
    public int getCookingProgress() {
        return cookingProgress;
    }
    public int getCookingTime() {
        return cookingTime;
    }
    public int getFuelTime() {
        return fuelTime;
    }
    public int getFuelDuration() {
        return fuelDuration;
    }

    // 获取红石信号强度
    public int getRedstoneSignal() {
        int filledSlots = 0;
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                filledSlots++;
            }
        }
        return (int) Math.floor((filledSlots * 15.0f) / itemHandler.getSlots());
    }
}