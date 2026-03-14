package quiet.enchantmentoutline.mixin.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quiet.enchantmentoutline.postprocess.MaskBufferManager;
import quiet.enchantmentoutline.postprocess.OutlinePostProcessor;

/**
 * 职责描述: 注入 GameRenderer 的每一帧渲染流程。
 * 交互映射: 在渲染开始前清空掩码缓冲区，在 GUI 渲染前执行后处理。
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "render", at = @At("HEAD"))
    private void onRenderHead(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        // 每帧先清空掩码颜色，深度在实际提交掩码前再同步。
        MaskBufferManager.getInstance().beginFrame();
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;render(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    private void onBeforeGuiRender(DeltaTracker deltaTracker, boolean bl, CallbackInfo ci) {
        OutlinePostProcessor.getInstance().process();
    }

    @Inject(method = "resize", at = @At("RETURN"))
    private void onResizeReturn(int width, int height, CallbackInfo ci) {
        MaskBufferManager.getInstance().onRescale(width, height);
    }
}
