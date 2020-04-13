package io.github.cottonmc.libcd.mixin;

import net.minecraft.tag.Tag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Tag.class_5145.class)
public interface class_5145Accessor {
    @Invoker("<init>")
    static Tag.class_5145 createClass_5145(Tag.Entry entry, String string) {
        throw new UnsupportedOperationException();
    }
}
