package io.github.cottonmc.libcd;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.github.cottonmc.jankson.JanksonFactory;
import io.github.cottonmc.libcd.condition.ConditionalData;
import io.github.cottonmc.libcd.impl.MatchTypeSetter;
import io.github.cottonmc.libcd.tweaker.*;
import io.github.cottonmc.libcd.tweaker.preparse.LiteralParser;
import io.github.cottonmc.libcd.util.CDConfig;
import io.github.cottonmc.libcd.util.NbtMatchType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.CommandRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.recipe.Ingredient;
import net.minecraft.resource.ResourceType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.tag.Tag;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class LibCD implements ModInitializer {
	public static final String MODID = "libcd";

	public static final Logger logger = LogManager.getLogger();
	public static CDConfig config;
	public static final Jankson jankson = JanksonFactory.createJankson();

	@Override
	public void onInitialize() {
		config = loadConfig();
		ConditionalData.init();
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new TweakerLoader());
		Tweaker.addTweaker(RecipeTweaker.INSTANCE);
		TweakerStackGetter.registerGetter(new Identifier("minecraft", "potion"), (id) -> {
			Potion potion = Potion.byId(id.toString());
			if (potion == Potions.EMPTY) return ItemStack.EMPTY;
			return PotionUtil.setPotion(new ItemStack(Items.POTION), potion);
		});
		CommandRegistry.INSTANCE.register(false, dispatcher -> dispatcher.register((
				CommandManager.literal("cd_subset").requires(source -> source.hasPermissionLevel(3))
						.then(CommandManager.argument("subset", StringArgumentType.string())
								.executes(context -> changeSubset(context, context.getArgument("subset", String.class))))
						.then(CommandManager.literal("-reset").executes(context -> changeSubset(context, "")))
		)));
		LiteralParser.registerFactory(new Identifier("minecraft", "ingredient"), inputs -> {
			List<ItemStack> stacks = new ArrayList<>();
			NbtMatchType match = NbtMatchType.NONE;
			for (int i = 0; i < inputs.length; i++) {
				Object input = inputs[i];
				if (input instanceof String) {
					String str = (String)input;
					if (i == 0 && str.contains("nbt::")) {
						String type = str.substring(5);
						match = NbtMatchType.forName(type);
					} else if (str.indexOf('{') != -1) {
						int index = str.indexOf('{');
						String name = str.substring(0, index);
						String nbt = str.substring(index);
						if (name.contains("#")) {
							String[] tag = TweakerUtils.getItemsInTag(str.substring(1));
							for (String item : tag) {
								ItemStack stack = new ItemStack(TweakerUtils.getItem(item));
								TweakerUtils.addNbtToStack(stack, nbt);
								stacks.add(stack);
							}
						} else {
							ItemStack stack = new ItemStack(TweakerUtils.getItem(name));
							TweakerUtils.addNbtToStack(stack, nbt);
							stacks.add(stack);
						}
					} else if (str.contains("#")) {
						String[] tag = TweakerUtils.getItemsInTag(str.substring(1));
						for (String name : tag) {
							Item item = TweakerUtils.getItem(name);
							stacks.add(new ItemStack(item));
						}
					} else {
						Item name = TweakerUtils.getItem((String) input);
						stacks.add(new ItemStack(name));
					}
				}
			}
			Ingredient ret = RecipeParser.hackStackIngredients(stacks.toArray(new ItemStack[]{}));
			((MatchTypeSetter)(Object)ret).libcd_setMatchType(match);
			return ret;
		});
	}

	private int changeSubset(CommandContext<ServerCommandSource> context, String setTo) {
		config.tweaker_subset = setTo;
		saveConfig(config);
		context.getSource().sendFeedback(new TranslatableText("libcd.reload.success"), false);
		(context.getSource()).getMinecraftServer().reload();
		return 1;
	}

	/**
	 * Moved to {@link ConditionalData#registerCondition(Identifier, Predicate)}
	 */
	@Deprecated
	public static void registerCondition(Identifier id, Predicate<Object> condition) {
		ConditionalData.registerCondition(id, condition);
	}

	public CDConfig loadConfig() {
		try {
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
			JsonElement json = jankson.toJson(config);
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
