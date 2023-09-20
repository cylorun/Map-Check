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
    private boolean boo = false;
    private File subFolder;
    private String unzippedFolderPath;
    public boolean ready = false;

    public void downloadMaps(String instance) {
        for (String fileURL : maps) {
            try {
                Files.createDirectories(Paths.get(instance));

                URL url = new URL(fileURL);
                HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                int responseCode = httpConn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    String fileName = "";
                    String disposition = httpConn.getHeaderField("Content-Disposition");
                    if (disposition != null && disposition.contains("filename=")) {
                        fileName = disposition.substring(21);
                    }
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
                        unzippedFolderPath = path.substring(0, path.lastIndexOf(".zip"));
                        unFolderInAFolder(new File(unzippedFolderPath));
                        new File(path).delete();
                    }
                    ready = true;

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void unZip(String file) {
        String extractPath = file.replace(".zip", "");

        try (ZipFile zipFile = new ZipFile(file)) {
            File targetDir = new File(extractPath);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }

            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                String entryName = entry.getName();

                File outputFile = new File(targetDir, entryName);

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


    public void unFolderInAFolder(File parentFolder) throws IOException {

        File[] files = parentFolder.listFiles();
        if (files != null && files.length > 0) {
            subFolder = new File(files[0].getAbsolutePath());
        }
        File f = new File(String.valueOf(subFolder));
        File[] subfolderContents = subFolder.listFiles();
        if (subfolderContents != null) {

            for (File subfolderContent : subfolderContents) {
                if (!subfolderContent.equals(subFolder)) {
                    Path destinationPath = parentFolder.toPath().resolve(subfolderContent.getName());

                    if (subfolderContent.isDirectory()) {
                        Files.move(subfolderContent.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                        unFolderInAFolder(destinationPath.toFile());
                    } else {
                        Files.move(subfolderContent.toPath(), destinationPath, StandardCopyOption.REPLACE_EXISTING);
                    }
                    //deletes empty folder in the map folder
                    f.delete();


                }
            }
        }
    }

    public static void copyFolder(File source, File destination) throws IOException {
        try {
                FileUtils.copyDirectory(source, destination);

        } catch(IOException e){
            e.printStackTrace();
        }

    }
}