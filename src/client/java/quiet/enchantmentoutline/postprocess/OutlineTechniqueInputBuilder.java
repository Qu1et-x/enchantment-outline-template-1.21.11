package quiet.enchantmentoutline.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;
import quiet.enchantmentoutline.technique.context.OutlineFrameData;
import quiet.enchantmentoutline.technique.context.OutlineTechniqueContext;
import quiet.enchantmentoutline.technique.context.OutlineTechniqueInput;
import quiet.enchantmentoutline.technique.preprocess.HollowMaskPreprocessor;

/**
 * 统一构建算法输入，并在进入算法前完成基础就绪性检查。
 */
public final class OutlineTechniqueInputBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-InputBuilder");
    private static int builtLogCount;
    private static int skipLogCount;

    private RenderTarget mainTarget;
    private RenderTarget rawMaskTarget;
    private RenderTarget hollowMaskTarget;
    private RenderTarget sceneDepthTarget;
    private OutlineFrameData frameData;

    public OutlineTechniqueInputBuilder mainTarget(RenderTarget target) {
        this.mainTarget = target;
        return this;
    }

    public OutlineTechniqueInputBuilder rawMaskTarget(RenderTarget target) {
        this.rawMaskTarget = target;
        return this;
    }

    public OutlineTechniqueInputBuilder hollowMaskTarget(RenderTarget target) {
        this.hollowMaskTarget = target;
        return this;
    }

    public OutlineTechniqueInputBuilder sceneDepthTarget(RenderTarget target) {
        this.sceneDepthTarget = target;
        return this;
    }

    public OutlineTechniqueInputBuilder frameData(OutlineFrameData frameData) {
        this.frameData = frameData;
        return this;
    }

    public OutlineTechniqueInput build() {
        if (mainTarget == null || rawMaskTarget == null || hollowMaskTarget == null || sceneDepthTarget == null || frameData == null) {
            logSkip("missing target/frame data object");
            return null;
        }

        if (rawMaskTarget.width != hollowMaskTarget.width || rawMaskTarget.height != hollowMaskTarget.height) {
            logSkip("raw/hollow mask size mismatch");
            return null;
        }

        // 统一在输入构建阶段完成 hollow mask 预处理，算法只消费稳定输入。
        HollowMaskPreprocessor.getInstance().process(rawMaskTarget, hollowMaskTarget);

        boolean ready = mainTarget.getColorTextureView() != null
                && rawMaskTarget.getColorTextureView() != null
                && rawMaskTarget.getDepthTextureView() != null
                && hollowMaskTarget.getColorTextureView() != null
                && sceneDepthTarget.getDepthTextureView() != null;

        if (!ready) {
            logSkip("required texture views are not ready");
            return null;
        }

        if (OutlineDebugFlags.TECHNIQUE && builtLogCount < 20) {
            builtLogCount++;
            LOGGER.info("Prepared technique input: frame={}, viewport={}x{}, rawMask={}x{}, hollowMask={}x{} ({}/20)",
                    frameData.frameIndex(),
                    frameData.viewportWidth(),
                    frameData.viewportHeight(),
                    rawMaskTarget.width,
                    rawMaskTarget.height,
                    hollowMaskTarget.width,
                    hollowMaskTarget.height,
                    builtLogCount);
        }

        return new OutlineTechniqueContext(mainTarget, rawMaskTarget, hollowMaskTarget, sceneDepthTarget, frameData);
    }

    private static void logSkip(String reason) {
        if (OutlineDebugFlags.TECHNIQUE && skipLogCount < 20) {
            skipLogCount++;
            LOGGER.info("Skip building technique input: reason={} ({}/20)", reason, skipLogCount);
        }
    }
}

