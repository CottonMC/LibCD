package io.github.cottonmc.libcd.api.tweaker;

import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class TweakerManager {
	public static final TweakerManager INSTANCE = new TweakerManager();

	private List<Tweaker> tweakers = new ArrayList<>();
	private Map<String, Function<Identifier, Object>> assistants = new HashMap<>();
	private Map<Identifier, TweakerStackFactory> factories = new HashMap<>();

	/**
	 * Add a new tweaker to store data in.
	 * @param callName A unique name to call this tweaker by in scripts. Names shared with addAssistant(Factory).
	 * @param tweaker An instanceof Tweaker to call whenever reloading.
	 */
	public void addTweaker(String callName, Tweaker tweaker) {
		tweakers.add(tweaker);
		assistants.put(callName, (id) -> {
			tweaker.prepareFor(id);
			return tweaker;
		});
	}

	/**
	 * Add a new assistant class for tweakers to access.
	 * DO NOT PASS TWEAKER INSTANCES HERE. They are automatically added in addTweaker.
	 * @param callName A unique name to call this object by in scripts. Names shared with addTweaker and addAssistantFactory.
	 * @param assistant An object of a class to use in scripts.
	 */
	public void addAssistant(String callName, Object assistant) {
		assistants.put(callName, id -> assistant);
	}

	/**
	 * Add a factory for assistants which has methods affected by Script ID.
	 * @param callName A unique name to call this object by in scripts. Names shared with addTweaker and addAssistant.
	 * @param assistant A function that takes an identifier and returns an object of a class to use in scripts.
	 */
	public void addAssistantFactory(String callName, Function<Identifier, Object> assistant) {
		assistants.put(callName, assistant);
	}

	public void addStackFactory(Identifier id, TweakerStackFactory getter) {
		factories.put(id, getter);
	}

	public List<Tweaker> getTweakers() {
		return tweakers;
	}

	public Map<String, Function<Identifier, Object>> getAssistants() {
		return assistants;
	}

	public Map<Identifier, TweakerStackFactory> getStackFactories() {
		return factories;
	}
}
