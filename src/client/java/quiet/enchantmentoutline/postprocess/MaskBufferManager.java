package quiet.enchantmentoutline.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * 职责描述: 管理独立的 RenderTarget 和相应的 MultiBufferSource。
 * 交互映射: 提供独立的渲染源，并支持从主渲染目标同步深度缓冲区。
 */
public class MaskBufferManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Buffer");
    private static MaskBufferManager instance;
    private RenderTarget maskTarget;

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
        LOGGER.info("Initializing mask buffer: {}x{}", width, height);
        maskTarget = new TextureTarget("Enchantment Mask", width, height, true);
    }

    /**
     * 每帧开始时清空掩码颜色。
     */
    public void beginFrame() {
        RenderSystem.assertOnRenderThread();
        RenderTarget target = getMaskTarget();

        // 使用显式清屏指令，避免空 RenderPass 和可空参数告警。
        RenderSystem.getDevice()
                .createCommandEncoder()
                .clearColorTexture(Objects.requireNonNull(target.getColorTexture(), "Mask color texture is not initialized"), 0);
    }

    /**
     * 在提交附魔掩码前，同步一次主深度到掩码目标，避免轮廓穿透遮挡物。
     */
    public void syncDepthFromMain() {
        RenderSystem.assertOnRenderThread();
        RenderTarget target = getMaskTarget();
        target.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
    }

    public void drawAndFlush() {
        // 当前实现不再使用独立的 BufferSource，保留此方法以兼容调用方。
    }

    public void onRescale(int width, int height) {
        if (maskTarget != null) {
            maskTarget.resize(width, height);
            LOGGER.debug("Rescaled mask buffer to {}x{}", width, height);
        }
    }
}
