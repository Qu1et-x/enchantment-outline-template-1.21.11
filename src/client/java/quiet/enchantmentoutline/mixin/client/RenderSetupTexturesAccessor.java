package quiet.enchantmentoutline.mixin.client;

import net.minecraft.client.renderer.rendertype.RenderSetup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RenderSetup.class)
public interface RenderSetupTexturesAccessor {
    @Accessor("textures")
    Map<String, ?> enchantmentOutline$getTextures();
}

