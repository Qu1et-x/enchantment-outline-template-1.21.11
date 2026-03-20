package quiet.enchantmentoutline.postprocess;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;
import quiet.enchantmentoutline.technique.OutlineTechniqueManager;
import quiet.enchantmentoutline.technique.OutlineTechniqueMode;
import quiet.enchantmentoutline.technique.context.OutlineFrameData;
import quiet.enchantmentoutline.technique.context.OutlineTechniqueInput;
import quiet.enchantmentoutline.technique.context.OutlineTechniqueSettings;

/**
 * 职责描述: 执行附魔描边的后处理着色逻辑。
 * 交互映射: 在 GameRenderer.render 的 GUI 渲染前被调用。
 */
public class OutlinePostProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Post");
    private static int processLogCount;
    private static int skipLogCount;
    private static int frameCounter;

    private static OutlinePostProcessor instance;
    private static final OutlineTechniqueSettings SHARED_SETTINGS = OutlineTechniqueSettings.builder()
            .outlineRadiusPixels(Integer.getInteger("enchantmentoutline.radius", 10))
            .alphaThreshold(parsePropertyFloat("enchantmentoutline.alphaThreshold", 0.001F))
            .depthEpsilon(parsePropertyFloat("enchantmentoutline.depthEpsilon", 0.00001F))
            .build();

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

        RenderTarget maskTarget = MaskBufferManager.getInstance().getMaskTarget();
        RenderTarget hollowMaskTarget = MaskBufferManager.getInstance().getHollowMaskTarget();
        RenderTarget sceneDepthTarget = MaskBufferManager.getInstance().getSceneDepthTarget();
        RenderTarget mainTarget = Minecraft.getInstance().getMainRenderTarget();
        OutlineTechniqueManager techniqueManager = OutlineTechniqueManager.getInstance();
        frameCounter++;

        if (OutlineDebugFlags.TECHNIQUE && processLogCount < 12) {
            processLogCount++;
            LOGGER.info("Outline process #{}, mode={}, mask={}x{}, hollowColorViewReady={}",
                    processLogCount,
                    techniqueManager.getCurrentMode().id(),
                    maskTarget.width,
                    maskTarget.height,
                    hollowMaskTarget != null && hollowMaskTarget.getColorTextureView() != null);
        }
        
        // 后处理入口只负责分发，具体算法由 technique 子模块实现。
        if (maskTarget != null && maskTarget.getColorTextureView() != null) {
            OutlineFrameData frameData = new OutlineFrameData(
                    frameCounter,
                    mainTarget.width,
                    mainTarget.height,
                    Minecraft.getInstance().level != null,
                    SHARED_SETTINGS
            );
            OutlineTechniqueInput input = new OutlineTechniqueInputBuilder()
                    .mainTarget(mainTarget)
                    .rawMaskTarget(maskTarget)
                    .hollowMaskTarget(hollowMaskTarget)
                    .sceneDepthTarget(sceneDepthTarget)
                    .frameData(frameData)
                    .build();
            if (input != null) {
                techniqueManager.process(input);
            } else if (OutlineDebugFlags.TECHNIQUE && skipLogCount < 12) {
                skipLogCount++;
                LOGGER.info("Outline process skipped: technique input not ready ({}/12)", skipLogCount);
            }
        } else if (OutlineDebugFlags.TECHNIQUE && skipLogCount < 12) {
            skipLogCount++;
            LOGGER.info("Outline process skipped: mask target or color view missing ({}/12)", skipLogCount);
        }
    }

    private static float parsePropertyFloat(String key, float fallback) {
        String raw = System.getProperty(key);
        if (raw == null) {
            return fallback;
        }

        try {
            return Float.parseFloat(raw);
        } catch (NumberFormatException ex) {
            LOGGER.warn("Invalid float property {}='{}', fallback to {}", key, raw, fallback);
            return fallback;
        }
    }
}
