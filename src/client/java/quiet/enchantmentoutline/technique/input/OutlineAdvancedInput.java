package quiet.enchantmentoutline.technique.input;

/**
 * 职责描述: 算法可选高级输入参数；基础算法可忽略该对象。
 * 交互映射: 由 process 模块归一化后注入到 OutlineTechniqueInput。
 */
public final class OutlineAdvancedInput {
    private static final OutlineAdvancedInput DISABLED = new OutlineAdvancedInput(
            false,
            0.0F,
            0.0F,
            0.0F,
            0L,
            0.0F,
            0.0,
            0.0,
            0.0,
            0.0F,
            0.0F,
            false
    );

    private final boolean advancedEffectEnabled;
    private final float gameDeltaTicks;
    private final float gamePartialTick;
    private final float realtimeDeltaTicks;
    private final long gameTime;
    private final float dayTime;
    private final double cameraX;
    private final double cameraY;
    private final double cameraZ;
    private final float cameraYaw;
    private final float cameraPitch;
    private final boolean cameraInitialized;

    public OutlineAdvancedInput(boolean advancedEffectEnabled,
                                float gameDeltaTicks,
                                float gamePartialTick,
                                float realtimeDeltaTicks,
                                long gameTime,
                                float dayTime,
                                double cameraX,
                                double cameraY,
                                double cameraZ,
                                float cameraYaw,
                                float cameraPitch,
                                boolean cameraInitialized) {
        this.advancedEffectEnabled = advancedEffectEnabled;
        this.gameDeltaTicks = gameDeltaTicks;
        this.gamePartialTick = gamePartialTick;
        this.realtimeDeltaTicks = realtimeDeltaTicks;
        this.gameTime = gameTime;
        this.dayTime = dayTime;
        this.cameraX = cameraX;
        this.cameraY = cameraY;
        this.cameraZ = cameraZ;
        this.cameraYaw = cameraYaw;
        this.cameraPitch = cameraPitch;
        this.cameraInitialized = cameraInitialized;
    }

    public static OutlineAdvancedInput disabled() {
        return DISABLED;
    }

    public boolean advancedEffectEnabled() {
        return advancedEffectEnabled;
    }

    public float gameDeltaTicks() {
        return gameDeltaTicks;
    }

    public float gamePartialTick() {
        return gamePartialTick;
    }

    public float realtimeDeltaTicks() {
        return realtimeDeltaTicks;
    }

    public long gameTime() {
        return gameTime;
    }

    public float dayTime() {
        return dayTime;
    }

    public double cameraX() {
        return cameraX;
    }

    public double cameraY() {
        return cameraY;
    }

    public double cameraZ() {
        return cameraZ;
    }

    public float cameraYaw() {
        return cameraYaw;
    }

    public float cameraPitch() {
        return cameraPitch;
    }

    public boolean cameraInitialized() {
        return cameraInitialized;
    }
}

