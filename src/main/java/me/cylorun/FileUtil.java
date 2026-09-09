package me.cylorun;

import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


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

        for (String fileURL : latestUrls) {
            int idx = fileURL.lastIndexOf('.');
            String end = idx == -1 ? ".zip" : fileURL.substring(idx);

            if(end.length() > 4){
                end =  ".zip";
            }
            String fileName = String.valueOf(latestUrls.indexOf(fileURL)) + end;
            Path saveFilePath = Paths.get(TEMP_FOLDER.toString(), fileName);
            System.out.println("Downloading map from "+fileURL);
            try (InputStream in = new BufferedInputStream(new URL(fileURL).openStream())) {
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
        String extractPath = removeFileExt(zipFilePath);
        String savesFile = null;
        if (new File(zipFilePath).exists() && !new File(zipFilePath).isDirectory()) {
            try (ZipFile zipFile = new ZipFile(zipFilePath)) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                boolean match = false;
                while (entries.hasMoreElements()) {

                    ZipEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    File outputFile = new File(new File(extractPath).getParentFile(), entryName);

                    if (!match && !outputFile.getParentFile().getAbsolutePath().endsWith("saves") && !outputFile.getParentFile().getAbsolutePath().endsWith("mc_temp")) {
                        savesFile = outputFile.getParentFile().getAbsolutePath();
                        match = true;
                    }

                    if (entry.isDirectory()) {
                        outputFile.mkdirs();

                    } else {
                        try (InputStream inputStream = zipFile.getInputStream(entry);
                             FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                            byte[] buffer = new byte[1024];
                            int bytesRead;

                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                }
            }
            Files.delete(Paths.get(zipFilePath));
        }
        if (savesFile == null) {
            throw new IOException("No map folder found in " + zipFilePath);
        }
        return savesFile;
    }

    public static String removeFileExt(String s) {
        return s.substring(0, s.lastIndexOf('.'));
    }

    public static void copyFromTemp(List<String> instances, List<String> tempPaths) throws IOException {
        System.out.println("Instance Paths: " + instances);
        System.out.println("World Paths: " + tempPaths);
        for (String instance : instances) {
            for (String map : tempPaths) {
                MapCheckFrame.getInstance().updateProgressBar();
                if (map != null) {
                    copyFolder(map.replace(".zip", ""), instance);
                }
            }
        }
        tempPaths.clear();

    }


    public static void copyFolder(String source, String destination) throws IOException {
        FileUtils.copyDirectoryToDirectory(new File(source), new File(destination));
    }
}
