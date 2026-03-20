package quiet.enchantmentoutline.technique.impl;

import quiet.enchantmentoutline.technique.OutlineTechnique;
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;

import java.util.Objects;

/**
 * 轻量父类：统一 mode/debugName 元数据，避免实现类重复样板代码。
 */
public abstract class AbstractOutlineTechnique implements OutlineTechnique {
    private final OutlineTechniqueMode mode;
    private final String debugName;

    protected AbstractOutlineTechnique(OutlineTechniqueMode mode, String debugName) {
        this.mode = Objects.requireNonNull(mode, "mode");
        this.debugName = Objects.requireNonNull(debugName, "debugName");
    }

    @Override
    public final OutlineTechniqueMode mode() {
        return mode;
    }

    @Override
    public final String debugName() {
        return debugName;
    }
}



