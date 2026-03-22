package quiet.enchantmentoutline.process.step;

import quiet.enchantmentoutline.acquire.dispatch.RawInputSnapshot;
import quiet.enchantmentoutline.process.validate.TechniqueInputValidateStep;
import quiet.enchantmentoutline.technique.input.OutlineAdvancedInput;
import quiet.enchantmentoutline.technique.input.OutlineFrameData;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueSettings;

/**
 * Converts raw frame data into normalized, algorithm-ready values.
 */
public final class ProcessNormalizeStep {
    public ProcessNormalizationResult execute(RawInputSnapshot raw, TechniqueInputValidateStep validateStep) {
        OutlineTechniqueSettings normalizedSettings = validateStep.normalizedSettings(raw);
        OutlineAdvancedInput advancedInput = validateStep.normalizedAdvancedInput(raw, normalizedSettings);
        OutlineFrameData frameData = new OutlineFrameData(
                validateStep.normalizedFrameIndex(raw),
                validateStep.normalizedViewportWidth(raw),
                validateStep.normalizedViewportHeight(raw),
                raw.worldLoaded(),
                normalizedSettings
        );
        return new ProcessNormalizationResult(frameData, advancedInput);
    }
}

