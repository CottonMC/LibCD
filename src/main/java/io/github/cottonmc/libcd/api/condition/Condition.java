package io.github.cottonmc.libcd.api.condition;

import io.github.cottonmc.libcd.api.CDSyntaxError;

public interface Condition {
	boolean test(Object toTest) throws CDSyntaxError;
}
