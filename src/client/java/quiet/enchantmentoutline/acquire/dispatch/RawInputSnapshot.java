package quiet.enchantmentoutline.acquire.dispatch;

import com.mojang.blaze3d.pipeline.RenderTarget;
import quiet.enchantmentoutline.technique.input.BranchRenderTargets;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueSettings;

import java.util.Objects;

/**
 * 职责描述: 承载采集阶段的原始输入快照。
 * 交互映射: 由 RawAcquireDispatcher 创建，交给 process 模块继续处理。
 */
public final class RawInputSnapshot {
    private final RenderTarget mainTarget;
    private final BranchRenderTargets worldBranch;
    private final BranchRenderTargets firstPersonBranch;
    private final int frameIndex;
    private final int viewportWidth;
    private final int viewportHeight;
    private final boolean worldLoaded;
    private final OutlineTechniqueSettings settings;
    private final RawAdvancedFrameData advancedRawData;

    public RawInputSnapshot(RenderTarget mainTarget,
                            BranchRenderTargets worldBranch,
                            BranchRenderTargets firstPersonBranch,
                            int frameIndex,
                            int viewportWidth,
                            int viewportHeight,
                            boolean worldLoaded,
                            OutlineTechniqueSettings settings,
                            RawAdvancedFrameData advancedRawData) {
        this.mainTarget = Objects.requireNonNull(mainTarget, "mainTarget");
        this.worldBranch = Objects.requireNonNull(worldBranch, "worldBranch");
        this.firstPersonBranch = Objects.requireNonNull(firstPersonBranch, "firstPersonBranch");
        this.frameIndex = frameIndex;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
        this.worldLoaded = worldLoaded;
        this.settings = Objects.requireNonNull(settings, "settings");
        this.advancedRawData = advancedRawData;
    }

    public RenderTarget mainTarget() {
        return mainTarget;
    }

    public BranchRenderTargets worldBranch() {
        return worldBranch;
    }

    public BranchRenderTargets firstPersonBranch() {
        return firstPersonBranch;
    }

    @Deprecated
    public RenderTarget rawMaskTarget() {
        return worldBranch.rawMaskTarget();
    }

    @Deprecated
    public RenderTarget hollowMaskTarget() {
        return worldBranch.hollowMaskTarget();
    }

    @Deprecated
    public RenderTarget sceneDepthTarget() {
        return worldBranch.sceneDepthTarget();
    }

    public RenderTarget worldRawMaskTarget() {
        return worldBranch.rawMaskTarget();
    }

    public RenderTarget firstPersonRawMaskTarget() {
        return firstPersonBranch.rawMaskTarget();
    }

    public RenderTarget worldHollowMaskTarget() {
        return worldBranch.hollowMaskTarget();
    }

    public RenderTarget firstPersonHollowMaskTarget() {
        return firstPersonBranch.hollowMaskTarget();
    }

    public RenderTarget worldSceneDepthTarget() {
        return worldBranch.sceneDepthTarget();
    }

    public RenderTarget firstPersonSceneDepthTarget() {
        return firstPersonBranch.sceneDepthTarget();
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

