package io.github.cottonmc.libcd.api.condition;

import blue.endless.jankson.*;
import blue.endless.jankson.api.SyntaxError;
import io.github.cottonmc.libcd.LibCD;
import io.github.cottonmc.libcd.api.CDSyntaxError;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class ConditionalData {
	static final Map<Identifier, Condition> conditions = new HashMap<>();

	public static boolean shouldLoad(Identifier resourceId, String meta) {
		try {
			JsonObject json = LibCD.newJankson().load(meta);
			JsonElement elem = json.get("when");
			if (elem instanceof JsonArray) {
				JsonArray array = (JsonArray)elem;
				for (JsonElement condition : array) {
					if (!(condition instanceof JsonObject)) {
						LibCD.logger.error("Error parsing meta for %s: item %s in condition list not a JsonObject", resourceId, condition.toString());
						return false;
					}
					JsonObject obj = (JsonObject)condition;
					for (String key : obj.keySet()) {
						Identifier id = key.equals("or")? new Identifier(LibCD.MODID, "or") : new Identifier(key);
						try {
							if (!testCondition(id, parseElement(obj.get(key)))) return false;
						} catch (CDSyntaxError e) {
							LibCD.logger.error("Error parsing meta for %s: %s", resourceId, e.getMessage());
						}
					}
				}
			} else if (elem == null) {
				LibCD.logger.error("Error parsing meta for %s: primary \"when\" key does not exist", resourceId);
				return false;
			} else {
				LibCD.logger.error("Error parsing meta for %s: primary \"when\" key is not a JsonArray", resourceId);
				return false;
			}
			return true;
		} catch (SyntaxError e) {
			LibCD.logger.error("Error parsing meta for %s: %s", resourceId, e.getLineMessage());
		}
		return false;
	}

	@Nullable
	public static Object parseElement(JsonElement element) {
		if (element instanceof JsonPrimitive) {
			return ((JsonPrimitive)element).getValue();
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
