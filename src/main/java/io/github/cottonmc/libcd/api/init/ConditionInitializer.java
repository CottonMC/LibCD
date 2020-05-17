package io.github.cottonmc.libcd.api.init;

import io.github.cottonmc.libcd.api.condition.ConditionManager;

public interface ConditionInitializer {
	/**
	 * Register conditions for conditional data.
	 * @param manager The condition manager to register in.
	 */
	void initConditions(ConditionManager manager);
}
