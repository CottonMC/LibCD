package io.github.cottonmc.libcd.loot;

import io.github.cottonmc.libcd.api.tag.TagHelper;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.tag.Tag;

public class DefaultedTagEntry extends ItemEntry {
	public DefaultedTagEntry(Item item, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
		super(item, weight, quality, conditions, functions);
	}

	public static Builder<?> builder(Tag<Item> itemTag) {
		return builder((weight, quality, conditions, functions) ->
				new DefaultedTagEntry(TagHelper.ITEM.getDefaultEntry(itemTag), weight, quality, conditions, functions));
	}
}
