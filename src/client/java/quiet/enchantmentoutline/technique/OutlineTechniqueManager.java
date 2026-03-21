package quiet.enchantmentoutline.technique;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueInput;
import quiet.enchantmentoutline.technique.impl.DelegatingPlaceholderTechnique;
import quiet.enchantmentoutline.technique.impl.LegacyRadiusSamplingTechnique;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * 算法注册与切换入口。
 */
public final class OutlineTechniqueManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique");

    private static OutlineTechniqueManager instance;

    private final Map<OutlineTechniqueMode, OutlineTechnique> techniques = new EnumMap<>(OutlineTechniqueMode.class);
    private volatile OutlineTechniqueMode currentMode = OutlineTechniqueMode.LEGACY_RADIUS;
    private int processDispatchLogCount;

    private OutlineTechniqueManager() {
        registerBuiltins();
        String modeFromProperty = System.getProperty("enchantmentoutline.technique");
        currentMode = OutlineTechniqueMode.parseOrDefault(modeFromProperty, OutlineTechniqueMode.LEGACY_RADIUS);
        if (OutlineDebugFlags.TECHNIQUE && modeFromProperty != null) {
            LOGGER.info("Technique mode configured by -Denchantmentoutline.technique={}: resolved={}",
                    modeFromProperty,
                    currentMode.id());
        }
        if (OutlineDebugFlags.TECHNIQUE) {
            LOGGER.info("Technique manager initialized. activeMode={}, availableModes={}",
                    currentMode.id(),
                    techniques.keySet());
        }
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

    public void process(OutlineTechniqueInput input) {
        Objects.requireNonNull(input, "input");
        OutlineTechnique technique = techniqueOrFallback(currentMode);
        if (OutlineDebugFlags.TECHNIQUE && processDispatchLogCount < 16) {
            processDispatchLogCount++;
            LOGGER.info("Dispatch technique: mode={}, impl={}, frame={}, viewport={}x{} ({}/16)",
                    currentMode.id(),
                    technique.debugName(),
                    input.frameData().frameIndex(),
                    input.frameData().viewportWidth(),
                    input.frameData().viewportHeight(),
                    processDispatchLogCount);
        }
        technique.process(input);
    }

    public void register(OutlineTechnique technique) {
        OutlineTechnique safe = Objects.requireNonNull(technique, "technique");
        OutlineTechnique previous = techniques.put(safe.mode(), safe);
        if (previous != null && previous != safe) {
            LOGGER.warn("Technique replaced: mode={}, oldImpl={}, newImpl={}",
                    safe.mode().id(),
                    previous.debugName(),
                    safe.debugName());
        } else if (OutlineDebugFlags.TECHNIQUE) {
            LOGGER.info("Technique registered: mode={}, impl={}", safe.mode().id(), safe.debugName());
        }
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
        register(legacy);
        registerPlaceholder(OutlineTechniqueMode.JFA, "JfaOutlineTechnique", legacy);
        registerPlaceholder(OutlineTechniqueMode.BILATERAL_GAUSSIAN, "BilateralGaussianOutlineTechnique", legacy);
        registerPlaceholder(OutlineTechniqueMode.STENCIL_EXPAND, "StencilExpandOutlineTechnique", legacy);
    }

    private void registerPlaceholder(OutlineTechniqueMode mode, String debugName, OutlineTechnique fallback) {
        register(new DelegatingPlaceholderTechnique(mode, debugName, fallback));
    }
}

