package quiet.enchantmentoutline.process.validate;

import quiet.enchantmentoutline.acquire.dispatch.RawInputSnapshot;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueSettings;

/**
 * 职责描述: 校验处理后输入在进入算法前的基础可用性。
 * 交互映射: 由 ProcessPipelineCoordinator 在输出 ProcessedInputSnapshot 前统一执行。
 */
public final class TechniqueInputValidateStep {
    public boolean isReady(RawInputSnapshot raw) {
        if (!isFinite(raw.settings())) {
            return false;
        }
        if (raw.rawMaskTarget().width <= 0 || raw.rawMaskTarget().height <= 0) {
            return false;
        }
        if (raw.rawMaskTarget().width != raw.hollowMaskTarget().width
                || raw.rawMaskTarget().height != raw.hollowMaskTarget().height) {
            return false;
        }

        return raw.mainTarget().getColorTextureView() != null
                && raw.rawMaskTarget().getColorTextureView() != null
                && raw.rawMaskTarget().getDepthTextureView() != null
                && raw.hollowMaskTarget().getColorTextureView() != null
                && raw.sceneDepthTarget().getDepthTextureView() != null;
    }

    public int normalizedFrameIndex(RawInputSnapshot raw) {
        return Math.max(0, raw.frameIndex());
    }

    public int normalizedViewportWidth(RawInputSnapshot raw) {
        return Math.max(1, raw.viewportWidth());
    }

    public int normalizedViewportHeight(RawInputSnapshot raw) {
        return Math.max(1, raw.viewportHeight());
    }

    public OutlineTechniqueSettings normalizedSettings(RawInputSnapshot raw) {
        OutlineTechniqueSettings source = raw.settings() == null ? OutlineTechniqueSettings.DEFAULT : raw.settings();
        return OutlineTechniqueSettings.builder()
                .outlineRadiusPixels(Math.max(1, source.outlineRadiusPixels()))
                .alphaThreshold(Math.max(0.0F, source.alphaThreshold()))
                .depthEpsilon(Math.max(0.0F, source.depthEpsilon()))
                .build();
    }

    private static boolean isFinite(OutlineTechniqueSettings settings) {
        if (settings == null) {
            return true;
        }
        return Float.isFinite(settings.alphaThreshold()) && Float.isFinite(settings.depthEpsilon());
    }
}

