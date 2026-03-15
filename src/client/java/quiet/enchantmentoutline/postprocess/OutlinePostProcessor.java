package quiet.enchantmentoutline.postprocess;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.blaze3d.textures.FilterMode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.OptionalInt;

/**
 * 职责描述: 执行附魔描边的后处理着色逻辑。
 * 交互映射: 在 GameRenderer.render 的 GUI 渲染前被调用。
 */
public class OutlinePostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Post");

    /**
     * 使用自定义屏幕四边形管线，在片元阶段比较掩码深度与主场景深度。
     */
    private static final RenderPipeline DEPTH_AWARE_OUTLINE_BLIT = RenderPipelines.register(RenderPipeline.builder()
            .withLocation(Identifier.parse("enchantment-outline:pipeline/depth_aware_outline_blit"))
            .withVertexShader("core/screenquad")
            .withFragmentShader(Identifier.parse("enchantment-outline:core/depth_aware_blit"))
            .withSampler("InSampler")
            .withSampler("MaskDepthSampler")
            .withSampler("MainDepthSampler")
            .withBlend(BlendFunction.ENTITY_OUTLINE_BLIT)
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withDepthWrite(false)
            .withColorWrite(true, false)
            .withVertexFormat(DefaultVertexFormat.EMPTY, VertexFormat.Mode.TRIANGLES)
            .build());

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
        if (source.getColorTextureView() == null || source.getDepthTextureView() == null || mainTarget.getDepthTextureView() == null) {
            LOGGER.debug("Skipping outline composite because a required texture view is missing.");
            return;
        }
        
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Enchantment Outline Post", 
                        mainTarget.getColorTextureView(), OptionalInt.empty())) {
            
            renderPass.setPipeline(DEPTH_AWARE_OUTLINE_BLIT);
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture("InSampler", source.getColorTextureView(), 
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("MaskDepthSampler", source.getDepthTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            renderPass.bindTexture("MainDepthSampler", mainTarget.getDepthTextureView(),
                    RenderSystem.getSamplerCache().getClampToEdge(FilterMode.NEAREST));
            
            renderPass.draw(0, 3);
        }
    }
}
