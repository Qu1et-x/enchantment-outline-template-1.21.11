package quiet.enchantmentoutline.technique.input;

import com.mojang.blaze3d.pipeline.RenderTarget;

/**
 * 算法统一输入契约。
 */
public interface OutlineTechniqueInput {
    RenderTarget mainTarget();

    BranchRenderTargets worldBranch();

    BranchRenderTargets firstPersonBranch();

    default RenderTarget worldRawMaskTarget() {
        return worldBranch().rawMaskTarget();
    }

    default RenderTarget firstPersonRawMaskTarget() {
        return firstPersonBranch().rawMaskTarget();
    }

    default RenderTarget worldHollowMaskTarget() {
        return worldBranch().hollowMaskTarget();
    }

    default RenderTarget firstPersonHollowMaskTarget() {
        return firstPersonBranch().hollowMaskTarget();
    }

    default RenderTarget worldSceneDepthTarget() {
        return worldBranch().sceneDepthTarget();
    }

    default RenderTarget firstPersonSceneDepthTarget() {
        return firstPersonBranch().sceneDepthTarget();
    }

    @Deprecated
    default RenderTarget rawMaskTarget() {
        return worldRawMaskTarget();
    }

    @Deprecated
    default RenderTarget hollowMaskTarget() {
        return worldHollowMaskTarget();
    }

    @Deprecated
    default RenderTarget sceneDepthTarget() {
        return worldSceneDepthTarget();
    }

    OutlineFrameData frameData();

    OutlineAdvancedInput advancedInput();
}

