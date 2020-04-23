package io.github.cottonmc.libcd;

import blue.endless.jankson.Comment;

import java.util.ArrayList;
import java.util.List;

public class CDConfig {
	@Comment("The set of tweakers to load in addition to the main tweakers.\n" +
			"Located in a data pack at `data/<namespace>/tweakers_<tweaker_subset>`.\n" +
			"Set to \"\" for no additional tweakers.")
	public String tweaker_subset = "";

	@Comment("Whether dev-env files, like the test tweaker, should be loaded.\n" +
			"This will affect the loaded data for your game.")
	public boolean dev_mode = false;

	@Comment("The preference order for namespaces in tags.")
	public List<String> namespace_preference = new ArrayList<>();
}
