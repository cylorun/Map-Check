package me.cylorun;

import javax.swing.*;
import java.io.File;

public class InstanceSelect {
    private JFileChooser fileChooser = new JFileChooser();

    public InstanceSelect() {
        String defaultDir = System.getProperty("user.home") + "\\Desktop";
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setCurrentDirectory(new File(defaultDir));

        int response = fileChooser.showOpenDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();

            for (File file : selectedFiles) {
                File savesFolder = new File(file + "\\.minecraft\\saves");

                if (savesFolder.exists()) {
                    FileUtil.instancePaths.add(savesFolder.getAbsolutePath());
                } else {

                    int choice = JOptionPane.showConfirmDialog(null, file.getAbsolutePath() + "\n is not a minecraft directory \n Would you still like to add it?", "Invalid Directory", JOptionPane.YES_NO_OPTION);

                    if (choice == JOptionPane.YES_OPTION) {
                        boolean matchFound = false;

                        for (File f : file.listFiles()) {
                            if (f.getName().contains(".minecraft")) {
                                savesFolder.mkdir();
                                FileUtil.instancePaths.add(savesFolder.getAbsolutePath());
                                matchFound = true;
                                break;
                            }
                        }
                        if (!matchFound) {
                            FileUtil.instancePaths.add(file.getAbsolutePath());
                        }

                    }
                    }
                }
            }
        }
    }
