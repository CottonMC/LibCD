package io.github.cottonmc.libcd;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.CDLogger;
import io.github.cottonmc.libcd.api.CDSyntaxError;
import io.github.cottonmc.libcd.api.LibCDInitializer;
import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.api.condition.ConditionalData;
import io.github.cottonmc.libcd.api.tweaker.TweakerManager;
import io.github.cottonmc.libcd.api.tweaker.util.TweakerUtils;
import io.github.cottonmc.libcd.tweaker.RecipeTweaker;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.List;

public class CDContent implements LibCDInitializer {
	@Override
	public void initTweakers(TweakerManager manager) {
		manager.addTweaker("RecipeTweaker", RecipeTweaker.INSTANCE);
		manager.addAssistant("TweakerUtils", TweakerUtils.INSTANCE);
		manager.addAssistantFactory("log", id -> new CDLogger(id.getNamespace()));
		manager.addStackFactory(new Identifier("minecraft", "potion"), (id) -> {
			Potion potion = Potion.byId(id.toString());
			if (potion == Potions.EMPTY) return ItemStack.EMPTY;
			return PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
		});
	}

	@Override
	public void initConditions(ConditionManager manager) {
		manager.registerCondition(new Identifier(CDCommons.MODID, "mod_loaded"), (value) -> {
			if (value instanceof String) return FabricLoader.getInstance().isModLoaded((String) value);
			if (value instanceof List) {
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) return false;
					Object obj = ((JsonPrimitive)el).getValue();
					if (obj instanceof String) {
						if (!FabricLoader.getInstance().isModLoaded((String)obj)) return false;
					}  else return false;
				}
				return true;
			}
			throw new CDSyntaxError("mod_loaded must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "item_exists"), (value) -> {
			if (value instanceof String) return Registry.ITEM.get(new Identifier((String)value)) != Items.AIR;
			if (value instanceof List) {
				for (JsonElement el : (List<JsonElement>)value) {
					if (!(el instanceof JsonPrimitive)) return false;
					Object obj = ((JsonPrimitive)el).getValue();
					if (obj instanceof String) {
						if (Registry.ITEM.get(new Identifier((String)obj)) == Items.AIR) return false;
					}  else return false;
				}
				return true;
			}
			throw new CDSyntaxError("item_exists must accept either a String or an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "not"), (value) -> {
			if (value instanceof JsonObject) {
				JsonObject json = (JsonObject)value;
				for (String key : json.keySet()) {
					Identifier id = new Identifier(key);
					Object result = ConditionalData.parseElement(json.get(key));
					if (ConditionalData.hasCondition(id)) {
						return !ConditionalData.testCondition(id, result);
					} else return false;
				}
			}
			throw new CDSyntaxError("not must accept an Object!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "or"), (value) -> {
			if (value instanceof JsonArray) {
				JsonArray json = (JsonArray) value;
				for (JsonElement elem : json) {
					if (elem instanceof JsonObject) {
						JsonObject obj = (JsonObject) elem;
						for (String key : obj.keySet()) {
							if (ConditionalData.testCondition(new Identifier(key), ConditionalData.parseElement(obj.get(key)))) return true;
						}
					}
				}
			}
			throw new CDSyntaxError("or must accept an Array!");
		});
		manager.registerCondition(new Identifier(CDCommons.MODID, "dev_mode"), value -> {
			if (value instanceof Boolean) return (Boolean)value == LibCD.isDevMode();
			throw new CDSyntaxError("dev_mode must accept a Boolean!");
		});
	}
}
