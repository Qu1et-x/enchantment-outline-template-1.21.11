package quiet.enchantmentoutline.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.Identifier;
import quiet.enchantmentoutline.mixin.client.RenderTypeAccessor;
import quiet.enchantmentoutline.postprocess.MaskBufferManager;

/**
 * 职责描述: 定义附魔描边所需的特殊渲染层。
 * 交互映射: 供 OutlineVertexConsumers 调用，用于获取掩码渲染层。
 */
public class OutlineRenderLayers {

    private static final RenderPipeline OUTLINE_ZFIX_DEPTH_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
                    .withLocation(Identifier.parse("enchantment-outline:pipeline/outline_zfix_depth"))
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .withShaderDefine("PER_FACE_LIGHTING")
                    .withSampler("Sampler1")
                    .withCull(false)
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withDepthWrite(true)
                    .withColorWrite(false)
                    .withoutBlend()
                    .build()
    );

    private static final RenderPipeline OUTLINE_COLOR_PIPELINE = RenderPipelines.register(
            RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
                    .withLocation(Identifier.parse("enchantment-outline:pipeline/outline_color"))
                    .withShaderDefine("ALPHA_CUTOUT", 0.1F)
                    .withShaderDefine("PER_FACE_LIGHTING")
                    .withSampler("Sampler1")
                    .withCull(false)
                    .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
                    .withDepthWrite(false)
                    .withColorWrite(true, true)
                    .withBlend(BlendFunction.TRANSLUCENT)
                    .build()
    );

    public static final OutputTarget ENCHANTMENT_MASK_TARGET = new OutputTarget(
            "enchantment_mask",
            () -> MaskBufferManager.getInstance().getMaskTarget()
    );

    public static final RenderType ZFIX_DEPTH_LAYER = RenderTypeAccessor.callCreate(
            "enchantment_mask_zfix_depth",
            RenderSetup.builder(OUTLINE_ZFIX_DEPTH_PIPELINE)
                    .withTexture("Sampler0", ItemRenderer.ENCHANTED_GLINT_ITEM)
                    .withTexture("Sampler1", ItemRenderer.ENCHANTED_GLINT_ITEM)
                    .setOutputTarget(ENCHANTMENT_MASK_TARGET)
                    .createRenderSetup()
    );

    public static final RenderType OUTLINE_COLOR_LAYER = RenderTypeAccessor.callCreate(
            "enchantment_mask_color",
            RenderSetup.builder(OUTLINE_COLOR_PIPELINE)
                    .withTexture("Sampler0", ItemRenderer.ENCHANTED_GLINT_ITEM)
                    .withTexture("Sampler1", ItemRenderer.ENCHANTED_GLINT_ITEM)
                    .setOutputTarget(ENCHANTMENT_MASK_TARGET)
                    .createRenderSetup()
    );
}
