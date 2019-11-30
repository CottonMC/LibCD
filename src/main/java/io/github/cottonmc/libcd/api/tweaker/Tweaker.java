package io.github.cottonmc.libcd.api.tweaker;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.concurrent.Executor;

public interface Tweaker {
	/**
	 * Called whenever the /reload command is run, before scripts are run.
	 * Use this time to empty out any lists or maps you need to.
	 * @param manager The ResourceManager reloading tweakers.
	 */
	void prepareReload(ResourceManager manager);

	/**
	 * Called whenever the /reload command is run, after scripts are run.
	 * Use this time to apply whatever you need to.
	 * @param manager The ResourceManager applying tweakers. Should be the same one called in prepareReload.
	 * @param executor The Executor applying tweakers.
	 */
	void applyReload(ResourceManager manager, Executor executor);

	/**
	 * Called after all scripts have been run, to log what tweakers have been applied.
	 * @return The number of applied tweaks and the description of what type of tweak it is, ex. "12 recipes"
	 */
	String getApplyMessage();

	/**
	 * Prepare anything needed based on the script ID, like namespaces. Called before each script is run.
	 * @param scriptId The ID of the script about to be run.
	 */
	default void prepareFor(Identifier scriptId) {}
}
