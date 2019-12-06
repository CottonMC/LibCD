package io.github.cottonmc.libcd.legacy;

import blue.endless.jankson.JsonObject;
import io.github.cottonmc.libcd.api.tweaker.Tweaker;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;

import java.util.concurrent.Executor;

public class LegacyTweaker implements Tweaker {
	private io.github.cottonmc.libcd.tweaker.Tweaker tweaker;

	public LegacyTweaker(io.github.cottonmc.libcd.tweaker.Tweaker tweaker) {
		this.tweaker = tweaker;
	}

	@Override
	public void prepareReload(ResourceManager manager) {
		tweaker.prepareReload(manager);
	}

	@Override
	public void applyReload(ResourceManager manager, Executor executor) {
		tweaker.applyReload(manager, executor);
	}

	@Override
	public String getApplyMessage() {
		return tweaker.getApplyMessage();
	}

	@Override
	public void prepareFor(Identifier scriptId) {
		tweaker.prepareFor(scriptId);
	}

	@Override
	public JsonObject getDebugInfo() {
		return new JsonObject();
	}
}
