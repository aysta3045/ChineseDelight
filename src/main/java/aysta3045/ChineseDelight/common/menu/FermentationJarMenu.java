package aysta3045.ChineseDelight.common.menu;

import aysta3045.ChineseDelight.common.blocks.entity.FermentationJarBlockEntity;
import aysta3045.ChineseDelight.common.registry.ModBlocks;
import aysta3045.ChineseDelight.common.registry.ModMenus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class FermentationJarMenu extends AbstractContainerMenu {
    public final FermentationJarBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public FermentationJarMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public FermentationJarMenu(int containerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenus.FERMENTATION_JAR_MENU.get(), containerId);
        checkContainerSize(inv, 11);
        blockEntity = (FermentationJarBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);

        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
            // 添加9个输入槽 (0-8)
            for (int row = 0; row < 3; ++row) {
                for (int col = 0; col < 3; ++col) {
                    this.addSlot(new SlotItemHandler(handler, col + row * 3, 30 + col * 18, 17 + row * 18));
                }
            }
            // 容器槽 (9)
            this.addSlot(new SlotItemHandler(handler, 9, 88, 53));
            // 输出槽 (10)
            this.addSlot(new SlotItemHandler(handler, 10, 126, 35) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false; // 输出槽不能手动放置物品
                }
            });
        });

        addDataSlots(data);
    }

    public boolean isCrafting() {
        return data.get(0) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        int progressArrowSize = 24; // 火焰图标的长度

        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }

    // 熔炉火焰动画的坐标偏移
    private static final int[] BURN_TIME_SPRITES = {0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 13, 13, 14, 14, 15, 15, 16, 16, 17, 17};

    public int getBurnProgressSprite() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);
        if (maxProgress == 0 || progress == 0) return 0;

        int scaledProgress = progress * BURN_TIME_SPRITES.length / maxProgress;
        if (scaledProgress >= BURN_TIME_SPRITES.length) {
            scaledProgress = BURN_TIME_SPRITES.length - 1;
        }
        return BURN_TIME_SPRITES[scaledProgress];
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            if (index < 36) {
                if (!this.moveItemStackTo(itemstack1, 36, 47, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, 36, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.FERMENTATION_JAR.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }
}