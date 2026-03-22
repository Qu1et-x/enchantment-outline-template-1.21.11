package quiet.enchantmentoutline.acquire.dispatch;

import com.mojang.blaze3d.pipeline.RenderTarget;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueSettings;

/**
 * 职责描述: 承载采集阶段的原始输入快照。
 * 交互映射: 由 RawAcquireDispatcher 创建，交给 process 模块继续处理。
 */
public final class RawInputSnapshot {
    private final RenderTarget mainTarget;
    private final RenderTarget worldRawMaskTarget;
    private final RenderTarget firstPersonRawMaskTarget;
    private final RenderTarget worldHollowMaskTarget;
    private final RenderTarget firstPersonHollowMaskTarget;
    private final RenderTarget worldSceneDepthTarget;
    private final RenderTarget firstPersonSceneDepthTarget;
    private final int frameIndex;
    private final int viewportWidth;
    private final int viewportHeight;
    private final boolean worldLoaded;
    private final OutlineTechniqueSettings settings;
    private final RawAdvancedFrameData advancedRawData;

    public RawInputSnapshot(RenderTarget mainTarget,
                            RenderTarget worldRawMaskTarget,
                            RenderTarget firstPersonRawMaskTarget,
                            RenderTarget worldHollowMaskTarget,
                            RenderTarget firstPersonHollowMaskTarget,
                            RenderTarget worldSceneDepthTarget,
                            RenderTarget firstPersonSceneDepthTarget,
                            int frameIndex,
                            int viewportWidth,
                            int viewportHeight,
                            boolean worldLoaded,
                            OutlineTechniqueSettings settings,
                            RawAdvancedFrameData advancedRawData) {
        this.mainTarget = mainTarget;
        this.worldRawMaskTarget = worldRawMaskTarget;
        this.firstPersonRawMaskTarget = firstPersonRawMaskTarget;
        this.worldHollowMaskTarget = worldHollowMaskTarget;
        this.firstPersonHollowMaskTarget = firstPersonHollowMaskTarget;
        this.worldSceneDepthTarget = worldSceneDepthTarget;
        this.firstPersonSceneDepthTarget = firstPersonSceneDepthTarget;
        this.frameIndex = frameIndex;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.worldLoaded = worldLoaded;
        this.settings = settings;
        this.advancedRawData = advancedRawData;
    }

    public RenderTarget mainTarget() {
        return mainTarget;
    }

    public RenderTarget rawMaskTarget() {
        return worldRawMaskTarget;
    }

    public RenderTarget hollowMaskTarget() {
        return worldHollowMaskTarget;
    }

    public RenderTarget sceneDepthTarget() {
        return worldSceneDepthTarget;
    }

    public RenderTarget worldRawMaskTarget() {
        return worldRawMaskTarget;
    }

    public RenderTarget firstPersonRawMaskTarget() {
        return firstPersonRawMaskTarget;
    }

    public RenderTarget worldHollowMaskTarget() {
        return worldHollowMaskTarget;
    }

    public RenderTarget firstPersonHollowMaskTarget() {
        return firstPersonHollowMaskTarget;
    }

    public RenderTarget worldSceneDepthTarget() {
        return worldSceneDepthTarget;
    }

    public RenderTarget firstPersonSceneDepthTarget() {
        return firstPersonSceneDepthTarget;
    }

    public int frameIndex() {
        return frameIndex;
    }

    public int viewportWidth() {
        return viewportWidth;
    }

    public int viewportHeight() {
        return viewportHeight;
    }

    public boolean worldLoaded() {
        return worldLoaded;
    }

    public OutlineTechniqueSettings settings() {
        return settings;
    }

    public RawAdvancedFrameData advancedRawData() {
        return advancedRawData;
    }
}

