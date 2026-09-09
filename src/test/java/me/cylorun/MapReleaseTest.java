package me.cylorun;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class MapReleaseTest {
    private final JsonObject map = map("Test map", ".*");

    @Test
    void selectsMapInsteadOfDatapack() throws Exception {
        JsonObject release = release("v1.4", "BTPractice-Datapack_v1.4.zip", "BTPractice-Map_v1.4.zip");
        assertEquals("https://example.com/BTPractice-Map_v1.4.zip",
                MapRelease.selectAsset(map("BT Practice", "BTPractice-Map.*\\.zip"), release));
    }

    @Test
    void acceptsRenamedZipAndIgnoresOtherFiles() throws Exception {
        assertEquals("https://example.com/new-map.zip",
                MapRelease.selectAsset(map, release("v2.0", "mod.jar", "new-map.zip")));
    }

    @Test
    void rejectsMissingOrAmbiguousMap() {
        assertThrows(IOException.class, () -> MapRelease.selectAsset(map, release("v2.0", "mod.jar")));
        assertThrows(IOException.class, () -> MapRelease.selectAsset(map, release("v2.0", "one.zip", "two.zip")));
        assertThrows(IOException.class, () -> MapRelease.selectAsset(map("BT Practice", "BTPractice-Map.*\\.zip"),
                release("v2.0", "BTPractice-Datapack_v2.0.zip")));
    }

    @Test
    void keepsMinecraftVersionWhenMatchingAssets() throws Exception {
        JsonObject endMap = map("End Practice", ".*-1\\.16\\.1\\.zip");
        assertEquals("https://example.com/End_v3.5.0-1.16.1.zip", MapRelease.selectAsset(endMap,
                release("v3.5.0", "End_v3.5.0-1.20.1.zip", "End_v3.5.0-1.16.1.zip")));
    }

    @Test
    void rejectsOldCatalogEntriesInsteadOfDownloadingPinnedUrls() {
        JsonObject oldMap = new JsonObject();
        oldMap.addProperty("label", "Zero Cycle");
        oldMap.addProperty("url", "https://github.com/Mescht/Zero-Practice/releases/download/v1.2.1/Zero.Practice.v1.2.1.zip");
        assertThrows(IOException.class, () -> MapRelease.latestUrl(oldMap));
    }

    @Test
    void bundledCatalogHasRepositoriesAndNoPinnedUrls() {
        boolean zeroFound = false;
        for (JsonElement element : MapCatalog.load()) {
            JsonObject entry = element.getAsJsonObject();
            assertTrue(entry.get("repository").getAsString().matches("[^/]+/[^/]+"));
            assertFalse(entry.has("url"));
            if (entry.get("label").getAsString().equals("Zero Cycle")) {
                assertEquals("Mescht/Zero-Practice", entry.get("repository").getAsString());
                zeroFound = true;
            }
        }
        assertTrue(zeroFound);
    }

    @Test
    void checksAgainOnEachInstallAndReportsHttpErrors() throws Exception {
        AtomicInteger requests = new AtomicInteger();
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/latest", exchange -> {
            int request = requests.incrementAndGet();
            byte[] body = release("v" + request, "map-" + request + ".zip").toString().getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(request > 2 ? 403 : 200, body.length);
            exchange.getResponseBody().write(body);
            exchange.close();
        });
        server.start();
        try {
            URL api = new URL("http://127.0.0.1:" + server.getAddress().getPort() + "/latest");
            assertEquals("https://example.com/map-1.zip", MapRelease.latestUrl(map, api));
            assertEquals("https://example.com/map-2.zip", MapRelease.latestUrl(map, api));
            IOException error = assertThrows(IOException.class, () -> MapRelease.latestUrl(map, api));
            assertTrue(error.getMessage().contains("HTTP 403"));
        } finally {
            server.stop(0);
        }
    }

    private static JsonObject map(String label, String pattern) {
        JsonObject map = new JsonObject();
        map.addProperty("label", label);
        map.addProperty("repository", "owner/repo");
        map.addProperty("assetPattern", pattern);
        return map;
    }

    private JsonObject release(String tag, String... names) {
        JsonObject release = new JsonObject();
        release.addProperty("tag_name", tag);
        release.add("assets", JsonParser.parseString("[]"));
        for (String name : names) {
            JsonObject asset = new JsonObject();
            asset.addProperty("name", name);
            asset.addProperty("browser_download_url", "https://example.com/" + name);
            release.getAsJsonArray("assets").add(asset);
        }
        return release;
    }
}
