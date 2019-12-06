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
	private Map<Tweaker, String> tweakerNames = new HashMap<>();
	private Map<String, Function<Identifier, Object>> assistants = new HashMap<>();
	private Map<Identifier, TweakerStackFactory> factories = new HashMap<>();

	private Map<String, Function<Identifier, Object>> legacyAssistants = new HashMap<>();

	/**
	 * Add a new tweaker to store data in.
	 * @param name A name to pass to `libcd.require`. Names shared with addAssistant(Factory). Namespace with package notation, ex. `libcd.util.TweakerUtils`
	 * @param tweaker An instanceof Tweaker to call whenever reloading.
	 * For deprecation purposes, the final part of the package-notated name will be passed directly to the script on its own.
	 */
	public void addTweaker(String name, Tweaker tweaker) {
		tweakers.add(tweaker);
		tweakerNames.put(tweaker, name);
		assistants.put(name, (id) -> {
			tweaker.prepareFor(id);
			return tweaker;
		});
		String[] split = name.split("\\.");
		legacyAssistants.put(split[split.length - 1], id -> {
			tweaker.prepareFor(id);
			return tweaker;
		});
	}

	/**
	 * Add a new assistant class for tweakers to access through `libcd.require`.
	 * DO NOT PASS TWEAKER INSTANCES HERE. They are automatically added in addTweaker.
	 * @param name A name to pass to `libcd.require`. Names shared with addTweaker and addAssistantFactory. Namespace with package notation, ex. `libcd.util.TweakerUtils`
	 * @param assistant An object of a class to use in scripts.
	 */
	public void addAssistant(String name, Object assistant) {
		assistants.put(name, id -> assistant);
		String[] split = name.split("\\.");
		legacyAssistants.put(split[split.length - 1], id -> assistant);
	}

	/**
	 * Add a factory for assistants which have methods affected by script ID.
	 * @param name A name to pass to `require`. Names shared with addTweaker and addAssistant. Namespace with package notation, x. `libcd.util.TweakerUtils`
	 * @param factory A function that takes an identifier and returns an object of a class to use in scripts.
	 * For deprecation purposes, the final part of the package-notated name will be passed directly to the script on its own.
	 */
	public void addAssistantFactory(String name, Function<Identifier, Object> factory) {
		assistants.put(name, factory);
		String[] split = name.split("\\.");
		legacyAssistants.put(split[split.length - 1], factory);
	}

	/**
	 * Add a new legacy assistant object for tweakers to access directly. Deprecated; add via {@link TweakerManager#addAssistant} instead.
	 */
	@Deprecated
	public void addLegacyAssistant(String callName, Object assistant) {
		legacyAssistants.put(callName, id -> assistant);
	}

	/**
	 * Add a factory for legacy assistants which have methods affected by script ID. Deprecated; add via {@link TweakerManager#addAssistantFactory} instead.
	 */
	@Deprecated
	public void addLegacyAssistantFactory(String callName, Function<Identifier, Object> factory) {
		legacyAssistants.put(callName, factory);
	}

	public void addStackFactory(Identifier id, TweakerStackFactory getter) {
		factories.put(id, getter);
	}

	public List<Tweaker> getTweakers() {
		return tweakers;
	}

	public Object getAssistant(String name, Identifier scriptFrom) {
		return assistants.get(name).apply(scriptFrom);
	}

	public Map<String, Function<Identifier, Object>> getLegacyAssistants() {
		return legacyAssistants;
	}

	public Map<Identifier, TweakerStackFactory> getStackFactories() {
		return factories;
	}

	public String getTweakerName(Tweaker tweaker) {
		return tweakerNames.get(tweaker);
	}
}
