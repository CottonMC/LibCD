package io.github.cottonmc.libcd.tweaker;

import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public interface Tweaker {
	List<Tweaker> TWEAKERS = new ArrayList<>();

	/**
	 * Add a new tweaker to store data in.
	 * @param tweaker an instanceof Tweaker to call whenever reloading.
	 */
	static void addTweaker(Tweaker tweaker) {
		TWEAKERS.add(tweaker);
	}

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
}