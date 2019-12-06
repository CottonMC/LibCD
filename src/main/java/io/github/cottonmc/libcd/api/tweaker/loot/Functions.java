package io.github.cottonmc.libcd.api.tweaker.loot;

import com.google.gson.JsonParser;
import io.github.cottonmc.libcd.api.util.Gsons;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetCountLootFunction;

/**
 * A utility class to assemble loot functions from JSR-223 code.
 */
public class Functions {
	public static final Functions INSTANCE = new Functions();
	private JsonParser parser = new JsonParser();

	/**
	 * Parse Stringified JSON into a special loot condition. Useful for complex or third-party functions.
	 * @param json Stringified JSON of the condition to add.
	 * @return The parsed function, ready to add to a table or entry.
	 */
	public LootFunction parse(String json) {
		return Gsons.LOOT_TABLE.fromJson(parser.parse(json), LootFunction.class);
	}

	/**
	 * Set the exact count of items to drop.
	 * @param amount How many items should drop.
	 * @return An assembled function, ready to add to a table or entry.
	 */
	public LootFunction countExact(int amount) {
		return SetCountLootFunction.builder(ConstantLootTableRange.create(amount)).build();
	}

	/**
	 * Set a range of counts of items to drop, with uniform distribution (equal probability for any result).
	 * @param min The minimum number of items to drop.
	 * @param max The maximum number of items to drop.
	 * @return An assembled function, ready to add to a table or entry.
	 */
	public LootFunction countRange(int min, int max) {
		return SetCountLootFunction.builder(UniformLootTableRange.between(min, max)).build();
	}

	/**
	 * Set a range of counts of items to drop, with binomial distribution (a bell curve of likeliness).
	 * @param n The maximum number of items to drop.
	 * @param p The most common number of items to drop, as a float percentage of the max.
	 * @return
	 */
	public LootFunction countBinomial(int n, float p) {
		return SetCountLootFunction.builder(BinomialLootTableRange.create(n, p)).build();
	}
}
