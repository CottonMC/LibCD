package io.github.cottonmc.libcd.api.tweaker.util;

import io.github.cottonmc.libcd.api.util.nbt.WrappedCompoundTag;
import io.github.cottonmc.libcd.api.util.nbt.WrappedListTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.List;

/**
 * Util class for accessing NBT from tweakers.
 */
public class Nbt {
	public static final Nbt INSTANCE = new Nbt();

	private Nbt() {}

	/**
	 * Create an empty NBT compound.
	 * @return A new NBT compound tag, wrapped for usability.
	 */
	public WrappedCompoundTag newCompound() {
		return WrappedCompoundTag.create();
	}

	/**
	 * Create an empty NBT list.
	 * @return A new NBT list tag, wrapped for usability.
	 */
	public WrappedListTag newList() {
		return WrappedListTag.create();
	}

	/**
	 * Create an NBT list from an array.
	 * @param items The items to add, as an array or as a vararg.
	 * @return A new NBT list tag with the passed items, wrapped for usability.
	 */
	public WrappedListTag listOf(Object... items) {
		return WrappedListTag.create(items);
	}

	/**
	 * Create an NBT list from a list.
	 * @param items The items to add, as a list of Objects.
	 * @return A new NBT list tag with the passed items, wrapped for usability.
	 */
	public WrappedListTag listOf(List<Object> items) {
		return WrappedListTag.create(items);
	}
}
