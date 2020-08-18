package io.github.cottonmc.libcd;

import com.google.gson.Gson;
import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.LibCDInitializer;
import io.github.cottonmc.libcd.api.advancement.AdvancementRewardsManager;
import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.api.init.AdvancementInitializer;
import io.github.cottonmc.libcd.api.init.ConditionInitializer;
import io.github.cottonmc.libcd.loot.DefaultedTagEntrySerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.*;

public class LibCD implements ModInitializer {
	public static final String MODID = "libcd";

	public static CDConfig config;

	public static boolean isDevMode() {
		return config.dev_mode;
	}

	@Override
	public void onInitialize() {
		config = loadConfig();
		FabricLoader.getInstance().getEntrypoints(MODID + ":conditions", ConditionInitializer.class).forEach(init -> init.initConditions(ConditionManager.INSTANCE));
		FabricLoader.getInstance().getEntrypoints(MODID + ":advancement_rewards", AdvancementInitializer.class).forEach(init -> init.initAdvancementRewards(AdvancementRewardsManager.INSTANCE));
		FabricLoader.getInstance().getEntrypoints(MODID, LibCDInitializer.class).forEach(init -> {
			init.initConditions(ConditionManager.INSTANCE);
			init.initAdvancementRewards(AdvancementRewardsManager.INSTANCE);
		});
		Registry.register(Registry.LOOT_POOL_ENTRY_TYPE, new Identifier(LibCD.MODID, "defaulted_tag"), new LootPoolEntryType(new DefaultedTagEntrySerializer()));
	}

	public static CDConfig loadConfig() {
		try {
			Gson gson = CDCommons.newGson();
			File file = FabricLoader.getInstance().getConfigDir().resolve("libcd.json").toFile();
			if (!file.exists()) saveConfig(new CDConfig());
			FileReader reader = new FileReader(file);
			return(gson.fromJson(reader, CDConfig.class));
			//TODO: delta thing? probs not lol
		} catch (IOException e) {
			CDCommons.logger.error("Error loading config: {}", e.getMessage());
		}

		return new CDConfig();
	}

	public static void saveConfig(CDConfig config) {
		try {
			File file = FabricLoader.getInstance().getConfigDir().resolve("libcd.json5").toFile();
			String result = CDCommons.newGson().toJson(config);
			if (!file.exists()) file.createNewFile();
			FileOutputStream out = new FileOutputStream(file,false);
			out.write(result.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			CDCommons.logger.error("Error saving config: {}", e.getMessage());
		}
	}
}
