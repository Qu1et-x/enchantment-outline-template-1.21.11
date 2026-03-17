package quiet.enchantmentoutline.technique.shell;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.technique.OutlineTechnique;
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;

/**
 * Placeholder for future geometry-shell based outline implementation.
 */
public class GeometryShellOutlineTechnique implements OutlineTechnique {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique-Shell");
    private static int fallbackLogCount;

    private final OutlineTechnique fallback;

    public GeometryShellOutlineTechnique(OutlineTechnique fallback) {
        this.fallback = fallback;
    }

    @Override
    public OutlineTechniqueMode mode() {
        return OutlineTechniqueMode.GEOMETRY_SHELL;
    }

    @Override
    public boolean isImplemented() {
        return false;
    }

    @Override
    public void process(RenderTarget maskTarget, RenderTarget sceneDepthTarget) {
        if (fallbackLogCount < 10) {
            fallbackLogCount++;
            LOGGER.warn("Geometry shell technique is not implemented yet, fallback to {} ({}/10)", fallback.mode(), fallbackLogCount);
        }
        fallback.process(maskTarget, sceneDepthTarget);
    }
}

