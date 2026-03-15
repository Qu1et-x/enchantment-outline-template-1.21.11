package quiet.enchantmentoutline.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 职责描述: 管理附魔轮廓掩码 RenderTarget。
 * 交互映射: 每帧重置掩码颜色/深度，供两阶段掩码写入与后处理采样。
 */
public class MaskBufferManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Buffer");
    private static MaskBufferManager instance;
    private RenderTarget maskTarget;
    private RenderTarget sceneDepthTarget;
    private int beginFrameCount;
    private int sceneCaptureCount;

    private MaskBufferManager() {}

    public static MaskBufferManager getInstance() {
        if (instance == null) {
            instance = new MaskBufferManager();
        }
        return instance;
    }

    public RenderTarget getMaskTarget() {
        if (maskTarget == null) {
            init(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        return maskTarget;
    }

    public void init(int width, int height) {
        RenderSystem.assertOnRenderThread();
        if (maskTarget != null) {
            maskTarget.destroyBuffers();
        }
        if (sceneDepthTarget != null) {
            sceneDepthTarget.destroyBuffers();
        }
        LOGGER.info("Initializing mask buffer: {}x{}", width, height);
        maskTarget = new TextureTarget("Enchantment Mask", width, height, true);
        sceneDepthTarget = new TextureTarget("Enchantment Scene Depth", width, height, true);
    }

    /**
     * 每帧开始时清空掩码颜色。
     */
    public void beginFrame() {
        RenderSystem.assertOnRenderThread();
        RenderTarget target = getMaskTarget();
        beginFrameCount++;
        if (beginFrameCount <= 8) {
            LOGGER.info("Mask beginFrame #{}, size={}x{}", beginFrameCount, target.width, target.height);
        }

        // 每帧重置掩码颜色和深度，后续仅写入本帧轮廓几何。
        RenderSystem.getDevice()
                .createCommandEncoder()
                .clearColorAndDepthTextures(
                        Objects.requireNonNull(target.getColorTexture(), "Mask color texture is not initialized"),
                        0,
                        Objects.requireNonNull(target.getDepthTexture(), "Mask depth texture is not initialized"),
                        1.0
                );
    }

    /**
     * 在 renderLevel 内手部清深度前抓取场景深度（包含方块与实体）。
     */
    public void captureSceneDepthBeforeHand() {
        RenderSystem.assertOnRenderThread();
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        RenderTarget sceneTarget = getSceneDepthTarget();
        sceneTarget.copyDepthFrom(mainTarget);
        sceneCaptureCount++;
        if (sceneCaptureCount <= 20) {
            LOGGER.info("Captured scene depth before hand clear: pass={}/20, main={}x{}", sceneCaptureCount, mainTarget.width, mainTarget.height);
        }
    }

    public RenderTarget getSceneDepthTarget() {
        if (sceneDepthTarget == null) {
            init(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        return sceneDepthTarget;
    }

    public void drawAndFlush() {
        // 当前实现不再使用独立的 BufferSource，保留此方法以兼容调用方。
    }

    public void onRescale(int width, int height) {
        if (maskTarget != null) {
            maskTarget.resize(width, height);
            LOGGER.info("Rescaled mask buffer to {}x{}", width, height);
        }
        if (sceneDepthTarget != null) {
            sceneDepthTarget.resize(width, height);
            LOGGER.info("Rescaled scene depth buffer to {}x{}", width, height);
        }
    }
}
