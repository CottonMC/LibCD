package io.github.cottonmc.libcd.impl;

import io.github.cottonmc.libcd.api.util.NbtMatchType;
import net.minecraft.item.ItemStack;

public interface IngredientAccessUtils {
	void libcd$setMatchType(NbtMatchType type);
	ItemStack[] libcd$getStackArray();
}
