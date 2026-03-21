package quiet.enchantmentoutline.process.dispatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.acquire.dispatch.RawInputSnapshot;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;
import quiet.enchantmentoutline.process.hollowmask.HollowMaskExtractor;
import quiet.enchantmentoutline.process.validate.TechniqueInputValidateStep;
import quiet.enchantmentoutline.technique.input.OutlineFrameData;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueSettings;

import java.util.Objects;

/**
 * 职责描述: 编排处理模块各步骤，输出算法最终输入快照。
 * 交互映射: 接收 acquire 快照，调用 preprocess/normalize/validate 步骤后产出 ProcessedInputSnapshot。
 */
public final class ProcessPipelineCoordinator {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Process");

    private static ProcessPipelineCoordinator instance;

    private final TechniqueInputValidateStep validateStep = new TechniqueInputValidateStep();

    private int processedLogCount;
    private int skipLogCount;

    private ProcessPipelineCoordinator() {
    }

    public static ProcessPipelineCoordinator getInstance() {
        if (instance == null) {
            instance = new ProcessPipelineCoordinator();
        }
        return instance;
    }

    public ProcessedInputSnapshot process(RawInputSnapshot raw) {
        Objects.requireNonNull(raw, "raw");

        if (!validateStep.isReady(raw)) {
            if (OutlineDebugFlags.TECHNIQUE && skipLogCount < 20) {
                skipLogCount++;
                LOGGER.info("Skip process pipeline output: frame={}, reason=input not ready ({}/20)",
                        raw.frameIndex(),
                        skipLogCount);
            }
            return null;
        }

        OutlineTechniqueSettings normalizedSettings = validateStep.normalizedSettings(raw);
        OutlineFrameData frameData = new OutlineFrameData(
                validateStep.normalizedFrameIndex(raw),
                validateStep.normalizedViewportWidth(raw),
                validateStep.normalizedViewportHeight(raw),
                raw.worldLoaded(),
                normalizedSettings
        );

        // 统一处理 hollow mask，保证算法层拿到的是稳定输入。
        HollowMaskExtractor.getInstance().process(raw.rawMaskTarget(), raw.hollowMaskTarget());

        ProcessedInputSnapshot snapshot = new ProcessedInputSnapshot.Builder()
                .mainTarget(raw.mainTarget())
                .rawMaskTarget(raw.rawMaskTarget())
                .hollowMaskTarget(raw.hollowMaskTarget())
                .sceneDepthTarget(raw.sceneDepthTarget())
                .frameData(frameData)
                .build();

        if (OutlineDebugFlags.TECHNIQUE && processedLogCount < 20) {
            processedLogCount++;
            LOGGER.info("Processed snapshot ready: frame={}, viewport={}x{} ({}/20)",
                    snapshot.frameData().frameIndex(),
                    snapshot.frameData().viewportWidth(),
                    snapshot.frameData().viewportHeight(),
                    processedLogCount);
        }

        return snapshot;
    }
}

