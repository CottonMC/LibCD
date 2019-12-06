package io.github.cottonmc.libcd.api.tweaker.loot;

import blue.endless.jankson.JsonObject;
import io.github.cottonmc.libcd.api.CDLogger;
import io.github.cottonmc.libcd.api.tweaker.ScriptBridge;
import io.github.cottonmc.libcd.api.tweaker.Tweaker;
import io.github.cottonmc.libcd.impl.LootTableMapAccessor;
import io.github.cottonmc.libcd.impl.ReloadListenersAccessor;
import net.minecraft.loot.*;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Identifier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

public class LootTweaker implements Tweaker {
	public static final LootTweaker INSTANCE = new LootTweaker();
	private LootManager lootManager;
	private int tableCount;
	private Map<Identifier, MutableLootTable> tables = new HashMap<>();
	private CDLogger logger;
	private JsonObject tableDebug;

	@Override
	public void prepareReload(ResourceManager manager) {
		tableDebug = new JsonObject();
		tables.clear();
		tableCount = 0;
		if (manager instanceof ReloadListenersAccessor) {
			List<ResourceReloadListener> listeners = ((ReloadListenersAccessor)manager).libcd$getListeners();
			for (ResourceReloadListener listener : listeners) {
				if (listener instanceof LootManager) {
					this.lootManager = (LootManager)listener;
					return;
				}
			}
			logger.error("No loot manager was found! Tweaker cannot edit loot tables!");
			throw new IllegalStateException("No loot manager was found! Tweaker cannot edit loot tables!");
		}
		logger.error("No reload listeners accessor found! Tweaker cannot edit loot tables!");
		throw new IllegalStateException("No reload listeners accessor found! Tweaker cannot edit loot tables!");
	}

	@Override
	public void applyReload(ResourceManager manager, Executor executor) {
		Map<Identifier, LootTable> tableMap = new HashMap<>(((LootTableMapAccessor)lootManager).libcd$getLootTableMap());
		Map<Identifier, LootTable> toAdd = new HashMap<>();
		for (Identifier id : tables.keySet()) {
			toAdd.put(id, tables.get(id).get());
		}
		if (toAdd.containsKey(LootTables.EMPTY)) {
			toAdd.remove(LootTables.EMPTY);
			logger.error("Tried to redefine empty loot table, ignoring");
		}
		LootTableReporter reporter = new LootTableReporter(LootContextTypes.GENERIC, ((LootTableMapAccessor)lootManager).libcd$getConditionManager()::get, toAdd::get);
		toAdd.forEach((id, table) -> check(reporter, id, table));
		reporter.getMessages().forEach((context, message) -> {
			logger.error("Found validation problem in modified table %s: %s", context, message);
			Identifier id = new Identifier(context.substring(1, context.indexOf('}')));
			toAdd.remove(id);
		});
		tableCount = toAdd.size();
		tableMap.putAll(toAdd);
		((LootTableMapAccessor)lootManager).libcd$setLootTableMap(tableMap);
	}

	private void check(LootTableReporter reporter, Identifier id, LootTable table) {
		table.check(reporter.withContextType(table.getType()).withSupplier("{" + id + "}", id));
	}

	@Override
	public String getApplyMessage() {
		return tableCount + " modified loot " + (tableCount == 1? "table" : "tables");
	}

	@Override
	public void prepareFor(ScriptBridge bridge) {
		this.logger = new CDLogger(bridge.getId().getNamespace());
	}

	/**
	 * Get a new loot table, or create one if it doesn't yet exist.
	 * @param id The ID of the table to get or create.
	 * @return A modifiable form of that table.
	 */
	public MutableLootTable getTable(String id) {
		Identifier tableId = new Identifier(id);
		if (tables.containsKey(tableId)) {
			return tables.get(tableId);
		} else {
			LootTable table = lootManager.getSupplier(tableId);
			MutableLootTable mutable = new MutableLootTable(table);
			tables.put(tableId, mutable);
			return mutable;
		}
	}

	@Override
	public JsonObject getDebugInfo() {
		return tableDebug;
	}

	public CDLogger getLogger() {
		return logger;
	}
}
