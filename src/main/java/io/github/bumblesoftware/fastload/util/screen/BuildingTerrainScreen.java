package io.github.bumblesoftware.fastload.util.screen;

import io.github.bumblesoftware.fastload.FastLoad;
import io.github.bumblesoftware.fastload.config.FLMath;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.NarratorManager;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class BuildingTerrainScreen extends Screen {
    private final Text text;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private Integer PROGRESS_STORAGE = 0;
    private Integer getLoadedChunkCount() {
        return client.world != null ? client.world.getChunkManager().getLoadedChunkCount() : 0;
    }
    private Double getPercentage() {
        return getLoadedChunkCount().doubleValue()/FLMath.getPreRenderArea().doubleValue();
    }
    private Integer getPercentageInt() {
        double d = getPercentage() * 100;
        return (int) d;
    }
    public BuildingTerrainScreen() {
        super(NarratorManager.EMPTY);
        text = Text.translatable("menu.generatingTerrain");
    }
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        this.renderBackgroundTexture(0);
        int white = 0xFFFFFF;
        int heightUpFromCentre = 50;
        final int i = getPercentageInt();
        String percentage = i + "%";
        if (PROGRESS_STORAGE < i) {
            FastLoad.LOGGER.info("Terrain Building Progress: " + percentage);
        }
        PROGRESS_STORAGE = i;
        DrawableHelper.drawCenteredText(matrices, this.textRenderer, text, this.width / 2, this.height / 2 - heightUpFromCentre, white);
        DrawableHelper.drawCenteredText(
                matrices,
                this.textRenderer,
                percentage,
                width / 2,
                height / 2 - heightUpFromCentre + 15,
                white);
        super.render(matrices, mouseX, mouseY, delta);
    }
    @Override
    public boolean shouldCloseOnEsc() {
        return getPercentage() >= 25;
    }
}