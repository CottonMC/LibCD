package io.github.cottonmc.libcd.api.tweaker.loot;

import com.google.gson.JsonObject;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.cottonmc.libcd.api.util.Gsons;
import net.minecraft.loot.condition.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class Conditions {
	public static final Conditions INSTANCE = new Conditions();

	public LootCondition parse(String json) {
		return Gsons.LOOT_TABLE.fromJson(json, LootCondition.class);
	}

	public LootCondition not(LootCondition condition) {
		String json = Gsons.LOOT_TABLE.toJson(condition);
		JsonObject cond = new JsonObject();
		cond.addProperty("condition", "minecraft:inverted");
		cond.add("term", Gsons.PARSER.parse(json));
		return Gsons.LOOT_TABLE.fromJson(cond, LootCondition.class);
	}

	public LootCondition killedByPlayer() {
		return KilledByPlayerLootCondition.builder().build();
	}

	public LootCondition chance(float chance) {
		return RandomChanceLootCondition.builder(chance).build();
	}

	public LootCondition chanceWithLooting(float chance, float multiplier) {
		return RandomChanceWithLootingLootCondition.builder(chance, multiplier).build();
	}

	public LootCondition survivesExplosion() {
		return SurvivesExplosionLootCondition.builder().build();
	}

	public LootCondition matchTool(String item, String nbt) {
		ItemPredicate.Builder builder = ItemPredicate.Builder.create();
		if (item.indexOf('#') == 0) {
			Identifier id = new Identifier(item.substring(1));
			builder.tag(ItemTags.getContainer().get(id));
		} else {
			Identifier id = new Identifier(item);
			builder.item(Registry.ITEM.get(id));
		}
		if (!nbt.equals("")) {
			try {
				CompoundTag tag = StringNbtReader.parse(nbt);
				builder.nbt(tag);
			} catch (CommandSyntaxException e) {

			}

		}
		return MatchToolLootCondition.builder(builder).build();
	}
}
