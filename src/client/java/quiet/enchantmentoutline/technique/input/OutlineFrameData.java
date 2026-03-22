package quiet.enchantmentoutline.technique.input;

import java.util.Objects;

/**
 * 每帧稳定输入容器；合法性由 process 模块在创建前统一保证。
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
        this.frameIndex = frameIndex;
        this.viewportWidth = viewportWidth;
        this.viewportHeight = viewportHeight;
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

