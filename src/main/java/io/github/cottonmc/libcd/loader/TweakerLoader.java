package io.github.cottonmc.libcd.loader;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.tweaker.ScriptBridge;
import io.github.cottonmc.libcd.api.tweaker.Tweaker;
import io.github.cottonmc.libcd.LibCD;
import io.github.cottonmc.libcd.api.tweaker.TweakerManager;
import io.github.cottonmc.parchment.api.Script;
import io.github.cottonmc.parchment.api.ScriptLoader;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.io.IOUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

//TODO: add another system to ScriptDataLoader to allow subsets and stuff, so I don't need to do this all myself?
public class TweakerLoader implements SimpleResourceReloadListener<Map<Identifier, ScriptBridge>> {
	public static Map<Identifier, ScriptBridge> SCRIPTS = new HashMap<>();

	@Override
	public CompletableFuture<Map<Identifier, ScriptBridge>> load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			Map<Identifier, ScriptBridge> scripts = new HashMap<>();
			Collection<Identifier> resources = manager.findResources("tweakers", name -> true);
			for (Identifier fileId : resources) {
				int localPath = fileId.getPath().indexOf('/')+1;
				Identifier scriptId = new Identifier(fileId.getNamespace(), fileId.getPath().substring(localPath));
				try {
					Resource res = manager.getResource(fileId);
					ScriptBridge script = (ScriptBridge) ScriptLoader.INSTANCE.loadScript(ScriptBridge::new, scriptId, res.getInputStream());
					ScriptBridge oldScript = scripts.put(scriptId, script);
					if (oldScript != null) {
						CDCommons.logger.error("Duplicate script file ignored with ID %s", scriptId.toString());
					}
				} catch (IOException e) {
					CDCommons.logger.error("Error when accessing tweaker script %s: %s", scriptId.toString(), e.getMessage());
				}
			}
			String subset = LibCD.config.tweaker_subset;
			if (!subset.equals("")) {
				Collection<Identifier> setResources = manager.findResources("tweakers_"+subset, name -> true);
				for (Identifier fileId : setResources) {
					Identifier scriptId = new Identifier(fileId.getNamespace(), fileId.getPath().substring("tweakers_".length()));
					try {
						Resource res = manager.getResource(fileId);
						ScriptBridge script = (ScriptBridge) ScriptLoader.INSTANCE.loadScript(ScriptBridge::new, scriptId, res.getInputStream());
						ScriptBridge oldScript = scripts.put(scriptId, script);
						if (oldScript != null) {
							CDCommons.logger.error("Duplicate script file ignored with ID %s", scriptId.toString());
						}
					} catch (IOException e) {
						CDCommons.logger.error("Error when accessing tweaker script %s (in subset %s): %s", scriptId.toString(), subset, e.getMessage());
					}
				}
			}
			return SCRIPTS;
		});
	}

	@Override
	public CompletableFuture<Void> apply(Map<Identifier, ScriptBridge> scripts, ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.runAsync(() -> {
			SCRIPTS = scripts;
			for (Tweaker tweaker : TweakerManager.INSTANCE.getTweakers()) {
				tweaker.prepareReload(manager);
			}
			int loaded = 0;
			for (Identifier id : scripts.keySet()) {
				ScriptBridge script = scripts.get(id);
				if (!script.hasRun()) script.run();
				if (!script.hadError()) loaded++;
			}
			List<String> applied = new ArrayList<>();
			for (Tweaker tweaker : TweakerManager.INSTANCE.getTweakers()) {
				tweaker.applyReload(manager, executor);
				applied.add(tweaker.getApplyMessage());
			}
			String confirm = formatApplied(applied);
			if (loaded > 0) CDCommons.logger.info("Applied %s tweaker %s, including %s", loaded, (loaded == 1? "script" : "scripts"), confirm);
		});
	}

	private String formatApplied(List<String> messages) {
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < messages.size(); i++) {
			String message = messages.get(i);
			ret.append(message);
			if (i < messages.size() - 1) {
				if (messages.size() <= 2) ret.append(" ");
				else ret.append(", ");
			}
			if (i == messages.size() - 2) ret.append("and ");
		}
		return ret.toString();
	}

	public static JsonObject getDebugObject() {
		JsonObject ret = new JsonObject();
		JsonArray successful = new JsonArray();
		JsonArray errored = new JsonArray();
		for (Identifier id : SCRIPTS.keySet()) {
			ScriptBridge bridge = SCRIPTS.get(id);
			if (bridge.hasErrored()) {
				errored.add(new JsonPrimitive(id.toString()));
			} else {
				successful.add(new JsonPrimitive(id.toString()));
			}
		}
		ret.put("successful", successful);
		ret.put("errored", errored);
		return ret;
	}

	@Override
	public Identifier getFabricId() {
		return new Identifier(CDCommons.MODID, "tweaker_loader");
	}

}