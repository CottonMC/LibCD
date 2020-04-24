package io.github.cottonmc.libcd.api.util;

import io.github.cottonmc.libcd.api.util.nbt.NbtUtils;
import io.github.cottonmc.libcd.api.util.nbt.WrappedCompoundTag;
import net.minecraft.item.ItemStack;
import net.minecraft.text.LiteralText;

/**
 * A wrapped version of an item stack that can be modified from scripts.
 */
public class MutableStack extends StackInfo {

	public MutableStack(ItemStack stack) {
		super(stack);
		this.stack = stack;
	}

	void setCount(int count) {
		stack.setCount(count);
	}

	void setName(String name) {
		stack.setCustomName(new LiteralText(name));
	}

	void setDamage(int damage) {
		stack.setDamage(damage);
	}

	void setTagValue(String key, Object value) {
		stack.getOrCreateTag().put(key, NbtUtils.getTagFor(value));
	}

	void setTag(WrappedCompoundTag tag) {
		stack.setTag(tag.getUnderlying());
	}

	public ItemStack get() {
		return stack;
	}
}
