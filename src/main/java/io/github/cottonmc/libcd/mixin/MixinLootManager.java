package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.impl.LootTableMapAccessor;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootConditionManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(LootManager.class)
public class MixinLootManager implements LootTableMapAccessor {
	@Shadow private Map<Identifier, LootTable> suppliers;

	@Shadow @Final private LootConditionManager conditionManager;

	@Override
	public Map<Identifier, LootTable> libcd$getLootTableMap() {
		return suppliers;
	}

	@Override
	public void libcd$setLootTableMap(Map<Identifier, LootTable> map) {
		this.suppliers = map;
	}

	@Override
	public LootConditionManager libcd$getConditionManager() {
		return conditionManager;
	}
}
