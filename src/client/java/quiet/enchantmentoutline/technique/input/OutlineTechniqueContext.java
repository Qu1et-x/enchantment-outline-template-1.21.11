package quiet.enchantmentoutline.technique.input;

import com.mojang.blaze3d.pipeline.RenderTarget;

import java.util.Objects;

/**
 * 算法执行输入快照，避免算法实现直接依赖外部单例。
 */
public final class OutlineTechniqueContext implements OutlineTechniqueInput {
    private final RenderTarget mainTarget;
    private final BranchRenderTargets worldBranch;
    private final BranchRenderTargets firstPersonBranch;
    private final OutlineFrameData frameData;
    private final OutlineAdvancedInput advancedInput;

    public OutlineTechniqueContext(RenderTarget mainTarget,
                                   BranchRenderTargets worldBranch,
                                   BranchRenderTargets firstPersonBranch,
                                   OutlineFrameData frameData,
                                   OutlineAdvancedInput advancedInput) {
        this.mainTarget = Objects.requireNonNull(mainTarget, "mainTarget");
        this.worldBranch = Objects.requireNonNull(worldBranch, "worldBranch");
        this.firstPersonBranch = Objects.requireNonNull(firstPersonBranch, "firstPersonBranch");
        this.frameData = Objects.requireNonNull(frameData, "frameData");
        this.advancedInput = Objects.requireNonNull(advancedInput, "advancedInput");
    }

    @Override
    public BranchRenderTargets worldBranch() {
        return worldBranch;
    }

    @Override
    public BranchRenderTargets firstPersonBranch() {
        return firstPersonBranch;
    }

    @Override
    public RenderTarget mainTarget() {
        return mainTarget;
    }

    @Override
    public OutlineFrameData frameData() {
        return frameData;
    }

    @Override
    public OutlineAdvancedInput advancedInput() {
        return advancedInput;
    }
}

