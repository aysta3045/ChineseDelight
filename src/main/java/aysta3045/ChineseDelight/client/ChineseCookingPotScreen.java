package aysta3045.ChineseDelight.client;

import aysta3045.ChineseDelight.ChineseDelight;
import aysta3045.ChineseDelight.common.menu.ChineseCookingPotMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ChineseCookingPotScreen extends AbstractContainerScreen<ChineseCookingPotMenu> {
    // 使用原版熔炉纹理
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("chinesedelight", "textures/gui/chinese_cooking_pot.png");

    public ChineseCookingPotScreen(ChineseCookingPotMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // 绘制主背景
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 渲染燃料进度（火焰）
        if (menu.isBurning()) {
            int fuelProgress = menu.getScaledFuel();
            ChineseDelight.LOGGER.debug("Rendering fuel progress: {} at position ({}, {})",
                    fuelProgress, x + 56, y + 36 + 13 - fuelProgress);

            // 火焰纹理：从 (176, 0) 开始，尺寸 14x14
            // 绘制位置：燃料槽上方 (x+56, y+36)，高度根据燃料进度调整
            if (fuelProgress > 0) {
                guiGraphics.blit(TEXTURE, x + 56, y + 36 + 13 - fuelProgress,
                        176, 13 - fuelProgress, 14, fuelProgress);
            }
        }

        // 渲染烹饪进度（箭头）
        if (menu.isCooking()) {
            int cookProgress = menu.getScaledProgress();
            ChineseDelight.LOGGER.debug("Rendering cook progress: {} at position ({}, {})",
                    cookProgress, x + 79, y + 34);

            // 箭头纹理：从 (176, 14) 开始，尺寸 24x17
            // 绘制位置：输入和输出槽之间 (x+79, y+34)，宽度根据进度调整
            if (cookProgress > 0) {
                guiGraphics.blit(TEXTURE, x + 79, y + 34,
                        176, 14, cookProgress, 16);
            }
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // 绘制标题
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        // 绘制玩家库存标题
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }
}