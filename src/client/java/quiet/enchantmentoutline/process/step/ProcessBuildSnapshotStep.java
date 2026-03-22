package quiet.enchantmentoutline.process.step;

import quiet.enchantmentoutline.acquire.dispatch.RawInputSnapshot;
import quiet.enchantmentoutline.process.dispatch.ProcessedInputSnapshot;

/**
 * Final assembly step that builds the processed snapshot for technique input.
 */
public final class ProcessBuildSnapshotStep {
    public ProcessedInputSnapshot execute(RawInputSnapshot raw, ProcessNormalizationResult normalized) {
        return new ProcessedInputSnapshot.Builder()
                .mainTarget(raw.mainTarget())
                .worldBranch(raw.worldBranch())
                .firstPersonBranch(raw.firstPersonBranch())
                .frameData(normalized.frameData())
                .advancedInput(normalized.advancedInput())
                .build();
    }
}

