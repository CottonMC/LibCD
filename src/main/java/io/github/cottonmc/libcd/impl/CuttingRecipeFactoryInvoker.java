package io.github.cottonmc.libcd.impl;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CuttingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;

public interface CuttingRecipeFactoryInvoker<T extends CuttingRecipe> {
	T libcd$create(Identifier id, String group, Ingredient input, ItemStack output);
}
