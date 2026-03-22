package quiet.enchantmentoutline.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.item.ItemDisplayContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import quiet.enchantmentoutline.runtime.rendering.OutlineRenderContext;

/**
 * 职责描述: 在物品层提交流程前后同步当前 ItemDisplayContext。
 * 交互映射: 注入 ItemStackRenderState.submit，向 OutlineRenderContext 写入上下文。
 */
@Mixin(ItemStackRenderState.class)
public class ItemStackRenderStateMixin {
    @Shadow
    ItemDisplayContext displayContext;

    @Inject(method = "submit", at = @At("HEAD"))
    private void enchantmentOutline$pushContext(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, int j, int k, CallbackInfo ci) {
        OutlineRenderContext.push(this.displayContext);
    }

    @Inject(method = "submit", at = @At("RETURN"))
    private void enchantmentOutline$clearContext(PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int i, int j, int k, CallbackInfo ci) {
        OutlineRenderContext.clear();
    }
}
