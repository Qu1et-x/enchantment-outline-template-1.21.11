package quiet.enchantmentoutline.acquire.rawmask;

import com.mojang.blaze3d.pipeline.RenderTarget;
import quiet.enchantmentoutline.runtime.buffer.MaskBufferManager;

/**
 * 职责描述: 获取原始附魔掩码及其 hollow 输出目标。
 * 交互映射: 对接 MaskBufferManager，供 acquire 流水线采集掩码相关原始数据。
 */
public final class RawMaskAcquireStep {
    public RenderTarget rawMaskTarget() {
        return MaskBufferManager.getInstance().getMaskTarget();
    }

    public RenderTarget hollowMaskTarget() {
        return MaskBufferManager.getInstance().getHollowMaskTarget();
    }
}

