package quiet.enchantmentoutline.technique.context;

import com.mojang.blaze3d.pipeline.RenderTarget;

import java.util.Objects;

/**
 * 算法执行上下文，避免算法实现直接依赖外部单例。
 */
public final class OutlineTechniqueContext {
    private final RenderTarget mainTarget;
    private final RenderTarget rawMaskTarget;
    private final RenderTarget hollowMaskTarget;
    private final RenderTarget sceneDepthTarget;
    private final OutlineFrameData frameData;

    public OutlineTechniqueContext(RenderTarget mainTarget, RenderTarget maskTarget, RenderTarget sceneDepthTarget) {
        this(requireMainTarget(mainTarget),
                maskTarget,
                maskTarget,
                sceneDepthTarget,
                defaultFrameData(mainTarget));
    }

    public OutlineTechniqueContext(RenderTarget mainTarget,
                                   RenderTarget rawMaskTarget,
                                   RenderTarget hollowMaskTarget,
                                   RenderTarget sceneDepthTarget) {
        this(mainTarget,
                rawMaskTarget,
                hollowMaskTarget,
                sceneDepthTarget,
                defaultFrameData(mainTarget));
    }

    public OutlineTechniqueContext(RenderTarget mainTarget,
                                   RenderTarget rawMaskTarget,
                                   RenderTarget hollowMaskTarget,
                                   RenderTarget sceneDepthTarget,
                                   OutlineFrameData frameData) {
        this.mainTarget = Objects.requireNonNull(mainTarget, "mainTarget");
        this.rawMaskTarget = Objects.requireNonNull(rawMaskTarget, "rawMaskTarget");
        this.hollowMaskTarget = Objects.requireNonNull(hollowMaskTarget, "hollowMaskTarget");
        this.sceneDepthTarget = Objects.requireNonNull(sceneDepthTarget, "sceneDepthTarget");
        this.frameData = Objects.requireNonNull(frameData, "frameData");
    }

    public RenderTarget mainTarget() {
        return mainTarget;
    }

    public RenderTarget maskTarget() {
        return rawMaskTarget;
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

    private static RenderTarget requireMainTarget(RenderTarget mainTarget) {
        return Objects.requireNonNull(mainTarget, "mainTarget");
    }

    private static OutlineFrameData defaultFrameData(RenderTarget mainTarget) {
        RenderTarget target = requireMainTarget(mainTarget);
        return new OutlineFrameData(0, target.width, target.height, true, OutlineTechniqueSettings.DEFAULT);
    }
}


