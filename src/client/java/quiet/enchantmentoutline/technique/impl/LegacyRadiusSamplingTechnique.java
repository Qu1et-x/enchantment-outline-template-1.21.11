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
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;
import quiet.enchantmentoutline.technique.context.OutlineTechniqueContext;

import java.util.OptionalInt;

/**
 * 现有半径采样描边实现，作为阶段一默认算法。
 */
public class LegacyRadiusSamplingTechnique extends AbstractOutlineTechnique {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique");
    private static int skipLogCount;

    private static final RenderPipeline DEPTH_AWARE_OUTLINE_BLIT = RenderPipelines.register(RenderPipeline.builder()
            .withLocation(Identifier.parse("enchantment-outline:pipeline/depth_aware_outline_blit"))
            .withVertexShader(Identifier.parse("enchantment-outline:core/depth_aware_blit"))
            .withFragmentShader(Identifier.parse("enchantment-outline:core/depth_aware_blit"))
            .withSampler("InSampler")
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
    public void process(OutlineTechniqueContext context) {
        RenderTarget source = context.maskTarget();
        RenderTarget sceneDepthTarget = context.sceneDepthTarget();
        if (source.getColorTextureView() == null || source.getDepthTextureView() == null || sceneDepthTarget.getDepthTextureView() == null) {
            if (skipLogCount < 20) {
                skipLogCount++;
                LOGGER.info("Skipping legacy composite: colorView={}, maskDepthView={}, sceneDepthView={} ({}/20)",
                        source.getColorTextureView() != null,
                        source.getDepthTextureView() != null,
                        sceneDepthTarget.getDepthTextureView() != null,
                        skipLogCount);
            }
            return;
        }

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Enchantment Outline Post",
                        context.mainTarget().getColorTextureView(), OptionalInt.empty())) {

            renderPass.setPipeline(DEPTH_AWARE_OUTLINE_BLIT);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture("InSampler", source.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("MaskDepthSampler", source.getDepthTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("SceneDepthSampler", sceneDepthTarget.getDepthTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));

            renderPass.draw(0, 3);
        }
    }
}



