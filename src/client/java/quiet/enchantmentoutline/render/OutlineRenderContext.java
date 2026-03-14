package quiet.enchantmentoutline.render;

import net.minecraft.world.item.ItemDisplayContext;

/**
 * 职责描述: 在渲染线程内传递当前物品显示上下文，供特殊模型掩码判定使用。
 * 交互映射: 由 ItemStackRenderStateMixin 写入，SubmitNodeCollectionMixin 读取。
 */
public final class OutlineRenderContext {
    private static final ThreadLocal<ItemDisplayContext> CURRENT =
            ThreadLocal.withInitial(() -> ItemDisplayContext.NONE);

    private OutlineRenderContext() {
    }

    public static void push(ItemDisplayContext context) {
        CURRENT.set(context);
    }

    public static ItemDisplayContext current() {
        return CURRENT.get();
    }

    public static void clear() {
        CURRENT.remove();
    }
}
