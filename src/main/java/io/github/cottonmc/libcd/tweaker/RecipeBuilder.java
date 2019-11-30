package io.github.cottonmc.libcd.tweaker;

import net.minecraft.recipe.RecipeSerializer;

@Deprecated
/**
 * use {@link io.github.cottonmc.libcd.api.tweaker.recipe.RecipeBuilder} instead
 */
public class RecipeBuilder extends io.github.cottonmc.libcd.api.tweaker.recipe.RecipeBuilder{
	public RecipeBuilder(RecipeSerializer serializer) {
		super(serializer);
	}
}
