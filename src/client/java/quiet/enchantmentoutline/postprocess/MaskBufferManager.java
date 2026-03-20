package quiet.enchantmentoutline.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;

/**
 * 职责描述: 管理附魔轮廓掩码 RenderTarget。
 * 交互映射: 每帧重置掩码颜色/深度，供两阶段掩码写入与后处理采样。
 */
public class MaskBufferManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Buffer");
    private static MaskBufferManager instance;
    private RenderTarget maskTarget;
    private RenderTarget hollowMaskTarget;
    private RenderTarget sceneDepthTarget;

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

    public RenderTarget getHollowMaskTarget() {
        if (hollowMaskTarget == null) {
            init(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        return hollowMaskTarget;
    }

    public void init(int width, int height) {
        RenderSystem.assertOnRenderThread();
        if (maskTarget != null) {
            maskTarget.destroyBuffers();
        }
        if (hollowMaskTarget != null) {
            hollowMaskTarget.destroyBuffers();
        }
        if (sceneDepthTarget != null) {
            sceneDepthTarget.destroyBuffers();
        }
        if (OutlineDebugFlags.BUFFER) {
            LOGGER.info("Initializing mask buffer: {}x{}", width, height);
        }
        maskTarget = new TextureTarget("Enchantment Mask", width, height, true);
        hollowMaskTarget = new TextureTarget("Enchantment Hollow Mask", width, height, true);
        sceneDepthTarget = new TextureTarget("Enchantment Scene Depth", width, height, true);
    }

    public RenderTarget getSceneDepthTarget() {
        if (sceneDepthTarget == null) {
            init(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        return sceneDepthTarget;
    }

    public void onRescale(int width, int height) {
        if (maskTarget != null) {
            maskTarget.resize(width, height);
            if (OutlineDebugFlags.BUFFER) {
                LOGGER.info("Rescaled mask buffer to {}x{}", width, height);
            }
        }
        if (sceneDepthTarget != null) {
            sceneDepthTarget.resize(width, height);
            if (OutlineDebugFlags.BUFFER) {
                LOGGER.info("Rescaled scene depth buffer to {}x{}", width, height);
            }
        }
        if (hollowMaskTarget != null) {
            hollowMaskTarget.resize(width, height);
            if (OutlineDebugFlags.BUFFER) {
                LOGGER.info("Rescaled hollow mask buffer to {}x{}", width, height);
            }
        }
    }
}
