package quiet.enchantmentoutline.technique.input;

import com.mojang.blaze3d.pipeline.RenderTarget;

/**
 * 算法统一输入契约。
 */
public interface OutlineTechniqueInput {
    RenderTarget mainTarget();

    RenderTarget worldRawMaskTarget();

    RenderTarget firstPersonRawMaskTarget();

    RenderTarget worldHollowMaskTarget();

    RenderTarget firstPersonHollowMaskTarget();

    RenderTarget worldSceneDepthTarget();

    RenderTarget firstPersonSceneDepthTarget();

    default RenderTarget rawMaskTarget() {
        return worldRawMaskTarget();
    }

    default RenderTarget hollowMaskTarget() {
        return worldHollowMaskTarget();
    }

    default RenderTarget sceneDepthTarget() {
        return worldSceneDepthTarget();
    }

    OutlineFrameData frameData();

    OutlineAdvancedInput advancedInput();
}

