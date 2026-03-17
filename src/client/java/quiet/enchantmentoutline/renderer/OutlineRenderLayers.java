package quiet.enchantmentoutline.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import quiet.enchantmentoutline.mixin.client.RenderSetupTextureBindingAccessor;
import quiet.enchantmentoutline.mixin.client.RenderSetupTexturesAccessor;
import quiet.enchantmentoutline.mixin.client.RenderTypeAccessor;
import quiet.enchantmentoutline.mixin.client.RenderTypeStateAccessor;
import quiet.enchantmentoutline.postprocess.MaskBufferManager;

import java.util.HashMap;
import java.util.Map;

/**
 * 职责描述: 定义附魔描边所需的特殊渲染层。
 * 交互映射: 供 OutlineVertexConsumers 调用，用于获取掩码渲染层。
 */
public class OutlineRenderLayers {
    private static final Identifier DEFAULT_ITEM_ATLAS = Identifier.withDefaultNamespace("textures/atlas/items.png");

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

    private static final Map<Identifier, RenderType> ZFIX_DEPTH_LAYERS = new HashMap<>();
    private static final Map<Identifier, RenderType> OUTLINE_COLOR_LAYERS = new HashMap<>();

    public static final RenderType ZFIX_DEPTH_LAYER = getZfixDepthLayer(DEFAULT_ITEM_ATLAS);

    public static final RenderType OUTLINE_COLOR_LAYER = getOutlineColorLayer(DEFAULT_ITEM_ATLAS);

    public static synchronized RenderType getZfixDepthLayer(Identifier textureId) {
        Identifier key = textureId == null ? DEFAULT_ITEM_ATLAS : textureId;
        return ZFIX_DEPTH_LAYERS.computeIfAbsent(key, OutlineRenderLayers::createZfixDepthLayer);
    }

    public static synchronized RenderType getOutlineColorLayer(Identifier textureId) {
        Identifier key = textureId == null ? DEFAULT_ITEM_ATLAS : textureId;
        return OUTLINE_COLOR_LAYERS.computeIfAbsent(key, OutlineRenderLayers::createOutlineColorLayer);
    }

    public static Identifier resolveMaskTexture(RenderType sourceRenderType) {
        if (sourceRenderType instanceof RenderTypeStateAccessor) {
            RenderSetup setup = ((RenderTypeStateAccessor) (Object) sourceRenderType).enchantmentOutline$getState();
            Map<String, ?> textures = ((RenderSetupTexturesAccessor) (Object) setup).enchantmentOutline$getTextures();
            Object binding = textures.get("Sampler0");
            if (binding instanceof RenderSetupTextureBindingAccessor
                    && ((RenderSetupTextureBindingAccessor) binding).enchantmentOutline$getLocation() != null) {
                return ((RenderSetupTextureBindingAccessor) binding).enchantmentOutline$getLocation();
            }
        }

        return DEFAULT_ITEM_ATLAS;
    }

    private static RenderType createZfixDepthLayer(Identifier textureId) {
        return RenderTypeAccessor.callCreate(
                "enchantment_mask_zfix_depth/" + textureId,
                RenderSetup.builder(OUTLINE_ZFIX_DEPTH_PIPELINE)
                        .withTexture("Sampler0", textureId)
                        .useLightmap()
                        .useOverlay()
                        .setOutputTarget(ENCHANTMENT_MASK_TARGET)
                        .createRenderSetup()
        );
    }

    private static RenderType createOutlineColorLayer(Identifier textureId) {
        return RenderTypeAccessor.callCreate(
                "enchantment_mask_color/" + textureId,
                RenderSetup.builder(OUTLINE_COLOR_PIPELINE)
                        .withTexture("Sampler0", textureId)
                        .useLightmap()
                        .useOverlay()
                        .setOutputTarget(ENCHANTMENT_MASK_TARGET)
                        .createRenderSetup()
        );
    }
}
