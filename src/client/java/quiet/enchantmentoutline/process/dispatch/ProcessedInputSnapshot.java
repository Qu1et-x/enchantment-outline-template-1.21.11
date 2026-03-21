package quiet.enchantmentoutline.process.dispatch;

import com.mojang.blaze3d.pipeline.RenderTarget;
import quiet.enchantmentoutline.technique.input.OutlineFrameData;

import java.util.Objects;

/**
 * 职责描述: 承载处理阶段产出的最终输入快照。
 * 交互映射: 由 ProcessPipelineCoordinator 构建，供 context 装配器消费。
 */
public final class ProcessedInputSnapshot {
    private final RenderTarget mainTarget;
    private final RenderTarget rawMaskTarget;
    private final RenderTarget hollowMaskTarget;
    private final RenderTarget sceneDepthTarget;
    private final OutlineFrameData frameData;

    private ProcessedInputSnapshot(Builder builder) {
        this.mainTarget = Objects.requireNonNull(builder.mainTarget, "mainTarget");
        this.rawMaskTarget = Objects.requireNonNull(builder.rawMaskTarget, "rawMaskTarget");
        this.hollowMaskTarget = Objects.requireNonNull(builder.hollowMaskTarget, "hollowMaskTarget");
        this.sceneDepthTarget = Objects.requireNonNull(builder.sceneDepthTarget, "sceneDepthTarget");
        this.frameData = Objects.requireNonNull(builder.frameData, "frameData");
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

    public OutlineFrameData frameData() {
        return frameData;
    }

    public static final class Builder {
        private RenderTarget mainTarget;
        private RenderTarget rawMaskTarget;
        private RenderTarget hollowMaskTarget;
        private RenderTarget sceneDepthTarget;
        private OutlineFrameData frameData;

        public Builder mainTarget(RenderTarget target) {
            this.mainTarget = target;
            return this;
        }

        public Builder rawMaskTarget(RenderTarget target) {
            this.rawMaskTarget = target;
            return this;
        }

        public Builder hollowMaskTarget(RenderTarget target) {
            this.hollowMaskTarget = target;
            return this;
        }

        public Builder sceneDepthTarget(RenderTarget target) {
            this.sceneDepthTarget = target;
            return this;
        }

        public Builder frameData(OutlineFrameData frameData) {
            this.frameData = frameData;
            return this;
        }

        public ProcessedInputSnapshot build() {
            return new ProcessedInputSnapshot(this);
        }
    }
}

