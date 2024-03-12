package me.cylorun;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class FileUtil {

    public static List<Path> downloadToTemp(List<String> maps){
        List<String> downloadedMapsPaths = new ArrayList<>();
        List<Path> newSavesPaths = new ArrayList<>();

        String tempFolder = Paths.get(System.getProperty("user.dir"), "mc_temp").toString();
        new File(tempFolder).mkdir();
        try {
            for (String fileURL : maps) {
                String fileName = fileURL.substring(fileURL.lastIndexOf('/') + 1);
                String saveFilePath = Paths.get(tempFolder, fileName).toString();
                Files.copy(new URL(fileURL).openStream(), Paths.get(saveFilePath));
                downloadedMapsPaths.add(saveFilePath);
                MapCheckFrame.updateProgressBar();

            }
            for (String path : downloadedMapsPaths) {
                newSavesPaths.add(unzipFolder(path));
                MapCheckFrame.updateProgressBar();


            }

        } catch (Exception e) {
            MapCheckFrame.exceptionPane(e);
        }

        return newSavesPaths;
    }

    public static Path unzipFolder(String zipFilePath) throws IOException {
        String extractPath = zipFilePath.substring(0, zipFilePath.lastIndexOf('.'));
        Path savesFile = null;
        if (new File(zipFilePath).exists() && !new File(zipFilePath).isDirectory()) {
            try (ZipFile zipFile = new ZipFile(zipFilePath)) {
                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                boolean match = false;
                while (entries.hasMoreElements()) {

                    ZipEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    File outputFile = new File(new File(extractPath).getParentFile(), entryName);

                    if (!match && !outputFile.getParentFile().getAbsolutePath().endsWith("saves") && !outputFile.getParentFile().getAbsolutePath().endsWith("mc_temp")) {
                        savesFile = outputFile.getParentFile().toPath();
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
            } catch (Exception e) {
                MapCheckFrame.exceptionPane(e);
            }
            new File(zipFilePath).delete();
        }
        return savesFile;
    }

    public static void copyFromTemp(List<Path> instances, List<Path> tempPaths) {
        System.out.println("Instance Paths: " + instances);
        System.out.println("World Paths: " + tempPaths);
        for (Path instance : instances) {
            for (Path map : tempPaths) {
                MapCheckFrame.updateProgressBar();
                copyFolder(FileUtil.removeFileExt(map.toString()), instance.toString());
            }
        }
        tempPaths.clear();
    }

    private static String removeFileExt(String filePath){
       return filePath.substring(0, filePath.lastIndexOf('.'));
    }
    public static void copyFolder(String source, String destination) {
        try {
            FileUtils.copyDirectoryToDirectory(new File(source), new File(destination));
        } catch (IOException e) {
            MapCheckFrame.exceptionPane(e);
        }
    }
}