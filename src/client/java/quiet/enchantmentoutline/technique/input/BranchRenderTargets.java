package quiet.enchantmentoutline.technique.input;

import com.mojang.blaze3d.pipeline.RenderTarget;

import java.util.Objects;

/**
 * Standardized render-target bundle used by one outline branch.
 */
public final class BranchRenderTargets {
    private final RenderTarget rawMaskTarget;
    private final RenderTarget hollowMaskTarget;
    private final RenderTarget sceneDepthTarget;

    public BranchRenderTargets(RenderTarget rawMaskTarget,
                               RenderTarget hollowMaskTarget,
                               RenderTarget sceneDepthTarget) {
        this.rawMaskTarget = Objects.requireNonNull(rawMaskTarget, "rawMaskTarget");
        this.hollowMaskTarget = Objects.requireNonNull(hollowMaskTarget, "hollowMaskTarget");
        this.sceneDepthTarget = Objects.requireNonNull(sceneDepthTarget, "sceneDepthTarget");
    }

    public RenderTarget rawMaskTarget() {
        return rawMaskTarget;
    }

    public RenderTarget hollowMaskTarget() {
        return hollowMaskTarget;
    }

    public RenderTarget sceneDepthTarget() {
        return sceneDepthTarget;
    }
}

