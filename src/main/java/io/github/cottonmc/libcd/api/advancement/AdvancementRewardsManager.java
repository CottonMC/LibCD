package io.github.cottonmc.libcd.api.advancement;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AdvancementRewardsManager {
    public static final AdvancementRewardsManager INSTANCE = new AdvancementRewardsManager();
    private final Map<Identifier, BiConsumer<ServerPlayerEntity, JsonObject>> handlers = Maps.newHashMap();

    private AdvancementRewardsManager() {
    }

    /**
     * Register a custom advancement reward handler.
     * @param id The ID of the handler to register.
     * @param handler A handler which accepts both the player earning this advancement and JSON configuration of the custom handler.
     */
    public void register(Identifier id, BiConsumer<ServerPlayerEntity, JsonObject> handler) {
        handlers.put(id, handler);
    }

    /**
     * Register a custom advancement reward handler.
     * @param id The ID of the handler to register.
     * @param handler A handler which accepts the player earning this advancement.
     */
    public void register(Identifier id, Consumer<ServerPlayerEntity> handler) {
        register(id, (serverPlayerEntity, o) -> handler.accept(serverPlayerEntity));
    }

    public Map<Identifier, BiConsumer<ServerPlayerEntity, JsonObject>> getHandlers() {
        return handlers;
    }
}
