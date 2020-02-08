package io.github.cottonmc.libcd.mixin;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import io.github.cottonmc.libcd.api.AdvancementRewardsManager;
import io.github.cottonmc.libcd.impl.CustomRewardsSettingsUtils;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.Map;

@Mixin(AdvancementRewards.Deserializer.class)
public class MixinAdvancementRewardsDeserializer {
    @Inject(method = "deserialize", at = @At("TAIL"), cancellable = true)
    public void onDeserialize(
            JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext,
            CallbackInfoReturnable<AdvancementRewards> cir
    ) {
        CustomRewardsSettingsUtils value = (CustomRewardsSettingsUtils) cir.getReturnValue();

        Map<Identifier, Object> customRewardsSettings = Maps.newHashMap();
        JsonArray jsonArray = JsonHelper.getArray(
                JsonHelper.asObject(jsonElement, "rewards"), "libcd:custom", new JsonArray());

        jsonArray.forEach(element -> {
            if (element.isJsonObject()) {
                JsonObject current = element.getAsJsonObject();
                Identifier id = new Identifier(JsonHelper.asString(current, "name"));
                JsonObject settings = JsonHelper.asObject(current, "value");
                customRewardsSettings.put(id,
                        AdvancementRewardsManager.DESERIALIZERS.get(id).deserialize(
                                settings, TypeToken.get(Object.class).getType(), jsonDeserializationContext));
            }
        });

        value.addAllRewardsSettings(customRewardsSettings);
        cir.setReturnValue((AdvancementRewards) value);
    }
}
