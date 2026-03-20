package quiet.enchantmentoutline.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;

import java.util.Objects;

/**
 * 帧阶段处理：重置掩码并在手部清深度前捕获场景深度。
 */
public final class OutlineFrameCaptureService {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-FrameCapture");
    private static OutlineFrameCaptureService instance;

    private int beginFrameCount;
    private int sceneCaptureCount;

    private OutlineFrameCaptureService() {
    }

    public static OutlineFrameCaptureService getInstance() {
        if (instance == null) {
            instance = new OutlineFrameCaptureService();
        }
        return instance;
    }

    public void beginFrame() {
        RenderSystem.assertOnRenderThread();
        RenderTarget target = MaskBufferManager.getInstance().getMaskTarget();
        RenderTarget hollowTarget = MaskBufferManager.getInstance().getHollowMaskTarget();
        beginFrameCount++;
        if (OutlineDebugFlags.FRAME && beginFrameCount <= 16) {
            LOGGER.info("Begin frame #{}, mask={}x{}", beginFrameCount, target.width, target.height);
        }

        RenderSystem.getDevice()
                .createCommandEncoder()
                .clearColorAndDepthTextures(
                        Objects.requireNonNull(target.getColorTexture(), "Mask color texture is not initialized"),
                        0,
                        Objects.requireNonNull(target.getDepthTexture(), "Mask depth texture is not initialized"),
                        1.0
                );

        RenderSystem.getDevice()
                .createCommandEncoder()
                .clearColorAndDepthTextures(
                        Objects.requireNonNull(hollowTarget.getColorTexture(), "Hollow mask color texture is not initialized"),
                        0,
                        Objects.requireNonNull(hollowTarget.getDepthTexture(), "Hollow mask depth texture is not initialized"),
                        1.0
                );
    }

    public void captureSceneDepthBeforeHand() {
        RenderSystem.assertOnRenderThread();
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        RenderTarget sceneTarget = MaskBufferManager.getInstance().getSceneDepthTarget();
        sceneTarget.copyDepthFrom(mainTarget);
        sceneCaptureCount++;
        if (OutlineDebugFlags.FRAME && sceneCaptureCount <= 24) {
            LOGGER.info("Captured scene depth before hand clear: pass={}/24, main={}x{}",
                    sceneCaptureCount,
                    mainTarget.width,
                    mainTarget.height);
        }
    }
}

