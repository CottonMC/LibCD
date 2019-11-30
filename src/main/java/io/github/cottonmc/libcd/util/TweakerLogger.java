package io.github.cottonmc.libcd.util;

import io.github.cottonmc.libcd.api.CDLogger;

@Deprecated
/**
 * use {@link CDLogger} instead
 */
public class TweakerLogger extends CDLogger {
	public TweakerLogger(String prefix) {
		super(prefix);
	}
	public TweakerLogger() {
		super();
	}
}
