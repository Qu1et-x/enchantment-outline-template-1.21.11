package quiet.enchantmentoutline.technique.impl.jfa;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Objects;

/**
 * JFA pass scratch targets (seed + ping-pong field) sized to current viewport.
 */
public final class JfaScratchBufferPool {
    private RenderTarget seedFieldTarget;
    private RenderTarget pingFieldTarget;
    private RenderTarget pongFieldTarget;

    public BranchScratch getOrCreate(String label, int width, int height) {
        RenderSystem.assertOnRenderThread();
        ensureTargetSize(width, height, label);
        return new BranchScratch(seedFieldTarget, pingFieldTarget, pongFieldTarget);
    }

    private void ensureTargetSize(int width, int height, String label) {
        if (seedFieldTarget == null || seedFieldTarget.width != width || seedFieldTarget.height != height) {
            destroyAll();
            seedFieldTarget = new TextureTarget("JFA Seed Field [" + label + "]", width, height, true);
            pingFieldTarget = new TextureTarget("JFA Ping Field [" + label + "]", width, height, true);
            pongFieldTarget = new TextureTarget("JFA Pong Field [" + label + "]", width, height, true);
        }
    }

    public void destroyAll() {
        destroy(seedFieldTarget);
        destroy(pingFieldTarget);
        destroy(pongFieldTarget);
        seedFieldTarget = null;
        pingFieldTarget = null;
        pongFieldTarget = null;
    }

    private static void destroy(RenderTarget target) {
        if (target != null) {
            target.destroyBuffers();
        }
    }

    public record BranchScratch(RenderTarget seedFieldTarget,
                                RenderTarget pingFieldTarget,
                                RenderTarget pongFieldTarget) {
        public BranchScratch {
            Objects.requireNonNull(seedFieldTarget, "seedFieldTarget");
            Objects.requireNonNull(pingFieldTarget, "pingFieldTarget");
            Objects.requireNonNull(pongFieldTarget, "pongFieldTarget");
        }
    }
}

