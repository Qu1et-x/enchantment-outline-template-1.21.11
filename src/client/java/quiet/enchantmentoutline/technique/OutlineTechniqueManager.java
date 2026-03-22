package quiet.enchantmentoutline.technique;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueInput;
import quiet.enchantmentoutline.technique.impl.LegacyRadiusSamplingTechnique;
import quiet.enchantmentoutline.technique.impl.jfa.JfaOutlineTechnique;

import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * 算法注册与切换入口。
 */
public final class OutlineTechniqueManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique");

    private static OutlineTechniqueManager instance;

    private final Map<OutlineTechniqueMode, OutlineTechnique> bindings = new EnumMap<>(OutlineTechniqueMode.class);
    private volatile OutlineTechniqueMode currentMode = OutlineTechniqueMode.JFA;
    private int processDispatchLogCount;

    private OutlineTechniqueManager() {
        registerBuiltins();
        String modeFromProperty = System.getProperty("enchantmentoutline.technique");
        currentMode = OutlineTechniqueMode.parseOrDefault(modeFromProperty, OutlineTechniqueMode.JFA);
        if (OutlineDebugFlags.TECHNIQUE && modeFromProperty != null) {
            LOGGER.info("Technique mode configured by -Denchantmentoutline.technique={}: resolved={}",
                    modeFromProperty,
                    currentMode.id());
        }
        if (OutlineDebugFlags.TECHNIQUE) {
            LOGGER.info("Technique manager initialized. activeMode={}, availableModes={}",
                    currentMode.id(),
                    bindings.keySet());
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
        if (!bindings.containsKey(mode)) {
            LOGGER.warn("Technique mode switch rejected: mode={} has no registered implementation. activeMode={}",
                    mode.id(),
                    currentMode.id());
            return;
        }
        if (mode == currentMode) {
            return;
        }
        currentMode = mode;
        OutlineTechnique implementation = implementationOrFallback(mode);
        LOGGER.info("Technique mode switched: mode={}, implementation={}",
                mode.id(),
                implementation.debugName());
    }

    public void setMode(String modeText) {
        setMode(OutlineTechniqueMode.parseOrDefault(modeText, OutlineTechniqueMode.JFA));
    }

    public void process(OutlineTechniqueInput input) {
        Objects.requireNonNull(input, "input");
        OutlineTechnique technique = implementationOrFallback(currentMode);
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
        register(safe.mode(), safe);
    }

    public void register(OutlineTechniqueMode mode, OutlineTechnique implementation) {
        OutlineTechniqueMode safeMode = Objects.requireNonNull(mode, "mode");
        OutlineTechnique safeImplementation = Objects.requireNonNull(implementation, "implementation");

        OutlineTechnique previous = bindings.put(safeMode, safeImplementation);
        if (previous != null && previous != safeImplementation) {
            LOGGER.warn("Technique binding replaced: mode={}, oldImpl={}, newImpl={}",
                    safeMode.id(),
                    previous.debugName(),
                    safeImplementation.debugName());
        } else if (OutlineDebugFlags.TECHNIQUE) {
            LOGGER.info("Technique binding registered: mode={}, impl={}",
                    safeMode.id(),
                    safeImplementation.debugName());
        }
    }

    private OutlineTechnique implementationOrFallback(OutlineTechniqueMode mode) {
        OutlineTechnique implementation = bindings.get(mode);
        if (implementation != null) {
            return implementation;
        }

        // Intentionally disable silent fallback during JFA validation.
        // If a mode has no bound implementation, fail fast so we never accidentally run legacy.
        throw new IllegalStateException("No technique implementation bound for mode=" + mode.id());
    }

    private void registerBuiltins() {
        OutlineTechnique legacy = new LegacyRadiusSamplingTechnique();
        OutlineTechnique jfa = new JfaOutlineTechnique();
        register(OutlineTechniqueMode.LEGACY_RADIUS, legacy);
        register(OutlineTechniqueMode.JFA, jfa);

        // Temporarily disabled during JFA validation to avoid any implicit legacy delegation:
        // register(OutlineTechniqueMode.BILATERAL_GAUSSIAN, legacy);
        // register(OutlineTechniqueMode.STENCIL_EXPAND, legacy);
    }
}

