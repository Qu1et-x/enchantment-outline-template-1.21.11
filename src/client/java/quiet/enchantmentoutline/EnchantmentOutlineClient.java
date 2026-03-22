package quiet.enchantmentoutline;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.debug.OutlineDebugFlags;
import quiet.enchantmentoutline.runtime.command.OutlineHotReloadCommands;
import quiet.enchantmentoutline.technique.OutlineTechniqueManager;

public class EnchantmentOutlineClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Client");

	@Override
	public void onInitializeClient() {
		OutlineHotReloadCommands.register();
		OutlineTechniqueManager manager = OutlineTechniqueManager.getInstance();
		LOGGER.info("Client initialized. Active mode={}, debug(frame={}, buffer={}, preprocess={}, technique={}, submit={})",
				manager.getCurrentMode().id(),
				OutlineDebugFlags.FRAME,
				OutlineDebugFlags.BUFFER,
				OutlineDebugFlags.PREPROCESS,
				OutlineDebugFlags.TECHNIQUE,
				OutlineDebugFlags.SUBMIT);
	}
}