package quiet.enchantmentoutline.technique;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.technique.distancefield.DistanceFieldOutlineTechnique;
import quiet.enchantmentoutline.technique.radius.RadiusSamplingOutlineTechnique;
import quiet.enchantmentoutline.technique.separable.SeparableDilationOutlineTechnique;
import quiet.enchantmentoutline.technique.shell.GeometryShellOutlineTechnique;

import java.util.EnumMap;
import java.util.Map;

/**
 * Central technique switchboard. New implementations plug in here only once.
 */
public final class OutlineTechniqueManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique");
    private static final OutlineTechniqueManager INSTANCE = new OutlineTechniqueManager();

    private final Map<OutlineTechniqueMode, OutlineTechnique> techniques = new EnumMap<>(OutlineTechniqueMode.class);
    private OutlineTechniqueMode activeMode = OutlineTechniqueMode.RADIUS_SAMPLING;

    private OutlineTechniqueManager() {
        RadiusSamplingOutlineTechnique radiusSampling = new RadiusSamplingOutlineTechnique();
        techniques.put(OutlineTechniqueMode.RADIUS_SAMPLING, radiusSampling);
        techniques.put(OutlineTechniqueMode.SEPARABLE_DILATION, new SeparableDilationOutlineTechnique(radiusSampling));
        techniques.put(OutlineTechniqueMode.DISTANCE_FIELD, new DistanceFieldOutlineTechnique(radiusSampling));
        techniques.put(OutlineTechniqueMode.GEOMETRY_SHELL, new GeometryShellOutlineTechnique(radiusSampling));
    }

    public static OutlineTechniqueManager getInstance() {
        return INSTANCE;
    }

    public synchronized OutlineTechniqueMode getActiveMode() {
        return activeMode;
    }

    public synchronized void switchMode(OutlineTechniqueMode newMode) {
        if (newMode == null || newMode == activeMode) {
            return;
        }
        OutlineTechnique target = techniques.get(newMode);
        if (target == null) {
            LOGGER.warn("Requested unknown outline mode: {}", newMode);
            return;
        }
        activeMode = newMode;
        LOGGER.info("Switched outline technique to {} (implemented={})", newMode, target.isImplemented());
    }

    public synchronized void process(RenderTarget maskTarget, RenderTarget sceneDepthTarget) {
        OutlineTechnique technique = techniques.get(activeMode);
        if (technique == null) {
            LOGGER.warn("No active technique found for mode {}, fallback to {}", activeMode, OutlineTechniqueMode.RADIUS_SAMPLING);
            activeMode = OutlineTechniqueMode.RADIUS_SAMPLING;
            technique = techniques.get(activeMode);
        }
        technique.process(maskTarget, sceneDepthTarget);
    }

    public synchronized void onResize(int width, int height) {
        for (OutlineTechnique technique : techniques.values()) {
            technique.onResize(width, height);
        }
    }
}

