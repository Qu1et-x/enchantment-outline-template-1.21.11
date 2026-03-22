package quiet.enchantmentoutline.runtime.rendering;

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
import quiet.enchantmentoutline.debug.OutlineDebugFlags;
import quiet.enchantmentoutline.mixin.client.RenderSetupTextureBindingAccessor;
import quiet.enchantmentoutline.mixin.client.RenderSetupTexturesAccessor;
import quiet.enchantmentoutline.mixin.client.RenderTypeAccessor;
import quiet.enchantmentoutline.mixin.client.RenderTypeStateAccessor;
import quiet.enchantmentoutline.runtime.buffer.MaskBufferManager;

import java.util.EnumMap;
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

    public static final OutputTarget WORLD_MASK_TARGET = new OutputTarget(
            "enchantment_mask_world",
            () -> MaskBufferManager.getInstance().getWorldMaskTarget()
    );

    public static final OutputTarget FIRST_PERSON_MASK_TARGET = new OutputTarget(
            "enchantment_mask_first_person",
            () -> MaskBufferManager.getInstance().getFirstPersonMaskTarget()
    );

    private static final Map<OutlineMaskBranch, Map<Identifier, RenderType>> ZFIX_DEPTH_LAYERS = new EnumMap<>(OutlineMaskBranch.class);
    private static final Map<OutlineMaskBranch, Map<Identifier, RenderType>> OUTLINE_COLOR_LAYERS = new EnumMap<>(OutlineMaskBranch.class);

    public static final RenderType ZFIX_DEPTH_LAYER = getZfixDepthLayer(DEFAULT_ITEM_ATLAS, OutlineMaskBranch.WORLD);

    public static final RenderType OUTLINE_COLOR_LAYER = getOutlineColorLayer(DEFAULT_ITEM_ATLAS, OutlineMaskBranch.WORLD);

    public static synchronized RenderType getZfixDepthLayer(Identifier textureId) {
        return getZfixDepthLayer(textureId, OutlineMaskBranch.WORLD);
    }

    public static synchronized RenderType getZfixDepthLayer(Identifier textureId, OutlineMaskBranch branch) {
        Identifier key = textureId == null ? DEFAULT_ITEM_ATLAS : textureId;
        Map<Identifier, RenderType> cache = ZFIX_DEPTH_LAYERS.computeIfAbsent(branch, ignored -> new HashMap<>());
        RenderType cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        if (cache.size() >= DYNAMIC_LAYER_CACHE_LIMIT) {
            return fallbackToDefaultLayer(cache, getDefaultLayerForBranch(true, branch), "zfix", key, branch);
        }

        RenderType created = createZfixDepthLayer(key, branch);
        cache.put(key, created);
        logLayerCreate("zfix", key, cache.size(), branch);
        return created;
    }

    public static synchronized RenderType getOutlineColorLayer(Identifier textureId) {
        return getOutlineColorLayer(textureId, OutlineMaskBranch.WORLD);
    }

    public static synchronized RenderType getOutlineColorLayer(Identifier textureId, OutlineMaskBranch branch) {
        Identifier key = textureId == null ? DEFAULT_ITEM_ATLAS : textureId;
        Map<Identifier, RenderType> cache = OUTLINE_COLOR_LAYERS.computeIfAbsent(branch, ignored -> new HashMap<>());
        RenderType cached = cache.get(key);
        if (cached != null) {
            return cached;
        }

        if (cache.size() >= DYNAMIC_LAYER_CACHE_LIMIT) {
            return fallbackToDefaultLayer(cache, getDefaultLayerForBranch(false, branch), "color", key, branch);
        }

        RenderType created = createOutlineColorLayer(key, branch);
        cache.put(key, created);
        logLayerCreate("color", key, cache.size(), branch);
        return created;
    }

    public static Identifier resolveMaskTexture(RenderType sourceRenderType) {
        if (sourceRenderType instanceof RenderTypeStateAccessor) {
            RenderSetup setup = ((RenderTypeStateAccessor) sourceRenderType).enchantmentOutline$getState();
            Map<String, ?> textures = ((RenderSetupTexturesAccessor) (Object) setup).enchantmentOutline$getTextures();
            Object binding = textures.get("Sampler0");
            if (binding instanceof RenderSetupTextureBindingAccessor
                    && ((RenderSetupTextureBindingAccessor) binding).enchantmentOutline$getLocation() != null) {
                return ((RenderSetupTextureBindingAccessor) binding).enchantmentOutline$getLocation();
            }
        }

        if (OutlineDebugFlags.SUBMIT && RESOLVE_FALLBACK_LOG_COUNT < OBSERVABILITY_WINDOW) {
            RESOLVE_FALLBACK_LOG_COUNT++;
            LOGGER.info("Mask texture fallback to default atlas for renderType={} ({}/{})",
                    sourceRenderType,
                    RESOLVE_FALLBACK_LOG_COUNT,
                    OBSERVABILITY_WINDOW);
        }

        return DEFAULT_ITEM_ATLAS;
    }

    private static RenderType fallbackToDefaultLayer(Map<Identifier, RenderType> cache,
                                                     RenderType defaultLayer,
                                                     String passName,
                                                     Identifier requestedTexture,
                                                     OutlineMaskBranch branch) {
        if (CACHE_LIMIT_LOG_COUNT < OBSERVABILITY_WINDOW) {
            CACHE_LIMIT_LOG_COUNT++;
            LOGGER.warn("Dynamic layer cache limit reached ({}). pass={}, branch={}, requestedTexture={}, fallbackTexture={} ({}/{})",
                    DYNAMIC_LAYER_CACHE_LIMIT,
                    passName,
                    branch,
                    requestedTexture,
                    DEFAULT_ITEM_ATLAS,
                    CACHE_LIMIT_LOG_COUNT,
                    OBSERVABILITY_WINDOW);
        }

        RenderType fallback = cache.get(DEFAULT_ITEM_ATLAS);
        return fallback != null ? fallback : defaultLayer;
    }

    private static void logLayerCreate(String passName, Identifier textureId, int size, OutlineMaskBranch branch) {
        if (OutlineDebugFlags.SUBMIT && CREATE_LOG_COUNT < OBSERVABILITY_WINDOW) {
            CREATE_LOG_COUNT++;
            LOGGER.info("Created dynamic mask layer: pass={}, branch={}, texture={}, cacheSize={}/{} ({}/{})",
                    passName,
                    branch,
                    textureId,
                    size,
                    DYNAMIC_LAYER_CACHE_LIMIT,
                    CREATE_LOG_COUNT,
                    OBSERVABILITY_WINDOW);
        }
    }

    private static RenderType createZfixDepthLayer(Identifier textureId, OutlineMaskBranch branch) {
        return RenderTypeAccessor.callCreate(
                "enchantment_mask_" + branch.name().toLowerCase() + "_zfix_depth/" + textureId,
                RenderSetup.builder(OUTLINE_ZFIX_DEPTH_PIPELINE)
                        .withTexture("Sampler0", textureId)
                        .useLightmap()
                        .useOverlay()
                        .setOutputTarget(outputTargetFor(branch))
                        .createRenderSetup()
        );
    }

    private static RenderType createOutlineColorLayer(Identifier textureId, OutlineMaskBranch branch) {
        return RenderTypeAccessor.callCreate(
                "enchantment_mask_" + branch.name().toLowerCase() + "_color/" + textureId,
                RenderSetup.builder(OUTLINE_COLOR_PIPELINE)
                        .withTexture("Sampler0", textureId)
                        .useLightmap()
                        .useOverlay()
                        .setOutputTarget(outputTargetFor(branch))
                        .createRenderSetup()
        );
    }

    private static RenderType getDefaultLayerForBranch(boolean zfixPass, OutlineMaskBranch branch) {
        Identifier key = DEFAULT_ITEM_ATLAS;
        Map<Identifier, RenderType> cache = zfixPass
                ? ZFIX_DEPTH_LAYERS.computeIfAbsent(branch, ignored -> new HashMap<>())
                : OUTLINE_COLOR_LAYERS.computeIfAbsent(branch, ignored -> new HashMap<>());
        RenderType existing = cache.get(key);
        if (existing != null) {
            return existing;
        }
        RenderType created = zfixPass ? createZfixDepthLayer(key, branch) : createOutlineColorLayer(key, branch);
        cache.put(key, created);
        return created;
    }

    private static OutputTarget outputTargetFor(OutlineMaskBranch branch) {
        return branch == OutlineMaskBranch.FIRST_PERSON ? FIRST_PERSON_MASK_TARGET : WORLD_MASK_TARGET;
    }
}


