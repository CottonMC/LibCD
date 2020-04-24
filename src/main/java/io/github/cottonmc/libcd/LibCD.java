package io.github.cottonmc.libcd;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import io.github.cottonmc.libcd.api.advancement.AdvancementRewardsManager;
import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.LibCDInitializer;
import io.github.cottonmc.libcd.api.condition.ConditionManager;
import io.github.cottonmc.libcd.api.tweaker.TweakerManager;
import io.github.cottonmc.libcd.api.util.crafting.CustomSpecialRecipeSerializer;
import io.github.cottonmc.libcd.command.DebugExportCommand;
import io.github.cottonmc.libcd.command.HeldItemCommand;
import io.github.cottonmc.libcd.loader.TweakerLoader;
import io.github.cottonmc.libcd.loot.DefaultedTagEntrySerializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.loot.v1.LootEntryTypeRegistry;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.File;
import java.io.FileOutputStream;

public class LibCD implements ModInitializer {
	public static final String MODID = "libcd";

	public static CDConfig config;

	public static boolean isDevMode() {
		return config.dev_mode;
	}

	@Override
	public void onInitialize() {
		config = loadConfig();
		FabricLoader.getInstance().getEntrypoints(MODID, LibCDInitializer.class).forEach(init -> {
			init.initTweakers(TweakerManager.INSTANCE);
			init.initConditions(ConditionManager.INSTANCE);
			init.initAdvancementRewards(AdvancementRewardsManager.INSTANCE);
		});
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new TweakerLoader());
		LootEntryTypeRegistry.INSTANCE.register(new DefaultedTagEntrySerializer());
		Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(MODID, "custom_special_crafting"), CustomSpecialRecipeSerializer.INSTANCE);
		CommandRegistry.INSTANCE.register(false, dispatcher -> {
			
			//New nodes
			LiteralCommandNode<ServerCommandSource> libcdNode = CommandManager
					.literal(MODID)
					.build();

			LiteralCommandNode<ServerCommandSource> subsetNode = CommandManager
					.literal("subset")
					.requires(source -> source.hasPermissionLevel(3))
					.build();

			ArgumentCommandNode<ServerCommandSource, String> setSubsetNode = CommandManager
					.argument("subset", StringArgumentType.string())
					.executes(context -> changeSubset(context, context.getArgument("subset", String.class)))
					.build();

			LiteralCommandNode<ServerCommandSource> resetSubsetNode = CommandManager
					.literal("-reset")
					.executes(context -> changeSubset(context, ""))
					.build();
			
			LiteralCommandNode<ServerCommandSource> heldNode = CommandManager
					.literal("held")
					.executes(new HeldItemCommand())
					.build();

			LiteralCommandNode<ServerCommandSource> debugNode = CommandManager
					.literal("debug")
					.requires(source -> source.hasPermissionLevel(3))
					.build();

			LiteralCommandNode<ServerCommandSource> debugExportNode = CommandManager
					.literal("export")
					.executes(new DebugExportCommand())
					.build();

			//Stitch nodes together
			subsetNode.addChild(setSubsetNode);
			subsetNode.addChild(resetSubsetNode);
			libcdNode.addChild(subsetNode);
			libcdNode.addChild(heldNode);
			debugNode.addChild(debugExportNode);
			libcdNode.addChild(debugNode);
			dispatcher.getRoot().addChild(libcdNode);
		});
	}

	private int changeSubset(CommandContext<ServerCommandSource> context, String setTo) {
		config.tweaker_subset = setTo;
		saveConfig(config);
		context.getSource().sendFeedback(new TranslatableText("libcd.reload.success"), false);
		(context.getSource()).getMinecraftServer().reload();
		return 1;
	}

	public static CDConfig loadConfig() {
		try {
			Jankson jankson = CDCommons.newJankson();
			File file = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("libcd.json5").toFile();
			if (!file.exists()) saveConfig(new CDConfig());
			JsonObject json = jankson.load(file);
			CDConfig result =  jankson.fromJson(json, CDConfig.class);
			JsonElement jsonElementNew = jankson.toJson(new CDConfig());
			if(jsonElementNew instanceof JsonObject){
				JsonObject jsonNew = (JsonObject) jsonElementNew;
				if(json.getDelta(jsonNew).size()>= 0){
					saveConfig(result);
				}
			}
			return result;
		} catch (Exception e) {
			CDCommons.logger.error("Error loading config: {}", e.getMessage());
		}
		return new CDConfig();
	}

	public static void saveConfig(CDConfig config) {
		try {
			File file = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("libcd.json5").toFile();
			JsonElement json = CDCommons.newJankson().toJson(config);
			String result = json.toJson(true, true);
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
