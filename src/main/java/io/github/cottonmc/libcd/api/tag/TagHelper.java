package io.github.cottonmc.libcd.api.tag;

import io.github.cottonmc.libcd.tag.ItemTagHelper;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public interface TagHelper<T> {
	TagHelper<Item> ITEM = ItemTagHelper.INSTANCE;

	T getDefaultEntry(Tag<T> tag);
}
