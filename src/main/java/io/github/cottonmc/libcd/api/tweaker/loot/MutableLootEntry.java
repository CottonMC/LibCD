package io.github.cottonmc.libcd.api.tweaker.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cottonmc.libcd.api.util.Gsons;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.JsonHelper;

public class MutableLootEntry {
	private JsonObject entryJson;

	public MutableLootEntry(LootEntry entry) {
		this((JsonObject)Gsons.PARSER.parse(Gsons.LOOT_TABLE.toJson(entry)));
	}

	public MutableLootEntry(JsonObject json) {
		this.entryJson = json;
	}

	public MutableLootEntry type(String type) {
		entryJson.addProperty("type", type);
		return this;
	}

	public MutableLootEntry name(String name) {
		entryJson.addProperty("name", name);
		return this;
	}

	public MutableLootEntry children(MutableLootEntry... children) {
		for (MutableLootEntry child : children) {
			getChildren().add(child.getJson());
		}
		return this;
	}

	public MutableLootEntry weight(int weight) {
		entryJson.addProperty("weight", weight);
		return this;
	}

	public MutableLootEntry quality(int quality) {
		entryJson.addProperty("quality", quality);
		return this;
	}

	public MutableLootEntry addConditions(LootCondition... conditions) {
		for (LootCondition condition : conditions) {
			getConditions().add(Gsons.PARSER.parse(Gsons.LOOT_TABLE.toJson(condition)));
		}
		return this;
	}

	public MutableLootEntry addFunctions(LootFunction... functions) {
		for (LootFunction function : functions) {
			getFunctions().add(Gsons.PARSER.parse(Gsons.LOOT_TABLE.toJson(function)));
		}
		return this;
	}

	public MutableLootEntry property(String name, Number value) {
		entryJson.addProperty(name, value);
		return this;
	}

	public MutableLootEntry property(String name, Boolean value) {
		entryJson.addProperty(name, value);
		return this;
	}

	public MutableLootEntry property(String name, String value) {
		entryJson.addProperty(name, value);
		return this;
	}

	public MutableLootEntry parsedProperty(String name, String value) {
		entryJson.add(name, Gsons.PARSER.parse(value));
		return this;
	}

	private JsonArray getConditions() {
		if (!entryJson.has("conditions")) {
			entryJson.add("conditions", new JsonArray());
		}
		return JsonHelper.getArray(entryJson, "conditions", new JsonArray());
	}

	private JsonArray getFunctions() {
		if (!entryJson.has("functions")) {
			entryJson.add("functions", new JsonArray());
		}
		return JsonHelper.getArray(entryJson, "functions", new JsonArray());
	}

	private JsonArray getChildren() {
		if (!entryJson.has("children")) {
			entryJson.add("children", new JsonArray());
		}
		return JsonHelper.getArray(entryJson, "children", new JsonArray());
	}

	public JsonObject getJson() {
		return entryJson;
	}

	public LootEntry get() {
		return Gsons.LOOT_TABLE.fromJson(entryJson, LootEntry.class);
	}
}
