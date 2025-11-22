package aysta3045.ChineseDelight.common.menu;

import aysta3045.ChineseDelight.common.blocks.entity.OilCookingPotBlockEntity;
import aysta3045.ChineseDelight.common.registry.ModBlocks;
import aysta3045.ChineseDelight.common.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OilCookingPotMenu extends AbstractContainerMenu {
    @Nullable
    public final OilCookingPotBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    // 修正的槽位位置定义 - 确保在GUI范围内
    private static final int FUEL_SLOT_X = 56;
    private static final int FUEL_SLOT_Y = 53;
    private static final int INPUT_SLOT_X = 56;
    private static final int INPUT_SLOT_Y = 17;
    private static final int OUTPUT_SLOT_1_X = 116;
    private static final int OUTPUT_SLOT_1_Y = 35;
    private static final int OUTPUT_SLOT_2_X = 146;
    private static final int OUTPUT_SLOT_2_Y = 35;

    // 玩家物品栏位置
    private static final int PLAYER_INVENTORY_X = 8;
    private static final int PLAYER_INVENTORY_Y = 84;
    private static final int PLAYER_HOTBAR_Y = 142;

    // 槽位常量
    private static final int MACHINE_SLOT_COUNT = 4;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = 27;
    private static final int PLAYER_HOTBAR_SLOT_COUNT = 9;
    private static final int TOTAL_SLOT_COUNT = MACHINE_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT + PLAYER_HOTBAR_SLOT_COUNT;

    // 槽位索引常量
    private static final int PLAYER_INVENTORY_START_INDEX = MACHINE_SLOT_COUNT;
    private static final int PLAYER_HOTBAR_START_INDEX = PLAYER_INVENTORY_START_INDEX + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int PLAYER_INVENTORY_END_INDEX = PLAYER_HOTBAR_START_INDEX + PLAYER_HOTBAR_SLOT_COUNT;

    public OilCookingPotMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory,
                getBlockEntityFromBuffer(playerInventory, extraData),
                createContainerData());
    }

    public OilCookingPotMenu(int containerId, Inventory playerInventory, @Nullable BlockEntity blockEntity, ContainerData data) {
        super(ModMenus.OIL_COOKING_POT_MENU.get(), containerId);

        this.blockEntity = (blockEntity instanceof OilCookingPotBlockEntity) ?
                (OilCookingPotBlockEntity) blockEntity : null;
        this.level = playerInventory.player.level();
        this.data = data;

        // 先添加机器槽位，再添加玩家槽位
        if (this.blockEntity != null) {
            addSlots();
        } else {
            addDummySlots();
        }

        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);

        addDataSlots(data);
    }


    @Nullable
    private static BlockEntity getBlockEntityFromBuffer(Inventory playerInventory, @Nullable FriendlyByteBuf extraData) {
        if (extraData == null) {
            return null;
        }

        BlockPos pos = extraData.readBlockPos();
        Level level = playerInventory.player.level();
        return level.getBlockEntity(pos);
    }

    private static ContainerData createContainerData() {
        return new SimpleContainerData(4) {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> 0; // cookingProgress
                    case 1 -> 200; // cookingTime
                    case 2 -> 0; // fuelTime
                    case 3 -> 0; // fuelDuration
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                // 客户端不需要设置数据
            }

            @Override
            public int getCount() {
                return 4;
            }
        };
    }

    private void addSlots() {
        if (blockEntity == null) return;

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // 燃料槽 - 只接受燃料物品
            this.addSlot(new SlotItemHandler(handler, OilCookingPotBlockEntity.FUEL_SLOT, FUEL_SLOT_X, FUEL_SLOT_Y) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return isFuel(stack);
                }

                @Override
                public int getMaxStackSize() {
                    return 64;
                }
            });

            // 输入槽
            this.addSlot(new SlotItemHandler(handler, OilCookingPotBlockEntity.INPUT_SLOT, INPUT_SLOT_X, INPUT_SLOT_Y) {
                @Override
                public int getMaxStackSize() {
                    return 64;
                }
            });

            // 输出槽1 - 不接受输入
            this.addSlot(new SlotItemHandler(handler, OilCookingPotBlockEntity.OUTPUT_SLOT_1, OUTPUT_SLOT_1_X, OUTPUT_SLOT_1_Y) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }

                @Override
                public int getMaxStackSize() {
                    return 64;
                }

                @Override
                public void onTake(Player player, ItemStack stack) {
                    super.onTake(player, stack);
                    // 可以在这里添加获取物品时的额外逻辑
                }
            });

            // 输出槽2 - 不接受输入
            this.addSlot(new SlotItemHandler(handler, OilCookingPotBlockEntity.OUTPUT_SLOT_2, OUTPUT_SLOT_2_X, OUTPUT_SLOT_2_Y) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    return false;
                }

                @Override
                public int getMaxStackSize() {
                    return 64;
                }

                @Override
                public void onTake(Player player, ItemStack stack) {
                    super.onTake(player, stack);
                    // 可以在这里添加获取物品时的额外逻辑
                }
            });
        });
    }

    private void addDummySlots() {
        // 添加空的槽位以保持槽位数量一致，但放在正确的位置
        SimpleContainer dummyContainer = new SimpleContainer(MACHINE_SLOT_COUNT);

        // 燃料槽
        this.addSlot(new Slot(dummyContainer, OilCookingPotBlockEntity.FUEL_SLOT, FUEL_SLOT_X, FUEL_SLOT_Y) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        // 输入槽
        this.addSlot(new Slot(dummyContainer, OilCookingPotBlockEntity.INPUT_SLOT, INPUT_SLOT_X, INPUT_SLOT_Y) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        // 输出槽1
        this.addSlot(new Slot(dummyContainer, OilCookingPotBlockEntity.OUTPUT_SLOT_1, OUTPUT_SLOT_1_X, OUTPUT_SLOT_1_Y) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });

        // 输出槽2
        this.addSlot(new Slot(dummyContainer, OilCookingPotBlockEntity.OUTPUT_SLOT_2, OUTPUT_SLOT_2_X, OUTPUT_SLOT_2_Y) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9,
                        PLAYER_INVENTORY_X + col * 18,
                        PLAYER_INVENTORY_Y + row * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; ++col) {
            this.addSlot(new Slot(playerInventory, col,
                    PLAYER_INVENTORY_X + col * 18,
                    PLAYER_HOTBAR_Y));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot == null || !slot.hasItem()) {
            return itemstack;
        }

        ItemStack itemstack1 = slot.getItem();
        itemstack = itemstack1.copy();

        // 如果点击的是机器槽位 (0-3)
        if (index < PLAYER_INVENTORY_START_INDEX) {
            // 尝试将物品移动到玩家物品栏
            if (!this.moveItemStackTo(itemstack1, PLAYER_INVENTORY_START_INDEX, PLAYER_INVENTORY_END_INDEX, true)) {
                return ItemStack.EMPTY;
            }
        }
        // 如果点击的是玩家物品栏槽位 (4-39)
        else {
            // 检查是否是燃料
            if (isFuel(itemstack1)) {
                // 尝试移动到燃料槽
                if (!this.moveItemStackTo(itemstack1, OilCookingPotBlockEntity.FUEL_SLOT, OilCookingPotBlockEntity.FUEL_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // 尝试移动到输入槽
                if (!this.moveItemStackTo(itemstack1, OilCookingPotBlockEntity.INPUT_SLOT, OilCookingPotBlockEntity.INPUT_SLOT + 1, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, itemstack1);
        return itemstack;
    }

    @Override
    protected boolean moveItemStackTo(ItemStack stack, int startIndex, int endIndex, boolean reverseDirection) {
        boolean flag = false;
        int i = startIndex;

        if (reverseDirection) {
            i = endIndex - 1;
        }

        if (stack.isStackable()) {
            while (!stack.isEmpty()) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot = this.slots.get(i);
                ItemStack itemstack = slot.getItem();

                if (!itemstack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemstack) && slot.mayPlace(stack)) {
                    int j = itemstack.getCount() + stack.getCount();
                    int maxSize = Math.min(slot.getMaxStackSize(), stack.getMaxStackSize());

                    if (j <= maxSize) {
                        stack.setCount(0);
                        itemstack.setCount(j);
                        slot.setChanged();
                        flag = true;
                    } else if (itemstack.getCount() < maxSize) {
                        stack.shrink(maxSize - itemstack.getCount());
                        itemstack.setCount(maxSize);
                        slot.setChanged();
                        flag = true;
                    }
                }

                if (reverseDirection) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        if (!stack.isEmpty()) {
            if (reverseDirection) {
                i = endIndex - 1;
            } else {
                i = startIndex;
            }

            while (true) {
                if (reverseDirection) {
                    if (i < startIndex) {
                        break;
                    }
                } else if (i >= endIndex) {
                    break;
                }

                Slot slot1 = this.slots.get(i);
                ItemStack itemstack1 = slot1.getItem();

                if (itemstack1.isEmpty() && slot1.mayPlace(stack)) {
                    if (stack.getCount() > slot1.getMaxStackSize()) {
                        slot1.setByPlayer(stack.split(slot1.getMaxStackSize()));
                    } else {
                        slot1.setByPlayer(stack.split(stack.getCount()));
                    }

                    slot1.setChanged();
                    flag = true;
                    break;
                }

                if (reverseDirection) {
                    i--;
                } else {
                    i++;
                }
            }
        }

        return flag;
    }

    private boolean isFuel(ItemStack stack) {
        return net.minecraftforge.common.ForgeHooks.getBurnTime(stack, null) > 0;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (blockEntity == null) {
            return false;
        }
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.OIL_COOKING_POT.get());
    }

    // 获取数据的方法，供屏幕渲染使用
    public int getCookProgress() {
        int cookingProgress = data.get(0);
        int cookingTime = data.get(1);
        return cookingTime != 0 && cookingProgress != 0 ? cookingProgress * 24 / cookingTime : 0;
    }

    public int getBurnProgress() {
        int fuelTime = data.get(2);
        int fuelDuration = data.get(3);
        if (fuelDuration == 0) {
            fuelDuration = 200; // 默认值
        }
        return fuelTime != 0 ? fuelTime * 13 / fuelDuration : 0;
    }

    public boolean isLit() {
        return data.get(2) > 0;
    }

    // Getter方法
    @Nullable
    public OilCookingPotBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public ContainerData getData() {
        return data;
    }
}