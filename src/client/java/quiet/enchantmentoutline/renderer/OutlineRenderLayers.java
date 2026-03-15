package quiet.enchantmentoutline.renderer;

import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.OutputTarget;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import quiet.enchantmentoutline.mixin.client.RenderTypeAccessor;
import quiet.enchantmentoutline.postprocess.MaskBufferManager;

/**
 * 职责描述: 定义附魔描边所需的特殊渲染层。
 * 交互映射: 供 OutlineVertexConsumers 调用，用于获取掩码渲染层。
 */
public class OutlineRenderLayers {

    public static final OutputTarget ENCHANTMENT_MASK_TARGET = new OutputTarget(
            "enchantment_mask",
            () -> MaskBufferManager.getInstance().getMaskTarget()
    );

    public static final RenderType MASK_LAYER = RenderTypeAccessor.callCreate(
            "enchantment_mask",
            RenderSetup.builder(RenderPipelines.ENTITY_TRANSLUCENT)
                    .withTexture("Sampler0", ItemRenderer.ENCHANTED_GLINT_ITEM)
                    .withTexture("Sampler1", ItemRenderer.ENCHANTED_GLINT_ITEM) // 填充 Sampler1 需求
                    .setOutputTarget(ENCHANTMENT_MASK_TARGET)
                    .createRenderSetup()
    );
}
