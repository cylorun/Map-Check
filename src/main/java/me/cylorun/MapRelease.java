package me.cylorun;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapRelease {
    public static String latestUrl(String mapUrl) throws IOException {
        URL url = new URL(mapUrl);
        String[] parts = url.getPath().split("/");
        if (!url.getHost().equalsIgnoreCase("github.com") || parts.length != 7
                || !parts[3].equals("releases") || !parts[4].equals("download")) {
            return mapUrl;
        }

        String repo = parts[1] + "/" + parts[2];
        URL api = new URL("https://api.github.com/repos/" + repo + "/releases/latest");
        System.out.println("Checking latest release for " + repo);
        return latestUrl(url, api);
    }

    static String latestUrl(URL mapUrl, URL api) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) api.openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(15000);
        connection.setUseCaches(false);
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setRequestProperty("User-Agent", "Map-Check/" + MapCheck.VERSION);
        try {
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK) {
                throw new IOException("Failed to check latest release for " + mapUrl + " (HTTP " + status + ")");
            }
            try (Reader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                return selectAsset(mapUrl, JsonParser.parseReader(reader).getAsJsonObject());
            } catch (RuntimeException e) {
                throw new IOException("Invalid release information for " + mapUrl, e);
            }
        } finally {
            connection.disconnect();
        }
    }

    static String selectAsset(URL mapUrl, JsonObject release) throws IOException {
        String[] parts = mapUrl.getPath().split("/");
        String oldTag = URLDecoder.decode(parts[5], "UTF-8");
        String oldName = URLDecoder.decode(parts[6], "UTF-8");
        String tag = release.get("tag_name").getAsString();
        List<JsonObject> zips = new ArrayList<>();
        List<JsonObject> matches = new ArrayList<>();
        for (JsonElement element : release.getAsJsonArray("assets")) {
            JsonObject asset = element.getAsJsonObject();
            String name = asset.get("name").getAsString();
            if (!name.toLowerCase(Locale.ROOT).endsWith(".zip")) {
                continue;
            }
            zips.add(asset);
            if (assetKey(name, tag).equals(assetKey(oldName, oldTag))) {
                matches.add(asset);
            }
        }
        if (matches.size() == 1) {
            return matches.get(0).get("browser_download_url").getAsString();
        }
        if (zips.size() == 1) {
            return zips.get(0).get("browser_download_url").getAsString();
        }
        throw new IOException("Cannot identify the map ZIP in latest release " + tag + " for " + mapUrl);
    }

    private static String assetKey(String name, String tag) {
        String version = tag.replaceFirst("^[vV](?=\\d)", "");
        return name.replace(tag, "").replace(version, "").toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
    }
}
