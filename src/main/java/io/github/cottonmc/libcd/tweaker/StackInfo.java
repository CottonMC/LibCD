package io.github.cottonmc.libcd.tweaker;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.Map;
import java.util.Optional;

public class StackInfo {
	private ItemStack stack;
	public StackInfo(ItemStack stack) {
		this.stack = stack.copy();
	}

	boolean isEmpty() {
		return stack.isEmpty();
	}

	String getItem() {
		return Registry.ITEM.getId(stack.getItem()).toString();
	}

	int getCount() {
		return stack.getCount();
	}

	String getName() {
		return stack.getName().asString();
	}

	int getDamage() {
		return stack.getDamage();
	}

	int getEnchantmentLevel(String enchantId) {
		if (!stack.hasEnchantments()) return 0;
		Optional<Enchantment> opt = Registry.ENCHANTMENT.getOrEmpty(new Identifier(enchantId));
		if (!opt.isPresent()) return 0;
		Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
		return enchants.getOrDefault(opt.get(), 0);
	}

	String getTagValue(String key) {
		CompoundTag tag = stack.getOrCreateTag();
		if (!tag.containsKey(key)) return "";
		else return tag.getTag(key).asString();
	}

}
