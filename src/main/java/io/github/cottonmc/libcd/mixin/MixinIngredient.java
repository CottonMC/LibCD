package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.impl.MatchTypeSetter;
import io.github.cottonmc.libcd.util.NbtMatchType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(Ingredient.class)
public class MixinIngredient implements MatchTypeSetter {
	private NbtMatchType type = NbtMatchType.NONE;
	@Inject(method = "method_8093", at = @At(value = "RETURN", ordinal = 2), cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void checkStackNbt(ItemStack test, CallbackInfoReturnable cir, ItemStack[] stackArray, int arrayLength, int i, ItemStack testAgainst) {
		if (!testAgainst.hasTag()) {
			cir.setReturnValue(true);
			return;
		}
		if (type != NbtMatchType.NONE && !test.hasTag()) return;
		CompoundTag testTag = test.getOrCreateTag();
		CompoundTag againstTag = testAgainst.getOrCreateTag();
		switch(type) {
			case FUZZY:
				for (String key : againstTag.getKeys()) {
					if (!testTag.containsKey(key)) cir.setReturnValue(false);
					Tag trial = testTag.getTag(key);
					Tag against = againstTag.getTag(key);
					if (trial.getType() == against.getType()) {
						if (!trial.asString().equals(against.asString())) {
							cir.setReturnValue(false);
							return;
						}
					}
				}
				cir.setReturnValue(true);
				break;
			case EXACT:
				cir.setReturnValue(testTag.asString().equals(againstTag.asString()));
				break;
			default:
				cir.setReturnValue(true);
		}
	}


	@Override
	public void libcd_setMatchType(NbtMatchType type) {
		this.type = type;
	}
}
