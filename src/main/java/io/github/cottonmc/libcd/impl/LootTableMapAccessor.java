package io.github.cottonmc.libcd.impl;

import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface LootTableMapAccessor {
	Map<Identifier, LootTable> libcd$getLootTableMap();
	void libcd$setLootTableMap(Map<Identifier, LootTable> map);
	LootConditionManager libcd$getConditionManager();
}
