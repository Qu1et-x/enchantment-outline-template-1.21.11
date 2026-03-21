package quiet.enchantmentoutline.acquire.dispatch;

import com.mojang.blaze3d.pipeline.RenderTarget;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueSettings;

/**
 * 职责描述: 承载采集阶段的原始输入快照。
 * 交互映射: 由 RawAcquireDispatcher 创建，交给 process 模块继续处理。
 */
public final class RawInputSnapshot {
    private final RenderTarget mainTarget;
    private final RenderTarget rawMaskTarget;
    private final RenderTarget hollowMaskTarget;
    private final RenderTarget sceneDepthTarget;
    private final int frameIndex;
    private final int viewportWidth;
    private final int viewportHeight;
    private final boolean worldLoaded;
    private final OutlineTechniqueSettings settings;
    private final RawAdvancedFrameData advancedRawData;

    public RawInputSnapshot(RenderTarget mainTarget,
                            RenderTarget rawMaskTarget,
                            RenderTarget hollowMaskTarget,
                            RenderTarget sceneDepthTarget,
                            int frameIndex,
                            int viewportWidth,
                            int viewportHeight,
                            boolean worldLoaded,
                            OutlineTechniqueSettings settings,
                            RawAdvancedFrameData advancedRawData) {
        this.mainTarget = mainTarget;
        this.rawMaskTarget = rawMaskTarget;
        this.hollowMaskTarget = hollowMaskTarget;
        this.sceneDepthTarget = sceneDepthTarget;
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
        return rawMaskTarget;
    }

    public RenderTarget hollowMaskTarget() {
        return hollowMaskTarget;
    }

    public RenderTarget sceneDepthTarget() {
        return sceneDepthTarget;
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

