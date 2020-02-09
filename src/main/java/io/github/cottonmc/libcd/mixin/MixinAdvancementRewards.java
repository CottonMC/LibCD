package io.github.cottonmc.libcd.mixin;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cottonmc.libcd.impl.CustomRewardsUtils;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.function.BiConsumer;

@Mixin(AdvancementRewards.class)
public class MixinAdvancementRewards implements CustomRewardsUtils {
    private final Map<Identifier, JsonObject> settings = Maps.newHashMap();
    private final Map<Identifier, BiConsumer<ServerPlayerEntity, JsonObject>> appliers = Maps.newHashMap();

    @Inject(method = "apply", at = @At("TAIL"))
    public void onApply(ServerPlayerEntity serverPlayerEntity, CallbackInfo ci) {
        getAllAppliers().forEach((id, applier) -> applier.accept(serverPlayerEntity, getSettings(id)));
    }

    @Inject(method = "toJson", at = @At("TAIL"), cancellable = true)
    public void onToJson(CallbackInfoReturnable<JsonElement> cir) {
        JsonObject jsonObject = cir.getReturnValue().getAsJsonObject();
        JsonArray jsonArray = new JsonArray();
        getAllSettings().forEach((id, settings) -> {
            if (settings == null) {
                jsonArray.add(id.toString());
            } else {
                JsonObject current = new JsonObject();
                current.addProperty("name", id.toString());
                current.add("settings", settings);
                jsonArray.add(current);
            }
        });
        jsonObject.add("libcd:custom", jsonArray);
        cir.setReturnValue(jsonObject);
    }

    @Override
    public Map<Identifier, JsonObject> getAllSettings() {
        return settings;
    }

    @Override
    public void setAllSettings(Map<Identifier, JsonObject> rewardsSettings) {
        settings.putAll(rewardsSettings);
    }

    @Override
    public JsonObject getSettings(Identifier id) {
        return settings.get(id);
    }

    @Override
    public void setSettings(Identifier id, JsonObject settings) {
        this.settings.put(id, settings);
    }

    @Override
    public Map<Identifier, BiConsumer<ServerPlayerEntity, JsonObject>> getAllAppliers() {
        return appliers;
    }

    @Override
    public void setAllAppliers(Map<Identifier, BiConsumer<ServerPlayerEntity, JsonObject>> appliers) {
        this.appliers.putAll(appliers);
    }

    @Override
    public BiConsumer<ServerPlayerEntity, JsonObject> getApplier(Identifier id) {
        return appliers.get(id);
    }

    @Override
    public void setApplier(Identifier id, BiConsumer<ServerPlayerEntity, JsonObject> applier) {
        appliers.put(id, applier);
    }
}
