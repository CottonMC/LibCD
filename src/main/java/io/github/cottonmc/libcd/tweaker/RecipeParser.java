package io.github.cottonmc.libcd.tweaker;

import io.github.cottonmc.libcd.api.CDSyntaxError;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.DefaultedList;

import java.util.*;

@Deprecated
/**
 * use {@link io.github.cottonmc.libcd.api.tweaker.recipe.RecipeParser} instead
 */
public class RecipeParser {
	public static Ingredient processIngredient(Object input) throws CDSyntaxError {
		return io.github.cottonmc.libcd.api.tweaker.recipe.RecipeParser.processIngredient(input);
	}

	public static ItemStack processItemStack(Object input) throws CDSyntaxError {
		return io.github.cottonmc.libcd.api.tweaker.recipe.RecipeParser.processItemStack(input);
	}

	public static String[] processStackGetter(String base) {
		return io.github.cottonmc.libcd.api.tweaker.recipe.RecipeParser.processStackFactory(base);
	}

	public static Object[] processGrid(Object[][] inputs) throws CDSyntaxError {
		return processGrid(inputs, 3, 3);
	}

	public static Object[] processGrid(Object[][] inputs, int maxWidth, int maxHeight) throws CDSyntaxError {
		return io.github.cottonmc.libcd.api.tweaker.recipe.RecipeParser.processGrid(inputs, maxWidth, maxHeight);
	}

	public static String[] processPattern(String... pattern) throws CDSyntaxError {
		return processPattern(3, 3, pattern);
	}

	public static String[] processPattern(int maxWidth, int maxHeight, String... pattern) throws CDSyntaxError {
		return io.github.cottonmc.libcd.api.tweaker.recipe.RecipeParser.processPattern(maxWidth, maxHeight, pattern);
	}

	public static Map<String, Ingredient> processDictionary(Map<String, Object> dictionary) throws CDSyntaxError {
		return io.github.cottonmc.libcd.api.tweaker.recipe.RecipeParser.processDictionary(dictionary);
	}


	public static DefaultedList<Ingredient> getIngredients(String[] pattern, Map<String, Ingredient> dictionary, int x, int y) throws CDSyntaxError {
		return io.github.cottonmc.libcd.api.tweaker.recipe.RecipeParser.getIngredients(pattern, dictionary, x, y);
	}

	public static Ingredient hackStackIngredients(ItemStack... stacks) {
		return io.github.cottonmc.libcd.api.tweaker.recipe.RecipeParser.hackStackIngredients(stacks);
	}
}

