package quiet.enchantmentoutline.process.step;

import quiet.enchantmentoutline.technique.input.OutlineAdvancedInput;
import quiet.enchantmentoutline.technique.input.OutlineFrameData;

import java.util.Objects;

/**
 * Normalization output shared by subsequent process steps.
 */
public final class ProcessNormalizationResult {
    private final OutlineFrameData frameData;
    private final OutlineAdvancedInput advancedInput;

    public ProcessNormalizationResult(OutlineFrameData frameData, OutlineAdvancedInput advancedInput) {
        this.frameData = Objects.requireNonNull(frameData, "frameData");
        this.advancedInput = Objects.requireNonNull(advancedInput, "advancedInput");
    }

    public OutlineFrameData frameData() {
        return frameData;
    }

    public OutlineAdvancedInput advancedInput() {
        return advancedInput;
    }
}

