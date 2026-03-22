package quiet.enchantmentoutline.technique.input;

import com.mojang.blaze3d.pipeline.RenderTarget;

import java.util.Objects;

/**
 * 算法执行输入快照，避免算法实现直接依赖外部单例。
 */
public final class OutlineTechniqueContext implements OutlineTechniqueInput {
    private final RenderTarget mainTarget;
    private final RenderTarget worldRawMaskTarget;
    private final RenderTarget firstPersonRawMaskTarget;
    private final RenderTarget worldHollowMaskTarget;
    private final RenderTarget firstPersonHollowMaskTarget;
    private final RenderTarget worldSceneDepthTarget;
    private final RenderTarget firstPersonSceneDepthTarget;
    private final OutlineFrameData frameData;
    private final OutlineAdvancedInput advancedInput;

    public OutlineTechniqueContext(RenderTarget mainTarget,
                                   RenderTarget worldRawMaskTarget,
                                   RenderTarget firstPersonRawMaskTarget,
                                   RenderTarget worldHollowMaskTarget,
                                   RenderTarget firstPersonHollowMaskTarget,
                                   RenderTarget worldSceneDepthTarget,
                                   RenderTarget firstPersonSceneDepthTarget,
                                   OutlineFrameData frameData,
                                   OutlineAdvancedInput advancedInput) {
        this.mainTarget = Objects.requireNonNull(mainTarget, "mainTarget");
        this.worldRawMaskTarget = Objects.requireNonNull(worldRawMaskTarget, "worldRawMaskTarget");
        this.firstPersonRawMaskTarget = Objects.requireNonNull(firstPersonRawMaskTarget, "firstPersonRawMaskTarget");
        this.worldHollowMaskTarget = Objects.requireNonNull(worldHollowMaskTarget, "worldHollowMaskTarget");
        this.firstPersonHollowMaskTarget = Objects.requireNonNull(firstPersonHollowMaskTarget, "firstPersonHollowMaskTarget");
        this.worldSceneDepthTarget = Objects.requireNonNull(worldSceneDepthTarget, "worldSceneDepthTarget");
        this.firstPersonSceneDepthTarget = Objects.requireNonNull(firstPersonSceneDepthTarget, "firstPersonSceneDepthTarget");
        this.frameData = Objects.requireNonNull(frameData, "frameData");
        this.advancedInput = Objects.requireNonNull(advancedInput, "advancedInput");
    }

    @Override
    public RenderTarget mainTarget() {
        return mainTarget;
    }

    @Override
    public RenderTarget worldRawMaskTarget() {
        return worldRawMaskTarget;
    }

    @Override
    public RenderTarget firstPersonRawMaskTarget() {
        return firstPersonRawMaskTarget;
    }

    @Override
    public RenderTarget worldHollowMaskTarget() {
        return worldHollowMaskTarget;
    }

    @Override
    public RenderTarget firstPersonHollowMaskTarget() {
        return firstPersonHollowMaskTarget;
    }

    @Override
    public RenderTarget worldSceneDepthTarget() {
        return worldSceneDepthTarget;
    }

    @Override
    public RenderTarget firstPersonSceneDepthTarget() {
        return firstPersonSceneDepthTarget;
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

