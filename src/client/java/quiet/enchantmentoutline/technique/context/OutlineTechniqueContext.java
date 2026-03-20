package quiet.enchantmentoutline.technique.context;

import com.mojang.blaze3d.pipeline.RenderTarget;

import java.util.Objects;

/**
 * 算法执行上下文，避免算法实现直接依赖外部单例。
 */
public final class OutlineTechniqueContext implements OutlineTechniqueInput {
    private final RenderTarget mainTarget;
    private final RenderTarget rawMaskTarget;
    private final RenderTarget hollowMaskTarget;
    private final RenderTarget sceneDepthTarget;
    private final OutlineFrameData frameData;

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

    @Override
    public RenderTarget mainTarget() {
        return mainTarget;
    }

    @Override
    public RenderTarget rawMaskTarget() {
        return rawMaskTarget;
    }

    @Override
    public RenderTarget hollowMaskTarget() {
        return hollowMaskTarget;
    }

    @Override
    public RenderTarget sceneDepthTarget() {
        return sceneDepthTarget;
    }

    @Override
    public OutlineFrameData frameData() {
        return frameData;
    }
}


