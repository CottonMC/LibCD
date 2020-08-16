package io.github.cottonmc.libcd.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.cottonmc.libcd.api.tag.TagHelper;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.tag.ServerTagManagerHolder;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

public class DefaultedTagEntrySerializer extends LeafEntry.Serializer<DefaultedTagEntry> {

	@Override
	protected DefaultedTagEntry fromJson(JsonObject entryJson, JsonDeserializationContext context, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
		String tagName = JsonHelper.getString(entryJson, "name");
		Tag<Item> itemTag = ServerTagManagerHolder.getTagManager().getItems().getTag(new Identifier(tagName));
		if (itemTag == null) {
			throw new JsonSyntaxException("Unknown tag " + tagName);
		}
		Item item = TagHelper.ITEM.getDefaultEntry(itemTag);
		if (item == Items.AIR) {
			throw new JsonSyntaxException("No items in tag " + tagName);
		}
		return new DefaultedTagEntry(item, weight, quality, conditions, functions);
	}
}
