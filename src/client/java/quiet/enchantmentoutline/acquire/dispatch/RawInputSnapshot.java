package quiet.enchantmentoutline.acquire.dispatch;

import com.mojang.blaze3d.pipeline.RenderTarget;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueSettings;

import java.util.Objects;

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

    public RawInputSnapshot(RenderTarget mainTarget,
                            RenderTarget rawMaskTarget,
                            RenderTarget hollowMaskTarget,
                            RenderTarget sceneDepthTarget,
                            int frameIndex,
                            int viewportWidth,
                            int viewportHeight,
                            boolean worldLoaded,
                            OutlineTechniqueSettings settings) {
        this.mainTarget = Objects.requireNonNull(mainTarget, "mainTarget");
        this.rawMaskTarget = Objects.requireNonNull(rawMaskTarget, "rawMaskTarget");
        this.hollowMaskTarget = Objects.requireNonNull(hollowMaskTarget, "hollowMaskTarget");
        this.sceneDepthTarget = Objects.requireNonNull(sceneDepthTarget, "sceneDepthTarget");
        this.frameIndex = Math.max(0, frameIndex);
        this.viewportWidth = Math.max(1, viewportWidth);
        this.viewportHeight = Math.max(1, viewportHeight);
        this.worldLoaded = worldLoaded;
        this.settings = Objects.requireNonNull(settings, "settings");
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
}

