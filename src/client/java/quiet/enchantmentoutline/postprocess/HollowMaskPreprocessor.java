package quiet.enchantmentoutline.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;

/**
 * Compatibility wrapper moved to technique.preprocess implementation.
 */
public final class HollowMaskPreprocessor {
    private static final HollowMaskPreprocessor INSTANCE = new HollowMaskPreprocessor();

    private HollowMaskPreprocessor() {
    }

    public static HollowMaskPreprocessor getInstance() {
        return INSTANCE;
    }

    public void process(RenderTarget rawMaskTarget, RenderTarget hollowMaskTarget) {
        quiet.enchantmentoutline.technique.preprocess.HollowMaskPreprocessor.getInstance().process(rawMaskTarget, hollowMaskTarget);
    }
}

