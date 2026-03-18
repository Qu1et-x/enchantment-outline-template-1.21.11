package quiet.enchantmentoutline.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quiet.enchantmentoutline.postprocess.MaskBufferManager;
import quiet.enchantmentoutline.postprocess.OutlinePostProcessor;

/**
 * 职责描述: 注入 GameRenderer 的每一帧渲染流程。
 * 交互映射: 在渲染开始前清空掩码缓冲区，在 GUI 渲染前执行后处理。
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Frame");
    private static int POST_HOOK_LOG_COUNT;
    private static int SCENE_CAPTURE_LOG_COUNT;

    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        // 每帧先清空掩码颜色和深度。
        MaskBufferManager.getInstance().beginFrame();
    }

    @Inject(method = "renderLevel", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearDepthTexture(Lcom/mojang/blaze3d/textures/GpuTexture;D)V"))
    private void onBeforeHandDepthClear(DeltaTracker deltaTracker, CallbackInfo ci) {
        if (SCENE_CAPTURE_LOG_COUNT < 20) {
            SCENE_CAPTURE_LOG_COUNT++;
            LOGGER.info("Frame hook before hand depth clear: pass={}/20", SCENE_CAPTURE_LOG_COUNT);
        }
        MaskBufferManager.getInstance().captureSceneDepthBeforeHand();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/CommandEncoder;clearDepthTexture(Lcom/mojang/blaze3d/textures/GpuTexture;D)V"))
    private void onBeforeMainDepthClear(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        if (Minecraft.getInstance().level == null) {
            return;
        }

        if (POST_HOOK_LOG_COUNT < 20) {
            POST_HOOK_LOG_COUNT++;
            LOGGER.info("Frame hook before main depth clear: pass={}/20", POST_HOOK_LOG_COUNT);
        }
        OutlinePostProcessor.getInstance().process();
    }

    @Inject(method = "resize", at = @At("RETURN"))
    private void onResizeReturn(int width, int height, CallbackInfo ci) {
        MaskBufferManager.getInstance().onRescale(width, height);
    }
}
