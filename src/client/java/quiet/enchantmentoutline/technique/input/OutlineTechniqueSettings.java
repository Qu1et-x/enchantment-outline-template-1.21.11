package quiet.enchantmentoutline.technique.input;

/**
 * 算法共享参数容器；参数合法化由 process 模块统一负责。
 */
public final class OutlineTechniqueSettings {
    public static final OutlineTechniqueSettings DEFAULT = new Builder().build();

    private final int outlineRadiusPixels;
    private final float alphaThreshold;
    private final float depthEpsilon;
    private final float outlineColorRed;
    private final float outlineColorGreen;
    private final float outlineColorBlue;
    private final float outlineColorMix;
    private final float outlineGlow;
    private final boolean advancedEffectEnabled;

    private OutlineTechniqueSettings(Builder builder) {
        this.outlineRadiusPixels = builder.outlineRadiusPixels;
        this.alphaThreshold = builder.alphaThreshold;
        this.depthEpsilon = builder.depthEpsilon;
        this.outlineColorRed = builder.outlineColorRed;
        this.outlineColorGreen = builder.outlineColorGreen;
        this.outlineColorBlue = builder.outlineColorBlue;
        this.outlineColorMix = builder.outlineColorMix;
        this.outlineGlow = builder.outlineGlow;
        this.advancedEffectEnabled = builder.advancedEffectEnabled;
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

    public float outlineColorRed() {
        return outlineColorRed;
    }

    public float outlineColorGreen() {
        return outlineColorGreen;
    }

    public float outlineColorBlue() {
        return outlineColorBlue;
    }

    public float outlineColorMix() {
        return outlineColorMix;
    }

    public float outlineGlow() {
        return outlineGlow;
    }

    public boolean advancedEffectEnabled() {
        return advancedEffectEnabled;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int outlineRadiusPixels = 10;
        private float alphaThreshold = 0.001F;
        private float depthEpsilon = 0.00001F;
        private float outlineColorRed = 1.0F;
        private float outlineColorGreen = 1.0F;
        private float outlineColorBlue = 1.0F;
        private float outlineColorMix = 0.0F;
        private float outlineGlow = 1.0F;
        private boolean advancedEffectEnabled;

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

        public Builder outlineColorRed(float outlineColorRed) {
            this.outlineColorRed = outlineColorRed;
            return this;
        }

        public Builder outlineColorGreen(float outlineColorGreen) {
            this.outlineColorGreen = outlineColorGreen;
            return this;
        }

        public Builder outlineColorBlue(float outlineColorBlue) {
            this.outlineColorBlue = outlineColorBlue;
            return this;
        }

        public Builder outlineColorMix(float outlineColorMix) {
            this.outlineColorMix = outlineColorMix;
            return this;
        }

        public Builder outlineGlow(float outlineGlow) {
            this.outlineGlow = outlineGlow;
            return this;
        }

        public Builder advancedEffectEnabled(boolean advancedEffectEnabled) {
            this.advancedEffectEnabled = advancedEffectEnabled;
            return this;
        }

        public OutlineTechniqueSettings build() {
            return new OutlineTechniqueSettings(this);
        }
    }
}

