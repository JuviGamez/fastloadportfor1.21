package io.github.bumblesoftware.fastload.unused;

import io.github.bumblesoftware.fastload.config.init.FLMath;
import io.github.bumblesoftware.fastload.init.Fastload;
import me.fallenbreath.conditionalmixin.api.annotation.Condition;
import me.fallenbreath.conditionalmixin.api.annotation.Restriction;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.WorldGenerationProgressTracker;
import net.minecraft.client.gui.screen.LevelLoadingScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.bumblesoftware.fastload.init.FastloadClient.ABSTRACTED_CLIENT;

/**
 * Fixes the chunkMap so that 32 getPregenKey getRenderKey distance can work
 */
@SuppressWarnings("UnusedMixin")
@Restriction(require = @Condition(value = "minecraft", versionPredicates = {
        "1.18.2",
        "1.19",
        "1.19.1",
        "1.19.2",
        "1.19.3"
}))
@Mixin(LevelLoadingScreen.class)
public abstract class LevelLoadingScreenMixin {

    @Shadow @Final private WorldGenerationProgressTracker progressProvider;
    private boolean check1 = false;
    private boolean check2 = false;
    private boolean check3 = false;

    @Shadow protected abstract String getPercentage();
    private int getChunkMapHeight() {
        return (((Screen)(Object)this).height * 5/8 - 20);
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;drawChunkMap(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/gui/WorldGenerationProgressTracker;IIII)V"), index = 3)
    private int changeChunkMapYLevel(int centerY) {
        if (FLMath.isDebugEnabled() && !check1) {
            Fastload.LOGGER.info("(LevelLoadingScreen) Chunk-map y-level changed.");
            check1 = true;
        }
        return getChunkMapHeight();
    }

    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/LevelLoadingScreen;drawChunkMap(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/gui/WorldGenerationProgressTracker;IIII)V"), index = 5)
    private int changeChunkMapPixelMargin(int pixelMargin) {
        if (FLMath.isDebugEnabled() && !check2) {
            Fastload.LOGGER.info("(LevelLoadingScreen) pixelMargin changed.");
            check2 = true;
        }
        return 0;
    }

    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen" +
            "/LevelLoadingScreen;drawCenteredText(Lnet/minecraft/client/util/math/MatrixStack;" +
            "Lnet/minecraft/client/font/TextRenderer;Ljava/lang/String;III)V"))
    private void drawCorrectedText(MatrixStack matrices, TextRenderer textRenderer, String s, int i, int j, int k) {
        if (FLMath.isDebugEnabled() && !check3) {
            Fastload.LOGGER.info("(LevelLoadingScreen) Corrected text y-level");
            check3 = true;
        }
        ABSTRACTED_CLIENT.drawCenteredText(
                matrices,
                textRenderer,
                getPercentage(),
                i,
                (getChunkMapHeight() -(progressProvider.getSize() + 15)),
                16777215
        );
    }
}