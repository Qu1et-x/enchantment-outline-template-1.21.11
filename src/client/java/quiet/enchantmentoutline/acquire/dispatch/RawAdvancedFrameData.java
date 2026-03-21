package quiet.enchantmentoutline.acquire.dispatch;

/**
 * 职责描述: 承载采集阶段可选的高级效果原始参数。
 * 交互映射: 由 RawAcquireDispatcher 采集，交给 process 模块做统一归一化。
 */
public final class RawAdvancedFrameData {
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

    public RawAdvancedFrameData(float gameDeltaTicks,
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

