package io.github.cottonmc.libcd.mixin;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cottonmc.libcd.impl.CuttingRecipeFactoryInvoker;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CuttingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(CuttingRecipe.Serializer.class)
public class MixinCuttingRecipeSerializer {
	private CuttingRecipeFactoryInvoker invoker;

	@Inject(method = "<init>", at = @At("RETURN"))
	private void saveInvoker(@Coerce Object invoker, CallbackInfo info) {
		this.invoker = (CuttingRecipeFactoryInvoker) invoker;
	}

	@Inject(method = "method_17881", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/JsonHelper;getString(Lcom/google/gson/JsonObject;Ljava/lang/String;)Ljava/lang/String;", ordinal = 0),
			cancellable = true, locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void read(Identifier id, JsonObject json, CallbackInfoReturnable info,
					  String group, Ingredient ingredient) {
		JsonElement elem = json.get("result");
		if (elem instanceof JsonObject) {
			ItemStack stack = ShapedRecipe.getItemStack((JsonObject)elem);
			info.setReturnValue(invoker.libcd$create(id, group, ingredient, stack));
		}
	}
}
