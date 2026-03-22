package quiet.enchantmentoutline.process.dispatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueContext;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueInput;

/**
 * 职责描述: 将处理模块输出装配为算法上下文输入。
 * 交互映射: 作为 process 到 technique.input 的统一装配入口。
 */
public final class TechniqueInputAssembler {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-InputAssembler");
    private static int builtLogCount;
    private static int skipLogCount;

    private ProcessedInputSnapshot snapshot;

    public TechniqueInputAssembler processedSnapshot(ProcessedInputSnapshot snapshot) {
        this.snapshot = snapshot;
        return this;
    }

    public OutlineTechniqueInput assemble() {
        if (snapshot == null) {
            if (OutlineDebugFlags.TECHNIQUE && skipLogCount < 20) {
                skipLogCount++;
                LOGGER.info("Skip technique input assemble: processed snapshot is null ({}/20)", skipLogCount);
            }
            return null;
        }

        if (OutlineDebugFlags.TECHNIQUE && builtLogCount < 20) {
            builtLogCount++;
            LOGGER.info("Assembled technique input: frame={}, viewport={}x{} ({}/20)",
                    snapshot.frameData().frameIndex(),
                    snapshot.frameData().viewportWidth(),
                    snapshot.frameData().viewportHeight(),
                    builtLogCount);
        }

        return new OutlineTechniqueContext(
                snapshot.mainTarget(),
                snapshot.worldBranch(),
                snapshot.firstPersonBranch(),
                snapshot.frameData(),
                snapshot.advancedInput());
    }
}

