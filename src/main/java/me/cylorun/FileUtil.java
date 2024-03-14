package me.cylorun;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class FileUtil {
    private  static Path TEMP_FOLDER = Paths.get(System.getProperty("user.dir"), "mc_temp");

    public static List<String> downloadToTemp(List<String> maps){
        List<String> downloadedMapsPaths = new ArrayList<>();
        List<String> newSavesPaths = new ArrayList<>();

        try {
            Files.createDirectory(TEMP_FOLDER);
        } catch (IOException ignored) {
        }
        for (String fileURL : maps) {
                String fileName = fileURL.substring(fileURL.lastIndexOf('/') + 1);
                Path saveFilePath = Paths.get(TEMP_FOLDER.toString(), fileName);
                try (InputStream in = new BufferedInputStream(new URL(fileURL).openStream())) {
                    Files.copy(in, saveFilePath, StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    JOptionPane.showMessageDialog(null,"Failed to download:\n"+fileURL,"Error",JOptionPane.ERROR_MESSAGE);
                }
                downloadedMapsPaths.add(saveFilePath.toString());
                MapCheckFrame.updateProgressBar();

            }
            for (String path : downloadedMapsPaths) {
                try {
                    newSavesPaths.add(unzipFolder(path));
                } catch (IOException e) {
                    MapCheckFrame.exceptionPane(e);
                }
                MapCheckFrame.updateProgressBar();


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
            } catch (Exception e) {
                MapCheckFrame.exceptionPane(e);
            }
            Files.delete(Paths.get(zipFilePath));
        }
        return savesFile;
    }
    public static String removeFileExt(String s){
        return s.substring(0,s.lastIndexOf('.'));
    }
    public static void copyFromTemp(List<String> instances, List<String> tempPaths) throws IOException {
        System.out.println("Instance Paths: " + instances);
        System.out.println("World Paths: " + tempPaths);
        for (String instance : instances) {
            for (String map : tempPaths) {
                MapCheckFrame.updateProgressBar();
                copyFolder(map.replace(".zip", ""), instance);
            }
        }
        tempPaths.clear();

    }


    public static void copyFolder(String source, String destination) throws IOException {
            FileUtils.copyDirectoryToDirectory(new File(source), new File(destination));

    }
}