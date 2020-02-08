package io.github.cottonmc.libcd.mixin;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cottonmc.libcd.api.AdvancementRewardsManager;
import io.github.cottonmc.libcd.impl.CustomRewardsSettingsUtils;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(AdvancementRewards.class)
public class MixinAdvancementRewards implements CustomRewardsSettingsUtils {
    private final Map<Identifier, Object> customRewardsSettings = Maps.newHashMap();

    @Inject(method = "apply", at = @At("TAIL"))
    public void onApply(ServerPlayerEntity serverPlayerEntity, CallbackInfo ci) {
        AdvancementRewardsManager.APPLIERS.forEach((id, applier) ->
                applier.accept(serverPlayerEntity, getRewardSettings(id))
        );
    }

    @Inject(method = "toString", at = @At("TAIL"), cancellable = true)
    public void onToString(CallbackInfoReturnable<String> cir) {
        cir.setReturnValue(cir.getReturnValue().replace("}",
                ", libcd:custom=[" + getAllRewardsSettings().toString() + "]}"
        ));
    }

    @Inject(method = "toJson", at = @At("TAIL"), cancellable = true)
    public void onToJson(CallbackInfoReturnable<JsonElement> cir) {
        JsonObject jsonObject = cir.getReturnValue().getAsJsonObject();
        JsonArray jsonArray = new JsonArray();
        Gson gson = new Gson();
        getAllRewardsSettings().forEach((id, o) -> {
            if (o == null) {
                jsonArray.add(id.toString());
            } else {
                JsonObject current = new JsonObject();
                current.addProperty("name", id.toString());
                current.add("value", gson.toJsonTree(o));
                jsonArray.add(current);
            }
        });
        jsonObject.add("libcd:custom", jsonArray);
        cir.setReturnValue(jsonObject);
    }

    @Override
    public Map<Identifier, Object> getAllRewardsSettings() {
        return customRewardsSettings;
    }

    @Override
    public void addAllRewardsSettings(Map<Identifier, Object> rewardsSettings) {
        customRewardsSettings.putAll(rewardsSettings);
    }

    @Override
    public Object getRewardSettings(Identifier id) {
        return customRewardsSettings.get(id);
    }

    @Override
    public void addRewardSettings(Identifier id, Object o) {
        customRewardsSettings.put(id, o);
    }
}
