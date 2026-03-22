package quiet.enchantmentoutline.process.dispatch;

import com.mojang.blaze3d.pipeline.RenderTarget;
import quiet.enchantmentoutline.technique.input.OutlineAdvancedInput;
import quiet.enchantmentoutline.technique.input.OutlineFrameData;

import java.util.Objects;

/**
 * 职责描述: 承载处理阶段产出的最终输入快照。
 * 交互映射: 由 ProcessPipelineCoordinator 构建，供 context 装配器消费。
 */
public final class ProcessedInputSnapshot {
    private final RenderTarget mainTarget;
    private final RenderTarget worldRawMaskTarget;
    private final RenderTarget firstPersonRawMaskTarget;
    private final RenderTarget worldHollowMaskTarget;
    private final RenderTarget firstPersonHollowMaskTarget;
    private final RenderTarget worldSceneDepthTarget;
    private final RenderTarget firstPersonSceneDepthTarget;
    private final OutlineFrameData frameData;
    private final OutlineAdvancedInput advancedInput;

    private ProcessedInputSnapshot(Builder builder) {
        this.mainTarget = Objects.requireNonNull(builder.mainTarget, "mainTarget");
        this.worldRawMaskTarget = Objects.requireNonNull(builder.worldRawMaskTarget, "worldRawMaskTarget");
        this.firstPersonRawMaskTarget = Objects.requireNonNull(builder.firstPersonRawMaskTarget, "firstPersonRawMaskTarget");
        this.worldHollowMaskTarget = Objects.requireNonNull(builder.worldHollowMaskTarget, "worldHollowMaskTarget");
        this.firstPersonHollowMaskTarget = Objects.requireNonNull(builder.firstPersonHollowMaskTarget, "firstPersonHollowMaskTarget");
        this.worldSceneDepthTarget = Objects.requireNonNull(builder.worldSceneDepthTarget, "worldSceneDepthTarget");
        this.firstPersonSceneDepthTarget = Objects.requireNonNull(builder.firstPersonSceneDepthTarget, "firstPersonSceneDepthTarget");
        this.frameData = Objects.requireNonNull(builder.frameData, "frameData");
        this.advancedInput = Objects.requireNonNull(builder.advancedInput, "advancedInput");
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

    public OutlineFrameData frameData() {
        return frameData;
    }

    public OutlineAdvancedInput advancedInput() {
        return advancedInput;
    }

    public static final class Builder {
        private RenderTarget mainTarget;
        private RenderTarget worldRawMaskTarget;
        private RenderTarget firstPersonRawMaskTarget;
        private RenderTarget worldHollowMaskTarget;
        private RenderTarget firstPersonHollowMaskTarget;
        private RenderTarget worldSceneDepthTarget;
        private RenderTarget firstPersonSceneDepthTarget;
        private OutlineFrameData frameData;
        private OutlineAdvancedInput advancedInput = OutlineAdvancedInput.disabled();

        public Builder mainTarget(RenderTarget target) {
            this.mainTarget = target;
            return this;
        }

        public Builder rawMaskTarget(RenderTarget target) {
            this.worldRawMaskTarget = target;
            return this;
        }

        public Builder hollowMaskTarget(RenderTarget target) {
            this.worldHollowMaskTarget = target;
            return this;
        }

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
            return new ProcessedInputSnapshot(this);
        }
    }
}

