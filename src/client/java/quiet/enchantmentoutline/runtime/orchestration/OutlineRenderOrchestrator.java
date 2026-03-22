package quiet.enchantmentoutline.runtime.orchestration;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.acquire.dispatch.RawAcquireDispatcher;
import quiet.enchantmentoutline.acquire.dispatch.RawInputSnapshot;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;
import quiet.enchantmentoutline.process.dispatch.ProcessPipelineCoordinator;
import quiet.enchantmentoutline.process.dispatch.ProcessedInputSnapshot;
import quiet.enchantmentoutline.process.dispatch.TechniqueInputAssembler;
import quiet.enchantmentoutline.technique.OutlineTechniqueManager;
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueInput;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueSettings;

/**
 * 职责描述: 编排 acquire/process/technique 三大模块并触发最终算法执行。
 * 交互映射: 在 GameRenderer 渲染帧钩子中被调用。
 */
public final class OutlineRenderOrchestrator {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Orchestrator");
    private static OutlineRenderOrchestrator instance;
    private static int processLogCount;
    private static int skipLogCount;
    private static int frameCounter;

    private static final OutlineTechniqueSettings DEFAULT_SETTINGS = OutlineTechniqueSettings.builder()
            .outlineRadiusPixels(Integer.getInteger("enchantmentoutline.radius", 10))
            .alphaThreshold(parsePropertyFloat("enchantmentoutline.alphaThreshold", 0.001F))
            .depthEpsilon(parsePropertyFloat("enchantmentoutline.depthEpsilon", 0.00001F))
            .outlineColorRed(parsePropertyFloat("enchantmentoutline.colorR", 1.0F))
            .outlineColorGreen(parsePropertyFloat("enchantmentoutline.colorG", 1.0F))
            .outlineColorBlue(parsePropertyFloat("enchantmentoutline.colorB", 1.0F))
            .outlineColorMix(parsePropertyFloat("enchantmentoutline.colorMix", 0.0F))
            .outlineGlow(parsePropertyFloat("enchantmentoutline.glow", 1.0F))
            .advancedEffectEnabled(Boolean.parseBoolean(System.getProperty("enchantmentoutline.advancedInput", "false")))
            .build();

    private volatile OutlineTechniqueSettings currentSettings = DEFAULT_SETTINGS;

    private OutlineRenderOrchestrator() {
    }

    public static OutlineRenderOrchestrator getInstance() {
        if (instance == null) {
            instance = new OutlineRenderOrchestrator();
        }
        return instance;
    }

    public void setTechniqueMode(OutlineTechniqueMode mode) {
        OutlineTechniqueManager.getInstance().setMode(mode);
    }

    public void setTechniqueMode(String modeText) {
        OutlineTechniqueManager.getInstance().setMode(modeText);
    }

    public void setOutlineRadiusPixels(int radius) {
        currentSettings = copySettings(currentSettings)
                .outlineRadiusPixels(Math.max(1, radius))
                .build();
    }

    public void setOutlineColor(float red, float green, float blue, float mix) {
        currentSettings = copySettings(currentSettings)
                .outlineColorRed(clamp01(red))
                .outlineColorGreen(clamp01(green))
                .outlineColorBlue(clamp01(blue))
                .outlineColorMix(clamp01(mix))
                .build();
    }

    public void setOutlineGlow(float glow) {
        currentSettings = copySettings(currentSettings)
                .outlineGlow(clampGlow(glow))
                .build();
    }

    public OutlineTechniqueSettings currentSettings() {
        return currentSettings;
    }

    public void process(DeltaTracker deltaTracker) {
        RenderSystem.assertOnRenderThread();

        OutlineTechniqueManager techniqueManager = OutlineTechniqueManager.getInstance();
        frameCounter++;
        OutlineTechniqueSettings settings = currentSettings;
        RawInputSnapshot rawSnapshot = RawAcquireDispatcher.getInstance().acquire(frameCounter, settings, deltaTracker);

        if (OutlineDebugFlags.TECHNIQUE && processLogCount < 12) {
            processLogCount++;
            LOGGER.info("Outline process #{}, mode={}, rawMask={}x{}",
                    processLogCount,
                    techniqueManager.getCurrentMode().id(),
                    rawSnapshot.worldBranch().rawMaskTarget().width,
                    rawSnapshot.worldBranch().rawMaskTarget().height);
        }

        ProcessedInputSnapshot processedSnapshot = ProcessPipelineCoordinator.getInstance().process(rawSnapshot);
        if (processedSnapshot == null) {
            skipLogCount++;
            if (OutlineDebugFlags.TECHNIQUE && skipLogCount < 12) {
                LOGGER.info("Outline process skipped: process pipeline output is null ({}/12)", skipLogCount);
            }
            return;
        }

        OutlineTechniqueInput input = new TechniqueInputAssembler()
                .processedSnapshot(processedSnapshot)
                .assemble();
        if (input != null) {
            techniqueManager.process(input);
        } else if (OutlineDebugFlags.TECHNIQUE && skipLogCount < 12) {
            skipLogCount++;
            LOGGER.info("Outline process skipped: technique input assembly failed ({}/12)", skipLogCount);
        }
    }

    private static float parsePropertyFloat(String key, float fallback) {
        String raw = System.getProperty(key);
        if (raw == null) {
            return fallback;
        }

        try {
            return Float.parseFloat(raw);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Invalid float property {}='{}', fallback to {}", key, raw, fallback);
            return fallback;
        }
    }

    private static OutlineTechniqueSettings.Builder copySettings(OutlineTechniqueSettings source) {
        return OutlineTechniqueSettings.builder()
                .outlineRadiusPixels(source.outlineRadiusPixels())
                .alphaThreshold(source.alphaThreshold())
                .depthEpsilon(source.depthEpsilon())
                .outlineColorRed(source.outlineColorRed())
                .outlineColorGreen(source.outlineColorGreen())
                .outlineColorBlue(source.outlineColorBlue())
                .outlineColorMix(source.outlineColorMix())
                .outlineGlow(source.outlineGlow())
                .advancedEffectEnabled(source.advancedEffectEnabled());
    }

    private static float clamp01(float value) {
        if (value < 0.0F) {
            return 0.0F;
        }
        if (value > 1.0F) {
            return 1.0F;
        }
        return value;
    }

    private static float clampGlow(float value) {
        if (value < 0.0F) {
            return 0.0F;
        }
        if (value > 4.0F) {
            return 4.0F;
        }
        return value;
    }
}

