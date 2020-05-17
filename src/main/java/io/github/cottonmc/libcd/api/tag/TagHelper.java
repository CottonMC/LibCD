package io.github.cottonmc.libcd.api.tag;

import io.github.cottonmc.libcd.tag.ItemTagHelper;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;

public interface TagHelper<T> {
	TagHelper<Item> ITEM = ItemTagHelper.INSTANCE; //TODO: does this make it impossible to have a separate API module? I have no idea how to fix it if so...

	T getDefaultEntry(Tag<T> tag);
}
