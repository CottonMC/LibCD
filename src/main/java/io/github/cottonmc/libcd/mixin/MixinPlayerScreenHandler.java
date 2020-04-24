package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.impl.PlayerScreenHandlerAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerScreenHandler.class)
public class MixinPlayerScreenHandler implements PlayerScreenHandlerAccessor {
	@Shadow @Final private PlayerEntity owner;

	@Override
	public PlayerEntity libcd$getOwner() {
		return owner;
	}
}
