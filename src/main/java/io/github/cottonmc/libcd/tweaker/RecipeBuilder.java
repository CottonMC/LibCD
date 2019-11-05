package io.github.cottonmc.libcd.tweaker;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.datafixers.NbtOps;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.registry.Registry;

public class RecipeBuilder {
	private JsonObject json = new JsonObject();
	private RecipeSerializer serializer;
	private ItemStack idStack = ItemStack.EMPTY;

	public RecipeBuilder(RecipeSerializer serializer) {
		this.serializer = serializer;
	}

	public RecipeBuilder ingredient(String key, Object ingredient) {
		try {
			Ingredient ing = RecipeParser.processIngredient(ingredient);
			json.add(key, ing.toJson());
		} catch (Exception e) {
			//TODO
		}
		return this;
	}

	public RecipeBuilder ingredientArray(String key, Object[] ingredients) {
		try {
			JsonArray array = new JsonArray();
			for (Object ing : ingredients) {
				array.add(RecipeParser.processIngredient(ing).toJson());
			}
			json.add(key, array);
		} catch (Exception e) {
			//TODO
		}
		return this;
	}

	public RecipeBuilder itemStack(String key, Object stack) {
		try {
			ItemStack s = RecipeParser.processItemStack(stack);
			if (idStack.isEmpty()) idStack = s;
			json.add(key, serializeStack(s));
		} catch (Exception e) {
			//TODO
		}
		return this;
	}

	public RecipeBuilder itemStackArray(String key, Object[] stacks) {
		try {
			JsonArray array = new JsonArray();
			for (Object ing : stacks) {
				array.add(serializeStack(RecipeParser.processItemStack(ing)));
			}
			json.add(key, array);
		} catch (Exception e) {
			//TODO
		}
		return this;
	}

	public RecipeBuilder property(String key, Object prop) {
		if (prop instanceof Number) {
			json.addProperty(key, (Number)prop);
		} else if (prop instanceof Boolean) {
			json.addProperty(key, (Boolean)prop);
		} else if (prop instanceof String) {
			json.addProperty(key, (String)prop);
		}
		return this;
	}

	public RecipeBuilder propertyArray(String key, Object[] props) {
		JsonArray array = new JsonArray();
		for (Object obj : props) {
			if (obj instanceof Number) {
				array.add((Number)obj);
			} else if (obj instanceof Boolean) {
				array.add((Boolean)obj);
			} else if (obj instanceof String) {
				array.add((String)obj);
			}
		}
		json.add(key, array);
		return this;
	}

	public void idStack(ItemStack keyStack) {
		this.idStack = keyStack;
	}

	public Recipe build() {
		System.out.println(json.toString());
		return serializer.read(RecipeTweaker.INSTANCE.getRecipeId(idStack), json);
	}

	private JsonObject serializeStack(ItemStack stack) {
		JsonObject ret = new JsonObject();
		ret.addProperty("item", Registry.ITEM.getId(stack.getItem()).toString());
		ret.addProperty("count", stack.getCount());
		//only add NBT for stacks if NBT Crafting is present so we don't run into any issues with `ShapedRecipe.getItemStack()`
		if (FabricLoader.getInstance().isModLoaded("nbtcrafting") && stack.hasTag()) {
			JsonObject data = Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, stack.getTag()).getAsJsonObject();
			ret.add("data", data);
		}
		return ret;
	}
}
