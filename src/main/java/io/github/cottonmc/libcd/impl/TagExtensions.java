package io.github.cottonmc.libcd.impl;

import blue.endless.jankson.JsonArray;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import io.github.cottonmc.libcd.LibCD;
import io.github.cottonmc.libcd.condition.ConditionalData;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public final class TagExtensions {
    private TagExtensions() {
    }

    public static <T> ExtensionResult<T> load(Function<Identifier, Optional<T>> getter, JsonObject json) {
        boolean shouldReplace = false;
        List<Tag.Entry<T>> entries = new ArrayList<>();

        if (json.containsKey("replace")) {
            shouldReplace = testCondition(json.get("replace"));
        }

        if (json.containsKey("entries")) {
            JsonElement rawEntries = json.get("entries");
            if (!(rawEntries instanceof JsonArray)) {
                throw new IllegalArgumentException("'entries' tag in LibCD tag extensions is not an array: " + rawEntries);
            }

            JsonArray entryArray = (JsonArray) rawEntries;
            for (JsonElement rawEntry : entryArray) {
                if (!(rawEntry instanceof JsonObject)) {
                    LibCD.logger.warn("Tag extension entry '" + rawEntry + "' is not a JsonObject! Skipping...");
                    continue;
                }

                JsonObject entry = (JsonObject) rawEntry;
                if (testCondition(entry.get("when"))) {
                    JsonElement rawValues = entry.get("values");
                    if (!(rawValues instanceof JsonArray)) {
                        LibCD.logger.warn("'values' of tag extension entry '" + rawEntry + "' is not a JsonArray! Skipping...");
                        continue;
                    }

                    JsonArray values = (JsonArray) rawValues;
                    for (int i = 0; i < values.size(); i++) {
                        @Nullable String value = values.get(String.class, i);

                        if (value == null) {
                            LibCD.logger.warn("Could not convert JSON element '" + values.get(i) + "' to a string in tag extensions! Skipping...");
                        } else if (value.startsWith("#")) {
                            entries.add(new Tag.TagEntry<>(new Identifier(value.substring(1))));
                        } else {
                            @Nullable T tagEntry = getter.apply(new Identifier(value)).orElse(null);
                            if (tagEntry == null) {
                                LibCD.logger.warn("Unknown tag value '" + value + "' in LibCD tag extensions! Skipping...");
                                continue;
                            }

                            entries.add(new Tag.CollectionEntry<>(Collections.singleton(tagEntry)));
                        }
                    }
                }
            }
        }

        return new ExtensionResult<>(shouldReplace, entries);
    }

    private static boolean testCondition(JsonElement condition) {
        if (condition instanceof JsonArray) {
            for (JsonElement child : (JsonArray) condition) {
                if (!testCondition(child)) return false;
            }
            return true;
        } else if (!(condition instanceof JsonObject)) {
            LibCD.logger.error("Error parsing tag extensions: item {} in condition list not a JsonObject", condition);
            return false;
        }

        JsonObject obj = (JsonObject) condition;
        for (String key : obj.keySet()) {
            Identifier id = key.equals("or") ? new Identifier(LibCD.MODID, "or"):new Identifier(key);
            if (!ConditionalData.testCondition(id, ConditionalData.parseElement(obj.get(key)))) return false;
        }
        return true;
    }

    public static final class ExtensionResult<T> {
        private final boolean shouldReplace;
        private final List<Tag.Entry<T>> entries;

        public ExtensionResult(boolean shouldReplace, List<Tag.Entry<T>> entries) {
            this.shouldReplace = shouldReplace;
            this.entries = entries;
        }

        public boolean shouldReplace() {
            return shouldReplace;
        }

        public List<Tag.Entry<T>> getEntries() {
            return entries;
        }
    }
}
