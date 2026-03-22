package quiet.enchantmentoutline.runtime.rendering;

import net.minecraft.world.item.ItemDisplayContext;

/**
 * Central place for deciding whether enchanted submissions write mask data
 * and which branch should receive the write.
 */
public final class MaskRoutingPolicy {
    private MaskRoutingPolicy() {
    }

    public static boolean shouldWriteMask(ItemDisplayContext context) {
        return context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_LEFT_HAND
                || context == ItemDisplayContext.THIRD_PERSON_RIGHT_HAND
                || context == ItemDisplayContext.GROUND
                // Some special-model submit paths can temporarily lose display context.
                // Treat NONE as world-branch fallback instead of dropping the mask write.
                || context == ItemDisplayContext.NONE;
    }

    public static OutlineMaskBranch resolveBranch(ItemDisplayContext context) {
        return OutlineMaskBranch.fromDisplayContext(context);
    }
}

