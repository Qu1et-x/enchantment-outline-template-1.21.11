package quiet.enchantmentoutline.postprocess;

import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.OptionalInt;

/**
 * 职责描述: 执行附魔描边的后处理着色逻辑。
 * 交互映射: 在 GameRenderer.render 的 GUI 渲染前被调用。
 */
public class OutlinePostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Post");
    private static OutlinePostProcessor instance;

    private OutlinePostProcessor() {}

    public static OutlinePostProcessor getInstance() {
        if (instance == null) {
            instance = new OutlinePostProcessor();
        }
        return instance;
    }

    public void process() {
        RenderSystem.assertOnRenderThread();
        // 确保所有代理写入的数据都已提交到渲染目标
        MaskBufferManager.getInstance().drawAndFlush();
        
        RenderTarget maskTarget = MaskBufferManager.getInstance().getMaskTarget();
        
        // 简单实现：将掩码缓冲区直接 Blit 到主屏幕
        if (maskTarget != null && maskTarget.getColorTextureView() != null) {
            drawToMain(maskTarget);
        }
    }

    private void drawToMain(RenderTarget source) {
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Enchantment Outline Post", 
                        mainTarget.getColorTextureView(), OptionalInt.empty())) {
            
            renderPass.setPipeline(RenderPipelines.ENTITY_OUTLINE_BLIT);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture("InSampler", source.getColorTextureView(), 
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            
            renderPass.draw(0, 3);
        }
    }
}
