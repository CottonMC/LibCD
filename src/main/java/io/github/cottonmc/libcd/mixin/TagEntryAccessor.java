package io.github.cottonmc.libcd.mixin;

import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

//TODO: we can't split this between duck and impl, to my knowledge. Is there any way around that other than AWs?
@Mixin(Tag.TrackedEntry.class)
public interface TagEntryAccessor {
    @Invoker("<init>")
    static Tag.TrackedEntry createTrackedEntry(Tag.Entry entry, String string) {
        throw new UnsupportedOperationException();
    }
}
