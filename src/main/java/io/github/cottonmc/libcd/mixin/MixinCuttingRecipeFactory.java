package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.impl.CuttingRecipeFactoryInvoker;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CuttingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.minecraft.recipe.CuttingRecipe$Serializer$class_3974")
public interface MixinCuttingRecipeFactory<T extends CuttingRecipe> extends CuttingRecipeFactoryInvoker {
	@Shadow T create(Identifier identifier, String s, Ingredient ingredient, ItemStack itemStack);

	default T libcd_create(Identifier id, String group, Ingredient input, ItemStack output) {
		return create(id, group, input, output);
	}
}
