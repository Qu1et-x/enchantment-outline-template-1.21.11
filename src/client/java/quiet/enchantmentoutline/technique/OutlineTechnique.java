package quiet.enchantmentoutline.technique;

import com.mojang.blaze3d.pipeline.RenderTarget;

/**
 * Common contract for all outline rendering techniques.
 */
public interface OutlineTechnique {
    OutlineTechniqueMode mode();

    default boolean isImplemented() {
        return true;
    }

    void process(RenderTarget maskTarget, RenderTarget sceneDepthTarget);

    default void onResize(int width, int height) {
        // Most techniques do not need extra resize handling yet.
    }
}

