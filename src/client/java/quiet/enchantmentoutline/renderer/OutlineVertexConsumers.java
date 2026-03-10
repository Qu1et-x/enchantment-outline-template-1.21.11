package quiet.enchantmentoutline.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 职责描述: 作为 VertexConsumer 的透明代理，将渲染指令同步分发给原始消费者和掩码消费者。
 * 交互映射: 由 ItemRenderer 注入，用于拦截附魔渲染流程。
 */
public class OutlineVertexConsumers implements VertexConsumer {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-VertexProxy");
    private final VertexConsumer original;
    private final VertexConsumer mask;

    public OutlineVertexConsumers(VertexConsumer original, VertexConsumer mask) {
        this.original = original;
        this.mask = mask;
        LOGGER.debug("Created new OutlineVertexConsumers proxy: original={}, mask={}", original, mask);
    }

    @Override
    public VertexConsumer addVertex(float f, float g, float h) {
        original.addVertex(f, g, h);
        mask.addVertex(f, g, h);
        return this;
    }

    @Override
    public VertexConsumer setColor(int i, int j, int k, int l) {
        original.setColor(i, j, k, l);
        mask.setColor(i, j, k, l);
        return this;
    }

    @Override
    public VertexConsumer setColor(int i) {
        original.setColor(i);
        mask.setColor(i);
        return this;
    }

    @Override
    public VertexConsumer setUv(float f, float g) {
        original.setUv(f, g);
        mask.setUv(f, g);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int i, int j) {
        original.setUv1(i, j);
        mask.setUv1(i, j);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int i, int j) {
        original.setUv2(i, j);
        mask.setUv2(i, j);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float f, float g, float h) {
        original.setNormal(f, g, h);
        mask.setNormal(f, g, h);
        return this;
    }

    @Override
    public VertexConsumer setLineWidth(float f) {
        original.setLineWidth(f);
        mask.setLineWidth(f);
        return this;
    }

    @Override
    public void putBulkData(PoseStack.Pose pose, BakedQuad bakedQuad, float[] fs, float f, float g, float h, float i, int[] is, int j) {
        original.putBulkData(pose, bakedQuad, fs, f, g, h, i, is, j);
        mask.putBulkData(pose, bakedQuad, fs, f, g, h, i, is, j);
    }
}
