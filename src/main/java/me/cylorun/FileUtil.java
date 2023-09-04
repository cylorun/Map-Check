package me.cylorun;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;




public class FileUtil {
    public static ArrayList<String> maps = new ArrayList<>();
    private static ArrayList<String> mapPaths = new ArrayList<>();
    public static ArrayList<String> instancePaths = new ArrayList<>();
    public static boolean b = false;

    public static void downloadMaps() {

        for (String fileURL : maps) {
            for (String instance : instancePaths) {
                try {
                    Files.createDirectories(Paths.get(instance));

                    URL url = new URL(fileURL);
                    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
                    int responseCode = httpConn.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        String fileName = "";
                        String disposition = httpConn.getHeaderField("Content-Disposition");

                        if (disposition != null) {
                            int index = disposition.indexOf("filename=");
                            if (index > 0) {
                                fileName = disposition.substring(index + 10);
                            }
                        } else {
                            int slashIndex = fileURL.lastIndexOf('/');
                            if (slashIndex >= 0 && slashIndex < fileURL.length() - 1) {
                                fileName = fileURL.substring(slashIndex + 1);
                            } else {
                                System.err.println("Invalid fileURL: " + fileURL);
                                continue; // Skip this URL and proceed to the next one
                            }
                        }
                        if (disposition != null) {
                            int index = disposition.indexOf("filename=");
                            if (index > 0) {
                                fileName = disposition.substring(index + 10);
                            }
                        } else {
                            int slashIndex = fileURL.lastIndexOf('/');
                            if (slashIndex >= 0 && slashIndex < fileURL.length() - 1) {
                                fileName = fileURL.substring(slashIndex + 1);
                            } else {
                                System.err.println("Invalid fileURL: " + fileURL);
                                continue; // Skip this URL and proceed to the next one
                            }
                        }

                        String saveFilePath = instance + File.separator + fileName;
                        mapPaths.add(saveFilePath);

                        try (InputStream inputStream = httpConn.getInputStream();
                             FileOutputStream outputStream = new FileOutputStream(saveFilePath)) {

                            int bytesRead;
                            byte[] buffer = new byte[4096];
                            while ((bytesRead = inputStream.read(buffer)) != -1) {
                                outputStream.write(buffer, 0, bytesRead);
                            }


                            httpConn.disconnect();
                            b = true;


                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();

                }

            }
        }
    }

    public static void unZip(String file) {
        String extractPath = file.replace("zip", "");

        try {
            ZipFile zipFile = new ZipFile(file);


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
                    InputStream inputStream = zipFile.getInputStream(entry);
                    FileOutputStream outputStream = new FileOutputStream(outputFile);
                    byte[] buffer = new byte[1024];
                    int bytesRead;

                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                    inputStream.close();
                }
            }
            zipFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void unFolderInAFolder(File parentFolder) throws IOException {

        File subFolder = new File(parentFolder.listFiles()[0].getAbsolutePath());
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

                    System.out.println("Moved: " + subfolderContent.getName());
                }
            }
        }
        subFolder.delete();
    }
    public static void getMaps(){
        downloadMaps();
        if (b) {
            for (String file : mapPaths) {
                unZip(file);
                String unzippedFolderPath = file.substring(0, file.lastIndexOf(".zip"));

                try {
                    unFolderInAFolder(new File(unzippedFolderPath));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                new File(file).delete();
            }
        }
        JOptionPane.showMessageDialog(new JFrame(),"Finished downloading");
    }

}
