package io.github.cottonmc.libcd.api.tweaker.loot;

import com.google.gson.JsonObject;

public class Entries {
	public static final Entries INSTANCE = new Entries();

	public MutableLootEntry item(String name) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:item");
		json.addProperty("name", name);
		return new MutableLootEntry(json);
	}

	public MutableLootEntry tag(String name) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:tag");
		json.addProperty("name", name);
		return new MutableLootEntry(json);
	}

	public MutableLootEntry table(String name) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:loot_table");
		json.addProperty("name", name);
		return new MutableLootEntry(json);
	}

	public MutableLootEntry dynamic(String name) {
		JsonObject json = new JsonObject();
		json.addProperty("type", "minecraft:dynamic");
		json.addProperty("name", name);
		return new MutableLootEntry(json);
	}
}
