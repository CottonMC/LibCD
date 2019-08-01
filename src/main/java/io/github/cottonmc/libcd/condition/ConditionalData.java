package io.github.cottonmc.libcd.condition;

import blue.endless.jankson.*;
import blue.endless.jankson.impl.SyntaxError;
import io.github.cottonmc.libcd.LibCD;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ConditionalData {
	private static final Map<Identifier, Predicate<Object>> conditions = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static void init() {
		registerCondition(new Identifier(LibCD.MODID, "mod_loaded"), (value) -> {
			if (value instanceof String) return FabricLoader.getInstance().isModLoaded((String) value);
			if (value instanceof List) {
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) return false;
					Object obj = ((JsonPrimitive)el).getValue();
					if (obj instanceof String) {
						if (!FabricLoader.getInstance().isModLoaded((String)obj)) return false;
					}  else return false;
				}
				return true;
			}
			return false;
		});
		registerCondition(new Identifier(LibCD.MODID, "item_exists"), (value) -> {
			if (value instanceof String) return Registry.ITEM.get(new Identifier((String)value)) != Items.AIR;
			if (value instanceof List) {
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) return false;
					Object obj = ((JsonPrimitive)el).getValue();
					if (obj instanceof String) {
						if (Registry.ITEM.get(new Identifier((String)obj)) == Items.AIR) return false;
					}  else return false;
				}
				return true;
			}
			return false;
		});
		registerCondition(new Identifier(LibCD.MODID, "not"), (value) -> {
			if (value instanceof JsonObject) {
				JsonObject json = (JsonObject)value;
				for (String key : json.keySet()) {
					Identifier id = new Identifier(key);
					Object result = parseElement(json.get(key));
					if (hasCondition(id)) {
						return !testCondition(id, result);
					} else return false;
				}
			}
			return false;
		});
		registerCondition(new Identifier(LibCD.MODID, "or"), (value) -> {
			if (value instanceof JsonArray) {
				JsonArray json = (JsonArray) value;
				for (JsonElement elem : json) {
					if (elem instanceof JsonObject) {
						JsonObject obj = (JsonObject) elem;
						for (String key : obj.keySet()) {
							if (!testCondition(new Identifier(key), obj)) return false;
						}
					}
				}
			}
			return false;
		});
	}

	public static boolean shouldLoad(Identifier resourceId, String meta) {
		try {
			JsonObject json = LibCD.newJankson().load(meta);
			JsonElement elem = json.get("when");
			if (elem instanceof JsonArray) {
				JsonArray array = (JsonArray)elem;
				for (JsonElement condition : array) {
					if (!(condition instanceof JsonObject)) {
						LibCD.logger.error("Error parsing meta for {}: item {} in condition list not a JsonObject", resourceId, condition.toString());
						return false;
					}
					JsonObject obj = (JsonObject)condition;
					for (String key : obj.keySet()) {
						Identifier id = key.equals("or")? new Identifier(LibCD.MODID, "or") : new Identifier(key);
						if (!testCondition(id, parseElement(obj.get(key)))) return false;
					}
				}
			}
			return true;
		} catch (SyntaxError e) {
			LibCD.logger.error("Error parsing meta for {}: {}", resourceId, e.getLineMessage());
		}
		return false;
	}

	@Nullable
	private static Object parseElement(JsonElement element) {
		if (element instanceof JsonPrimitive) {
			return ((JsonPrimitive)element).getValue();
		} else if (element instanceof JsonNull) {
			return null;
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

	public static boolean hasCondition(Identifier id) {
		return conditions.containsKey(id);
	}

	public static boolean testCondition(Identifier id, Object toTest) {
		if (!hasCondition(id)) {
			//put a log here if I can find a way to get it to trace back to which file it should check?
			return false;
		}
		return conditions.get(id).test(toTest);
	}
}
