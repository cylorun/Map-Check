package me.cylorun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import org.apache.commons.io.FileUtils;

import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static me.cylorun.MapCheckFrame.downloadedMapsPaths;


public class FileUtil {

    public static void downloadMaps(String instance, List<String> maps) {
        for (String fileURL : maps) {
            try {
                URL url = new URL(fileURL);
                String fileName = fileURL.substring(fileURL.lastIndexOf('/') + 1);
                String saveFilePath = Paths.get(instance, fileName).toString();
                Files.copy(url.openStream(), Path.of(saveFilePath));

                downloadedMapsPaths.add(saveFilePath);

                for (String path : downloadedMapsPaths) {
                    unZip(path);
                }
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    }


    public static void unZip(String file) {
        String extractPath = file.replace(".zip", "");
        if (new File(file).exists() && !new File(file).isDirectory()) {
            try (ZipFile zipFile = new ZipFile(file)) {
                File targetDir = new File(extractPath);

                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                boolean match = false;
                while (entries.hasMoreElements()) {

                    ZipEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    File outputFile = new File(targetDir.getParentFile(), entryName);

                    if (!match && !outputFile.getParentFile().getAbsolutePath().endsWith("saves")) {
                        downloadedMapsPaths.add(String.valueOf(outputFile.getParentFile()));
                        downloadedMapsPaths.remove(file);
                        System.out.println(outputFile.getParentFile().getAbsolutePath()+" feinburh");
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
            } catch (IOException e) {
                e.printStackTrace();
            }
            new File(file).delete();
        }
    }


    public static void copyFolder(File source, File destination) {
        try {
            if (source.exists()) {
                FileUtils.copyDirectoryToDirectory(source, destination);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}