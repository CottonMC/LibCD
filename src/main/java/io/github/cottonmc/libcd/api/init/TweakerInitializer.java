package io.github.cottonmc.libcd.api.init;

import io.github.cottonmc.libcd.api.tweaker.TweakerManager;

public interface TweakerInitializer {
	/**
	 * Register tweakers and assistant scripts.
	 * @param manager The tweaker manager to register in.
	 */
	void initTweakers(TweakerManager manager);
}
