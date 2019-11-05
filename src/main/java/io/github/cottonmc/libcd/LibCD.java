package io.github.cottonmc.libcd;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;

import io.github.cottonmc.jankson.JanksonFactory;
import io.github.cottonmc.libcd.condition.ConditionalData;
import io.github.cottonmc.libcd.command.HeldItemCommand;
import io.github.cottonmc.libcd.tweaker.*;
import io.github.cottonmc.libcd.util.CDConfig;
import io.github.cottonmc.libcd.util.TweakerLogger;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.FileOutputStream;

public class LibCD implements ModInitializer {
	public static final String MODID = "libcd";

	public static final TweakerLogger logger = new TweakerLogger();
	public static CDConfig config;

	public static Jankson newJankson() {
		return JanksonFactory.createJankson();
	}

	public static boolean isDevMode() {
		return FabricLoader.getInstance().isDevelopmentEnvironment() || config.dev_mode;
	}

	@Override
	public void onInitialize() {
		config = loadConfig();
		ConditionalData.init();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new TweakerLoader());
		Tweaker.addTweaker("RecipeTweaker", RecipeTweaker.INSTANCE);
		Tweaker.addAssistant("TweakerUtils", TweakerUtils.INSTANCE);
		Tweaker.addAssistantFactory("log", (id) -> new TweakerLogger(id.getNamespace()));
		TweakerStackGetter.registerGetter(new Identifier("minecraft", "potion"), (id) -> {
			Potion potion = Potion.byId(id.toString());
			if (potion == Potions.EMPTY) return ItemStack.EMPTY;
			return PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
		});
		CommandRegistry.INSTANCE.register(false, dispatcher -> {
			
			//New nodes
			LiteralCommandNode<ServerCommandSource> libcdNode = CommandManager
					.literal("libcd")
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
			
			//Stitch nodes together
			subsetNode.addChild(setSubsetNode);
			subsetNode.addChild(resetSubsetNode);
			libcdNode.addChild(subsetNode);
			libcdNode.addChild(heldNode);
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

	public CDConfig loadConfig() {
		try {
			Jankson jankson = newJankson();
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
		} catch (Exception e) {
			logger.error("Error loading config: {}", e.getMessage());
		}
		return new CDConfig();
	}

	public void saveConfig(CDConfig config) {
		try {
			File file = FabricLoader.getInstance().getConfigDirectory().toPath().resolve("libcd.json5").toFile();
			JsonElement json = newJankson().toJson(config);
			String result = json.toJson(true, true);
			if (!file.exists()) file.createNewFile();
			FileOutputStream out = new FileOutputStream(file,false);
			out.write(result.getBytes());
			out.flush();
			out.close();
		} catch (Exception e) {
			logger.error("Error saving config: {}", e.getMessage());
		}
	}
}
