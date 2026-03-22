package quiet.enchantmentoutline.technique.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.technique.OutlineTechnique;
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueInput;

/**
 * 占位算法：用于先打通结构切换，未实现时回退到默认算法。
 */
public class DelegatingPlaceholderTechnique extends AbstractOutlineTechnique {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique");
    private static int placeholderLogCount;

    private final OutlineTechnique fallback;

    public DelegatingPlaceholderTechnique(OutlineTechniqueMode mode, String debugName, OutlineTechnique fallback) {
        super(mode, debugName);
        this.fallback = fallback;
    }


    @Override
    public void process(OutlineTechniqueInput input) {
        if (placeholderLogCount < 30) {
            placeholderLogCount++;
            LOGGER.warn("Technique {} is not implemented yet, fallback to {}. pass={}/30",
                    mode().id(),
                    fallback.debugName(),
                    placeholderLogCount);
        }
        fallback.process(input);
    }
}




