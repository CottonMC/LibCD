package io.github.cottonmc.libcd.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import io.github.cottonmc.libcd.LibCD;
import io.github.cottonmc.libcd.api.tag.TagHelper;
import io.github.cottonmc.libcd.mixin.ItemEntrySerializerAccessor;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.tag.ItemTags;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.registry.Registry;

public class DefaultedTagEntrySerializer extends LeafEntry.Serializer<ItemEntry> {
	private static final ItemEntry.Serializer serializer = new ItemEntry.Serializer();

	public DefaultedTagEntrySerializer() {
		super(new Identifier(LibCD.MODID, "tag"), ItemEntry.class);
	}

	@Override
	protected ItemEntry fromJson(JsonObject entryJson, JsonDeserializationContext context, int weight, int quality, LootCondition[] conditions, LootFunction[] functions) {
		String tagName = JsonHelper.getString(entryJson, "name");
		Tag<Item> itemTag = ItemTags.getContainer().get(new Identifier(tagName));
		if (itemTag == null) {
			throw new JsonSyntaxException("Unknown tag " + tagName);
		}
		Item item = TagHelper.ITEM.getDefaultEntry(itemTag);
		if (item == Items.AIR) {
			throw new JsonSyntaxException("No items in tag " + tagName);
		}
		entryJson.addProperty("name", Registry.ITEM.getId(item).toString());
		return ((ItemEntrySerializerAccessor) serializer).invokeFromJson(entryJson, context, weight, quality, conditions, functions);
	}
}
