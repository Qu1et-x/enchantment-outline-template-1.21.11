package quiet.enchantmentoutline.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.rendertype.RenderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import quiet.enchantmentoutline.renderer.OutlineRenderLayers;
import quiet.enchantmentoutline.renderer.OutlineVertexConsumers;
import quiet.enchantmentoutline.postprocess.MaskBufferManager;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import java.util.List;

/**
 * 职责描述: 注入 ItemRenderer 的附魔缓冲获取逻辑，并实现同步刷新以确保矩阵正确。
 * 交互映射: 注入至 getFoilBuffer (拦截点) 和 renderItem (刷新点)。
 */
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-ItemMixin");

    @Inject(method = "getFoilBuffer", at = @At("RETURN"), cancellable = true)
    private static void wrapForOutline(MultiBufferSource multiBufferSource, RenderType renderType, boolean bl, boolean bl2, CallbackInfoReturnable<VertexConsumer> cir) {
        if (bl2) {
            VertexConsumer original = cir.getReturnValue();
            VertexConsumer maskConsumer = MaskBufferManager.getInstance()
                    .getMaskBufferSource()
                    .getBuffer(OutlineRenderLayers.MASK_LAYER);

            cir.setReturnValue(new OutlineVertexConsumers(original, maskConsumer));
        }
    }

    @Inject(method = "getSpecialFoilBuffer", at = @At("RETURN"), cancellable = true)
    private static void wrapSpecialForOutline(MultiBufferSource multiBufferSource, RenderType renderType, PoseStack.Pose pose, CallbackInfoReturnable<VertexConsumer> cir) {
        VertexConsumer original = cir.getReturnValue();
        VertexConsumer maskConsumer = MaskBufferManager.getInstance()
                .getMaskBufferSource()
                .getBuffer(OutlineRenderLayers.MASK_LAYER);

        cir.setReturnValue(new OutlineVertexConsumers(original, maskConsumer));
    }

    /**
     * 关键点：在物品渲染完成后立即刷新掩码缓冲区。
     * 因为 1.21.11 的 RenderType.draw 会在调用时拍摄矩阵快照，
     * 所以必须在 PoseStack 变换仍然有效时调用 endBatch。
     */
    @Inject(method = "renderItem", at = @At("RETURN"))
    private static void syncFlushMask(ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, int[] is, List<BakedQuad> list, RenderType renderType, ItemStackRenderState.FoilType foilType, CallbackInfo ci) {
        if (foilType != ItemStackRenderState.FoilType.NONE) {
            MaskBufferManager.getInstance().drawAndFlush();
        }
    }
}
