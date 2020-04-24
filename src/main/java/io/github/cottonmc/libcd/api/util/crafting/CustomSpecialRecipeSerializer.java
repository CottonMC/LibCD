package io.github.cottonmc.libcd.api.util.crafting;

import io.github.cottonmc.libcd.api.tweaker.recipe.CustomSpecialCraftingRecipe;
import net.minecraft.recipe.SpecialRecipeSerializer;

public class CustomSpecialRecipeSerializer extends SpecialRecipeSerializer<CustomSpecialCraftingRecipe> {
	public static final CustomSpecialRecipeSerializer INSTANCE = new CustomSpecialRecipeSerializer();

	private CustomSpecialRecipeSerializer() {
		super(CustomSpecialCraftingRecipe::new);
	}
}
