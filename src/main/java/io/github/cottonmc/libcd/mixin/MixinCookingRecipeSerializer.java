package io.github.cottonmc.libcd.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cottonmc.libcd.impl.CookingRecipeFactoryInvoker;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CookingRecipeSerializer;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CookingRecipeSerializer.class)
public class MixinCookingRecipeSerializer {
	@Shadow @Final private int cookingTime;

	private CookingRecipeFactoryInvoker invoker;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void saveInvoker(@Coerce Object invoker, int cookingTime, CallbackInfo info) {
		this.invoker = (CookingRecipeFactoryInvoker) invoker;
	}

	@Inject(method = "method_17736", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/JsonHelper;getString(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/String;", ordinal = 0),
			cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void read(Identifier id, JsonObject json, CallbackInfoReturnable info,
					  String group, JsonElement ingElem, Ingredient ingredient) {
		JsonElement elem = json.get("result");
		if (elem instanceof JsonObject) {
			ItemStack stack = ShapedRecipe.getItemStack((JsonObject)elem);
			float experience = JsonHelper.getFloat(json, "experience", 0.0F);
			int cookingtime = JsonHelper.getInt(json, "cookingtime", this.cookingTime);
			info.setReturnValue(invoker.libcd$create(id, group, ingredient, stack, experience, cookingtime));
		}
	}
}
