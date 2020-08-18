package io.github.cottonmc.libcd.api.condition;

import com.google.gson.*;
import io.github.cottonmc.libcd.api.CDSyntaxError;
import io.github.cottonmc.libcd.api.CDCommons;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ConditionalData {
	static final Map<Identifier, Condition> conditions = new HashMap<>();

	public static boolean shouldLoad(Identifier resourceId, String meta) {
		if (conditions.isEmpty()) {
			CDCommons.logger.warn("List of conditions is empty, loading {} anyway", resourceId);
			return true;
		}
		JsonObject json = JsonHelper.deserialize(meta);
		JsonElement elem = json.get("when");
		if (elem instanceof JsonArray) {
			JsonArray array = (JsonArray)elem;
			for (JsonElement condition : array) {
				if (!(condition instanceof JsonObject)) {
					CDCommons.logger.error("Error parsing meta for{}: item {} in condition list not a JsonObject", resourceId, condition.toString());
					return false;
				}
				JsonObject obj = (JsonObject)condition;
				for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
					String key = entry.getKey();
					Identifier id = key.equals("or")? new Identifier(CDCommons.MODID, "or") : new Identifier(key);
					try {
						if (!testCondition(id, parseElement(obj.get(key)))) return false;
					} catch (CDSyntaxError e) {
						CDCommons.logger.error("Error parsing meta for {}: {}", resourceId, e.getMessage());
					}
				}
			}
		} else if (elem == null) {
			CDCommons.logger.error("Error parsing meta for {}: primary \"when\" key does not exist", resourceId);
			return false;
		} else {
			CDCommons.logger.error("Error parsing meta for {}: primary \"when\" key is not a JsonArray", resourceId);
			return false;
		}
		return true;
	}

	@Nullable
	public static Object parseElement(JsonElement element) {
		if (element instanceof JsonPrimitive) {
			JsonPrimitive prim = (JsonPrimitive) element;
			if (prim.isNumber()) return prim.getAsNumber();
			if (prim.isBoolean()) return prim.getAsBoolean();
			else return prim.getAsString();
		} else if (element instanceof JsonNull) {
			return null;
		} else {
			return element;
		}
	}

	public static boolean hasCondition(Identifier id) {
		return conditions.containsKey(id);
	}

	public static boolean testCondition(Identifier id, Object toTest) throws CDSyntaxError{
		if (!hasCondition(id)) {
			throw new CDSyntaxError("Condition " + id.toString() + "does not exist");
		}
		return conditions.get(id).test(toTest);
	}
}
