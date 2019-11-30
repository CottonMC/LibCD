package io.github.cottonmc.libcd.legacy;

import io.github.cottonmc.libcd.api.tweaker.TweakerStackFactory;
import io.github.cottonmc.libcd.tweaker.TweakerStackGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class LegacyGetter implements TweakerStackFactory {
	private TweakerStackGetter getter;

	public LegacyGetter(TweakerStackGetter getter) {
		this.getter = getter;
	}

	@Override
	public ItemStack getSpecialStack(Identifier entry) {
		return getter.getSpecialStack(entry);
	}
}
