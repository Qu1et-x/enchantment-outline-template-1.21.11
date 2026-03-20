package quiet.enchantmentoutline.technique;

import com.mojang.blaze3d.pipeline.RenderTarget;

import java.util.Objects;

/**
 * 算法执行上下文，避免算法实现直接依赖外部单例。
 */
public final class OutlineTechniqueContext {
    private final RenderTarget mainTarget;
    private final RenderTarget maskTarget;
    private final RenderTarget sceneDepthTarget;

    public OutlineTechniqueContext(RenderTarget mainTarget, RenderTarget maskTarget, RenderTarget sceneDepthTarget) {
        this.mainTarget = Objects.requireNonNull(mainTarget, "mainTarget");
        this.maskTarget = Objects.requireNonNull(maskTarget, "maskTarget");
        this.sceneDepthTarget = Objects.requireNonNull(sceneDepthTarget, "sceneDepthTarget");
    }

    public RenderTarget mainTarget() {
        return mainTarget;
    }

    public RenderTarget maskTarget() {
        return maskTarget;
    }

    public RenderTarget sceneDepthTarget() {
        return sceneDepthTarget;
    }
}

