package io.github.cottonmc.libcd.api.tweaker;

import net.minecraft.util.Identifier;

import javax.script.ScriptEngine;

/**
 * A bridge for specific LibCD hooks between Java and JSR-223 languages. An instance is provided to every script as `libcd`.
 * Contains information for other extension systems, such as the script engine, the text of the script, and the script's ID
 */
public class ScriptBridge {
	private ScriptEngine engine;
	private String scriptText;
	private Identifier id;

	public ScriptBridge(ScriptEngine engine, String scriptText, Identifier id) {
		this.engine = engine;
		this.scriptText = scriptText;
		this.id = id;
	}

	public Object require(String assistant) {
		return TweakerManager.INSTANCE.getAssistant(assistant, id);
	}

	//TODO: be able to require other scripts, instead of just assistants?

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
}
