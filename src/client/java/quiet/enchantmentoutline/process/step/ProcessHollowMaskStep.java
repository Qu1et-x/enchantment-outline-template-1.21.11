package quiet.enchantmentoutline.process.step;

import quiet.enchantmentoutline.acquire.dispatch.RawInputSnapshot;
import quiet.enchantmentoutline.process.hollowmask.HollowMaskExtractor;

/**
 * Produces hollow masks for all branches from their raw masks.
 */
public final class ProcessHollowMaskStep {
    public void execute(RawInputSnapshot raw) {
        HollowMaskExtractor extractor = HollowMaskExtractor.getInstance();
        extractor.process(raw.worldBranch().rawMaskTarget(), raw.worldBranch().hollowMaskTarget());
        extractor.process(raw.firstPersonBranch().rawMaskTarget(), raw.firstPersonBranch().hollowMaskTarget());
    }
}

