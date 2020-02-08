package io.github.cottonmc.libcd.mixin;

import com.google.common.collect.Maps;
import io.github.cottonmc.libcd.impl.CustomRewardsBuilder;
import io.github.cottonmc.libcd.impl.CustomRewardsSettingsUtils;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AdvancementRewards.Builder.class)
public class MixinAdvancementRewardsBuilder implements CustomRewardsBuilder {
    private final Map<Identifier, Object> customRewardsSettings = Maps.newHashMap();

    @Inject(method = "build", at = @At("TAIL"), cancellable = true)
    public void onBuild(CallbackInfoReturnable<AdvancementRewards> cir) {
        CustomRewardsSettingsUtils value = (CustomRewardsSettingsUtils) cir.getReturnValue();
        value.addAllRewardsSettings(customRewardsSettings);
        cir.setReturnValue((AdvancementRewards) value);
    }

    @Override
    public CustomRewardsBuilder add(Identifier id, Object settings) {
        customRewardsSettings.put(id, settings);
        return this;
    }

    @Override
    public CustomRewardsBuilder addAll(Map<Identifier, Object> rewardsSettings) {
        customRewardsSettings.putAll(rewardsSettings);
        return this;
    }
}
