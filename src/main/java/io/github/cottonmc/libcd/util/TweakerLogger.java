package io.github.cottonmc.libcd.util;

import io.github.cottonmc.libcd.LibCD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TweakerLogger {
	private String prefix;
	private Logger log;

	public TweakerLogger(String prefix) {
		String toUse = prefix.substring(0, 1).toUpperCase() + prefix.substring(1);
		this.prefix = "LibCD|" + toUse;
		this.log = LogManager.getFormatterLogger("LibCD|" + toUse);
	}

	public TweakerLogger() {
		this.prefix = "LibCD";
		this.log = LogManager.getFormatterLogger("LibCD");
	}

	public void info(String s) {
		log.info(getPrefix() + s);
	}

	public void info(String s, Object... objs) {
		log.info(getPrefix() + s, objs);
	}

	public void warn(String s) {
		log.warn(getPrefix() + s);
	}

	public void warn(String s, Object... objs) {
		log.warn(getPrefix() + s, objs);
	}

	public void error(String s) {
		log.error(getPrefix() + s);
	}

	public void error(String s, Throwable e) {
		log.error(getPrefix() + s, e);
	}

	public void error(String s, Object... objs) {
		log.error(getPrefix() + s, objs);
	}

	public void debug(String s) {
		if (LibCD.isDevMode()) log.info(getDebugPrefix() + s);
	}

	public void debug(String s, Object... objs) {
		if (LibCD.isDevMode()) log.info(getDebugPrefix() + s, objs);
	}

	private String getPrefix() {
		return "[" + prefix + "] ";
	}

	private String getDebugPrefix() {
		return "[" + prefix + "|Debug] ";
	}
}
