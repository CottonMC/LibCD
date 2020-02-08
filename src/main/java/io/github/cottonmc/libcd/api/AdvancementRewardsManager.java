package io.github.cottonmc.libcd.api;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AdvancementRewardsManager {
    public static final Map<Identifier, JsonDeserializer<Object>> DESERIALIZERS = Maps.newHashMap();
    public static final Map<Identifier, BiConsumer<ServerPlayerEntity, Object>> APPLIERS = Maps.newHashMap();

    public static void addRewardType(
            Identifier id, JsonDeserializer<Object> deserializer, BiConsumer<ServerPlayerEntity, Object> applier
    ) {
        DESERIALIZERS.put(id, deserializer);
        APPLIERS.put(id, applier);
    }

    public static void addRewardType(Identifier id, Consumer<ServerPlayerEntity> applier) {
        addRewardType(id, null, (serverPlayerEntity, o) -> applier.accept(serverPlayerEntity));
    }
}
