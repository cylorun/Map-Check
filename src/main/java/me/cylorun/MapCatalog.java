package me.cylorun;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public class MapCatalog {
    public static JsonArray load() {
        InputStream stream = MapCatalog.class.getResourceAsStream("/maps.json");
        if (stream == null) {
            throw new IllegalStateException("Map catalog is missing from the JAR");
        }
        try (Reader reader = new InputStreamReader(stream, StandardCharsets.UTF_8)) {
            return JsonParser.parseReader(reader).getAsJsonArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read the bundled map catalog", e);
        }
    }
}
