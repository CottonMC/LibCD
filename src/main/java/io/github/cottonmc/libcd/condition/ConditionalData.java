package io.github.cottonmc.libcd.condition;

import blue.endless.jankson.JsonElement;
import io.github.cottonmc.libcd.api.CDSyntaxError;
import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.legacy.LegacyCondition;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.function.Predicate;

@Deprecated
/**
 * Deprecated, use {@link io.github.cottonmc.libcd.api.condition.ConditionalData} instead
 */
public class ConditionalData {

	public static void registerCondition(Identifier id, Predicate<Object> pred) {
		ConditionManager.INSTANCE.registerCondition(id, new LegacyCondition(pred));
	}

	public static boolean shouldLoad(Identifier resourceId, String meta) {
		return io.github.cottonmc.libcd.api.condition.ConditionalData.shouldLoad(resourceId, meta);
	}

	@Nullable
	public static Object parseElement(JsonElement element) {
		return io.github.cottonmc.libcd.api.condition.ConditionalData.parseElement(element);
	}

	public static boolean hasCondition(Identifier id) {
		return io.github.cottonmc.libcd.api.condition.ConditionalData.hasCondition(id);
	}

	public static boolean testCondition(Identifier id, Object toTest) throws CDSyntaxError {
		return io.github.cottonmc.libcd.api.condition.ConditionalData.testCondition(id, toTest);
	}
}
