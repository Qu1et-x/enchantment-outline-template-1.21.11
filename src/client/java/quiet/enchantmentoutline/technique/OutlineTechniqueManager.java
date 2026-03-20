package quiet.enchantmentoutline.technique;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.technique.impl.DelegatingPlaceholderTechnique;
import quiet.enchantmentoutline.technique.impl.legacy.LegacyRadiusSamplingTechnique;

import java.util.EnumMap;
import java.util.Map;

/**
 * 算法注册与切换入口。
 */
public final class OutlineTechniqueManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique");

    private static OutlineTechniqueManager instance;

    private final Map<OutlineTechniqueMode, OutlineTechnique> techniques = new EnumMap<>(OutlineTechniqueMode.class);
    private volatile OutlineTechniqueMode currentMode = OutlineTechniqueMode.LEGACY_RADIUS;

    private OutlineTechniqueManager() {
        registerBuiltins();
        String modeFromProperty = System.getProperty("enchantmentoutline.technique");
        currentMode = OutlineTechniqueMode.parseOrDefault(modeFromProperty, OutlineTechniqueMode.LEGACY_RADIUS);
        if (modeFromProperty != null) {
            LOGGER.info("Technique mode configured by -Denchantmentoutline.technique={}: resolved={}",
                    modeFromProperty,
                    currentMode.id());
        }
        LOGGER.info("Technique manager initialized. activeMode={}, availableModes={}",
                currentMode.id(),
                techniques.keySet());
    }

    public static OutlineTechniqueManager getInstance() {
        if (instance == null) {
            instance = new OutlineTechniqueManager();
        }
        return instance;
    }

    public OutlineTechniqueMode getCurrentMode() {
        return currentMode;
    }

    public void setMode(OutlineTechniqueMode mode) {
        if (mode == null) {
            return;
        }
        if (mode == currentMode) {
            return;
        }
        currentMode = mode;
        OutlineTechnique technique = techniqueOrFallback(mode);
        LOGGER.info("Technique mode switched: mode={}, implementation={}", mode.id(), technique.debugName());
    }

    public void setMode(String modeText) {
        setMode(OutlineTechniqueMode.parseOrDefault(modeText, OutlineTechniqueMode.LEGACY_RADIUS));
    }

    public void process(OutlineTechniqueContext context) {
        OutlineTechnique technique = techniqueOrFallback(currentMode);
        technique.process(context);
    }

    private OutlineTechnique techniqueOrFallback(OutlineTechniqueMode mode) {
        OutlineTechnique technique = techniques.get(mode);
        if (technique != null) {
            return technique;
        }

        OutlineTechnique fallback = techniques.get(OutlineTechniqueMode.LEGACY_RADIUS);
        if (fallback == null) {
            throw new IllegalStateException("Legacy technique must be registered.");
        }

        LOGGER.warn("Technique mode {} is missing from registry, fallback to {}",
                mode.id(),
                OutlineTechniqueMode.LEGACY_RADIUS.id());
        return fallback;
    }

    private void registerBuiltins() {
        OutlineTechnique legacy = new LegacyRadiusSamplingTechnique();
        techniques.put(OutlineTechniqueMode.LEGACY_RADIUS, legacy);
        techniques.put(OutlineTechniqueMode.JFA,
                new DelegatingPlaceholderTechnique(OutlineTechniqueMode.JFA, "JfaOutlineTechnique", legacy));
        techniques.put(OutlineTechniqueMode.BILATERAL_GAUSSIAN,
                new DelegatingPlaceholderTechnique(OutlineTechniqueMode.BILATERAL_GAUSSIAN, "BilateralGaussianOutlineTechnique", legacy));
        techniques.put(OutlineTechniqueMode.STENCIL_EXPAND,
                new DelegatingPlaceholderTechnique(OutlineTechniqueMode.STENCIL_EXPAND, "StencilExpandOutlineTechnique", legacy));
    }
}

