package quiet.enchantmentoutline.acquire.dispatch;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.acquire.rawmask.RawMaskAcquireStep;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;
import quiet.enchantmentoutline.runtime.buffer.MaskBufferManager;
import quiet.enchantmentoutline.technique.input.BranchRenderTargets;
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
        return acquire(frameIndex, settings, DeltaTracker.ZERO);
    }

    public RawInputSnapshot acquire(int frameIndex, OutlineTechniqueSettings settings, DeltaTracker deltaTracker) {
        RenderSystem.assertOnRenderThread();

        Minecraft minecraft = Minecraft.getInstance();
        RenderTarget mainTarget = minecraft.getMainRenderTarget();
        RenderTarget worldRawMaskTarget = rawMaskAcquireStep.worldRawMaskTarget();
        RenderTarget firstPersonRawMaskTarget = rawMaskAcquireStep.firstPersonRawMaskTarget();
        RenderTarget worldHollowMaskTarget = rawMaskAcquireStep.worldHollowMaskTarget();
        RenderTarget firstPersonHollowMaskTarget = rawMaskAcquireStep.firstPersonHollowMaskTarget();
        RenderTarget worldSceneDepthTarget = MaskBufferManager.getInstance().getWorldSceneDepthTarget();
        RenderTarget firstPersonSceneDepthTarget = MaskBufferManager.getInstance().getFirstPersonSceneDepthTarget();
        BranchRenderTargets worldBranch = new BranchRenderTargets(worldRawMaskTarget, worldHollowMaskTarget, worldSceneDepthTarget);
        BranchRenderTargets firstPersonBranch = new BranchRenderTargets(firstPersonRawMaskTarget, firstPersonHollowMaskTarget, firstPersonSceneDepthTarget);
        int viewportWidth = mainTarget.width;
        int viewportHeight = mainTarget.height;
        boolean worldLoaded = minecraft.level != null;
        DeltaTracker safeDeltaTracker = deltaTracker == null ? DeltaTracker.ZERO : deltaTracker;
        RawAdvancedFrameData advancedRawData = collectAdvancedRawData(minecraft, safeDeltaTracker);

        if (OutlineDebugFlags.TECHNIQUE && dispatchLogCount < 20) {
            dispatchLogCount++;
            LOGGER.info("Acquire snapshot: frame={}, viewport={}x{}, worldMask={}x{}, handMask={}x{} ({}/20)",
                    frameIndex,
                    viewportWidth,
                    viewportHeight,
                    worldRawMaskTarget.width,
                    worldRawMaskTarget.height,
                    firstPersonRawMaskTarget.width,
                    firstPersonRawMaskTarget.height,
                    dispatchLogCount);
        }

        return new RawInputSnapshot(
                mainTarget,
                worldBranch,
                firstPersonBranch,
                frameIndex,
                viewportWidth,
                viewportHeight,
                worldLoaded,
                settings,
                advancedRawData
        );
    }

    private static RawAdvancedFrameData collectAdvancedRawData(Minecraft minecraft, DeltaTracker deltaTracker) {
        Camera camera = minecraft.gameRenderer.getMainCamera();
        boolean cameraReady = camera.isInitialized();
        double cameraX = cameraReady ? camera.position().x : 0.0;
        double cameraY = cameraReady ? camera.position().y : 0.0;
        double cameraZ = cameraReady ? camera.position().z : 0.0;
        float cameraYaw = cameraReady ? camera.yaw() : 0.0F;
        float cameraPitch = cameraReady ? camera.xRot() : 0.0F;
        long gameTime = minecraft.level != null ? minecraft.level.getGameTime() : 0L;
        float dayTime = (gameTime % 24000L) / 24000.0F;

        return new RawAdvancedFrameData(
                deltaTracker.getGameTimeDeltaTicks(),
                deltaTracker.getGameTimeDeltaPartialTick(false),
                deltaTracker.getRealtimeDeltaTicks(),
                gameTime,
                dayTime,
                cameraX,
                cameraY,
                cameraZ,
                cameraYaw,
                cameraPitch,
                cameraReady
        );
    }
}

