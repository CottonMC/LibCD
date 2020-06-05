package io.github.cottonmc.libcd.api.util;

import com.mojang.serialization.JsonOps;

/**
 * A version of JsonOps that has the patch from Mojang/DataFixerUpper#42.
 *
 * @deprecated Now that the fix is merged, use JsonOps.
 */
@Deprecated
public class GsonOps extends JsonOps {
    public static final GsonOps INSTANCE = new GsonOps();

    protected GsonOps() {
        super(false);
    }
}
