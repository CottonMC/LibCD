package io.github.cottonmc.libcd.mixin;

import com.google.gson.JsonObject;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import io.github.cottonmc.jankson.JanksonOps;
import io.github.cottonmc.libcd.impl.TagBuilderWarningAccessor;
import io.github.cottonmc.libcd.loader.TagExtensions;
import io.github.cottonmc.libcd.tag.ItemTagHelper;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
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
import java.util.Optional;
import java.util.function.Function;

@Mixin(Tag.Builder.class)
public class MixinTagBuilder implements TagBuilderWarningAccessor {

    @Shadow @Final private List<Tag.TrackedEntry> entries;
    @Unique
    private final List<Object> libcdWarnings = new ArrayList<>();

    private Identifier defaultEntry;

    @Inject(method = "read", at = @At(value = "RETURN", remap = false))
    private void onFromJson(JsonObject json, String string, CallbackInfoReturnable<Tag.Builder> info) {
        try {
            if (json.has("libcd")) {
                TagExtensions.ExtensionResult result = TagExtensions.load(
                        (blue.endless.jankson.JsonObject) Dynamic.convert(
                                JsonOps.INSTANCE, JanksonOps.INSTANCE, JsonHelper.getObject(json, "libcd")
                        )
                );

                if (result.shouldReplace()) {
                    entries.clear();
                }

                result.getEntries().forEach((entry) -> {
                    this.entries.add(TagEntryAccessor.createTrackedEntry(entry, string));
                });

                libcdWarnings.addAll(result.getWarnings());
                defaultEntry = result.getDefaultEntry();
            }
        } catch (Exception e) {
            libcdWarnings.add(e);
        }
    }

    @Override
    public List<Object> libcd$getWarnings() {
        return libcdWarnings;
    }

    @SuppressWarnings("unchecked")
    @Inject(method = "build", at = @At("RETURN"))
    private <T> void injectDefaultEntry(Function<Identifier, Tag<T>> tagGetter, Function<Identifier, T> objectGetter, CallbackInfoReturnable<Optional<Tag<T>>> info) {
        Optional<Tag<T>> opt = info.getReturnValue();
        if (opt.isPresent()) {
            Tag<T> tag = opt.get();
            T t = objectGetter.apply(defaultEntry);
            if (t instanceof Item) {
                ItemTagHelper.INSTANCE.add((Tag<Item>) tag, (Item) t);
            }
        }
    }
}
