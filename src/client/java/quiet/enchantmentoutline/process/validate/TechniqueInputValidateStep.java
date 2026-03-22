package quiet.enchantmentoutline.process.validate;

import quiet.enchantmentoutline.acquire.dispatch.RawInputSnapshot;
import quiet.enchantmentoutline.acquire.dispatch.RawAdvancedFrameData;
import quiet.enchantmentoutline.technique.input.OutlineAdvancedInput;
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
        if (!isFinite(raw.advancedRawData())) {
            return false;
        }
        if (raw.worldRawMaskTarget().width <= 0 || raw.worldRawMaskTarget().height <= 0) {
            return false;
        }
        if (raw.firstPersonRawMaskTarget().width <= 0 || raw.firstPersonRawMaskTarget().height <= 0) {
            return false;
        }
        if (raw.worldRawMaskTarget().width != raw.worldHollowMaskTarget().width
                || raw.worldRawMaskTarget().height != raw.worldHollowMaskTarget().height) {
            return false;
        }
        if (raw.firstPersonRawMaskTarget().width != raw.firstPersonHollowMaskTarget().width
                || raw.firstPersonRawMaskTarget().height != raw.firstPersonHollowMaskTarget().height) {
            return false;
        }

        return raw.mainTarget().getColorTextureView() != null
                && raw.worldRawMaskTarget().getColorTextureView() != null
                && raw.worldRawMaskTarget().getDepthTextureView() != null
                && raw.worldHollowMaskTarget().getColorTextureView() != null
                && raw.worldSceneDepthTarget().getDepthTextureView() != null
                && raw.firstPersonRawMaskTarget().getColorTextureView() != null
                && raw.firstPersonRawMaskTarget().getDepthTextureView() != null
                && raw.firstPersonHollowMaskTarget().getColorTextureView() != null
                && raw.firstPersonSceneDepthTarget().getDepthTextureView() != null;
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
                .advancedEffectEnabled(source.advancedEffectEnabled())
                .build();
    }

    public OutlineAdvancedInput normalizedAdvancedInput(RawInputSnapshot raw, OutlineTechniqueSettings normalizedSettings) {
        if (normalizedSettings == null || !normalizedSettings.advancedEffectEnabled()) {
            return OutlineAdvancedInput.disabled();
        }

        RawAdvancedFrameData source = raw.advancedRawData();
        if (source == null) {
            return OutlineAdvancedInput.disabled();
        }

        return new OutlineAdvancedInput(
                true,
                finiteOrZero(source.gameDeltaTicks()),
                finiteOrZero(source.gamePartialTick()),
                finiteOrZero(source.realtimeDeltaTicks()),
                Math.max(0L, source.gameTime()),
                finiteOrZero(source.dayTime()),
                finiteOrZero(source.cameraX()),
                finiteOrZero(source.cameraY()),
                finiteOrZero(source.cameraZ()),
                finiteOrZero(source.cameraYaw()),
                finiteOrZero(source.cameraPitch()),
                source.cameraInitialized()
        );
    }

    private static boolean isFinite(OutlineTechniqueSettings settings) {
        if (settings == null) {
            return true;
        }
        return Float.isFinite(settings.alphaThreshold()) && Float.isFinite(settings.depthEpsilon());
    }

    private static boolean isFinite(RawAdvancedFrameData data) {
        if (data == null) {
            return true;
        }
        return Float.isFinite(data.gameDeltaTicks())
                && Float.isFinite(data.gamePartialTick())
                && Float.isFinite(data.realtimeDeltaTicks())
                && Float.isFinite(data.dayTime())
                && Double.isFinite(data.cameraX())
                && Double.isFinite(data.cameraY())
                && Double.isFinite(data.cameraZ())
                && Float.isFinite(data.cameraYaw())
                && Float.isFinite(data.cameraPitch());
    }

    private static float finiteOrZero(float value) {
        return Float.isFinite(value) ? value : 0.0F;
    }

    private static double finiteOrZero(double value) {
        return Double.isFinite(value) ? value : 0.0;
    }
}

