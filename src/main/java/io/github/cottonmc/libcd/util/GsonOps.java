package io.github.cottonmc.libcd.util;

import com.google.gson.JsonElement;
import com.mojang.datafixers.types.JsonOps;

import java.util.Optional;

/**
 * A version of JsonOps that has the patch from Mojang/DataFixerUpper#42.
 */
public class GsonOps extends JsonOps {
    public static final GsonOps INSTANCE = new GsonOps();

    protected GsonOps() {}

    @Override
    public Optional<Number> getNumberValue(final JsonElement input) {
        if (input.isJsonPrimitive() && input.getAsJsonPrimitive().isBoolean()) {
            return Optional.of(input.getAsBoolean() ? 1 : 0);
        }
        return super.getNumberValue(input);
    }
}
