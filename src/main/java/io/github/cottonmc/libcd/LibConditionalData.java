package io.github.cottonmc.libcd;

import blue.endless.jankson.*;
import blue.endless.jankson.impl.SyntaxError;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class LibConditionalData implements ModInitializer {
	public static final String MODID = "libcd";

	private static final Logger LOGGER = LogManager.getLogger();
	private static final Map<Identifier, Predicate<Object>> conditions = new HashMap<>();

	@Override
	public void onInitialize() {
		registerCondition(new Identifier(MODID, "mod_loaded"), (value) -> value instanceof String && FabricLoader.getInstance().isModLoaded((String)value));
		registerCondition(new Identifier(MODID, "item_exists"), (value) -> value instanceof String && Registry.ITEM.get(new Identifier((String)value)) != Items.AIR);
		registerCondition(new Identifier(MODID, "not"), (value) -> {
			if (value instanceof JsonObject) {
				JsonObject json = (JsonObject)value;
				for (String key : json.keySet()) {
					Identifier id = new Identifier(key);
					Object result = parseElement(json.get(key));
					if (conditions.containsKey(id)) {
						return !conditions.get(id).test(result);
					} else return false;
				}
			}
			return false;
		});
		registerCondition(new Identifier(MODID, "any_of"), (value) -> {
			if (value instanceof JsonObject) {
				JsonObject json = (JsonObject)value;
				for (String key : json.keySet()) {
					Identifier id = new Identifier(key);
					Object result = parseElement(json.get(key));
					if (conditions.containsKey(id)) {
						if (conditions.get(id).test(result)) return true;
					} else return false;
				}
			}
			return false;
		});
	}

	public static boolean shouldLoad(Identifier resourceId, String meta) {
		Jankson jankson = new Jankson.Builder().build();
		try {
			JsonObject json = jankson.load(meta);
			for (String key : json.keySet()) {
				Identifier id = new Identifier(key);
				Object result = parseElement(json.get(key));
				if (conditions.containsKey(id)) {
					if (!conditions.get(id).test(result)) return false;
				} else {
					LOGGER.error("Error parsing meta for {}: could not find condition {}", resourceId, id.toString());
					return false;
				}
			}
			return true;
		} catch (SyntaxError e) {
			LOGGER.error("Error parsing meta for {}: {}", resourceId, e.getLineMessage());
		}
		return false;
	}

	@Nullable
	private static Object parseElement(JsonElement element) {
		if (element instanceof JsonPrimitive) {
			return ((JsonPrimitive)element).getValue();
		} else if (element instanceof JsonNull) {
			return null;
		} else if (element instanceof JsonArray) {
			return new ArrayList<>((JsonArray)element);
		} else {
			return element;
		}
	}

	/**
	 * Register a condition that recipes can use as a requirement for loading.
	 * @param id The Id of the condition, namespaced to prevent duplicates.
	 * @param condition What must be true for the condition to be met.
	 */
	public static void registerCondition(Identifier id, Predicate<Object> condition) {
		conditions.put(id, condition);
	}
}
