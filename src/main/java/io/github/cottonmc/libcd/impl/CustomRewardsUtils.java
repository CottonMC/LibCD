package io.github.cottonmc.libcd.impl;

import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.BiConsumer;

public interface CustomRewardsUtils {
    Map<Identifier, JsonObject> getAllSettings();

    void setAllSettings(Map<Identifier, JsonObject> settings);

    JsonObject getSettings(Identifier id);

    void setSettings(Identifier id, JsonObject settings);

    Map<Identifier, BiConsumer<ServerPlayerEntity, JsonObject>> getAllAppliers();

    void setAllAppliers(Map<Identifier, BiConsumer<ServerPlayerEntity, JsonObject>> appliers);

    BiConsumer<ServerPlayerEntity, JsonObject> getApplier(Identifier id);

    void setApplier(Identifier id, BiConsumer<ServerPlayerEntity, JsonObject> applier);
}
