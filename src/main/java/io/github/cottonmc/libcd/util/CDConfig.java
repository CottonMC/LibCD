package io.github.cottonmc.libcd.util;

import blue.endless.jankson.Comment;

public class CDConfig {
	@Comment("The set of tweakers to load in addition to the main tweakers.\n" +
			"Located in a data pack at `data/<namespace>/tweakers_<tweaker_subset>`.\n" +
			"Set to \"\" for no additional tweakers.")
	public String tweaker_subset = "";
}
