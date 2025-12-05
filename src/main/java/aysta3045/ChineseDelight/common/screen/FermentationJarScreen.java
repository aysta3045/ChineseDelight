package aysta3045.ChineseDelight.common.screen;

import aysta3045.ChineseDelight.common.menu.FermentationJarMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FermentationJarScreen extends AbstractContainerScreen<FermentationJarMenu> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("chinesedelight", "textures/gui/fermentation_jar.png");

    public FermentationJarScreen(FermentationJarMenu menu, Inventory inventory, Component component) {
        super(menu, inventory, component);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        // 渲染进度动画
        if (menu.isCrafting()) {
            int flameSprite = menu.getBurnProgressSprite();
            // 自己的动画
            guiGraphics.blit(TEXTURE,
                    x + 95, y + 21 + 17 - flameSprite,
                    177, 36 - flameSprite,
                    10, flameSprite + 1);
        }

        // 渲染进度箭头
        int progress = menu.getScaledProgress();
        guiGraphics.blit(TEXTURE,
                x + 91, y + 34,
                176, 1,
                progress + 1, 16);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}