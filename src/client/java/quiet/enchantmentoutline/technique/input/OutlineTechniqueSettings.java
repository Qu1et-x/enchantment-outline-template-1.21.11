package quiet.enchantmentoutline.technique.input;

/**
 * 算法共享参数容器；参数合法化由 process 模块统一负责。
 */
public final class OutlineTechniqueSettings {
    public static final OutlineTechniqueSettings DEFAULT = new Builder().build();

    private final int outlineRadiusPixels;
    private final float alphaThreshold;
    private final float depthEpsilon;
    private final float[] outlineColorRgb;
    private final float outlineColorMix;
    private final float outlineGlow;
    private final boolean advancedEffectEnabled;

    private OutlineTechniqueSettings(Builder builder) {
        this.outlineRadiusPixels = builder.outlineRadiusPixels;
        this.alphaThreshold = builder.alphaThreshold;
        this.depthEpsilon = builder.depthEpsilon;
        this.outlineColorRgb = new float[]{
                builder.outlineColorRgb[0],
                builder.outlineColorRgb[1],
                builder.outlineColorRgb[2]
        };
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

    public float[] outlineColorRgb() {
        return new float[]{outlineColorRgb[0], outlineColorRgb[1], outlineColorRgb[2]};
    }

    public float outlineColorRed() {
        return outlineColorRgb[0];
    }

    public float outlineColorGreen() {
        return outlineColorRgb[1];
    }

    public float outlineColorBlue() {
        return outlineColorRgb[2];
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
        private final float[] outlineColorRgb = new float[]{1.0F, 1.0F, 1.0F};
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

        public Builder outlineColorRgb(float red, float green, float blue) {
            this.outlineColorRgb[0] = red;
            this.outlineColorRgb[1] = green;
            this.outlineColorRgb[2] = blue;
            return this;
        }

        public Builder outlineColorRgb(float[] rgb) {
            if (rgb == null || rgb.length < 3) {
                return this;
            }
            this.outlineColorRgb[0] = rgb[0];
            this.outlineColorRgb[1] = rgb[1];
            this.outlineColorRgb[2] = rgb[2];
            return this;
        }

        public Builder outlineColorRed(float outlineColorRed) {
            this.outlineColorRgb[0] = outlineColorRed;
            return this;
        }

        public Builder outlineColorGreen(float outlineColorGreen) {
            this.outlineColorRgb[1] = outlineColorGreen;
            return this;
        }

        public Builder outlineColorBlue(float outlineColorBlue) {
            this.outlineColorRgb[2] = outlineColorBlue;
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

