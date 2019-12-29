package io.github.cottonmc.libcd.loader;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.JsonPrimitive;
import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.tweaker.ScriptBridge;
import io.github.cottonmc.libcd.api.tweaker.Tweaker;
import io.github.cottonmc.libcd.LibCD;
import io.github.cottonmc.libcd.api.tweaker.TweakerManager;
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

public class TweakerLoader implements SimpleResourceReloadListener<Map<Identifier, ScriptBridge>> {
	public static Map<Identifier, ScriptBridge> SCRIPTS = new HashMap<>();
	public static final ScriptEngineManager SCRIPT_MANAGER = new ScriptEngineManager();

	@Override
	public CompletableFuture load(ResourceManager manager, Profiler profiler, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			SCRIPTS.clear();
			Collection<Identifier> resources = manager.findResources("tweakers", name -> true);
			for (Identifier fileId : resources) {
				int localPath = fileId.getPath().indexOf('/')+1;
				Identifier scriptId = new Identifier(fileId.getNamespace(), fileId.getPath().substring(localPath));
				try {
					Resource res = manager.getResource(fileId);
					String script = IOUtils.toString(res.getInputStream(), StandardCharsets.UTF_8);
					String extension = scriptId.getPath().substring(scriptId.getPath().lastIndexOf('.') + 1);
					ScriptEngine engine = SCRIPT_MANAGER.getEngineByExtension(extension);
					if (engine == null) {
						CDCommons.logger.error("Engine for tweaker script not found: " + scriptId.toString());
						continue;
					}
					SCRIPTS.put(scriptId, new ScriptBridge(engine, script, scriptId));
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
						String script = IOUtils.toString(res.getInputStream(), StandardCharsets.UTF_8);
						String extension = scriptId.getPath().substring(scriptId.getPath().lastIndexOf('.') + 1);
						ScriptEngine engine = SCRIPT_MANAGER.getEngineByExtension(extension);
						if (engine == null) {
							CDCommons.logger.error("Engine for tweaker script not found: " + scriptId.toString());
							continue;
						}
						SCRIPTS.put(scriptId, new ScriptBridge(engine, script, scriptId));
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
			for (Tweaker tweaker : TweakerManager.INSTANCE.getTweakers()) {
				tweaker.prepareReload(manager);
			}
			int loaded = 0;
			for (Identifier id : SCRIPTS.keySet()) {
				ScriptBridge script = SCRIPTS.get(id);
				if (!script.hasRun()) script.run();
				if (!script.hasErrored()) loaded++;
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

	public String formatApplied(List<String> messages) {
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