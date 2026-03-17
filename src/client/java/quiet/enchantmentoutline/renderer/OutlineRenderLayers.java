package quiet.enchantmentoutline.renderer;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Layers");
    private static final int OBSERVABILITY_WINDOW = 24;
    private static final int DYNAMIC_LAYER_CACHE_LIMIT = 128;
    private static final Identifier DEFAULT_ITEM_ATLAS = Identifier.withDefaultNamespace("textures/atlas/items.png");
    private static int CREATE_LOG_COUNT;
    private static int CACHE_LIMIT_LOG_COUNT;
    private static int RESOLVE_FALLBACK_LOG_COUNT;

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
        RenderType cached = ZFIX_DEPTH_LAYERS.get(key);
        if (cached != null) {
            return cached;
        }

        if (ZFIX_DEPTH_LAYERS.size() >= DYNAMIC_LAYER_CACHE_LIMIT) {
            return fallbackToDefaultLayer(ZFIX_DEPTH_LAYERS, ZFIX_DEPTH_LAYER, "zfix", key);
        }

        RenderType created = createZfixDepthLayer(key);
        ZFIX_DEPTH_LAYERS.put(key, created);
        logLayerCreate("zfix", key, ZFIX_DEPTH_LAYERS.size());
        return created;
    }

    public static synchronized RenderType getOutlineColorLayer(Identifier textureId) {
        Identifier key = textureId == null ? DEFAULT_ITEM_ATLAS : textureId;
        RenderType cached = OUTLINE_COLOR_LAYERS.get(key);
        if (cached != null) {
            return cached;
        }

        if (OUTLINE_COLOR_LAYERS.size() >= DYNAMIC_LAYER_CACHE_LIMIT) {
            return fallbackToDefaultLayer(OUTLINE_COLOR_LAYERS, OUTLINE_COLOR_LAYER, "color", key);
        }

        RenderType created = createOutlineColorLayer(key);
        OUTLINE_COLOR_LAYERS.put(key, created);
        logLayerCreate("color", key, OUTLINE_COLOR_LAYERS.size());
        return created;
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

        if (RESOLVE_FALLBACK_LOG_COUNT < OBSERVABILITY_WINDOW) {
            RESOLVE_FALLBACK_LOG_COUNT++;
            LOGGER.info("Mask texture fallback to default atlas for renderType={} ({}/{})",
                    sourceRenderType,
                    RESOLVE_FALLBACK_LOG_COUNT,
                    OBSERVABILITY_WINDOW);
        }

        return DEFAULT_ITEM_ATLAS;
    }

    private static RenderType fallbackToDefaultLayer(Map<Identifier, RenderType> cache, RenderType defaultLayer, String passName, Identifier requestedTexture) {
        if (CACHE_LIMIT_LOG_COUNT < OBSERVABILITY_WINDOW) {
            CACHE_LIMIT_LOG_COUNT++;
            LOGGER.warn("Dynamic layer cache limit reached ({}). pass={}, requestedTexture={}, fallbackTexture={} ({}/{})",
                    DYNAMIC_LAYER_CACHE_LIMIT,
                    passName,
                    requestedTexture,
                    DEFAULT_ITEM_ATLAS,
                    CACHE_LIMIT_LOG_COUNT,
                    OBSERVABILITY_WINDOW);
        }

        RenderType fallback = cache.get(DEFAULT_ITEM_ATLAS);
        return fallback != null ? fallback : defaultLayer;
    }

    private static void logLayerCreate(String passName, Identifier textureId, int size) {
        if (CREATE_LOG_COUNT < OBSERVABILITY_WINDOW) {
            CREATE_LOG_COUNT++;
            LOGGER.info("Created dynamic mask layer: pass={}, texture={}, cacheSize={}/{} ({}/{})",
                    passName,
                    textureId,
                    size,
                    DYNAMIC_LAYER_CACHE_LIMIT,
                    CREATE_LOG_COUNT,
                    OBSERVABILITY_WINDOW);
        }
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
