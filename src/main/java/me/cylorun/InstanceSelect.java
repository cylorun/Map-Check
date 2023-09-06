package me.cylorun;

import javax.swing.*;
import java.io.File;

public class InstanceSelect {
    private JFileChooser fileChooser = new JFileChooser();
    public InstanceSelect(){
        //String defaultDir = System.getProperty("user.home") + "\\Desktop";
        String defaultDir = "C:\\Users\\alfgr\\Desktop\\mcsr\\MultiMC\\instances";
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fileChooser.setCurrentDirectory(new File(defaultDir));


        int response = fileChooser.showOpenDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles){
                String savesFolder = file + "\\.minecraft\\saves";
                if (new File(savesFolder).exists()) {
                    FileUtil.instancePaths.add(savesFolder);
                } else {
                    JOptionPane.showMessageDialog(new JFrame(),savesFolder + "\n is not a minecraft directory");
                }
            }
        }
    }
}
