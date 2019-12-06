package io.github.cottonmc.libcd.api.tweaker.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cottonmc.libcd.api.util.Gsons;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.util.JsonHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MutableLootPool {
    private JsonObject poolJson;

    public MutableLootPool(LootPool pool) {
        this((JsonObject)Gsons.PARSER.parse(Gsons.LOOT_TABLE.toJson(pool)));
    }

    public MutableLootPool(JsonObject json) {
        this.poolJson = json;
    }

    //TODO: mutable entries?

    public void removeEntry(String type, String name) {
        List<JsonElement> toRemove = new ArrayList<>();
        for (JsonElement elem : getEntries()) {
            if (elem instanceof JsonObject) {
                JsonObject obj = (JsonObject)elem;
                String objType = JsonHelper.getString(obj, "type", "");
                String objName = JsonHelper.getString(obj, "name", "");
                if (objType.equals(type) && objName.equals(name)) toRemove.add(elem);
            }
        }
        for (JsonElement elem : toRemove) {
            getEntries().remove(elem);
        }
    }

    public void addLeafEntry(String type, String name, int weight, int quality, LootFunction[] functions, LootCondition[] conditions) {
        addLeafEntry(type, name, weight, quality, functions, conditions, new JsonObject());
    }

    public void addLeafEntry(String type, String name, int weight, int quality, LootFunction[] functions, LootCondition[] conditions, String extra) {
        addLeafEntry(type, name, weight, quality, functions, conditions, (JsonObject)Gsons.PARSER.parse(extra));
    }

    public void addLeafEntry(String type, String name, int weight, int quality, LootFunction[] functions, LootCondition[] conditions, JsonObject extra) {
        JsonObject entry = new JsonObject();
        entry.addProperty("type", type);
        entry.addProperty("name", name);
        entry.addProperty("weight", weight);
        entry.addProperty("quality", quality);
        if (functions.length != 0) {
            JsonArray funs = new JsonArray();
            for (LootFunction fun : functions) {
                funs.add(Gsons.PARSER.parse(Gsons.LOOT_TABLE.toJson(fun)));
            }
            entry.add("functions", funs);
        }
        if (conditions.length != 0) {
            JsonArray cons = new JsonArray();
            for (LootCondition con : conditions) {
                cons.add(Gsons.PARSER.parse(Gsons.LOOT_TABLE.toJson(con)));
            }
            entry.add("conditions", cons);
        }
        for (Map.Entry<String, JsonElement> exEntry : extra.entrySet()) {
            entry.add(exEntry.getKey(), exEntry.getValue());
        }
        getEntries().add(entry);
    }

    public void addItemEntry(String id, int weight, int quality, LootFunction[] functions, LootCondition[] conditions) {
        addLeafEntry("minecraft:item", id, weight, quality, functions, conditions, new JsonObject());
    }

    public void addTagEntry(String id, int weight, int quality, LootFunction[] functions, LootCondition[] conditions, boolean expand) {
        JsonObject json = new JsonObject();
        json.addProperty("expand", expand);
        addLeafEntry("minecraft:tag", id, weight, quality, functions, conditions, json);
    }

    public void addTableEntry(String id, int weight, int quality, LootFunction[] functions, LootCondition[] conditions) {
        addLeafEntry("minecraft:loot_table", id, weight, quality, functions, conditions);
    }

    public void addCondition(LootCondition condition) {
        JsonElement json = Gsons.PARSER.parse(Gsons.LOOT_TABLE.toJson(condition));
        getConditions().add(json);
    }

    public void removeCondition(int index) {
        getConditions().remove(index);
    }

    private JsonArray getEntries() {
        if (!poolJson.has("entries")) {
            poolJson.add("entries", new JsonArray());
        }
        return JsonHelper.getArray(poolJson, "entries", new JsonArray());
    }

    private JsonArray getConditions() {
        if (!poolJson.has("conditions")) {
            poolJson.add("conditions", new JsonArray());
        }
        return JsonHelper.getArray(poolJson, "conditions", new JsonArray());
    }

    public LootPool get() {
        return Gsons.LOOT_TABLE.fromJson(poolJson, LootPool.class);
    }
}
