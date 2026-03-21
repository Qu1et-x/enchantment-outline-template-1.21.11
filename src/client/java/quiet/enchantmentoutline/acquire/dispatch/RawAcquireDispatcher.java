package quiet.enchantmentoutline.acquire.dispatch;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.acquire.rawmask.RawMaskAcquireStep;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;
import quiet.enchantmentoutline.runtime.buffer.MaskBufferManager;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueSettings;

/**
 * 职责描述: 聚合各原始数据来源，输出统一 RawInputSnapshot。
 * 交互映射: 在 OutlineRenderOrchestrator 中被调用，连接 acquire 与 process 两大模块。
 */
public final class RawAcquireDispatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Acquire");
    private static RawAcquireDispatcher instance;
    private final RawMaskAcquireStep rawMaskAcquireStep = new RawMaskAcquireStep();
    private int dispatchLogCount;

    private RawAcquireDispatcher() {
    }

    public static RawAcquireDispatcher getInstance() {
        if (instance == null) {
            instance = new RawAcquireDispatcher();
        }
        return instance;
    }

    public RawInputSnapshot acquire(int frameIndex, OutlineTechniqueSettings settings) {
        RenderSystem.assertOnRenderThread();

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        RenderTarget rawMaskTarget = rawMaskAcquireStep.rawMaskTarget();
        RenderTarget hollowMaskTarget = rawMaskAcquireStep.hollowMaskTarget();
        RenderTarget sceneDepthTarget = MaskBufferManager.getInstance().getSceneDepthTarget();
        int rawFrameIndex = frameIndex;
        int viewportWidth = mainTarget.width;
        int viewportHeight = mainTarget.height;
        boolean worldLoaded = minecraft.level != null;
        OutlineTechniqueSettings acquiredSettings = settings;

        if (OutlineDebugFlags.TECHNIQUE && dispatchLogCount < 20) {
            dispatchLogCount++;
            LOGGER.info("Acquire snapshot: frame={}, viewport={}x{}, mask={}x{} ({}/20)",
                    rawFrameIndex,
                    viewportWidth,
                    viewportHeight,
                    rawMaskTarget.width,
                    rawMaskTarget.height,
                    dispatchLogCount);
        }

        return new RawInputSnapshot(
                mainTarget,
                rawMaskTarget,
                hollowMaskTarget,
                sceneDepthTarget,
                rawFrameIndex,
                viewportWidth,
                viewportHeight,
                worldLoaded,
                acquiredSettings
        );
    }
}

