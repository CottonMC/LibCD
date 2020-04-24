package io.github.cottonmc.libcd.impl;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.PlayerScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

public interface PlayerScreenHandlerAccessor {
	PlayerEntity libcd$getOwner();
}
