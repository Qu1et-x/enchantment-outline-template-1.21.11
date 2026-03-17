package quiet.enchantmentoutline.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.technique.OutlineTechniqueManager;

/**
 * 职责描述: 执行附魔描边的后处理着色逻辑。
 * 交互映射: 在 GameRenderer.render 的 GUI 渲染前被调用。
 */
public class OutlinePostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Post");
    private static int processLogCount;
    private static int skipLogCount;

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
        if (processLogCount < 12) {
            processLogCount++;
            LOGGER.info("Outline process #{}, mask={}x{}", processLogCount, maskTarget.width, maskTarget.height);
        }
        
        if (maskTarget != null && maskTarget.getColorTextureView() != null) {
            OutlineTechniqueManager.getInstance().process(maskTarget, MaskBufferManager.getInstance().getSceneDepthTarget());
        } else if (skipLogCount < 12) {
            skipLogCount++;
            LOGGER.info("Outline process skipped: mask target or color view missing ({}/12)", skipLogCount);
        }
    }
}
