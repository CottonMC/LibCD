package io.github.cottonmc.libcd.impl;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

import java.util.Map;

public interface RecipeMapAccessor {
	Map<RecipeType<?>, Map<Identifier, Recipe<?>>> libcd$getRecipeMap();
	void libcd$setRecipeMap(Map<RecipeType<?>, Map<Identifier, Recipe<?>>> map);
}
