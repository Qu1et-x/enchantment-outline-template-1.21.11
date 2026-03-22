package quiet.enchantmentoutline.technique.impl.jfa;

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
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;
import quiet.enchantmentoutline.technique.impl.AbstractOutlineTechnique;
import quiet.enchantmentoutline.technique.input.BranchRenderTargets;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueInput;
import quiet.enchantmentoutline.technique.input.OutlineTechniqueSettings;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;

/**
 * JFA outline implementation scaffold.
 *
 * Current status:
 * - Core JFA seed/jump/composite pipeline implemented.
 * - Wired into manager registration and used as the default startup mode.
 */
public final class JfaOutlineTechnique extends AbstractOutlineTechnique {
    private static final Identifier FULLSCREEN_VERTEX = Identifier.parse("enchantment-outline:core/depth_aware_blit");
    private static final Identifier JFA_SEED_FRAGMENT = Identifier.parse("enchantment-outline:core/jfa_seed");
    private static final Identifier JFA_JUMP_FRAGMENT = Identifier.parse("enchantment-outline:core/jfa_jump");
    private static final Identifier JFA_COMPOSITE_FRAGMENT = Identifier.parse("enchantment-outline:core/outline_composite_shared");

    private static final RenderPipeline JFA_SEED_PIPELINE = RenderPipelines.register(RenderPipeline.builder()
            .withLocation(Identifier.parse("enchantment-outline:pipeline/jfa_seed"))
            .withVertexShader(FULLSCREEN_VERTEX)
            .withFragmentShader(JFA_SEED_FRAGMENT)
            .withSampler("RawMaskSampler")
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withColorWrite(true, true)
            .withBlend(BlendFunction.TRANSLUCENT)
            .withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
            .build());

    private static final Map<Integer, RenderPipeline> JFA_JUMP_PIPELINES = new HashMap<>();
    private static final Map<String, RenderPipeline> JFA_COMPOSITE_PIPELINES = new HashMap<>();

    private final JfaScratchBufferPool worldScratchPool = new JfaScratchBufferPool();
    private final JfaScratchBufferPool firstPersonScratchPool = new JfaScratchBufferPool();

    public JfaOutlineTechnique() {
        super(OutlineTechniqueMode.JFA, "JfaOutlineTechnique");
    }

    @Override
    public void process(OutlineTechniqueInput input) {
        processBranch(input, "world", input.worldBranch(), worldScratchPool);
        processBranch(input, "first_person", input.firstPersonBranch(), firstPersonScratchPool);
    }

    private static void processBranch(OutlineTechniqueInput input,
                                      String branchLabel,
                                      BranchRenderTargets branch,
                                      JfaScratchBufferPool scratchPool) {
        RenderTarget rawMaskTarget = branch.rawMaskTarget();
        RenderTarget hollowMaskTarget = branch.hollowMaskTarget();
        RenderTarget sceneDepthTarget = branch.sceneDepthTarget();

        if (rawMaskTarget.getColorTextureView() == null
                || rawMaskTarget.getDepthTextureView() == null
                || hollowMaskTarget.getColorTextureView() == null
                || sceneDepthTarget.getDepthTextureView() == null
                || input.mainTarget().getColorTextureView() == null) {
            return;
        }

        OutlineTechniqueSettings settings = input.frameData().settings();
        int radius = Math.max(1, settings.outlineRadiusPixels());

        JfaScratchBufferPool.BranchScratch scratch = scratchPool.getOrCreate(branchLabel, rawMaskTarget.width, rawMaskTarget.height);

        runSeedPass(rawMaskTarget, scratch.seedFieldTarget());

        RenderTarget currentField = scratch.seedFieldTarget();
        RenderTarget ping = scratch.pingFieldTarget();
        RenderTarget pong = scratch.pongFieldTarget();
        boolean writePing = true;

        for (int step = highestPowerOfTwoAtLeast(radius); step >= 1; step >>= 1) {
            RenderTarget outTarget = writePing ? ping : pong;
            runJumpPass(currentField, outTarget, step);
            currentField = outTarget;
            writePing = !writePing;
        }

        runCompositePass(input.mainTarget(), hollowMaskTarget, rawMaskTarget, sceneDepthTarget, currentField, settings);
    }

