package io.github.cottonmc.libcd.api.tweaker.loot;

import io.github.cottonmc.libcd.api.tweaker.Tweaker;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.concurrent.Executor;

public class LootTableTweaker implements Tweaker {
	public static final LootTableTweaker INSTANCE = new LootTableTweaker();
	private LootManager manager;
	private int tableCount;
	private int poolCount;
	private Map<Identifier, LootTable> tables;
	private Map<Identifier, LootPool> pools;

	@Override
	public void prepareReload(ResourceManager manager) {

	}

	@Override
	public void applyReload(ResourceManager manager, Executor executor) {

	}

	@Override
	public String getApplyMessage() {
		return null;
	}

	@Override
	public void prepareFor(Identifier scriptId) {

	}

	public void setLootTable(String id, LootTable table) {

	}

	public void addPoolToTable(String id, LootPool pool) {

	}

	public void addDropToTable(String id, String item) {

	}
}
