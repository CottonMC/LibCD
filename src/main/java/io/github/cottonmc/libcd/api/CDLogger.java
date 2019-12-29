package io.github.cottonmc.libcd.api;

import io.github.cottonmc.libcd.LibCD;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A logger with set prefixes for scripts. An instance is provided to every script as `log`.
 */
public class CDLogger {
	private String prefix;
	private Logger log;

	public CDLogger(String prefix) {
		this.prefix = "LibCD|" + prefix;
		this.log = LogManager.getFormatterLogger("LibCD|" + prefix);
	}

	public CDLogger() {
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
