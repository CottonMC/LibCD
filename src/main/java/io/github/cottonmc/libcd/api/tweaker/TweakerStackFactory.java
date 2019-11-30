package io.github.cottonmc.libcd.api.tweaker;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/**
 * Some ItemStacks, like vanilla potions, have entirely different functions and names based on NBT.
 * Because of that, it's hard to use those stacks in recipes.
 * CottonTweaker uses a "[getter id]->[entry id]" syntax to get those recipes
 */
public interface TweakerStackFactory {
	/**
	 * Get an ItemStack from a registered processor
	 * @param entry The Identifier of the entry to get
	 * @return the proper ItemStack for the given Identifier, or an empty stack if the entry doesn't exist
	 */
	ItemStack getSpecialStack(Identifier entry);
}
