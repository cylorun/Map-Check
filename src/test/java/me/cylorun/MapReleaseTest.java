package me.cylorun;

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
    private final URL mapUrl;

    MapReleaseTest() throws Exception {
        mapUrl = new URL("https://github.com/Mescht/BTPractice/releases/download/v1.3/BTPractice-Map_v1.3.zip");
    }

    @Test
    void selectsMapInsteadOfDatapack() throws Exception {
        JsonObject release = release("v1.4", "BTPractice-Datapack_v1.4.zip", "BTPractice-Map_v1.4.zip");
        assertEquals("https://example.com/BTPractice-Map_v1.4.zip", MapRelease.selectAsset(mapUrl, release));
    }

    @Test
    void acceptsRenamedZipAndIgnoresOtherFiles() throws Exception {
        assertEquals("https://example.com/new-map.zip",
                MapRelease.selectAsset(mapUrl, release("v2.0", "mod.jar", "new-map.zip")));
    }

    @Test
    void rejectsMissingOrAmbiguousMap() {
        assertThrows(IOException.class, () -> MapRelease.selectAsset(mapUrl, release("v2.0", "mod.jar")));
        assertThrows(IOException.class, () -> MapRelease.selectAsset(mapUrl, release("v2.0", "one.zip", "two.zip")));
    }

    @Test
    void keepsMinecraftVersionWhenMatchingAssets() throws Exception {
        URL endMap = new URL("https://github.com/owner/repo/releases/download/v3.4.0/End_v3.4.0-1.16.1.zip");
        assertEquals("https://example.com/End_v3.5.0-1.16.1.zip", MapRelease.selectAsset(endMap,
                release("v3.5.0", "End_v3.5.0-1.20.1.zip", "End_v3.5.0-1.16.1.zip")));
    }

    @Test
    void leavesDirectDownloadsAlone() throws Exception {
        String direct = "https://example.com/map.zip";
        assertEquals(direct, MapRelease.latestUrl(direct));
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
            assertEquals("https://example.com/map-1.zip", MapRelease.latestUrl(mapUrl, api));
            assertEquals("https://example.com/map-2.zip", MapRelease.latestUrl(mapUrl, api));
            IOException error = assertThrows(IOException.class, () -> MapRelease.latestUrl(mapUrl, api));
            assertTrue(error.getMessage().contains("HTTP 403"));
        } finally {
            server.stop(0);
        }
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
