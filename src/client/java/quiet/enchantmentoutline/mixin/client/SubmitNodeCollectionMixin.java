package quiet.enchantmentoutline.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quiet.enchantmentoutline.renderer.OutlineRenderLayers;
import quiet.enchantmentoutline.render.OutlineRenderContext;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 职责描述: 拦截渲染图节点的提交过程。
 * 交互映射: 当主流程提交一个附魔节点时，同步提交一个掩码节点。
 */
@Mixin(SubmitNodeCollection.class)
public class SubmitNodeCollectionMixin {
    @Unique
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Submit");

    @Unique
    private static int ENCHANTMENT_OUTLINE$specialSkipLogCount;

    @Unique
    private static final Map<ItemDisplayContext, Integer> ENCHANTMENT_OUTLINE$maskPassLogCountByContext = new EnumMap<>(ItemDisplayContext.class);

    /**
     * 针对标准物品的节点提交。
     */
    @Inject(method = "submitItem", at = @At("HEAD"))
    private void onSubmitItem(PoseStack poseStack, ItemDisplayContext itemDisplayContext, int i, int j, int k, int[] is, List<BakedQuad> list, RenderType renderType, ItemStackRenderState.FoilType foilType, CallbackInfo ci) {
        boolean shouldWrite = foilType != ItemStackRenderState.FoilType.NONE && shouldWriteMask(itemDisplayContext);
        if (foilType != ItemStackRenderState.FoilType.NONE && LOGGER.isDebugEnabled()) {
            LOGGER.debug("submitItem enchanted: context={}, foilType={}, maskAllowed={}", itemDisplayContext, foilType, shouldWrite);
        }

        if (shouldWrite) {
            // 仅提交掩码两阶段，遮挡在后处理阶段与最终场景深度比较。
            SubmitNodeCollection self = (SubmitNodeCollection) (Object) this;
            Identifier textureId = OutlineRenderLayers.resolveMaskTexture(renderType);
            self.submitItem(poseStack, itemDisplayContext, i, j, k, is, list, OutlineRenderLayers.getZfixDepthLayer(textureId), ItemStackRenderState.FoilType.NONE);
            self.submitItem(poseStack, itemDisplayContext, i, j, k, is, list, OutlineRenderLayers.getOutlineColorLayer(textureId), ItemStackRenderState.FoilType.NONE);
            int count = nextMaskPassLogCount(itemDisplayContext);
            if (count <= 12) {
                LOGGER.info("submitItem mask two-pass: context={}, foilType={}, contextPass={}/12", itemDisplayContext, foilType, count);
            }
        }
    }

    /**
     * 针对特殊模型（三叉戟、盾牌等）的节点提交。
     */
    @Inject(method = "submitModelPart", at = @At("HEAD"))
    private void onSubmitModelPart(ModelPart modelPart, PoseStack poseStack, RenderType renderType, int i, int j, TextureAtlasSprite textureAtlasSprite, boolean bl, boolean bl2, int k, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, int l, CallbackInfo ci) {
        ItemDisplayContext context = OutlineRenderContext.current();
        boolean shouldWrite = bl2 && shouldWriteMask(context);

        if (bl2 && !shouldWrite && context == ItemDisplayContext.NONE && ENCHANTMENT_OUTLINE$specialSkipLogCount < 12) {
            ENCHANTMENT_OUTLINE$specialSkipLogCount++;
            LOGGER.info("Skipping enchanted special model because display context is NONE. renderType={}, lit={}, skipCount={}", renderType, bl, ENCHANTMENT_OUTLINE$specialSkipLogCount);
        } else if (bl2 && LOGGER.isDebugEnabled()) {
            LOGGER.debug("submitModelPart enchanted: context={}, renderType={}, maskAllowed={}", context, renderType, shouldWrite);
        }

        if (shouldWrite) { // bl2 在此处对应是否有附魔效果
            SubmitNodeCollection self = (SubmitNodeCollection) (Object) this;
            Identifier textureId = OutlineRenderLayers.resolveMaskTexture(renderType);
            // 提交一个掩码节点，注意将 bl2 设为 false 以防无限递归
            self.submitModelPart(modelPart, poseStack, OutlineRenderLayers.getZfixDepthLayer(textureId), i, j, textureAtlasSprite, bl, false, k, crumblingOverlay, l);
            self.submitModelPart(modelPart, poseStack, OutlineRenderLayers.getOutlineColorLayer(textureId), i, j, textureAtlasSprite, bl, false, k, crumblingOverlay, l);
            int count = nextMaskPassLogCount(context);
            if (count <= 12) {
                LOGGER.info("submitModelPart mask two-pass: context={}, renderType={}, contextPass={}/12", context, renderType, count);
            }
        }
    }

    @Unique
    private static boolean shouldWriteMask(ItemDisplayContext context) {
        return context == ItemDisplayContext.GROUND
                || context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND;
    }

    @Unique
    private static int nextMaskPassLogCount(ItemDisplayContext context) {
        ItemDisplayContext key = context == null ? ItemDisplayContext.NONE : context;
        int next = ENCHANTMENT_OUTLINE$maskPassLogCountByContext.getOrDefault(key, 0) + 1;
        ENCHANTMENT_OUTLINE$maskPassLogCountByContext.put(key, next);
        return next;
    }

}
