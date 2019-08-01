package io.github.cottonmc.libcd.tweaker;

import com.google.common.collect.ImmutableMap;
import io.github.cottonmc.libcd.LibCD;
import io.github.cottonmc.libcd.impl.MatchTypeSetter;
import io.github.cottonmc.libcd.impl.RecipeMapAccessor;
import io.github.cottonmc.libcd.impl.ReloadListenersAccessor;
import io.github.cottonmc.libcd.util.NbtMatchType;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.*;
import java.util.concurrent.Executor;

public class RecipeTweaker implements Tweaker {
	public static final RecipeTweaker INSTANCE = new RecipeTweaker();
	private RecipeManager recipeManager;
	private int recipeCount;
	private int removeCount;
	private Map<RecipeType<?>, List<Recipe<?>>> toAdd = new HashMap<>();
	private Map<RecipeType<?>, List<Identifier>> toRemove = new HashMap<>();

	/**
	 * Used during data pack loading to set up recipe adding.
	 * DO NOT CALL THIS YOURSELF, EVER. IT WILL LIKELY MESS THINGS UP.
	 */
	@Override
	public void prepareReload(ResourceManager manager) {
		recipeCount = 0;
		removeCount = 0;
		toAdd.clear();
		toRemove.clear();
		if (manager instanceof ReloadListenersAccessor) {
			List<ResourceReloadListener> listeners = ((ReloadListenersAccessor)manager).libcd_getListeners();
			for (ResourceReloadListener listener : listeners) {
				if (listener instanceof RecipeManager) {
					this.recipeManager = (RecipeManager)listener;
					return;
				}
			}
			LibCD.logger.error("No recipe manager was found! Tweaker cannot register recipes!");
			throw new IllegalStateException("No recipe manager was found! Tweaker cannot register recipes!");
		}
		LibCD.logger.error("No reload listeners accessor found! Tweaker cannot register recipes!");
		throw new IllegalStateException("No reload listeners accessor found! Tweaker cannot register recipes!");
	}

	/**
	 * Used during data pack applying to directly apply recipes.
	 * This is "safe" to call yourself, but will result in a *lot* of log spam.
	 * NOTE: for some reason, Mojang decided to make the recipe map entirely immutable!
	 *   I don't like this but I respect it, so this code will preserve the map's immutability,
	 *   even though it might be a better idea to leave it mutable.
	 */
	@Override
	public void applyReload(ResourceManager manager, Executor executor) {
		Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipeMap = new HashMap<>(((RecipeMapAccessor)recipeManager).libcd_getRecipeMap());
		Set<RecipeType<?>> types = new HashSet<>(recipeMap.keySet());
		types.addAll(toAdd.keySet());
		for (RecipeType<?> type : types) {
			Map<Identifier, Recipe<?>> map = new HashMap<>(recipeMap.getOrDefault(type, new HashMap<>()));
			for (Recipe<?> recipe : toAdd.getOrDefault(type, new ArrayList<>())) {
				Identifier id = recipe.getId();
				if (map.containsKey(id)) {
					LibCD.logger.error("Failed to add recipe from tweaker - duplicate recipe ID: " + id);
				} else try {
					map.put(id, recipe);
					recipeCount++;
				} catch (Exception e) {
					LibCD.logger.error("Failed to add recipe from tweaker - " + e.getMessage());
				}
			}
			for (Identifier recipeId : toRemove.getOrDefault(type, new ArrayList<>())) {
				if (map.containsKey(recipeId)) {
					map.remove(recipeId);
					removeCount++;
				} else LibCD.logger.error("Could not find recipe to remove: " + recipeId.toString());
			}
			recipeMap.put(type, ImmutableMap.copyOf(map));
		}
		((RecipeMapAccessor)recipeManager).libcd_setRecipeMap(ImmutableMap.copyOf(recipeMap));
	}

	@Override
	public String getApplyMessage() {
		return recipeCount + " " + (recipeCount == 1? "recipe" : "recipes" + (removeCount == 0? "" : " (" + removeCount + " removed)"));
	}

	/**
	 * Generate a recipe ID. Call this from Java tweaker classes.
	 * @param output The output stack of the recipe.
	 * @return A unique identifier for the recipe.
	 */
	public Identifier getRecipeId(ItemStack output) {
		String resultName = Registry.ITEM.getId(output.getItem()).getPath();
		return new Identifier(LibCD.MODID, "tweaked/"+resultName+"-"+recipeCount);
	}

	/**
	 * Remove a recipe from the recipe manager.
	 * @param id The id of the recipe to remove.
	 */
	public void removeRecipe(String id) {
		Identifier formatted = new Identifier(id);
		Optional<? extends Recipe<?>> opt = recipeManager.get(formatted);
		if (opt.isPresent()) {
			Recipe<?> recipe = opt.get();
			RecipeType<?> type = recipe.getType();
			if (!toRemove.containsKey(type)) toRemove.put(type, new ArrayList<>());
			List<Identifier> removal = toRemove.get(type);
			removal.add(formatted);
		}
	}

