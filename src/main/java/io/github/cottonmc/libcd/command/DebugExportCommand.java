package io.github.cottonmc.libcd.command;

import blue.endless.jankson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.github.cottonmc.libcd.tweaker.RecipeTweaker;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

import java.io.File;
import java.io.FileOutputStream;

public class DebugExportCommand implements Command<ServerCommandSource> {
	@Override
	public int run(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		try {
			File file = FabricLoader.getInstance().getGameDirectory().toPath().resolve("debug/libcd.json5").toFile();
			JsonObject json = RecipeTweaker.INSTANCE.getRecipeDebug();
			String result = json.toJson(true, true);
			if (!file.exists()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
			}
			FileOutputStream out = new FileOutputStream(file,false);
			out.write(result.getBytes());
			out.flush();
			out.close();
			context.getSource().sendFeedback(new LiteralText("Debug info exported!"), true);
			return 1;
		} catch (Exception e) {
			context.getSource().sendError(new LiteralText("Error exporting debug info: " + e.getMessage()));
			return 0;
		}
	}
}
