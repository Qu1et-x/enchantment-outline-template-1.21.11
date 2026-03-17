package quiet.enchantmentoutline.technique.radius;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.technique.OutlineTechnique;
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;

import java.util.OptionalInt;

/**
 * Current production implementation: direct radius sampling in the post shader.
 */
public class RadiusSamplingOutlineTechnique implements OutlineTechnique {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Technique-Radius");
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

    @Override
    public OutlineTechniqueMode mode() {
        return OutlineTechniqueMode.RADIUS_SAMPLING;
    }

    @Override
    public void process(RenderTarget maskTarget, RenderTarget sceneDepthTarget) {
        RenderSystem.assertOnRenderThread();
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        if (maskTarget == null
                || sceneDepthTarget == null
                || maskTarget.getColorTextureView() == null
                || maskTarget.getDepthTextureView() == null
                || sceneDepthTarget.getDepthTextureView() == null) {
            if (skipLogCount < 12) {
                skipLogCount++;
                LOGGER.info("Skipping radius outline composite: colorView={}, maskDepthView={}, sceneDepthView={} ({}/12)",
                        maskTarget != null && maskTarget.getColorTextureView() != null,
                        maskTarget != null && maskTarget.getDepthTextureView() != null,
                        sceneDepthTarget != null && sceneDepthTarget.getDepthTextureView() != null,
                        skipLogCount);
            }
            return;
        }

        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Enchantment Outline Radius Post", mainTarget.getColorTextureView(), OptionalInt.empty())) {

            renderPass.setPipeline(DEPTH_AWARE_OUTLINE_BLIT);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture("InSampler", maskTarget.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("MaskDepthSampler", maskTarget.getDepthTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("SceneDepthSampler", sceneDepthTarget.getDepthTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));

            renderPass.draw(0, 3);
        }
    }
}

