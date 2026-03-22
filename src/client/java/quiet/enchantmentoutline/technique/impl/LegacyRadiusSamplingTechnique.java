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

import java.util.Objects;
import java.util.OptionalInt;

/**
 * 现有半径采样描边实现，作为阶段一默认算法。
 */
public class LegacyRadiusSamplingTechnique extends AbstractOutlineTechnique {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique");
    private static int skipLogCount;
    private static int processLogCount;

    private static final RenderPipeline DEPTH_AWARE_OUTLINE_BLIT = RenderPipelines.register(RenderPipeline.builder()
            .withLocation(Identifier.parse("enchantment-outline:pipeline/depth_aware_outline_blit"))
            .withVertexShader(Identifier.parse("enchantment-outline:core/depth_aware_blit"))
            .withFragmentShader(Identifier.parse("enchantment-outline:core/depth_aware_blit"))
            .withSampler("HollowSampler")
            .withSampler("RawMaskSampler")
            .withSampler("MaskDepthSampler")
            .withSampler("SceneDepthSampler")
            .withBlend(BlendFunction.ENTITY_OUTLINE_BLIT)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withColorWrite(true, false)
            .withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
            .build());

    public LegacyRadiusSamplingTechnique() {
        super(OutlineTechniqueMode.LEGACY_RADIUS, "LegacyRadiusSamplingTechnique");
    }

    @Override
    public void process(OutlineTechniqueInput input) {
        composeBranch(input,
                "world",
                input.worldBranch().hollowMaskTarget(),
                input.worldBranch().rawMaskTarget(),
                input.worldBranch().sceneDepthTarget());
        composeBranch(input,
                "first_person",
                input.firstPersonBranch().hollowMaskTarget(),
                input.firstPersonBranch().rawMaskTarget(),
                input.firstPersonBranch().sceneDepthTarget());
    }

    private static void composeBranch(OutlineTechniqueInput input,
                                      String branchLabel,
                                      RenderTarget hollowMaskTarget,
                                      RenderTarget rawMaskTarget,
                                      RenderTarget sceneDepthTarget) {
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

            renderPass.setPipeline(DEPTH_AWARE_OUTLINE_BLIT);
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
}



