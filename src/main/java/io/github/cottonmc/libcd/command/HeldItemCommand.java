package io.github.cottonmc.libcd.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.MessageType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HeldItemCommand implements Command<ServerCommandSource> {

	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		ItemStack toDescribe = player.getMainHandStack();
		StringBuilder description = new StringBuilder();
		Identifier id = Registry.ITEM.getId(toDescribe.getItem());
		String idString = (id==null) ? "unknown" : id.toString();
		description.append(idString);
		
		BaseText feedback = new LiteralText(idString); //TODO: the new modifiable-text class once it's remapped
		
		CompoundTag tag = toDescribe.getTag();
		if (tag!=null) {
			description.append(tag.asString());
			feedback.append(tag.toText());
		}
		
		Style clickableStyle = Style.field_24360
			.setHoverEvent(new HoverEvent(HoverEvent.class_5247.field_24342, new LiteralText("Click to copy to clipboard")))
			.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, description.toString()));
		
		feedback.setStyle(clickableStyle);
		player.sendMessage(feedback, MessageType.CHAT);
		
		return 1;
	}

}
