package io.github.cottonmc.libcd.api.tweaker.recipe;

import io.github.cottonmc.libcd.api.CDLogger;
import io.github.cottonmc.libcd.api.tweaker.ScriptBridge;
import io.github.cottonmc.libcd.api.util.StackInfo;
import io.github.cottonmc.libcd.api.util.WorldInfo;
import io.github.cottonmc.libcd.api.util.WrappedPlayer;
import io.github.cottonmc.libcd.api.util.crafting.CraftingUtils;
import io.github.cottonmc.libcd.api.util.crafting.CustomSpecialRecipeSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

public class CustomSpecialCraftingRecipe extends SpecialCraftingRecipe {
	private ScriptBridge bridge;
	private CDLogger logger;

	public CustomSpecialCraftingRecipe(ScriptBridge bridge, Identifier id) {
		super(id);
		this.bridge = bridge;
		this.logger = new CDLogger(bridge.getId().toString());
	}

	public CustomSpecialCraftingRecipe(Identifier id) {
		super(id);
	}

	//TODO: make sure this is only called on server?
	@Override
	public boolean matches(CraftingInventory inv, World world) {
		try {
			PlayerEntity player = CraftingUtils.findPlayer(inv);
			Object result = bridge.invokeFunction("matches", CraftingUtils.getInvStacks(inv), inv.getWidth(), inv.getHeight(), player != null? new WrappedPlayer(player) : null, new WorldInfo(world));
			if (result instanceof Boolean) return (Boolean) result;
			else {
				logger.error("Could not check match for custom special crafting recipe %s, returning false: function 'matches' must return, boolean but returned %s instead", getId(), result.getClass().getName());
				return false;
			}
		} catch (Exception e) {
			logger.error("Could not check match for custom special crafting recipe %s, returning false: %s", getId(), e.getMessage());
		}
		return false;
	}

	//TODO: make sure this is only called on server?
	@Override
	public ItemStack craft(CraftingInventory inv) {
		try {
			Object result = bridge.invokeFunction("preview", CraftingUtils.getInvStacks(inv), inv.getWidth(), inv.getHeight(), new WrappedPlayer(CraftingUtils.findPlayer(inv)));
			if (result == null) {
				logger.error("Could not get preview output for custom special crafting recipe %s, returning empty stack: function 'preview' must not return null", getId());
				return ItemStack.EMPTY;
			} else {
				return RecipeParser.processItemStack(result);
			}
		} catch (Exception e) {
			logger.error("Could not get preview output for custom special crafting recipe %s, returning empty: %s", getId(), e.getMessage());
			return ItemStack.EMPTY;
		}
	}

	@Override
	public boolean fits(int width, int height) {
		return true; //TODO: this doesn't matter, since it's a special crafting recipe
	}

	//TODO: make sure this is only called on server?
	@Override
	public DefaultedList<ItemStack> getRemainingStacks(CraftingInventory inv) {
		DefaultedList<ItemStack> remainingStacks = super.getRemainingStacks(inv);
		try {
			PlayerEntity player = CraftingUtils.findPlayer(inv);
			bridge.invokeFunction("craft", CraftingUtils.getInvStacks(inv), player != null? new WrappedPlayer(player) : null, new StackInfo(craft(inv)));
		} catch (Exception e) {
			logger.error("Could not fully craft custom special crafting recipe %s, ignoring: %s", getId(), e.getMessage());
		}
		return remainingStacks;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return CustomSpecialRecipeSerializer.INSTANCE;
	}
}
