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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import quiet.enchantmentoutline.renderer.OutlineRenderLayers;
import quiet.enchantmentoutline.renderer.OutlineVertexConsumers;
import quiet.enchantmentoutline.postprocess.MaskBufferManager;

/**
 * 职责描述: 注入 ItemRenderer 的附魔缓冲获取逻辑，实现全局附魔渲染的拦截与代理包装。
 * 交互映射: 注入至 ItemRenderer.getFoilBuffer 方法的返回点。使用独立的 MultiBufferSource 防止状态冲突。
 */
@Mixin(ItemRenderer.class)
public class ItemRendererMixin {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-ItemMixin");

    @Inject(method = "getFoilBuffer", at = @At("RETURN"), cancellable = true)
    private static void wrapForOutline(MultiBufferSource multiBufferSource, RenderType renderType, boolean bl, boolean bl2, CallbackInfoReturnable<VertexConsumer> cir) {
        if (bl2) {
            VertexConsumer original = cir.getReturnValue();
            // 核心修复：使用 MaskBufferManager 中维护的独立源，而不是传入的 multiBufferSource
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
}
