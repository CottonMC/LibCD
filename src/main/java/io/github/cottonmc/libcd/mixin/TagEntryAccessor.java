package io.github.cottonmc.libcd.mixin;

import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Tag.TrackedEntry.class)
public interface TagEntryAccessor {
    @Invoker("<init>")
    static Tag.TrackedEntry createTrackedEntry(Tag.Entry entry, String string) {
        throw new UnsupportedOperationException();
    }
}
