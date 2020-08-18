package io.github.cottonmc.libcd;

import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.advancement.AdvancementRewardsManager;
import io.github.cottonmc.libcd.api.init.AdvancementInitializer;
import net.minecraft.util.Identifier;

public class DevAdvancementRewards implements AdvancementInitializer {
	@Override
	public void initAdvancementRewards(AdvancementRewardsManager manager) {
		if (LibCD.isDevMode()) {
			manager.register(
					new Identifier("libcd:without_settings"),
					(serverPlayerEntity) -> CDCommons.logger.info(
							"{} earned libcd:without_settings",
							serverPlayerEntity.getDisplayName().asString())
			);
			manager.register(
					new Identifier("libcd:with_settings"),
					(serverPlayerEntity, settings) -> CDCommons.logger.info(
							"{} earned libcd:with_settings{setting1: {}}",
							serverPlayerEntity.getDisplayName().asString(),
							settings.get("setting1").getAsNumber())
			);
		}
	}
}
