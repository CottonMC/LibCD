package io.github.cottonmc.libcd.api.util;

import io.github.cottonmc.libcd.api.util.nbt.NbtUtils;
import io.github.cottonmc.libcd.api.util.nbt.WrappedCompoundTag;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

/**
 * A wrapped version of an item stack that can be modified from scripts.
 * Has all the methods of {@link StackInfo}.
 */
public class MutableStack extends StackInfo {

	public MutableStack(ItemStack stack) {
		super(stack);
		this.stack = stack;
	}

	/**
	 * Set the count of the item stack.
	 * @param count The count of items in the stack. Will be limited to the max stack size for this item.
	 */
	public MutableStack setCount(int count) {
		stack.setCount(count);
		return this;
	}

	/**
	 * Set the custom name of the item stack.
	 * @param name The name for this stack.
	 */
	public MutableStack setName(String name) {
		stack.setCustomName(new LiteralText(name));
		return this;
	}

	public MutableStack setFormattedName(String name) {
		stack.setCustomName(Text.Serializer.fromJson(name));
		return this;
	}

	/**
	 * Set the damage of the item stack.
	 * @param damage The amount of damage the item has taken.
	 */
	public MutableStack setDamage(int damage) {
		stack.setDamage(damage);
		return this;
	}

	/**
	 * Set the value of a tag in the stack's main NBT tag.
	 * @param key The name of the tag to set.
	 * @param value The value to set it to.
	 */
	public MutableStack setTagValue(String key, Object value) {
		stack.getOrCreateTag().put(key, NbtUtils.getTagFor(value));
		return this;
	}

	/**
	 * Set the entire stack NBT tag.
	 * @param tag The tag to set it to.
	 */
	public MutableStack setTag(WrappedCompoundTag tag) {
		stack.setTag(tag.getUnderlying());
		return this;
	}

	public MutableStack enchant(String enchantmentName, int level) {
		stack.addEnchantment(Registry.ENCHANTMENT.get(new Identifier(enchantmentName)), level);
		return this;
	}

	public ItemStack get() {
		return stack;
	}
}
