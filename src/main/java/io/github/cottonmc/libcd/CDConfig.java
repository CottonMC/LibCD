package io.github.cottonmc.libcd;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CDConfig {
//	@Comment("Whether dev-env files, like the test tweaker, should be loaded.\n" +
//			"This will affect the loaded data for your game.")
	public boolean dev_mode = false;

//	@Comment("The preference order for namespaces in tags.")
	public List<String> namespace_preference = new ArrayList<>();

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CDConfig cdConfig = (CDConfig) o;
		return dev_mode == cdConfig.dev_mode &&
				Objects.equals(namespace_preference, cdConfig.namespace_preference);
	}

	@Override
	public int hashCode() {
		return Objects.hash(dev_mode, namespace_preference);
	}
}
