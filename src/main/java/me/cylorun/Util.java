package me.cylorun;

import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class Util {
    public static java.util.List<String> mapUrls = new ArrayList<>();
    public static java.util.List<String> mapPaths = new ArrayList<>();
    public static java.util.List<String> instancePaths = new ArrayList<>();

    public static void log(int level, String msg) {
        LocalTime localTime = LocalTime.now();
        String time = localTime.toString().substring(0, localTime.toString().lastIndexOf('.'));
        switch (level) {
            case 0 -> System.out.printf("[%s/INFO] %s\n", time, msg);
            case 1 -> System.out.printf("[%s/WARN] %s\n", time, msg);
            case 2 -> System.err.printf("[%s/ERROR] %s\n", time, msg);
        }
    }

    public static void errorPane(Exception e) {
        log(2, e.toString());
        String errorMessage = String.format("<html>An error has occured, you can send this to @cylorun on discord for help! <br> <font color='yellow'>%s</font> </html>\n", e);

        JPanel panel = new JPanel(new FlowLayout());
        JButton copyButton = new JButton("Copy");

        panel.add(new JLabel(errorMessage));
        panel.add(copyButton);

        copyButton.setPreferredSize(new Dimension(73, 35));
        copyButton.addActionListener(event -> {
            StringSelection stringSelection = new StringSelection(e.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(null,
                    "Copied error to clipboard",
                    "Send to @cylorun",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        JOptionPane.showMessageDialog(
                null,
                panel,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public static void downloadMaps(String instance) {
        for (String fileURL : mapUrls) {
            try {
                URL url = new URL(fileURL);
                String fileName = fileURL.substring(fileURL.lastIndexOf('/') + 1);
                String saveFilePath = Paths.get(instance, fileName).toString();
                Files.copy(url.openStream(), Path.of(saveFilePath));

                mapPaths.add(saveFilePath);
                log(0, "Downloaded " + fileName+ ", now unzipping");
                for (String path : mapPaths) {
                    unZip(path);
                }
            } catch (IOException e) {
                errorPane(e);
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
                        mapPaths.add(String.valueOf(outputFile.getParentFile()));
                        mapPaths.remove(file);
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
                errorPane(e);
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
            errorPane(e);
        }
    }
}