package io.github.cottonmc.libcd.api;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AdvancementRewardsManager {
    public static final AdvancementRewardsManager INSTANCE = new AdvancementRewardsManager();
    private final Map<Identifier, BiConsumer<ServerPlayerEntity, JsonObject>> appliers = Maps.newHashMap();

    private AdvancementRewardsManager() {
    }

    public void register(Identifier id, BiConsumer<ServerPlayerEntity, JsonObject> applier) {
        appliers.put(id, applier);
    }

    public void register(Identifier id, Consumer<ServerPlayerEntity> applier) {
        register(id, (serverPlayerEntity, o) -> applier.accept(serverPlayerEntity));
    }

    public Map<Identifier, BiConsumer<ServerPlayerEntity, JsonObject>> getAppliers() {
        return appliers;
    }
}
