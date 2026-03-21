package quiet.enchantmentoutline.technique.input;

import com.mojang.blaze3d.pipeline.RenderTarget;

/**
 * 算法统一输入契约。
 */
public interface OutlineTechniqueInput {
    RenderTarget mainTarget();

    RenderTarget rawMaskTarget();

    RenderTarget hollowMaskTarget();

    RenderTarget sceneDepthTarget();

    OutlineFrameData frameData();
}

