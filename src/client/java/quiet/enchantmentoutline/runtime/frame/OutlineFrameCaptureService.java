package quiet.enchantmentoutline.runtime.frame;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;
import quiet.enchantmentoutline.runtime.buffer.MaskBufferManager;

import java.util.Objects;

/**
 * 职责描述: 帧阶段处理，负责清理掩码并捕获手部清深度前的场景深度。
 * 交互映射: 在 GameRendererMixin 帧钩子中调用。
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
        RenderTarget worldMaskTarget = MaskBufferManager.getInstance().getWorldMaskTarget();
        RenderTarget firstPersonMaskTarget = MaskBufferManager.getInstance().getFirstPersonMaskTarget();
        RenderTarget worldHollowTarget = MaskBufferManager.getInstance().getWorldHollowMaskTarget();
        RenderTarget firstPersonHollowTarget = MaskBufferManager.getInstance().getFirstPersonHollowMaskTarget();
        beginFrameCount++;
        if (OutlineDebugFlags.FRAME && beginFrameCount <= 16) {
            LOGGER.info("Begin frame #{}, worldMask={}x{}, handMask={}x{}",
                    beginFrameCount,
                    worldMaskTarget.width,
                    worldMaskTarget.height,
                    firstPersonMaskTarget.width,
                    firstPersonMaskTarget.height);
        }

        clearTarget(worldMaskTarget, "World mask");
        clearTarget(firstPersonMaskTarget, "First-person mask");
        clearTarget(worldHollowTarget, "World hollow mask");
        clearTarget(firstPersonHollowTarget, "First-person hollow mask");
    }

    public void captureSceneDepthBeforeHand() {
        RenderSystem.assertOnRenderThread();
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        RenderTarget sceneTarget = MaskBufferManager.getInstance().getWorldSceneDepthTarget();
        sceneTarget.copyDepthFrom(mainTarget);
        sceneCaptureCount++;
        if (OutlineDebugFlags.FRAME && sceneCaptureCount <= 24) {
            LOGGER.info("Captured scene depth before hand clear: pass={}/24, main={}x{}",
                    sceneCaptureCount,
                    mainTarget.width,
                    mainTarget.height);
        }
    }

    public void captureSceneDepthAfterHand() {
        RenderSystem.assertOnRenderThread();
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        RenderTarget sceneTarget = MaskBufferManager.getInstance().getFirstPersonSceneDepthTarget();
        sceneTarget.copyDepthFrom(mainTarget);
        sceneCaptureCount++;
        if (OutlineDebugFlags.FRAME && sceneCaptureCount <= 24) {
            LOGGER.info("Captured scene depth after hand render: pass={}/24, main={}x{}",
                    sceneCaptureCount,
                    mainTarget.width,
                    mainTarget.height);
        }
    }

    private static void clearTarget(RenderTarget target, String name) {
        RenderSystem.getDevice()
                .createCommandEncoder()
                .clearColorAndDepthTextures(
                        Objects.requireNonNull(target.getColorTexture(), name + " color texture is not initialized"),
                        0,
                        Objects.requireNonNull(target.getDepthTexture(), name + " depth texture is not initialized"),
                        1.0
                );
    }
}

