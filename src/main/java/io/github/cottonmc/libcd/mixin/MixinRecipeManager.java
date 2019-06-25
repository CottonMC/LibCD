package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.impl.RecipeMapAccessor;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(RecipeManager.class)
public abstract class MixinRecipeManager implements RecipeMapAccessor {
	@Shadow
	private Map<RecipeType<?>, Map<Identifier, Recipe<?>>> recipeMap;

	@Override
	public Map<RecipeType<?>, Map<Identifier, Recipe<?>>> libcd_getRecipeMap() {
		return recipeMap;
	}

	@Override
	public void libcd_setRecipeMap(Map<RecipeType<?>, Map<Identifier, Recipe<?>>> map) {
		recipeMap = map;
	}
}
