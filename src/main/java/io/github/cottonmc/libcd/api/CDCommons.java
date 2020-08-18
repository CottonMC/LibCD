package io.github.cottonmc.libcd.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class CDCommons {
    public static final String MODID = "libcd";

    public static final Logger logger = LogManager.getLogger("libcd");

    public static Gson newGson() {
        return new GsonBuilder().setLenient().setPrettyPrinting().serializeNulls().create();
    }

}
