package io.github.cottonmc.libcd.api.init;

import io.github.cottonmc.libcd.api.advancement.AdvancementRewardsManager;

public interface AdvancementInitializer {
	/**
	 * Register custom advancement rewards.
	 * @param manager The advancement rewards manager to register in.
	 */
	void initAdvancementRewards(AdvancementRewardsManager manager);
}
