package quiet.enchantmentoutline.technique;

/**
 * 描边算法统一契约。
 */
public interface OutlineTechnique {
    OutlineTechniqueMode mode();

    String debugName();

    void process(OutlineTechniqueContext context);
}

