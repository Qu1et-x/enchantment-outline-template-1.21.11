package quiet.enchantmentoutline.technique.impl;

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
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueInput;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * 现有半径采样描边实现，作为阶段一默认算法。
 */
public class LegacyRadiusSamplingTechnique extends AbstractOutlineTechnique {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique");
    private static int skipLogCount;
    private static int processLogCount;

    private static final Map<String, RenderPipeline> LEGACY_PIPELINES = new HashMap<>();

    public LegacyRadiusSamplingTechnique() {
        super(OutlineTechniqueMode.LEGACY_RADIUS, "LegacyRadiusSamplingTechnique");
    }

    @Override
    public void process(OutlineTechniqueInput input) {
        composeBranch(input,
                "world",
                input.worldBranch().hollowMaskTarget(),
                input.worldBranch().rawMaskTarget(),
                input.worldBranch().sceneDepthTarget(),
                input.frameData().settings());
        composeBranch(input,
                "first_person",
                input.firstPersonBranch().hollowMaskTarget(),
                input.firstPersonBranch().rawMaskTarget(),
                input.firstPersonBranch().sceneDepthTarget(),
                input.frameData().settings());
    }

    private static void composeBranch(OutlineTechniqueInput input,
                                      String branchLabel,
                                      RenderTarget hollowMaskTarget,
                                      RenderTarget rawMaskTarget,
                                      RenderTarget sceneDepthTarget,
                                      OutlineTechniqueSettings settings) {
        if (hollowMaskTarget.getColorTextureView() == null
                || rawMaskTarget.getColorTextureView() == null
                || rawMaskTarget.getDepthTextureView() == null
                || sceneDepthTarget.getDepthTextureView() == null) {
            if (OutlineDebugFlags.TECHNIQUE && skipLogCount < 20) {
                skipLogCount++;
                LOGGER.info("Skipping legacy composite branch={}: hollowColorView={}, rawColorView={}, maskDepthView={}, sceneDepthView={} ({}/20)",
                        branchLabel,
                        hollowMaskTarget.getColorTextureView() != null,
                        rawMaskTarget.getColorTextureView() != null,
                        rawMaskTarget.getDepthTextureView() != null,
                        sceneDepthTarget.getDepthTextureView() != null,
                        skipLogCount);
            }
            return;
        }

        if (OutlineDebugFlags.TECHNIQUE && processLogCount < 16) {
            processLogCount++;
            LOGGER.info("Legacy composite pass #{} branch={}: hollowColor={}, rawColor={}, rawDepth={}, sceneDepth={}",
                    processLogCount,
                    branchLabel,
                    hollowMaskTarget.getColorTextureView() != null,
                    rawMaskTarget.getColorTextureView() != null,
                    rawMaskTarget.getDepthTextureView() != null,
                    sceneDepthTarget.getDepthTextureView() != null);
        }

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Enchantment Outline Post [" + branchLabel + "]",
                        Objects.requireNonNull(input.mainTarget().getColorTextureView(), "Main target color view is not initialized"),
                        OptionalInt.empty())) {

            renderPass.setPipeline(legacyPipeline(settings));
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture("HollowSampler", hollowMaskTarget.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("RawMaskSampler", rawMaskTarget.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("MaskDepthSampler", rawMaskTarget.getDepthTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("SceneDepthSampler", sceneDepthTarget.getDepthTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));

            renderPass.draw(0, 3);
        }
    }

    private static RenderPipeline legacyPipeline(OutlineTechniqueSettings settings) {
        int radius = Math.max(1, settings.outlineRadiusPixels());
        int alphaScaled = Math.max(0, Math.round(settings.alphaThreshold() * 10000.0F));
        int depthScaled = Math.max(0, Math.round(settings.depthEpsilon() * 1000000.0F));
        int colorRScaled = Math.max(0, Math.round(settings.outlineColorRed() * 255.0F));
        int colorGScaled = Math.max(0, Math.round(settings.outlineColorGreen() * 255.0F));
        int colorBScaled = Math.max(0, Math.round(settings.outlineColorBlue() * 255.0F));
        int colorMixScaled = Math.max(0, Math.round(settings.outlineColorMix() * 1000.0F));

        String key = radius + "_" + alphaScaled + "_" + depthScaled + "_"
                + colorRScaled + "_" + colorGScaled + "_" + colorBScaled + "_" + colorMixScaled;
        return LEGACY_PIPELINES.computeIfAbsent(
                key,
                ignored -> buildLegacyPipeline(settings, radius, alphaScaled, depthScaled, colorRScaled, colorGScaled, colorBScaled, colorMixScaled));
    }

    private static RenderPipeline buildLegacyPipeline(OutlineTechniqueSettings settings,
                                                      int radius,
                                                      int alphaScaled,
                                                      int depthScaled,
                                                      int colorRScaled,
                                                      int colorGScaled,
                                                      int colorBScaled,
                                                      int colorMixScaled) {
        return RenderPipelines.register(RenderPipeline.builder()
                .withLocation(Identifier.parse("enchantment-outline:pipeline/depth_aware_outline_blit_r" + radius
                        + "_a" + alphaScaled
                        + "_d" + depthScaled
                        + "_cr" + colorRScaled
                        + "_cg" + colorGScaled
                        + "_cb" + colorBScaled
                        + "_cm" + colorMixScaled))
                .withVertexShader(Identifier.parse("enchantment-outline:core/depth_aware_blit"))
                .withFragmentShader(Identifier.parse("enchantment-outline:core/outline_composite_shared"))
                .withSampler("HollowSampler")
                .withSampler("RawMaskSampler")
                .withSampler("MaskDepthSampler")
                .withSampler("SceneDepthSampler")
                .withShaderDefine("LEGACY_EDGE")
                .withShaderDefine("OUTLINE_RADIUS", radius)
                .withShaderDefine("ALPHA_THRESHOLD", settings.alphaThreshold())
                .withShaderDefine("DEPTH_EPSILON", settings.depthEpsilon())
                .withShaderDefine("OUTLINE_COLOR_R", settings.outlineColorRed())
                .withShaderDefine("OUTLINE_COLOR_G", settings.outlineColorGreen())
                .withShaderDefine("OUTLINE_COLOR_B", settings.outlineColorBlue())
                .withShaderDefine("OUTLINE_COLOR_MIX", settings.outlineColorMix())
                .withBlend(BlendFunction.ENTITY_OUTLINE_BLIT)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withDepthWrite(false)
                .withColorWrite(true, false)
                .withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
                .build());
    }
}



