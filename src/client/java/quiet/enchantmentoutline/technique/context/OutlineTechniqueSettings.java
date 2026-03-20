package quiet.enchantmentoutline.technique.context;

/**
 * 算法共享调参入口：阶段一先统一参数承载，便于后续多算法对齐。
 */
public final class OutlineTechniqueSettings {
    public static final OutlineTechniqueSettings DEFAULT = new Builder().build();

    private final int outlineRadiusPixels;
    private final float alphaThreshold;
    private final float depthEpsilon;

    private OutlineTechniqueSettings(Builder builder) {
        this.outlineRadiusPixels = builder.outlineRadiusPixels;
        this.alphaThreshold = builder.alphaThreshold;
        this.depthEpsilon = builder.depthEpsilon;
    }

    public int outlineRadiusPixels() {
        return outlineRadiusPixels;
    }

    public float alphaThreshold() {
        return alphaThreshold;
    }

    public float depthEpsilon() {
        return depthEpsilon;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int outlineRadiusPixels = 10;
        private float alphaThreshold = 0.001F;
        private float depthEpsilon = 0.00001F;

        public Builder outlineRadiusPixels(int outlineRadiusPixels) {
            this.outlineRadiusPixels = Math.max(1, outlineRadiusPixels);
            return this;
        }

        public Builder alphaThreshold(float alphaThreshold) {
            this.alphaThreshold = Math.max(0.0F, alphaThreshold);
            return this;
        }

        public Builder depthEpsilon(float depthEpsilon) {
            this.depthEpsilon = Math.max(0.0F, depthEpsilon);
            return this;
        }

        public OutlineTechniqueSettings build() {
            if (!Float.isFinite(alphaThreshold) || !Float.isFinite(depthEpsilon)) {
                throw new IllegalArgumentException("Threshold values must be finite.");
            }
            return new OutlineTechniqueSettings(this);
        }
    }
}



