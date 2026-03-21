package quiet.enchantmentoutline.technique;

import quiet.enchantmentoutline.technique.input.OutlineTechniqueInput;

/**
 * 描边算法统一契约。
 */
public interface OutlineTechnique {
    OutlineTechniqueMode mode();

    String debugName();

    void process(OutlineTechniqueInput input);
}

