package io.github.cottonmc.libcd.compat.nbtcrafting;

import de.siphalor.nbtcrafting.ingredient.IIngredient;
import de.siphalor.nbtcrafting.ingredient.IngredientStackEntry;
import de.siphalor.nbtcrafting.util.ICloneable;
import io.github.cottonmc.libcd.api.tweaker.recipe.RecipeTweaker;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class IngredientAssembler {
	public static Ingredient constructFromStacks(ItemStack... stacks) {
		List<IngredientStackEntry> entries = new ArrayList<>();
		for (ItemStack stack : stacks) {
			entries.add(new IngredientStackEntry(stack));
		}
		Stream<IngredientStackEntry> entryStream = entries.stream();
		try {
			Ingredient ingredient = (Ingredient)((ICloneable)(Object)Ingredient.EMPTY).clone();
			((IIngredient)(Object)ingredient).setAdvancedEntries(entryStream);
			return ingredient;
		} catch (CloneNotSupportedException e) {
			RecipeTweaker.INSTANCE.getLogger().error("Failed to assemble ingredient with NBT Crafting: " + e.getMessage());
			return Ingredient.EMPTY;
		}
	}
}
