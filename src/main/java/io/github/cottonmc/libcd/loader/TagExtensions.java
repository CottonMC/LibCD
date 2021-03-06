package io.github.cottonmc.libcd.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.cottonmc.libcd.api.CDCommons;
import io.github.cottonmc.libcd.api.CDSyntaxError;
import io.github.cottonmc.libcd.api.condition.ConditionalData;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;

import javax.annotation.Nullable;
import java.util.*;

public final class TagExtensions {
    private TagExtensions() {
    }

    /**
     * Loads the tag extensions for a given tag from the JSON object.
     *
     * @param json the JSON object
     */
    public static ExtensionResult load(JsonObject json) {
        boolean shouldReplace = false;
        List<Tag.Entry> entries = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        Identifier defaultEntry = null;

        if (json.has("replace")) {
            shouldReplace = testCondition(json.get("replace"), warnings);
        }

        if (json.has("entries")) {
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
                        @Nullable String value = values.get(i).getAsString();

                        if (value == null) {
                            warnings.add("Could not convert JSON element '" + values.get(i) + "' to a string in tag extensions! Skipping...");
                        } else if (value.startsWith("#")) {
                            entries.add(new Tag.TagEntry(new Identifier(value.substring(1))));
                        } else {
                            // value is stored as an Identifier in an ObjectEntry and then later read/verified in ObjectEntry#resolve.
                            entries.add(new Tag.ObjectEntry(new Identifier(value)));
                        }
                    }
                }
            }
        }

        if (json.has("default")) {
            defaultEntry = new Identifier(json.get("default").getAsString());
        }

        return new ExtensionResult(shouldReplace, entries, warnings, defaultEntry);
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
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String key = entry.getKey();
            Identifier id = key.equals("or") ? new Identifier(CDCommons.MODID, "or") : Identifier.tryParse(key);
            if (id == null || !ConditionalData.hasCondition(id)) {
                warnings.add("Found unknown condition: " + key);
            }

            try {
                if (!ConditionalData.testCondition(id, ConditionalData.parseElement(obj.get(key)))) return false;
            } catch (CDSyntaxError e) {
                warnings.add("Error parsing tag extensions: item '" + condition + "' in condition list errored: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    public static final class ExtensionResult {
        private final boolean shouldReplace;
        private final List<Tag.Entry> entries;
        private final List<String> warnings;
        private final Identifier defaultEntry;

        public ExtensionResult(boolean shouldReplace, List<Tag.Entry> entries, List<String> warnings, Identifier defaultEntry) {
            this.shouldReplace = shouldReplace;
            this.entries = entries;
            this.warnings = warnings;
            this.defaultEntry = defaultEntry;
        }

        public boolean shouldReplace() {
            return shouldReplace;
        }

        public List<Tag.Entry> getEntries() {
            return entries;
        }

        public List<String> getWarnings() {
            return warnings;
        }

        public Identifier getDefaultEntry() {
            return defaultEntry;
        }
    }
}
