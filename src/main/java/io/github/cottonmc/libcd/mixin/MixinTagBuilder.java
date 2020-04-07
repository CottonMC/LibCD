package io.github.cottonmc.libcd.mixin;

import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import io.github.cottonmc.libcd.api.util.GsonOps;
import io.github.cottonmc.libcd.api.util.JanksonOps;
import io.github.cottonmc.libcd.impl.TagBuilderWarningAccessor;
import io.github.cottonmc.libcd.loader.TagExtensions;
import net.minecraft.tag.Tag;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Mixin(Tag.Builder.class)
public class MixinTagBuilder implements TagBuilderWarningAccessor {
    @Shadow
    @Final
    private Set<Tag.Entry> entries;

    @Unique
    private final List<Object> libcdWarnings = new ArrayList<>();

    @Inject(method = "read", at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z", remap = false))
    private void onFromJson(JsonObject json, CallbackInfoReturnable<Tag.Builder> cir) {
        try {
            if (json.has("libcd")) {
                TagExtensions.ExtensionResult result = TagExtensions.load(
                        (blue.endless.jankson.JsonObject) Dynamic.convert(
                                GsonOps.INSTANCE, JanksonOps.INSTANCE, JsonHelper.getObject(json, "libcd")
                        )
                );

                if (result.shouldReplace()) {
                    entries.clear();
                }

                entries.addAll(result.getEntries());
                libcdWarnings.addAll(result.getWarnings());
            }
        } catch (Exception e) {
            libcdWarnings.add(e);
        }
    }

    @Override
    public List<Object> getWarnings() {
        return libcdWarnings;
    }
}
