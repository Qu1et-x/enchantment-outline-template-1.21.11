package quiet.enchantmentoutline.technique.input;

/**
 * 算法共享参数容器；参数合法化由 process 模块统一负责。
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
            this.outlineRadiusPixels = outlineRadiusPixels;
            return this;
        }

        public Builder alphaThreshold(float alphaThreshold) {
            this.alphaThreshold = alphaThreshold;
            return this;
        }

        public Builder depthEpsilon(float depthEpsilon) {
            this.depthEpsilon = depthEpsilon;
            return this;
        }

        public OutlineTechniqueSettings build() {
            return new OutlineTechniqueSettings(this);
        }
    }
}

