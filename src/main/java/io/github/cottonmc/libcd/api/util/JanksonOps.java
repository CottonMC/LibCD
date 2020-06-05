package io.github.cottonmc.libcd.api.util;

// Copied from Jankson-Fabric 2.x

/**
 * A DynamicOps instance for Jankson. Loosely based on Mojang's JsonOps for Gson.
 *
 * @deprecated Use upstream JanksonOps instead.
 */
@Deprecated
public class JanksonOps extends io.github.cottonmc.jankson.JanksonOps {
    public static final JanksonOps INSTANCE = new JanksonOps(false);

    protected JanksonOps(boolean compressed) {
        super(compressed);
    }
}
