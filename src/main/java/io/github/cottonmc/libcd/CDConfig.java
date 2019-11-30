package io.github.cottonmc.libcd;

import blue.endless.jankson.Comment;

public class CDConfig {
	@Comment("The set of tweakers to load in addition to the main tweakers.\n" +
			"Located in a data pack at `data/<namespace>/tweakers_<tweaker_subset>`.\n" +
			"Set to \"\" for no additional tweakers.")
	public String tweaker_subset = "";

	@Comment("Whether dev-env files, like the test tweaker, should be loaded.\n" +
			"Will always be treated as true in a mod development environment.")
	public boolean dev_mode = false;
}
