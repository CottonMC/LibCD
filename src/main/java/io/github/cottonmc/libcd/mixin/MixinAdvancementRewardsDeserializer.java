package io.github.cottonmc.libcd.mixin;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cottonmc.libcd.api.AdvancementRewardsManager;
import io.github.cottonmc.libcd.impl.CustomRewardsUtils;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(AdvancementRewards.Deserializer.class)
public class MixinAdvancementRewardsDeserializer {
    @Inject(method = "deserialize", at = @At("TAIL"), cancellable = true)
    public void onDeserialize(
            JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext,
            CallbackInfoReturnable<AdvancementRewards> cir
    ) {
        CustomRewardsUtils value = (CustomRewardsUtils) cir.getReturnValue();

        Map<Identifier, BiConsumer<ServerPlayerEntity, JsonObject>> appliers = Maps.newHashMap();
        Map<Identifier, JsonObject> settings = Maps.newHashMap();

        JsonHelper.getArray(
                JsonHelper.asObject(jsonElement, "rewards"),
                "libcd:custom",
                new JsonArray()
        ).forEach(element -> {
            if (element.isJsonObject()) {
                JsonObject current = JsonHelper.asObject(element, "libcd:custom array entry");
                Identifier id = new Identifier(JsonHelper.getString(current, "name"));
                appliers.put(id, AdvancementRewardsManager.INSTANCE.getAppliers().get(id));
                settings.put(id, JsonHelper.getObject(current, "settings"));
            } else {
                Identifier id = new Identifier(JsonHelper.asString(element, "libcd:custom array entry"));
                appliers.put(id, AdvancementRewardsManager.INSTANCE.getAppliers().get(id));
                settings.put(id, null);
            }
        });

        value.setAllAppliers(appliers);
        value.setAllSettings(settings);
        cir.setReturnValue((AdvancementRewards) value);
    }
}
