package quiet.enchantmentoutline.technique.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.technique.OutlineTechnique;
import quiet.enchantmentoutline.technique.OutlineTechniqueContext;
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;

/**
 * 占位算法：用于先打通结构切换，未实现时回退到默认算法。
 */
public class DelegatingPlaceholderTechnique implements OutlineTechnique {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique");
    private static int placeholderLogCount;

    private final OutlineTechniqueMode mode;
    private final String debugName;
    private final OutlineTechnique fallback;

    public DelegatingPlaceholderTechnique(OutlineTechniqueMode mode, String debugName, OutlineTechnique fallback) {
        this.mode = mode;
        this.debugName = debugName;
        this.fallback = fallback;
    }

    @Override
    public OutlineTechniqueMode mode() {
        return mode;
    }

    @Override
    public String debugName() {
        return debugName;
    }

    @Override
    public void process(OutlineTechniqueContext context) {
        if (placeholderLogCount < 30) {
            placeholderLogCount++;
            LOGGER.warn("Technique {} is not implemented yet, fallback to {}. pass={}/30",
                    mode.id(),
                    fallback.debugName(),
                    placeholderLogCount);
        }
        fallback.process(context);
    }
}