	/**
	 * Register a recipe to the recipe manager.
	 * @param recipe A constructed recipe.
	 */
	public void addRecipe(Recipe<?> recipe) {
		RecipeType<?> type = recipe.getType();
		if (!toAdd.containsKey(type)) {
			toAdd.put(type, new ArrayList<>());
		}
		List<Recipe<?>> recipeList = toAdd.get(type);
		recipeList.add(recipe);
	}

	/**
	 * Get a recipe ingredient from an item stack. Call this from java tweaker classes.
	 * @param stack The item stack to make an ingredient for.
	 * @return The wrapped ingredient of the stack.
	 */
	public Ingredient ingredientForStack(ItemStack stack) {
		return RecipeParser.hackStackIngredients(stack);
	}

	/**
	 * Make an Ingredient object to pass to recipes from a string of inputs.
	 * @param nbtMatch The NBT matching type to use: "none", "fuzzy", or "exact".
	 * @param inputs The string forms of inputs to add to the Ingredient.
	 * @return An Ingredient object to pass to recipes.
	 * @throws TweakerSyntaxException If an input is malformed.
	 */
	public Ingredient makeIngredient(String nbtMatch, String...inputs) {
		List<ItemStack> stacks = new ArrayList<>();
		NbtMatchType match = NbtMatchType.forName(nbtMatch);
		for (String input : inputs) {
			try {
				ItemStack[] in = RecipeParser.processIngredient(input).getStackArray();
				stacks.addAll(Arrays.asList(in));
			} catch (TweakerSyntaxException e) {
				LibCD.logger.error("Could not add stack to ingredient: malformed stack string {}", input);
			}
		}
		Ingredient ret = RecipeParser.hackStackIngredients(stacks.toArray(new ItemStack[]{}));
		((MatchTypeSetter)(Object)ret).libcd_setMatchType(match);
		return ret;
	}

	public void addShaped(Object[][] inputs, ItemStack output) {
		addShaped(inputs, output, "");
	}

