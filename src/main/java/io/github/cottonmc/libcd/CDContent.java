package io.github.cottonmc.libcd;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.CDSyntaxError;
import io.github.cottonmc.libcd.api.LibCDInitializer;
import io.github.cottonmc.libcd.api.advancement.AdvancementRewardsManager;
import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.api.condition.ConditionalData;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;
import java.util.Map;

public class CDContent implements LibCDInitializer {

	@Override
	public void initConditions(ConditionManager manager) {
		manager.registerCondition(new Identifier(CDCommons.MODID, "mod_loaded"), value -> {
			if (value instanceof String) return FabricLoader.getInstance().isModLoaded((String) value);
			if (value instanceof List) {
				//noinspection unchecked
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) return false;
					String name = el.getAsString();
					if (!FabricLoader.getInstance().isModLoaded(name)) return false;
				}
				return true;
			}
			throw new CDSyntaxError("mod_loaded must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "item_exists"), value -> {
			if (value instanceof String) return Registry.ITEM.get(new Identifier((String)value)) != Items.AIR;
			if (value instanceof List) {
				//noinspection unchecked
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) throw new CDSyntaxError("item_exists array must only contain Strings!");
					String name = el.getAsString();
					if (Registry.ITEM.get(new Identifier(name)) == Items.AIR) return false;
				}
				return true;
			}
			throw new CDSyntaxError("item_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "item_tag_exists"), value -> {
			if (value instanceof String) return ServerTagManagerHolder.getTagManager().getItems().getTagIds().contains(new Identifier((String)value));
			if (value instanceof List) {
				//noinspection unchecked
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) throw new CDSyntaxError("item_tag_exists array must only contain Strings!");
					String name = el.getAsString();
					Identifier id = new Identifier(name);
					if (!ServerTagManagerHolder.getTagManager().getItems().getTagIds().contains(id)) return false;
					if (ServerTagManagerHolder.getTagManager().getItems().getTagOrEmpty(id).values().isEmpty()) return false;
				}
				return true;
			}
			throw new CDSyntaxError("item_tag_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "block_exists"), value -> {
			if (value instanceof String) return Registry.BLOCK.get(new Identifier((String)value)) != Blocks.AIR;
			if (value instanceof List) {
				//noinspection unchecked
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) throw new CDSyntaxError("block_exists array must only contain Strings!");
					String name = el.getAsString();
					if (Registry.BLOCK.get(new Identifier(name)) == Blocks.AIR) return false;
				}
				return true;
			}
			throw new CDSyntaxError("block_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "block_tag_exists"), value -> {
			if (value instanceof String) return ServerTagManagerHolder.getTagManager().getBlocks().getTagIds().contains(new Identifier((String)value));
			if (value instanceof List) {
				//noinspection unchecked
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) throw new CDSyntaxError("block_tag_exists array must only contain Strings!");
					String name = (el).getAsString();
					Identifier id = new Identifier(name);
					if (!ServerTagManagerHolder.getTagManager().getBlocks().getTagIds().contains(id)) return false;
					if (ServerTagManagerHolder.getTagManager().getBlocks().getTagOrEmpty(id).values().isEmpty()) return false;
				}
				return true;
			}
			throw new CDSyntaxError("block_tag_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "not"), value -> {
			if (value instanceof JsonObject) {
				JsonObject json = (JsonObject)value;
				for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
					String key = entry.getKey();
					Identifier id = new Identifier(key);
					Object result = ConditionalData.parseElement(json.get(key));
					if (ConditionalData.hasCondition(id)) {
						return !ConditionalData.testCondition(id, result);
					} else return false;
				}
			}
			throw new CDSyntaxError("not must accept an Object!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "none"), value -> {
			if (value instanceof JsonArray) {
				JsonArray json = (JsonArray) value;
				for (JsonElement elem : json) {
					if (elem instanceof JsonObject) {
						JsonObject obj = (JsonObject) elem;
						for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
							String key = entry.getKey();
							if (ConditionalData.testCondition(new Identifier(key), ConditionalData.parseElement(obj.get(key)))) return false;
						}
					}
				}
				return true;
			}
			throw new CDSyntaxError("none must accept an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "or"), value -> {
			if (value instanceof JsonArray) {
				JsonArray json = (JsonArray) value;
				for (JsonElement elem : json) {
					if (elem instanceof JsonObject) {
						JsonObject obj = (JsonObject) elem;
						for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
							String key = entry.getKey();
							if (ConditionalData.testCondition(new Identifier(key), ConditionalData.parseElement(obj.get(key)))) return true;
						}
					}
				}
			}
			throw new CDSyntaxError("or must accept an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "xor"), value -> {
			if (value instanceof JsonArray) {
				JsonArray json = (JsonArray) value;
				boolean ret = false;
				for (JsonElement elem : json) {
					if (elem instanceof JsonObject) {
						JsonObject obj = (JsonObject) elem;
						for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
							String key = entry.getKey();
							if (ConditionalData.testCondition(new Identifier(key), ConditionalData.parseElement(obj.get(key)))) {
								if(ret) return false;
								else ret = true;
							}
						}
					}
				}
				return ret;
			}
			throw new CDSyntaxError("xor must accept an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "dev_mode"), value -> {
			if (value instanceof Boolean) return (Boolean)value == LibCD.isDevMode();
			throw new CDSyntaxError("dev_mode must accept a Boolean!");
		});
	}

	@Override
	public void initAdvancementRewards(AdvancementRewardsManager manager) {
		//no-op
	}
}
