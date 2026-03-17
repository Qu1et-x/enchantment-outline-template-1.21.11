package quiet.enchantmentoutline.technique.separable;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.technique.OutlineTechnique;
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;

/**
 * Placeholder for separable dilation (horizontal+vertical pass) implementation.
 */
public class SeparableDilationOutlineTechnique implements OutlineTechnique {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique-Separable");
    private static int fallbackLogCount;

    private final OutlineTechnique fallback;

    public SeparableDilationOutlineTechnique(OutlineTechnique fallback) {
        this.fallback = fallback;
    }

    @Override
    public OutlineTechniqueMode mode() {
        return OutlineTechniqueMode.SEPARABLE_DILATION;
    }

    @Override
    public boolean isImplemented() {
        return false;
    }

    @Override
    public void process(RenderTarget maskTarget, RenderTarget sceneDepthTarget) {
        if (fallbackLogCount < 10) {
            fallbackLogCount++;
            LOGGER.warn("Separable dilation technique is not implemented yet, fallback to {} ({}/10)", fallback.mode(), fallbackLogCount);
        }
        fallback.process(maskTarget, sceneDepthTarget);
    }
}

