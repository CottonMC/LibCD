package io.github.cottonmc.libcd.api.util.crafting;

import net.minecraft.entity.damage.DamageSource;

public class CraftingDamageSource extends DamageSource {
	public static final CraftingDamageSource INSTANCE = new CraftingDamageSource();

	private CraftingDamageSource() {
		super("libcd.crafting");
		this.setBypassesArmor();
		this.setUnblockable();
	}
}
