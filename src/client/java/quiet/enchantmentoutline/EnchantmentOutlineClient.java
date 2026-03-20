package quiet.enchantmentoutline;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import quiet.enchantmentoutline.technique.OutlineTechniqueManager;

public class EnchantmentOutlineClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("EnchantmentOutline-Client");

	@Override
	public void onInitializeClient() {
		OutlineTechniqueManager manager = OutlineTechniqueManager.getInstance();
		LOGGER.info("Client initialized. Active outline technique mode={}", manager.getCurrentMode().id());
	}
}