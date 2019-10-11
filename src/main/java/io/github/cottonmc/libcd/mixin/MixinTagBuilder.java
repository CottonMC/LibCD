package io.github.cottonmc.libcd.mixin;

import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import io.github.cottonmc.libcd.LibCD;
import io.github.cottonmc.libcd.impl.TagExtensions;
import io.github.cottonmc.libcd.util.GsonOps;
import io.github.cottonmc.libcd.util.JanksonOps;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Mixin(Tag.Builder.class)
public class MixinTagBuilder<T> {
    @Shadow
    @Final
    private Set<Tag.Entry<T>> entries;

    /* Run before Set.addAll so that vanilla's replacing doesn't clear our entries
       and our replacing doesn't clear theirs */
    @Inject(method = "fromJson", at = @At(value = "INVOKE", target = "Ljava/util/Set;addAll(Ljava/util/Collection;)Z", remap = false))
    private void onFromJson(Function<Identifier, Optional<T>> getter, JsonObject json, CallbackInfoReturnable<Tag.Builder<T>> info) {
        try {
            if (json.has("libcd")) {
                TagExtensions.ExtensionResult<T> result = TagExtensions.load(
                        getter,
                        (blue.endless.jankson.JsonObject) Dynamic.convert(
                                GsonOps.INSTANCE, JanksonOps.INSTANCE, JsonHelper.getObject(json, "libcd")
                        )
                );

                if (result.shouldReplace()) {
                    entries.clear();
                }
                entries.addAll(result.getEntries());
            }
        } catch (Exception e) {
            LibCD.logger.warn("Failed to load LibCD tag extensions in {}", json, e);
        }
    }
}
