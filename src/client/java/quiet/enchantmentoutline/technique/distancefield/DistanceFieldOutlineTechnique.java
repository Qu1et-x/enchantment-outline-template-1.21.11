package quiet.enchantmentoutline.technique.distancefield;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.technique.OutlineTechnique;
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;

/**
 * Placeholder for future JFA/SDF distance-field outline implementation.
 */
public class DistanceFieldOutlineTechnique implements OutlineTechnique {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique-DistanceField");
    private static int fallbackLogCount;

    private final OutlineTechnique fallback;

    public DistanceFieldOutlineTechnique(OutlineTechnique fallback) {
        this.fallback = fallback;
    }

    @Override
    public OutlineTechniqueMode mode() {
        return OutlineTechniqueMode.DISTANCE_FIELD;
    }

    @Override
    public boolean isImplemented() {
        return false;
    }

    @Override
    public void process(RenderTarget maskTarget, RenderTarget sceneDepthTarget) {
        if (fallbackLogCount < 10) {
            fallbackLogCount++;
            LOGGER.warn("Distance field technique is not implemented yet, fallback to {} ({}/10)", fallback.mode(), fallbackLogCount);
        }
        fallback.process(maskTarget, sceneDepthTarget);
    }
}

