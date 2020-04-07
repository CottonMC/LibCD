package io.github.cottonmc.libcd.api.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.minecraft.loot.*;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditions;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootEntries;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;

public class Gsons {
    public static final JsonParser PARSER = new JsonParser();

    public static final Gson LOOT_TABLE = new GsonBuilder()
            .registerTypeAdapter(UniformLootTableRange.class, new UniformLootTableRange.Serializer())
            .registerTypeAdapter(BinomialLootTableRange.class, new BinomialLootTableRange.Serializer())
            .registerTypeAdapter(ConstantLootTableRange.class, new ConstantLootTableRange.Serializer())
            .registerTypeAdapter(BoundedIntUnaryOperator.class, new BoundedIntUnaryOperator.Serializer())
            .registerTypeAdapter(LootPool.class, new LootPool.Serializer())
            .registerTypeAdapter(LootTable.class, new LootTable.Serializer())
            .registerTypeHierarchyAdapter(LootEntry.class, new LootEntries.Serializer())
            .registerTypeHierarchyAdapter(LootFunction.class, new LootFunctions.Factory())
            .registerTypeHierarchyAdapter(LootCondition.class, new LootConditions.Factory())
            .registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer())
            .create();
}
