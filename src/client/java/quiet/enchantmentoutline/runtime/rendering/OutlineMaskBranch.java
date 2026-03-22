package quiet.enchantmentoutline.runtime.rendering;

import net.minecraft.world.item.ItemDisplayContext;

/**
 * Defines which mask/depth branch a rendered enchanted item belongs to.
 */
public enum OutlineMaskBranch {
    WORLD,
    FIRST_PERSON;

    public static OutlineMaskBranch fromDisplayContext(ItemDisplayContext context) {
        if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
                || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            return FIRST_PERSON;
        }
        return WORLD;
    }
}

