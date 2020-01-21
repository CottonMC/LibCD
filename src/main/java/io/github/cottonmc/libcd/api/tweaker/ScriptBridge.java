package io.github.cottonmc.libcd.api.tweaker;

import io.github.cottonmc.libcd.LibCD;
import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.CDLogger;
import io.github.cottonmc.libcd.loader.TweakerLoader;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

/**
 * A bridge for specific LibCD hooks between Java and JSR-223 languages. An instance is provided to every script as `libcd`.
 * Contains information for other extension systems, such as the script engine, the text of the script, and the script's ID
 */
public class ScriptBridge {
	private ScriptEngine engine;
	private String scriptText;
	private Identifier id;
	private boolean hasRun = false;
	private boolean hasErrored = false;
	private List<Tweaker> requiredTweakers = new ArrayList<>();

	public ScriptBridge(ScriptEngine engine, String scriptText, Identifier id) {
		this.engine = engine;
		this.scriptText = scriptText;
		this.id = id;
	}

	/**
	 * Import an assistant object for this script to use.
	 * @param assistant The name of the assistant to get.
	 * @return The assistant object with the given name, prepared for this script.
	 */
	public Object require(String assistant) {
		Object ret = TweakerManager.INSTANCE.getAssistant(assistant, this);
		//if it's a tweaker, note that so we can re-prep if `importScript` gets run
		if (ret instanceof Tweaker) {
			requiredTweakers.add((Tweaker)ret);
		}
		return ret;
	}

	/**
	 * Import another script to get variables from or invoke a function from. Will evaluate the imported script if it hasn't been eval'd yet.
	 * @param scriptId The ID of the script to import.
	 * @return The script bridge of the imported script, or null if the script doesn't exist.
	 */
	@Nullable
	public ScriptBridge importScript(String scriptId) {
		Identifier id = new Identifier(scriptId);
		if (!TweakerLoader.SCRIPTS.containsKey(id)) {
			CDCommons.logger.error("Script %s could not find other script %s", this.id.toString(), id.toString());
			return null;
		}
		ScriptBridge bridge = TweakerLoader.SCRIPTS.get(id);
		if (!bridge.hasRun()) bridge.run();
		//now that the other script has run, re-prep any tweakers we've required before, since the other script mighta messed them up
		for (Tweaker tweaker : requiredTweakers) {
			tweaker.prepareFor(this);
		}
		return bridge;
	}

	public ScriptEngine getEngine() {
		return engine;
	}

	//TODO: is this helpful?
	public String getScriptText() {
		return scriptText;
	}

	public Identifier getId() {
		return id;
	}

	/**
	 * Get a variable from this script.
	 * @param varName The name of the variable to get.
	 * @return The variable with this name, or null in case of an error.
	 */
	@Nullable
	public Object getVar(String varName) {
		if (!hasRun()) run();
		if (hasErrored()) {
			CDCommons.logger.error("Cannot get variable from errored script %s, returning null", id.toString());
			return null;
		}
		return engine.getBindings(ScriptContext.ENGINE_SCOPE).get(varName);
	}

	/**
	 * Invoke a function in this script and return the result. The script engine must implement {@link Invocable} for this to work.
	 * @param funcName The name of the function to run.
	 * @param args The arguments to supply to the function.
	 * @return The result of the function, or null in case of an error.
	 */
	@Nullable
	public Object invokeFunction(String funcName, Object...args) {
		if (!hasRun()) run();
		if (hasErrored()) {
			CDCommons.logger.error("Cannot invoke function from errored script %s, returning null", id.toString());
			return null;
		}
		if (engine instanceof Invocable) {
			Invocable invocable = (Invocable)engine;
			try {
				return invocable.invokeFunction(funcName, args);
			} catch (Exception e) {
				CDCommons.logger.error("Error invoking function %s from script %s: %s", funcName, id.toString(), e.getMessage());
				if (LibCD.isDevMode()) e.printStackTrace();
				return null;
			}
		} else {
			CDCommons.logger.error("Cannot invoke functions from script %s: engine is not invocable", id.toString());
			return null;
		}
	}

	/**
	 * Run a script! You probably don't need to call this yourself.
	 */
	public void run() {
		if (hasRun()) return;
		if (!scriptText.contains("libcd.require")) {
			CDCommons.logger.warn("WARNING! Script %s doesn't use the new `libcd.require` system! It may break in a future update!", id.toString());
		}
		ScriptContext ctx = engine.getContext();
		ctx.setAttribute("libcd", this, ScriptContext.ENGINE_SCOPE);
		ctx.setAttribute("log", new CDLogger(id.toString()), ScriptContext.ENGINE_SCOPE);
		try {
			engine.eval(scriptText);
		} catch (ScriptException e) {
			hasErrored = true;
			CDCommons.logger.error("Error executing tweaker script %s: %s", id.toString(), e.getMessage());
			if (LibCD.isDevMode()) e.printStackTrace();
		}
		hasRun = true;
	}

	/**
	 * @return Whether this script has already run.
	 */
	public boolean hasRun() {
		return hasRun;
	}

	/**
	 * @return Whether this script errored when running. If true, you won't be able to invoke any functions in it or get any vars from it.
	 */
	public boolean hasErrored() {
		return hasErrored;
	}
}
