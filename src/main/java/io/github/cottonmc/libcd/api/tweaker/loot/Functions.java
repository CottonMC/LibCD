package io.github.cottonmc.libcd.api.tweaker.loot;

import com.google.gson.JsonParser;
import io.github.cottonmc.libcd.api.util.Gsons;
import net.minecraft.loot.BinomialLootTableRange;
import net.minecraft.loot.ConstantLootTableRange;
import net.minecraft.loot.UniformLootTableRange;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.SetCountLootFunction;

public class Functions {
	public static final Functions INSTANCE = new Functions();
	private JsonParser parser = new JsonParser();

	public LootFunction parse(String json) {
		return Gsons.LOOT_TABLE.fromJson(parser.parse(json), LootFunction.class);
	}

	public LootFunction countExact(int amount) {
		return SetCountLootFunction.builder(ConstantLootTableRange.create(amount)).build();
	}

	public LootFunction countRange(int min, int max) {
		return SetCountLootFunction.builder(UniformLootTableRange.between(min, max)).build();
	}

	public LootFunction countBinomial(int n, float p) {
		return SetCountLootFunction.builder(BinomialLootTableRange.create(n, p)).build();
	}
}
