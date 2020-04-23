package io.github.cottonmc.libcd.mixin;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import io.github.cottonmc.libcd.api.tag.TagHelper;
import net.minecraft.datafixer.NbtOps;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.tag.ItemTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ShapedRecipe.class)
public class MixinShapedRecipe {
	//TODO: nbt crafting support?
	@Inject(method = "getItemStack", at = @At("HEAD"), cancellable = true)
	private static void loadResource(JsonObject json, CallbackInfoReturnable<ItemStack> info) {
		if (json.has("tag")) {
			String tagName = JsonHelper.getString(json, "tag");
			Identifier id = new Identifier(tagName);
			net.minecraft.tag.Tag<Item> itemTag = ItemTags.getContainer().get(id);
			if (itemTag == null) {
				throw new JsonSyntaxException("Unknown tag " + tagName);
			}
			Item item = TagHelper.ITEM.getDefaultEntry(itemTag);
			if (item == Items.AIR) {
				throw new JsonSyntaxException("No items in tag " + tagName);
			}
			int count = JsonHelper.getInt(json, "count", 1);
			ItemStack stack = new ItemStack(item, count);
			if (json.has("data")) {
				JsonObject data = JsonHelper.getObject(json, "data");
				net.minecraft.nbt.Tag tag = Dynamic.convert(JsonOps.INSTANCE, NbtOps.INSTANCE, data);
				if (tag instanceof CompoundTag) {
					stack.setTag((CompoundTag)tag);
				}
			}
			info.setReturnValue(stack);
		}
	}
}
