package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.impl.CraftingInventoryAccessor;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingInventory.class)
public class MixinCraftingInventory implements CraftingInventoryAccessor {
	@Shadow @Final private ScreenHandler handler;

	@Override
	public ScreenHandler libcd$getHandler() {
		return handler;
	}
}
