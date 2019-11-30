package io.github.cottonmc.libcd.tweaker;

import io.github.cottonmc.libcd.api.tweaker.TweakerManager;
import io.github.cottonmc.libcd.api.tweaker.TweakerStackFactory;
import io.github.cottonmc.libcd.legacy.LegacyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Deprecated
/**
 * use {@link TweakerStackFactory} instead
 */
public interface TweakerStackGetter {

	static void registerGetter(Identifier id, TweakerStackGetter getter) {
		TweakerManager.INSTANCE.addStackFactory(id, new LegacyGetter(getter));
	}

	/**
	 * Get an ItemStack from a registered processor
	 * @param entry The Identifier of the entry to get
	 * @return the proper ItemStack for the given Identifier, or an empty stack if the entry doesn't exist
	 */
	ItemStack getSpecialStack(Identifier entry);
}
