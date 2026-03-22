package quiet.enchantmentoutline.runtime.buffer;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;

/**
 * 职责描述: 管理附魔轮廓渲染使用的 RenderTarget 生命周期。
 * 交互映射: 被渲染钩子、采集流程和渲染层配置统一复用。
 */
public class MaskBufferManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Buffer");
    private static MaskBufferManager instance;
    private RenderTarget worldMaskTarget;
    private RenderTarget firstPersonMaskTarget;
    private RenderTarget worldHollowMaskTarget;
    private RenderTarget firstPersonHollowMaskTarget;
    private RenderTarget worldSceneDepthTarget;
    private RenderTarget firstPersonSceneDepthTarget;

    private MaskBufferManager() {
    }

    public static MaskBufferManager getInstance() {
        if (instance == null) {
            instance = new MaskBufferManager();
        }
        return instance;
    }

    public RenderTarget getMaskTarget() {
        if (worldMaskTarget == null) {
            init(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        return worldMaskTarget;
    }

    public RenderTarget getWorldMaskTarget() {
        return getMaskTarget();
    }

    public RenderTarget getFirstPersonMaskTarget() {
        if (firstPersonMaskTarget == null) {
            init(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        return firstPersonMaskTarget;
    }

    public RenderTarget getHollowMaskTarget() {
        if (worldHollowMaskTarget == null) {
            init(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        return worldHollowMaskTarget;
    }

    public RenderTarget getWorldHollowMaskTarget() {
        return getHollowMaskTarget();
    }

    public RenderTarget getFirstPersonHollowMaskTarget() {
        if (firstPersonHollowMaskTarget == null) {
            init(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        return firstPersonHollowMaskTarget;
    }

    public RenderTarget getSceneDepthTarget() {
        if (worldSceneDepthTarget == null) {
            init(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        return worldSceneDepthTarget;
    }

    public RenderTarget getWorldSceneDepthTarget() {
        return getSceneDepthTarget();
    }

    public RenderTarget getFirstPersonSceneDepthTarget() {
        if (firstPersonSceneDepthTarget == null) {
            init(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        return firstPersonSceneDepthTarget;
    }

    public void init(int width, int height) {
        RenderSystem.assertOnRenderThread();
        destroyIfPresent(worldMaskTarget);
        destroyIfPresent(firstPersonMaskTarget);
        destroyIfPresent(worldHollowMaskTarget);
        destroyIfPresent(firstPersonHollowMaskTarget);
        destroyIfPresent(worldSceneDepthTarget);
        destroyIfPresent(firstPersonSceneDepthTarget);
        if (OutlineDebugFlags.BUFFER) {
            LOGGER.info("Initializing mask buffer: {}x{}", width, height);
        }
        worldMaskTarget = new TextureTarget("Enchantment World Mask", width, height, true);
        firstPersonMaskTarget = new TextureTarget("Enchantment First Person Mask", width, height, true);
        worldHollowMaskTarget = new TextureTarget("Enchantment World Hollow Mask", width, height, true);
        firstPersonHollowMaskTarget = new TextureTarget("Enchantment First Person Hollow Mask", width, height, true);
        worldSceneDepthTarget = new TextureTarget("Enchantment World Scene Depth", width, height, true);
        firstPersonSceneDepthTarget = new TextureTarget("Enchantment First Person Scene Depth", width, height, true);
    }

    public void onRescale(int width, int height) {
        resizeIfPresent(worldMaskTarget, width, height, "world mask");
        resizeIfPresent(firstPersonMaskTarget, width, height, "first-person mask");
        resizeIfPresent(worldSceneDepthTarget, width, height, "world scene depth");
        resizeIfPresent(firstPersonSceneDepthTarget, width, height, "first-person scene depth");
        resizeIfPresent(worldHollowMaskTarget, width, height, "world hollow mask");
        resizeIfPresent(firstPersonHollowMaskTarget, width, height, "first-person hollow mask");
    }

    private static void destroyIfPresent(RenderTarget target) {
        if (target != null) {
            target.destroyBuffers();
        }
    }

    private static void resizeIfPresent(RenderTarget target, int width, int height, String name) {
        if (target == null) {
            return;
        }
        target.resize(width, height);
        if (OutlineDebugFlags.BUFFER) {
            LOGGER.info("Rescaled {} buffer to {}x{}", name, width, height);
        }
    }
}

