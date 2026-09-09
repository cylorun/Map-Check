package me.cylorun;

import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FileUtil {
    private static final Path TEMP_FOLDER = Paths.get(System.getProperty("user.dir"), "mc_temp");

    public static List<String> downloadMapsToTemp(List<JsonObject> maps) throws IOException {
        return downloadMapsToTemp(maps, () -> MapCheckFrame.getInstance().updateProgressBar());
    }

    static List<String> downloadMapsToTemp(List<JsonObject> maps, Runnable progress) throws IOException {
        List<String> downloadedMapsPaths = new ArrayList<>();
        List<String> newSavesPaths = new ArrayList<>();
        List<String> latestUrls = new ArrayList<>();
        for (JsonObject map : maps) {
            latestUrls.add(MapRelease.latestUrl(map));
        }

        Files.createDirectories(TEMP_FOLDER);

        for (int i = 0; i < latestUrls.size(); i++) {
            String fileURL = latestUrls.get(i);
            Path saveFilePath = TEMP_FOLDER.resolve(i + ".zip");
            System.out.println("Downloading map from "+fileURL);
            URLConnection connection = new URL(fileURL).openConnection();
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            try (InputStream in = new BufferedInputStream(connection.getInputStream())) {
                Files.copy(in, saveFilePath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new IOException("Failed to download:\n" + fileURL, e);
            }
            downloadedMapsPaths.add(saveFilePath.toString());
            progress.run();
        }

        for (String path : downloadedMapsPaths) {
            newSavesPaths.add(unzipFolder(path));
            progress.run();
        }

        return newSavesPaths;
    }


    public static String unzipFolder(String zipFilePath) throws IOException {
        Path zipPath = Paths.get(zipFilePath).toAbsolutePath();
        Path extractPath = Files.createTempDirectory(zipPath.getParent(), "map-");
        try (ZipFile zipFile = new ZipFile(zipPath.toFile())) {
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                Path output = extractPath.resolve(entry.getName()).normalize();
                if (!output.startsWith(extractPath)) {
                    throw new IOException("ZIP entry is outside the map folder: " + entry.getName());
                }
                if (entry.isDirectory()) {
                    Files.createDirectories(output);
                } else {
                    Files.createDirectories(output.getParent());
                    try (InputStream in = zipFile.getInputStream(entry)) {
                        Files.copy(in, output, StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
        }
        List<Path> worlds;
        try (Stream<Path> files = Files.walk(extractPath)) {
            worlds = files.filter(path -> path.getFileName().toString().equals("level.dat") && Files.isRegularFile(path))
                    .map(Path::getParent).collect(Collectors.toList());
        }
        if (worlds.size() != 1 || worlds.get(0).equals(extractPath)) {
            throw new IOException("Expected one named Minecraft world folder in " + zipFilePath);
        }
        Files.delete(zipPath);
        return worlds.get(0).toString();
    }

    public static void copyFromTemp(List<String> instances, List<String> tempPaths) throws IOException {
        System.out.println("Instance Paths: " + instances);
        System.out.println("World Paths: " + tempPaths);
        for (String instance : instances) {
            for (String map : tempPaths) {
                MapCheckFrame.getInstance().updateProgressBar();
                FileUtils.copyDirectoryToDirectory(new File(map), new File(instance));
            }
        }
    }


    public static void clearTemp() {
        FileUtils.deleteQuietly(TEMP_FOLDER.toFile());
    }
}
