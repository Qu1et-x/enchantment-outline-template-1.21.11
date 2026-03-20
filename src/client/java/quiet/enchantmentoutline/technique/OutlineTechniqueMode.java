package quiet.enchantmentoutline.technique;

import java.util.Locale;

/**
 * 可切换的描边算法模式。
 */
public enum OutlineTechniqueMode {
    LEGACY_RADIUS("legacy_radius"),
    JFA("jfa"),
    BILATERAL_GAUSSIAN("bilateral_gaussian"),
    STENCIL_EXPAND("stencil_expand");

    private final String id;

    OutlineTechniqueMode(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public static OutlineTechniqueMode parseOrDefault(String raw, OutlineTechniqueMode fallback) {
        if (raw == null) {
            return fallback;
        }

        String normalized = raw.trim().toLowerCase(Locale.ROOT);
        for (OutlineTechniqueMode value : values()) {
            if (value.id.equals(normalized)) {
                return value;
            }
        }
        return fallback;
    }
}

