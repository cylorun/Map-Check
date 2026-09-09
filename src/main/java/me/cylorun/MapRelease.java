package me.cylorun;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class MapRelease {
    public static String latestUrl(JsonObject map) throws IOException {
        if (!map.has("repository")) {
            throw new IOException("Map catalog entry has no GitHub repository: " + map.get("label"));
        }
        String repo = map.get("repository").getAsString();
        URL api = new URL("https://api.github.com/repos/" + repo + "/releases/latest");
        System.out.println("Checking latest release for " + repo);
        return latestUrl(map, api);
    }

    static String latestUrl(JsonObject map, URL api) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) api.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setRequestProperty("User-Agent", "Map-Check/" + MapCheck.VERSION);
        try {
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("Failed to check latest release for " + map.get("label").getAsString() + " (HTTP " + status + ")");
            }
            try (Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                return selectAsset(map, JsonParser.parseReader(reader).getAsJsonObject());
            } catch (RuntimeException e) {
                throw new IOException("Invalid release information for " + map.get("label").getAsString(), e);
            }
        } finally {
            connection.disconnect();
        }
    }

    static String selectAsset(JsonObject map, JsonObject release) throws IOException {
        String tag = release.get("tag_name").getAsString();
        Pattern pattern = Pattern.compile(map.has("assetPattern") ? map.get("assetPattern").getAsString() : ".*", Pattern.CASE_INSENSITIVE);
        List<JsonObject> matches = new ArrayList<>();
        for (JsonElement element : release.getAsJsonArray("assets")) {
            JsonObject asset = element.getAsJsonObject();
            String name = asset.get("name").getAsString();
            if (!name.toLowerCase(Locale.ROOT).endsWith(".zip")) {
                continue;
            }
            if (pattern.matcher(name).matches()) {
                matches.add(asset);
            }
        }
        if (matches.size() == 1) {
            System.out.println(map.get("label").getAsString() + ": " + tag + " - " + matches.get(0).get("name").getAsString());
            return matches.get(0).get("browser_download_url").getAsString();
        }
        throw new IOException("Cannot identify the map ZIP in latest release " + tag + " for " + map.get("label").getAsString());
    }
}
