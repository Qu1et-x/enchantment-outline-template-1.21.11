package quiet.enchantmentoutline.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.OptionalDouble;
import java.util.OptionalInt;

/**
 * 职责描述: 管理独立的 RenderTarget 和相应的 MultiBufferSource。
 * 交互映射: 提供独立的渲染源，并支持从主渲染目标同步深度缓冲区。
 */
public class MaskBufferManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Buffer");
    private static MaskBufferManager instance;
    private RenderTarget maskTarget;
    private MultiBufferSource.BufferSource maskBufferSource;
    private ByteBufferBuilder byteBufferBuilder;

    private MaskBufferManager() {}

    public static MaskBufferManager getInstance() {
        if (instance == null) {
            instance = new MaskBufferManager();
        }
        return instance;
    }

    public RenderTarget getMaskTarget() {
        if (maskTarget == null) {
            init(Minecraft.getInstance().getWindow().getWidth(), Minecraft.getInstance().getWindow().getHeight());
        }
        return maskTarget;
    }

    public MultiBufferSource getMaskBufferSource() {
        if (maskBufferSource == null) {
            if (byteBufferBuilder == null) {
                byteBufferBuilder = new ByteBufferBuilder(1536);
            }
            maskBufferSource = MultiBufferSource.immediate(byteBufferBuilder);
        }
        return maskBufferSource;
    }

    public void init(int width, int height) {
        RenderSystem.assertOnRenderThread();
        if (maskTarget != null) {
            maskTarget.destroyBuffers();
        }
        LOGGER.info("Initializing mask buffer: {}x{}", width, height);
        maskTarget = new TextureTarget("Enchantment Mask", width, height, true);
    }

    /**
     * 准备掩码缓冲区：清空颜色并从主目标同步深度，确保遮挡关系正确。
     */
    public void prepare(RenderTarget mainTarget) {
        RenderSystem.assertOnRenderThread();
        RenderTarget target = getMaskTarget();
        
        // 清空掩码缓冲区的颜色
        try (RenderPass renderPass = RenderSystem.getDevice()
                .createCommandEncoder()
                .createRenderPass(() -> "Clear Ench Mask Color", 
                        target.getColorTextureView(), OptionalInt.of(0))) {
        }
        
        // 从主渲染目标拷贝深度数据，这样掩码渲染就能识别遮挡
        target.copyDepthFrom(mainTarget);
    }

    public void drawAndFlush() {
        if (maskBufferSource != null) {
            maskBufferSource.endBatch();
        }
    }

    public void onRescale(int width, int height) {
        if (maskTarget != null) {
            maskTarget.resize(width, height);
            LOGGER.debug("Rescaled mask buffer to {}x{}", width, height);
        }
    }
}
