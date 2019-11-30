package io.github.cottonmc.libcd.api;

import blue.endless.jankson.Jankson;
import io.github.cottonmc.jankson.JanksonFactory;

public class CDCommons {
    public static final String MODID = "libcd";

    public static final CDLogger logger = new CDLogger();

    public static Jankson newJankson() {
        return JanksonFactory.createJankson();
    }
}
