package quiet.enchantmentoutline.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.SubmitNodeCollection;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quiet.enchantmentoutline.renderer.OutlineRenderLayers;

import java.util.List;

/**
 * 职责描述: 拦截渲染图节点的提交过程。
 * 交互映射: 当主流程提交一个附魔节点时，同步提交一个掩码节点。
 */
@Mixin(SubmitNodeCollection.class)
public class SubmitNodeCollectionMixin {

    /**
     * 针对标准物品的节点提交。
     */
    @Inject(method = "submitItem", at = @At("HEAD"))
    private void onSubmitItem(PoseStack poseStack, ItemDisplayContext itemDisplayContext, int i, int j, int k, int[] is, List<BakedQuad> list, RenderType renderType, ItemStackRenderState.FoilType foilType, CallbackInfo ci) {
        if (foilType != ItemStackRenderState.FoilType.NONE) {
            // 同步提交一个掩码节点，使用完全相同的矩阵和几何体数据
            SubmitNodeCollection self = (SubmitNodeCollection) (Object) this;
            self.submitItem(poseStack, itemDisplayContext, i, j, k, is, list, OutlineRenderLayers.MASK_LAYER, ItemStackRenderState.FoilType.NONE);
        }
    }

    /**
     * 针对特殊模型（三叉戟、盾牌等）的节点提交。
     */
    @Inject(method = "submitModelPart", at = @At("HEAD"))
    private void onSubmitModelPart(ModelPart modelPart, PoseStack poseStack, RenderType renderType, int i, int j, TextureAtlasSprite textureAtlasSprite, boolean bl, boolean bl2, int k, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay, int l, CallbackInfo ci) {
        if (bl2) { // bl2 在此处对应是否有附魔效果
            SubmitNodeCollection self = (SubmitNodeCollection) (Object) this;
            // 提交一个掩码节点，注意将 bl2 设为 false 以防无限递归
            self.submitModelPart(modelPart, poseStack, OutlineRenderLayers.MASK_LAYER, i, j, textureAtlasSprite, bl, false, k, crumblingOverlay, l);
        }
    }
}
