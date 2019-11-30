package io.github.cottonmc.libcd.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public interface CookingRecipeFactoryInvoker<T extends AbstractCookingRecipe> {
	T libcd$create(Identifier id, String group, Ingredient ingredient, ItemStack output, float experience, int cookingTime);
}
