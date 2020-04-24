package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.impl.CraftingResultSlotAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.slot.CraftingResultSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(CraftingResultSlot.class)
public class MixinCraftingResultSlot implements CraftingResultSlotAccessor {
	@Shadow @Final private PlayerEntity player;

	@Override
	public PlayerEntity libcd$getPlayer() {
		return player;
	}
}