	/**
	 * Add a shaped recipe from a 2D array of inputs, like a standard CraftTweaker recipe.
	 * @param inputs the 2D array (array of arrays) of inputs to use.
	 * @param output The output of the recipe.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addShaped(Object[][] inputs, ItemStack output, String group) {
		try {
			Object[] processed = RecipeParser.processGrid(inputs);
			int width = inputs[0].length;
			int height = inputs.length;
			addShaped(processed, output, width, height, group);
		} catch (Exception e) {
			LibCD.logger.error("Error parsing shaped recipe - " + e.getMessage());
		}
	}

	public void addShaped(String[] inputs, ItemStack output, int width, int height) {
		addShaped(inputs, output, width, height, "");
	}

	/**
	 * Register a shaped crafting recipe from a 1D array of inputs.
	 * @param inputs The input item or tag ids required in order: left to right, top to bottom.
	 * @param output The output of the recipe.
	 * @param width How many rows the recipe needs.
	 * @param height How many columns the recipe needs.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addShaped(Object[] inputs, ItemStack output, int width, int height, String group){
		Identifier recipeId = getRecipeId(output);
		try {
			DefaultedList<Ingredient> ingredients = DefaultedList.of();
			for (int i = 0; i < Math.min(inputs.length, width * height); i++) {
				Object id = inputs[i];
				if (id.equals("")) continue;
				ingredients.add(i, RecipeParser.processIngredient(id));
			}
			addRecipe(new ShapedRecipe(recipeId, group, width, height, ingredients, output));
		} catch (Exception e) {
			LibCD.logger.error("Error parsing shaped recipe - " + e.getMessage());
		}
	}

	public void addShaped(String[] pattern, Map<String, Object> dictionary, ItemStack output) {
		addShaped(pattern, dictionary, output, "");
	}

	/**
	 * Register a shaped crafting recipe from a pattern and dictionary.
	 * @param pattern A crafting pattern like one you'd find in a vanilla recipe JSON.
	 * @param dictionary A map of single characters to item or tag ids.
	 * @param output The output of the recipe.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addShaped(String[] pattern, Map<String, Object> dictionary, ItemStack output, String group) {
		Identifier recipeId = getRecipeId(output);
		try {
			pattern = RecipeParser.processPattern(pattern);
			Map<String, Ingredient> map = RecipeParser.processDictionary(dictionary);
			int x = pattern[0].length();
			int y = pattern.length;
			DefaultedList<Ingredient> ingredients = RecipeParser.getIngredients(pattern, map, x, y);
			addRecipe(new ShapedRecipe(recipeId, group, x, y, ingredients, output));
		} catch (Exception e) {
			LibCD.logger.error("Error parsing shaped recipe - " + e.getMessage());
		}
	}

	public void addShapeless(Object[] inputs, ItemStack output) {
		addShapeless(inputs, output, "");
	}

	/**
	 * Register a shapeless crafting recipe from an array of inputs.
	 * @param inputs A list of input item or tag ids required for the recipe.
	 * @param output The output of the recipe.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addShapeless(Object[] inputs, ItemStack output, String group) {
		Identifier recipeId = getRecipeId(output);
		try {
			DefaultedList<Ingredient> ingredients = DefaultedList.of();
			for (int i = 0; i < Math.min(inputs.length, 9); i++) {
				Object id = inputs[i];
				if (id.equals("")) continue;
				ingredients.add(i, RecipeParser.processIngredient(id));
			}
			addRecipe(new ShapelessRecipe(recipeId, group, output, ingredients));
		} catch (Exception e) {
			LibCD.logger.error("Error parsing shapeless recipe - " + e.getMessage());
		}
	}

	public void addSmelting(Object input, ItemStack output, int ticks, float xp) {
		addSmelting(input, output, ticks, xp, "");
	}

	/**
	 * Register a recipe to smelt in a standard furnace.
	 * @param input The input item or tag id.
	 * @param output The output of the recipe.
	 * @param cookTime How many ticks (1/20 of a second) to cook for. Standard value: 200
	 * @param xp How many experience points to drop per item, on average.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addSmelting(Object input, ItemStack output, int cookTime, float xp, String group) {
		Identifier recipeId = getRecipeId(output);
		try {
			Ingredient ingredient = RecipeParser.processIngredient(input);
			addRecipe(new SmeltingRecipe(recipeId, group, ingredient, output, xp, cookTime));
		} catch (Exception e) {
			LibCD.logger.error("Error parsing smelting recipe - " + e.getMessage());
		}
	}

	public void addBlasting(Object input, ItemStack output, int ticks, float xp) {
		addBlasting(input, output, ticks, xp, "");
	}

	/**
	 * Register a recipe to smelt in a blast furnace.
	 * @param input The input item or tag id.
	 * @param output The output of the recipe.
	 * @param cookTime How many ticks (1/20 of a second) to cook for. Standard value: 100
	 * @param xp How many experience points to drop per item, on average.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addBlasting(Object input, ItemStack output, int cookTime, float xp, String group) {
		Identifier recipeId = getRecipeId(output);
		try {
			Ingredient ingredient = RecipeParser.processIngredient(input);
			addRecipe(new BlastingRecipe(recipeId, group, ingredient, output, xp, cookTime));
		} catch (Exception e) {
			LibCD.logger.error("Error parsing blasting recipe - " + e.getMessage());
		}
	}

	public void addSmoking(Object input, ItemStack output, int ticks, float xp) {
		addSmoking(input, output, ticks, xp, "");
	}

	/**
	 * Register a recipe to cook in a smoker.
	 * @param input The input item or tag id.
	 * @param output The output of the recipe.
	 * @param cookTime How many ticks (1/20 of a second) to cook for. Standard value: 100
	 * @param xp How many experience points to drop per item, on average.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addSmoking(Object input, ItemStack output, int cookTime, float xp, String group) {
		Identifier recipeId = getRecipeId(output);
		try {
			Ingredient ingredient = RecipeParser.processIngredient(input);
			addRecipe(new SmokingRecipe(recipeId, group, ingredient, output, xp, cookTime));
		} catch (Exception e) {
			LibCD.logger.error("Error parsing smokig recipe - " + e.getMessage());
		}
	}

	public void addCampfire(Object input, ItemStack output, int ticks, float xp) {
		addCampfire(input, output, ticks, xp, "");
	}

	/**
	 * Register a recipe to cook on a campfire.
	 * @param input The input item or tag id.
	 * @param output The output of the recipe.
	 * @param cookTime How many ticks (1/20 of a second) to cook for. Standard value: 600
	 * @param xp How many experience points to drop per item, on average.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addCampfire(Object input, ItemStack output, int cookTime, float xp, String group) {
		Identifier recipeId = getRecipeId(output);
		try {
			Ingredient ingredient = RecipeParser.processIngredient(input);
			addRecipe(new CampfireCookingRecipe(recipeId, group, ingredient, output, xp, cookTime));
		} catch (Exception e) {
			LibCD.logger.error("Error parsing campfire recipe - " + e.getMessage());
		}
	}

	public void addStonecutting(Object input, ItemStack output) {
		addStonecutting(input, output, "");
	}

	/**
	 * Register a recipe to cut in the stonecutter.
	 * @param input The input item or tag id.
	 * @param output The output of the recipe.
	 * @param group The recipe group to go in, or "" for none.
	 */
	public void addStonecutting(Object input, ItemStack output, String group) {
		Identifier recipeId = getRecipeId(output);
		try {
			Ingredient ingredient = RecipeParser.processIngredient(input);
			addRecipe(new StonecuttingRecipe(recipeId, group, ingredient, output));
		} catch (Exception e) {
			LibCD.logger.error("Error parsing stonecutter recipe - " + e.getMessage());
		}
	}

}
