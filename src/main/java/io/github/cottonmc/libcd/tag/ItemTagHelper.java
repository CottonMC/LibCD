package io.github.cottonmc.libcd.tag;

import io.github.cottonmc.libcd.LibCD;
import io.github.cottonmc.libcd.api.tag.TagHelper;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public final class ItemTagHelper implements TagHelper<Item> {
	public static final ItemTagHelper INSTANCE = new ItemTagHelper();

	private final Map<Tag<Item>, Item> defaultEntries = new HashMap<>();

	@Nullable
	@Override
	//TODO: What do we do with stuff like nether ores or mods that register multiple things to the same tag? Have extra tags for nether/end ores that get appended in?
	public Item getDefaultEntry(Tag<Item> tag) {
		if (defaultEntries.containsKey(tag)) {
			return defaultEntries.get(tag);
		}
		Item ret = Items.AIR;
		int currentPref = -1;
		for (Item item : tag.values()) {
			Identifier id = Registry.ITEM.getId(item);
			String namespace = id.getNamespace();
			int index = LibCD.config.namespace_preference.indexOf(namespace);
			if (index == -1) {
				LibCD.config.namespace_preference.add(namespace);
				LibCD.
				saveConfig(LibCD.config);
				index = LibCD.config.namespace_preference.indexOf(namespace);
			}
			if (ret == Items.AIR) {
				ret = item;
				currentPref = index;
			} else {
				if (currentPref > index) {
					ret = item;
					currentPref = index;
				}
			}
		}
		return ret;
	}

	public void reset() {
		defaultEntries.clear();
	}

	public void add(Tag<Item> id, Item value) {
		defaultEntries.put(id, value);
	}

	private ItemTagHelper() { }
}
