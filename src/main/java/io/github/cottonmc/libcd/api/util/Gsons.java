package io.github.cottonmc.libcd.api.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.minecraft.loot.*;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctions;
import net.minecraft.util.BoundedIntUnaryOperator;

public class Gsons {
    public static final JsonParser PARSER = new JsonParser();

    public static final Gson LOOT_TABLE = new GsonBuilder()
            .registerTypeAdapter(UniformLootTableRange.class, new UniformLootTableRange.Serializer())
            .registerTypeAdapter(BinomialLootTableRange.class, new net.minecraft.loot.BinomialLootTableRange.Serializer())
            .registerTypeAdapter(ConstantLootTableRange.class, new net.minecraft.loot.ConstantLootTableRange.Serializer())
            .registerTypeAdapter(BoundedIntUnaryOperator.class, new net.minecraft.util.BoundedIntUnaryOperator.Serializer())
            .registerTypeAdapter(LootPool.class, new net.minecraft.loot.LootPool.Serializer())
            .registerTypeAdapter(LootTable.class, new net.minecraft.loot.LootTable.Serializer())
            .registerTypeHierarchyAdapter(LootEntry.class, new net.minecraft.loot.entry.LootEntries.Serializer())
            .registerTypeHierarchyAdapter(LootFunction.class, new LootFunctions.Factory())
            .registerTypeHierarchyAdapter(LootCondition.class, new net.minecraft.loot.condition.LootConditions.Factory())
            .registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new net.minecraft.loot.context.LootContext.EntityTarget.Serializer())
            .create();
}
