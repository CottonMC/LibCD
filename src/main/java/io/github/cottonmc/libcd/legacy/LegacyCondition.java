package io.github.cottonmc.libcd.legacy;

import io.github.cottonmc.libcd.api.CDSyntaxError;
import io.github.cottonmc.libcd.api.condition.Condition;

import java.util.function.Predicate;

public class LegacyCondition implements Condition {
	private Predicate<Object> pred;

	public LegacyCondition(Predicate<Object> pred) {
		this.pred = pred;
	}

	@Override
	public boolean test(Object toTest) throws CDSyntaxError {
		return pred.test(toTest);
	}
}
