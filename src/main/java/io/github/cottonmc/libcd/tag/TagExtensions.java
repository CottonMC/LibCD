package io.github.cottonmc.libcd.tag;

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

    /**
     * Loads the tag extensions for a given tag from the JSON object.
     *
     * @param getter a getter converting from value ID to optional value
     * @param json the JSON object
     * @param <T> the tag value type
     */
    public static <T> ExtensionResult<T> load(Function<Identifier, Optional<T>> getter, JsonObject json) {
        boolean shouldReplace = false;
        List<Tag.Entry<T>> entries = new ArrayList<>();
        List<String> warnings = new ArrayList<>();

        if (json.containsKey("replace")) {
            shouldReplace = testCondition(json.get("replace"), warnings);
        }

        if (json.containsKey("entries")) {
            JsonElement rawEntries = json.get("entries");
            if (!(rawEntries instanceof JsonArray)) {
                throw new IllegalArgumentException("'entries' tag in LibCD tag extensions is not an array: " + rawEntries);
            }

            JsonArray entryArray = (JsonArray) rawEntries;
            for (JsonElement rawEntry : entryArray) {
                if (!(rawEntry instanceof JsonObject)) {
                    warnings.add("Tag extension entry '" + rawEntry + "' is not a JsonObject! Skipping...");
                    continue;
                }

                JsonObject entry = (JsonObject) rawEntry;
                if (testCondition(entry.get("when"), warnings)) {
                    JsonElement rawValues = entry.get("values");
                    if (!(rawValues instanceof JsonArray)) {
                        warnings.add("'values' of tag extension entry '" + rawEntry + "' is not a JsonArray! Skipping...");
                        continue;
                    }

                    JsonArray values = (JsonArray) rawValues;
                    for (int i = 0; i < values.size(); i++) {
                        @Nullable String value = values.get(String.class, i);

                        if (value == null) {
                            warnings.add("Could not convert JSON element '" + values.get(i) + "' to a string in tag extensions! Skipping...");
                        } else if (value.startsWith("#")) {
                            entries.add(new Tag.TagEntry<>(new Identifier(value.substring(1))));
                        } else {
                            @Nullable T tagEntry = getter.apply(new Identifier(value)).orElse(null);
                            if (tagEntry == null) {
                                warnings.add("Unknown tag value '" + value + "' in LibCD tag extensions! Skipping...");
                                continue;
                            }

                            entries.add(new Tag.CollectionEntry<>(Collections.singleton(tagEntry)));
                        }
                    }
                }
            }
        }

        return new ExtensionResult<>(shouldReplace, entries, warnings);
    }

    private static boolean testCondition(JsonElement condition, List<String> warnings) {
        if (condition instanceof JsonArray) {
            for (JsonElement child : (JsonArray) condition) {
                if (!testCondition(child, warnings)) return false;
            }
            return true;
        } else if (!(condition instanceof JsonObject)) {
            warnings.add("Error parsing tag extensions: item '" + condition + "' in condition list not a JsonObject");
            return false;
        }

        JsonObject obj = (JsonObject) condition;
        for (String key : obj.keySet()) {
            Identifier id = key.equals("or") ? new Identifier(LibCD.MODID, "or") : Identifier.tryParse(key);
            if (id == null || !ConditionalData.hasCondition(id)) {
                warnings.add("Found unknown condition: " + key);
            }

            if (!ConditionalData.testCondition(id, ConditionalData.parseElement(obj.get(key)))) return false;
        }
        return true;
    }

    public static final class ExtensionResult<T> {
        private final boolean shouldReplace;
        private final List<Tag.Entry<T>> entries;
        private final List<String> warnings;

        public ExtensionResult(boolean shouldReplace, List<Tag.Entry<T>> entries, List<String> warnings) {
            this.shouldReplace = shouldReplace;
            this.entries = entries;
            this.warnings = warnings;
        }

        public boolean shouldReplace() {
            return shouldReplace;
        }

        public List<Tag.Entry<T>> getEntries() {
            return entries;
        }

        public List<String> getWarnings() {
            return warnings;
        }
    }
}