    private static void runSeedPass(RenderTarget rawMaskTarget, RenderTarget seedFieldTarget) {
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "JFA Seed",
                        Objects.requireNonNull(seedFieldTarget.getColorTextureView(), "JFA seed target color view is missing"),
                        OptionalInt.empty())) {
            renderPass.setPipeline(JFA_SEED_PIPELINE);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture("RawMaskSampler", rawMaskTarget.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.draw(0, 3);
        }
    }

    private static void runJumpPass(RenderTarget inFieldTarget, RenderTarget outFieldTarget, int jumpStep) {
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "JFA Jump step=" + jumpStep,
                        Objects.requireNonNull(outFieldTarget.getColorTextureView(), "JFA jump output color view is missing"),
                        OptionalInt.empty())) {
            renderPass.setPipeline(jumpPipeline(jumpStep));
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture("JfaInSampler", inFieldTarget.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.draw(0, 3);
        }
    }

    private static void runCompositePass(RenderTarget mainTarget,
                                         RenderTarget hollowMaskTarget,
                                         RenderTarget rawMaskTarget,
                                         RenderTarget sceneDepthTarget,
                                         RenderTarget jfaFieldTarget,
                                         OutlineTechniqueSettings settings) {
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "JFA Composite",
                        Objects.requireNonNull(mainTarget.getColorTextureView(), "Main target color view is missing"),
                        OptionalInt.empty())) {
            renderPass.setPipeline(compositePipeline(settings));
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture("HollowSampler", hollowMaskTarget.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("RawMaskSampler", rawMaskTarget.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("JfaFieldSampler", jfaFieldTarget.getColorTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("MaskDepthSampler", rawMaskTarget.getDepthTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("SceneDepthSampler", sceneDepthTarget.getDepthTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.draw(0, 3);
        }
    }

    private static RenderPipeline jumpPipeline(int step) {
        return JFA_JUMP_PIPELINES.computeIfAbsent(step, JfaOutlineTechnique::buildJumpPipeline);
    }

    private static RenderPipeline buildJumpPipeline(int step) {
        return RenderPipelines.register(RenderPipeline.builder()
                .withLocation(Identifier.parse("enchantment-outline:pipeline/jfa_jump_" + step))
                .withVertexShader(FULLSCREEN_VERTEX)
                .withFragmentShader(JFA_JUMP_FRAGMENT)
                .withSampler("JfaInSampler")
                .withShaderDefine("JFA_STEP", step)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withDepthWrite(false)
                .withColorWrite(true, true)
                .withBlend(BlendFunction.TRANSLUCENT)
                .withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
                .build());
    }

    private static RenderPipeline compositePipeline(OutlineTechniqueSettings settings) {
        int radius = Math.max(1, settings.outlineRadiusPixels());
        int alphaScaled = Math.max(0, Math.round(settings.alphaThreshold() * 10000.0F));
        int depthScaled = Math.max(0, Math.round(settings.depthEpsilon() * 1000000.0F));
        int colorRScaled = Math.max(0, Math.round(settings.outlineColorRed() * 255.0F));
        int colorGScaled = Math.max(0, Math.round(settings.outlineColorGreen() * 255.0F));
        int colorBScaled = Math.max(0, Math.round(settings.outlineColorBlue() * 255.0F));
        int colorMixScaled = Math.max(0, Math.round(settings.outlineColorMix() * 1000.0F));
        String key = radius + "_" + alphaScaled + "_" + depthScaled + "_"
                + colorRScaled + "_" + colorGScaled + "_" + colorBScaled + "_" + colorMixScaled;
        return JFA_COMPOSITE_PIPELINES.computeIfAbsent(
                key,
                ignored -> buildCompositePipeline(
                        radius,
                        settings.alphaThreshold(),
                        settings.depthEpsilon(),
                        settings.outlineColorRed(),
                        settings.outlineColorGreen(),
                        settings.outlineColorBlue(),
                        settings.outlineColorMix(),
                        alphaScaled,
                        depthScaled,
                        colorRScaled,
                        colorGScaled,
                        colorBScaled,
                        colorMixScaled));
    }

    private static RenderPipeline buildCompositePipeline(int radius,
                                                         float alphaThreshold,
                                                         float depthEpsilon,
                                                         float colorR,
                                                         float colorG,
                                                         float colorB,
                                                         float colorMix,
                                                         int alphaScaled,
                                                         int depthScaled,
                                                         int colorRScaled,
                                                         int colorGScaled,
                                                         int colorBScaled,
                                                         int colorMixScaled) {
        return RenderPipelines.register(RenderPipeline.builder()
                .withLocation(Identifier.parse("enchantment-outline:pipeline/jfa_composite_r" + radius
                        + "_a" + alphaScaled
                        + "_d" + depthScaled
                        + "_cr" + colorRScaled
                        + "_cg" + colorGScaled
                        + "_cb" + colorBScaled
                        + "_cm" + colorMixScaled))
                .withVertexShader(FULLSCREEN_VERTEX)
                .withFragmentShader(JFA_COMPOSITE_FRAGMENT)
                .withSampler("HollowSampler")
                .withSampler("RawMaskSampler")
                .withSampler("JfaFieldSampler")
                .withSampler("MaskDepthSampler")
                .withSampler("SceneDepthSampler")
                .withShaderDefine("JFA_EDGE")
                .withShaderDefine("OUTLINE_RADIUS", radius)
                .withShaderDefine("ALPHA_THRESHOLD", alphaThreshold)
                .withShaderDefine("DEPTH_EPSILON", depthEpsilon)
                .withShaderDefine("OUTLINE_COLOR_R", colorR)
                .withShaderDefine("OUTLINE_COLOR_G", colorG)
                .withShaderDefine("OUTLINE_COLOR_B", colorB)
                .withShaderDefine("OUTLINE_COLOR_MIX", colorMix)
                .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
                .withDepthWrite(false)
                .withColorWrite(true, false)
                .withBlend(BlendFunction.ENTITY_OUTLINE_BLIT)
                .withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
                .build());
    }

    private static int highestPowerOfTwoAtLeast(int value) {
        int p = 1;
        while (p < value) {
            p <<= 1;
        }
        return p;
    }

}


