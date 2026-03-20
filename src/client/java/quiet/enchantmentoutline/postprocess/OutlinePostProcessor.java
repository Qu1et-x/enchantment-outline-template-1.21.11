package quiet.enchantmentoutline.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.technique.OutlineTechniqueContext;
import quiet.enchantmentoutline.technique.OutlineTechniqueManager;
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;

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

    public void setTechniqueMode(OutlineTechniqueMode mode) {
        OutlineTechniqueManager.getInstance().setMode(mode);
    }

    public void setTechniqueMode(String modeText) {
        OutlineTechniqueManager.getInstance().setMode(modeText);
    }

    public void process() {
        RenderSystem.assertOnRenderThread();
        // 确保所有代理写入的数据都已提交到渲染目标
        MaskBufferManager.getInstance().drawAndFlush();
        
        RenderTarget maskTarget = MaskBufferManager.getInstance().getMaskTarget();
        RenderTarget sceneDepthTarget = MaskBufferManager.getInstance().getSceneDepthTarget();
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        OutlineTechniqueManager techniqueManager = OutlineTechniqueManager.getInstance();

        if (processLogCount < 12) {
            processLogCount++;
            LOGGER.info("Outline process #{}, mode={}, mask={}x{}",
                    processLogCount,
                    techniqueManager.getCurrentMode().id(),
                    maskTarget.width,
                    maskTarget.height);
        }
        
        // 后处理入口只负责分发，具体算法由 technique 子模块实现。
        if (maskTarget != null && maskTarget.getColorTextureView() != null) {
            OutlineTechniqueContext context = new OutlineTechniqueContext(mainTarget, maskTarget, sceneDepthTarget);
            techniqueManager.process(context);
        } else if (skipLogCount < 12) {
            skipLogCount++;
            LOGGER.info("Outline process skipped: mask target or color view missing ({}/12)", skipLogCount);
        }
    }
}
