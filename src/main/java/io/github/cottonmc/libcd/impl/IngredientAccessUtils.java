package io.github.cottonmc.libcd.impl;

import io.github.cottonmc.libcd.util.NbtMatchType;
import net.minecraft.item.ItemStack;

public interface IngredientAccessUtils {
	void libcd_setMatchType(NbtMatchType type);
	ItemStack[] libcd_getStackArray();
}
