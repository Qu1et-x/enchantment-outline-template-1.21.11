package quiet.enchantmentoutline.debug;

/**
 * 开发期日志开关，默认关闭高频日志，按模块通过 JVM 参数启用。
 */
public final class OutlineDebugFlags {
    public static final boolean FRAME = propertyEnabled("enchantmentoutline.debug.frame");
    public static final boolean BUFFER = propertyEnabled("enchantmentoutline.debug.buffer");
    public static final boolean PREPROCESS = propertyEnabled("enchantmentoutline.debug.preprocess");
    public static final boolean TECHNIQUE = propertyEnabled("enchantmentoutline.debug.technique");
    public static final boolean SUBMIT = propertyEnabled("enchantmentoutline.debug.submit");

    private OutlineDebugFlags() {
    }

    private static boolean propertyEnabled(String key) {
        return Boolean.parseBoolean(System.getProperty(key, "false"));
    }
}

