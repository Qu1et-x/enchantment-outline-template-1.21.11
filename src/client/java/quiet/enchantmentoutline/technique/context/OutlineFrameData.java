package quiet.enchantmentoutline.technique.context;

import java.util.Objects;

/**
 * 每帧稳定输入：算法共享的基础信息，避免各实现直接读全局状态。
 */
public final class OutlineFrameData {
    private final int frameIndex;
    private final int viewportWidth;
    private final int viewportHeight;
    private final float inverseViewportWidth;
    private final float inverseViewportHeight;
    private final boolean worldLoaded;
    private final OutlineTechniqueSettings settings;

    public OutlineFrameData(int frameIndex,
                            int viewportWidth,
                            int viewportHeight,
                            boolean worldLoaded,
                            OutlineTechniqueSettings settings) {
        this.frameIndex = Math.max(0, frameIndex);
        this.viewportWidth = Math.max(1, viewportWidth);
        this.viewportHeight = Math.max(1, viewportHeight);
        this.inverseViewportWidth = 1.0F / this.viewportWidth;
        this.inverseViewportHeight = 1.0F / this.viewportHeight;
        this.worldLoaded = worldLoaded;
        this.settings = Objects.requireNonNull(settings, "settings");
    }

    public int frameIndex() {
        return frameIndex;
    }

    public int viewportWidth() {
        return viewportWidth;
    }

    public int viewportHeight() {
        return viewportHeight;
    }

    public float inverseViewportWidth() {
        return inverseViewportWidth;
    }

    public float inverseViewportHeight() {
        return inverseViewportHeight;
    }

    public boolean worldLoaded() {
        return worldLoaded;
    }

    public OutlineTechniqueSettings settings() {
        return settings;
    }
}


