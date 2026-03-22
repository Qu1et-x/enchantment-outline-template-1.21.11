package quiet.enchantmentoutline.process.hollowmask;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

/**
 * 职责描述: 计算 raw mask 的边缘镂空结果，产出 hollow mask。
 * 交互映射: 由 process.hollowmask 模块调用，不再放在 technique 模块中。
 */
public final class HollowMaskExtractor {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-HollowMask");
    private static int skipLogCount;
    private static int preprocessLogCount;

    private static final Map<Integer, RenderPipeline> HOLLOW_MASK_EXTRACT_PIPELINES = new HashMap<>();

    private static HollowMaskExtractor instance;

    private HollowMaskExtractor() {
    }

    public static HollowMaskExtractor getInstance() {
        if (instance == null) {
            instance = new HollowMaskExtractor();
        }
        return instance;
    }

    public void process(RenderTarget rawMaskTarget, RenderTarget hollowMaskTarget, int radiusPixels) {
        if (rawMaskTarget.getColorTextureView() == null || hollowMaskTarget.getColorTextureView() == null) {
            if (OutlineDebugFlags.PREPROCESS && skipLogCount < 20) {
                skipLogCount++;
                LOGGER.info("Skip hollow mask preprocess: rawColorView={}, hollowColorView={} ({}/20)",
                        rawMaskTarget.getColorTextureView() != null,
                        hollowMaskTarget.getColorTextureView() != null,
                        skipLogCount);
            }
            return;
        }

        if (OutlineDebugFlags.PREPROCESS && preprocessLogCount < 16) {
            preprocessLogCount++;
            LOGGER.info("Hollow preprocess pass #{}, raw={}x{}, hollow={}x{}",
                    preprocessLogCount,
                    rawMaskTarget.width,
                    rawMaskTarget.height,
                    hollowMaskTarget.width,
                    hollowMaskTarget.height);
        }

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Enchantment Hollow Mask Preprocess",
                        hollowMaskTarget.getColorTextureView(), OptionalInt.empty())) {
            renderPass.setPipeline(hollowMaskPipeline(radiusPixels));
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture("InSampler", rawMaskTarget.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.draw(0, 3);
        }
    }

    private static RenderPipeline hollowMaskPipeline(int radiusPixels) {
        int radius = Math.max(1, radiusPixels);
        return HOLLOW_MASK_EXTRACT_PIPELINES.computeIfAbsent(radius, HollowMaskExtractor::buildHollowMaskPipeline);
    }

    private static RenderPipeline buildHollowMaskPipeline(int radius) {
        return RenderPipelines.register(RenderPipeline.builder()
                .withLocation(Identifier.parse("enchantment-outline:pipeline/hollow_mask_extract_r" + radius))
                .withVertexShader(Identifier.parse("enchantment-outline:core/depth_aware_blit"))
                .withFragmentShader(Identifier.parse("enchantment-outline:core/hollow_mask_extract"))
                .withSampler("InSampler")
                .withShaderDefine("OUTLINE_RADIUS_PIXELS", radius)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withDepthWrite(false)
                .withColorWrite(true, true)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
                .build());
    }
}

