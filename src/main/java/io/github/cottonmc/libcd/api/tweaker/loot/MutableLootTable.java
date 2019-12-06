package io.github.cottonmc.libcd.api.tweaker.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cottonmc.libcd.api.util.Gsons;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.JsonHelper;

public class MutableLootTable {
	private JsonObject tableJson;

	public MutableLootTable(LootTable table) {
		this((JsonObject)Gsons.PARSER.parse(Gsons.LOOT_TABLE.toJson(table)));
	}

	public MutableLootTable(JsonObject json) {
		this.tableJson = json;
	}

	public MutableLootPool getPool(int index) {
		return new MutableLootPool((JsonObject)getPools().get(index));
	}

	public MutableLootPool addPool(int rolls) {
		JsonObject json = new JsonObject();
		json.addProperty("rolls", rolls);
		getPools().add(json);
		return new MutableLootPool(json);
	}

	public MutableLootPool addPool(int minRolls, int maxRolls) {
		JsonObject json = new JsonObject();
		JsonObject rolls = new JsonObject();
		rolls.addProperty("min", minRolls);
		rolls.addProperty("max", maxRolls);
		json.add("rolls", rolls);
		getPools().add(json);
		return new MutableLootPool(json);
	}

	public MutableLootPool addPool(int minRolls, int maxRolls, int minBonusRolls, int maxBonusRolls) {
		JsonObject json = new JsonObject();
		JsonObject rolls = new JsonObject();
		JsonObject bonus = new JsonObject();
		rolls.addProperty("min", minRolls);
		rolls.addProperty("max", maxRolls);
		json.add("rolls", rolls);
		bonus.addProperty("min", minBonusRolls);
		bonus.addProperty("max", maxBonusRolls);
		json.add("bonus_rolls", rolls);
		getPools().add(json);
		return new MutableLootPool(json);
	}

	public void removePool(int index) {
		getPools().remove(index);
	}

	public void addFunction(LootFunction function) {
		getFunctions().add(Gsons.PARSER.parse(Gsons.LOOT_TABLE.toJson(function)));
	}

	public void removeFunction(int index) {
		getFunctions().remove(index);
	}

	private JsonArray getPools() {
		if (!tableJson.has("pools")) {
			tableJson.add("pools", new JsonArray());
		}
		return JsonHelper.getArray(tableJson, "pools", new JsonArray());
	}

	private JsonArray getFunctions() {
		if (!tableJson.has("functions")) {
			tableJson.add("functions", new JsonArray());
		}
		return JsonHelper.getArray(tableJson, "functions", new JsonArray());
	}

	public LootTable get() {
		return Gsons.LOOT_TABLE.fromJson(tableJson, LootTable.class);
	}
}
