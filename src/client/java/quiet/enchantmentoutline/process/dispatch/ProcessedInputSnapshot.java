package quiet.enchantmentoutline.process.dispatch;

import com.mojang.blaze3d.pipeline.RenderTarget;
import quiet.enchantmentoutline.technique.input.BranchRenderTargets;
import quiet.enchantmentoutline.technique.input.OutlineAdvancedInput;
import quiet.enchantmentoutline.technique.input.OutlineFrameData;

import java.util.Objects;

/**
 * 职责描述: 承载处理阶段产出的最终输入快照。
 * 交互映射: 由 ProcessPipelineCoordinator 构建，供 context 装配器消费。
 */
public final class ProcessedInputSnapshot {
    private final RenderTarget mainTarget;
    private final BranchRenderTargets worldBranch;
    private final BranchRenderTargets firstPersonBranch;
    private final OutlineFrameData frameData;
    private final OutlineAdvancedInput advancedInput;

    private ProcessedInputSnapshot(Builder builder) {
        this.mainTarget = Objects.requireNonNull(builder.mainTarget, "mainTarget");
        this.worldBranch = Objects.requireNonNull(builder.worldBranch, "worldBranch");
        this.firstPersonBranch = Objects.requireNonNull(builder.firstPersonBranch, "firstPersonBranch");
        this.frameData = Objects.requireNonNull(builder.frameData, "frameData");
        this.advancedInput = Objects.requireNonNull(builder.advancedInput, "advancedInput");
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

    public OutlineFrameData frameData() {
        return frameData;
    }

    public OutlineAdvancedInput advancedInput() {
        return advancedInput;
    }

    public static final class Builder {
        private RenderTarget mainTarget;
        private BranchRenderTargets worldBranch;
        private BranchRenderTargets firstPersonBranch;
        private RenderTarget worldRawMaskTarget;
        private RenderTarget worldHollowMaskTarget;
        private RenderTarget worldSceneDepthTarget;
        private RenderTarget firstPersonRawMaskTarget;
        private RenderTarget firstPersonHollowMaskTarget;
        private RenderTarget firstPersonSceneDepthTarget;
        private OutlineFrameData frameData;
        private OutlineAdvancedInput advancedInput = OutlineAdvancedInput.disabled();

        public Builder mainTarget(RenderTarget target) {
            this.mainTarget = target;
            return this;
        }

        public Builder worldBranch(BranchRenderTargets branch) {
            this.worldBranch = branch;
            return this;
        }

        public Builder firstPersonBranch(BranchRenderTargets branch) {
            this.firstPersonBranch = branch;
            return this;
        }

        @Deprecated
        public Builder rawMaskTarget(RenderTarget target) {
            this.worldRawMaskTarget = target;
            return this;
        }

        @Deprecated
        public Builder hollowMaskTarget(RenderTarget target) {
            this.worldHollowMaskTarget = target;
            return this;
        }

        @Deprecated
        public Builder sceneDepthTarget(RenderTarget target) {
            this.worldSceneDepthTarget = target;
            return this;
        }

        public Builder worldRawMaskTarget(RenderTarget target) {
            this.worldRawMaskTarget = target;
            return this;
        }

        public Builder firstPersonRawMaskTarget(RenderTarget target) {
            this.firstPersonRawMaskTarget = target;
            return this;
        }

        public Builder worldHollowMaskTarget(RenderTarget target) {
            this.worldHollowMaskTarget = target;
            return this;
        }

        public Builder firstPersonHollowMaskTarget(RenderTarget target) {
            this.firstPersonHollowMaskTarget = target;
            return this;
        }

        public Builder worldSceneDepthTarget(RenderTarget target) {
            this.worldSceneDepthTarget = target;
            return this;
        }

        public Builder firstPersonSceneDepthTarget(RenderTarget target) {
            this.firstPersonSceneDepthTarget = target;
            return this;
        }

        public Builder frameData(OutlineFrameData frameData) {
            this.frameData = frameData;
            return this;
        }

        public Builder advancedInput(OutlineAdvancedInput advancedInput) {
            this.advancedInput = advancedInput;
            return this;
        }

        public ProcessedInputSnapshot build() {
            if (worldBranch == null) {
                worldBranch = requireBranch("world", worldRawMaskTarget, worldHollowMaskTarget, worldSceneDepthTarget);
            }
            if (firstPersonBranch == null) {
                firstPersonBranch = requireBranch("first_person", firstPersonRawMaskTarget, firstPersonHollowMaskTarget, firstPersonSceneDepthTarget);
            }
            return new ProcessedInputSnapshot(this);
        }

        private static BranchRenderTargets requireBranch(String branchName,
                                                         RenderTarget rawMaskTarget,
                                                         RenderTarget hollowMaskTarget,
                                                         RenderTarget sceneDepthTarget) {
            if (rawMaskTarget == null || hollowMaskTarget == null || sceneDepthTarget == null) {
                throw new IllegalStateException("Missing branch targets for " + branchName + " branch");
            }
            return new BranchRenderTargets(rawMaskTarget, hollowMaskTarget, sceneDepthTarget);
        }
    }
}

