package me.cylorun;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.*;
import org.apache.commons.io.FileUtils;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;




public class FileUtil {
    public static ArrayList<String> maps = new ArrayList<>();
    public static ArrayList<String> mapPaths = new ArrayList<>();
    public static ArrayList<String> instancePaths = new ArrayList<>();

    public static void downloadMaps(String instance) {
        for (String fileURL : maps) {
            try {
                Files.createDirectories(Paths.get(instance));

                URL url = new URL(fileURL);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                int responseCode = httpConn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String fileName = fileURL.substring(fileURL.lastIndexOf('/') + 1);
                    String saveFilePath = Paths.get(instance, fileName).toString();
                    mapPaths.add(saveFilePath);

                    try (InputStream inputStream = httpConn.getInputStream();
                         FileOutputStream outputStream = new FileOutputStream(saveFilePath)) {
                        int bytesRead;
                        byte[] buffer = new byte[4096];
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    for (String path : mapPaths) {
                        unZip(path);

                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void unZip(String file) {
        String extractPath = file.replace(".zip", "");
        if (new File(file).exists()) {
            try (ZipFile zipFile = new ZipFile(file)) {
                File targetDir = new File(extractPath);

                if (!targetDir.exists()) {  // useless i think
                    targetDir.mkdirs();
                }

                Enumeration<? extends ZipEntry> entries = zipFile.entries();
                boolean match = false;
                while (entries.hasMoreElements()) {
                    ZipEntry entry = entries.nextElement();
                    String entryName = entry.getName();
                    File outputFile = new File(targetDir.getParentFile(), entryName);

                    //System.out.println(outputFile);
                    if (outputFile.isDirectory() && !match) { // add a check to make sure it's the closest folder to the map origin
                        match = true;
                        File f = new File(String.valueOf(outputFile.getParentFile()));
                        mapPaths.add(f.getAbsolutePath());
                        mapPaths.remove(file);
                        System.out.println(f);


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
            new File(file).delete(); // zip folder
            new File(extractPath).delete(); // funny folder, currently the copyFolder method used in download.aactionlist copies this empty folder and not the pther one


        }
    }



    public static void copyFolder(File source, File destination){
        try {
            if (source.exists()) {
                FileUtils.copyDirectoryToDirectory(source, destination);
                }
        } catch(IOException e) {
                e.printStackTrace();
        }
    }
}