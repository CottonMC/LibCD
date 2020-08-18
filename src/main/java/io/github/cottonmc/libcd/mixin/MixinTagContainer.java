package io.github.cottonmc.libcd.mixin;

import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.impl.TagBuilderWarningAccessor;
import net.minecraft.tag.Tag;
import net.minecraft.tag.TagGroup;
import net.minecraft.tag.TagGroupLoader;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Mixin(TagGroupLoader.class)
public class MixinTagContainer<T> {


    @Inject(method = "applyReload", at = @At(value = "INVOKE", target = "Lnet/minecraft/tag/Tag$Builder;build(Ljava/util/function/Function;Ljava/util/function/Function;)Ljava/util/Optional;"), locals = LocalCapture.CAPTURE_FAILHARD)
    private void onPut(
            Map<Identifier, Tag.Builder> map,
            CallbackInfoReturnable<TagGroup<T>> ci,
            Map<Identifier, Tag<T>> map2,
            Function<Identifier, Tag<T>> function,
            Function function2,
            boolean bl,
            Iterator iterator,
            Map.Entry<Identifier, Tag.Builder> entry
            ) {
        List<Object> warnings = ((TagBuilderWarningAccessor) entry.getValue()).libcd$getWarnings();
        if(!warnings.isEmpty()) {
            CDCommons.logger.warn("Found problems in tag extensions of tag " + entry.getKey() + ':');
            for (Object warning : warnings) {
                if (warning instanceof Throwable) {
                    Throwable t = (Throwable) warning;
                    CDCommons.logger.error("\t- {}", t.getMessage(), t);
                } else {
                    CDCommons.logger.warn("\t- {}", warning);
                }
            }

            warnings.clear();
        }
    }
}
