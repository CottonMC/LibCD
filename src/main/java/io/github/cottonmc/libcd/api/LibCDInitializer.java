package io.github.cottonmc.libcd.api;

import io.github.cottonmc.libcd.api.advancement.AdvancementRewardsManager;
import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.api.init.AdvancementInitializer;
import io.github.cottonmc.libcd.api.init.ConditionInitializer;
import io.github.cottonmc.libcd.api.init.TweakerInitializer;
import io.github.cottonmc.libcd.api.tweaker.TweakerManager;

/**
 * Initializer that initializes tweakers, conditions, and advancement rewards all at once.
 */
public interface LibCDInitializer extends TweakerInitializer, ConditionInitializer, AdvancementInitializer {

	/**
	 * Register tweakers and assistant scripts.
	 * @param manager The tweaker manager to register in.
	 */
	@Override
	default void initTweakers(TweakerManager manager) {}

	/**
	 * Register conditions for conditional data.
	 * @param manager The condition manager to register in.
	 */
	@Override
	default void initConditions(ConditionManager manager) {}

	/**
	 * Register custom advancement rewards.
	 * @param manager The advancement rewards manager to register in.
	 */
	@Override
	default void initAdvancementRewards(AdvancementRewardsManager manager) {}
}
