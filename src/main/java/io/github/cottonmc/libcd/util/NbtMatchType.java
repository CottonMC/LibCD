package io.github.cottonmc.libcd.util;

/**
 * The matching mode for nbt checking in an ingredient.
 */
public enum NbtMatchType {
	/**
	 * Nbt is not checked; any nbt will return true.
	 */
	NONE,
	/**
	 * Nbt is fuzzily checked; the input stack may have extra tags,
	 * but it must have all of the tags that the ingredient stack has,
	 * with the same values.
	 */
	FUZZY,
	/**
	 * Nbt is strictly checked; only identical nbt will return true.
	 */
	EXACT
}
