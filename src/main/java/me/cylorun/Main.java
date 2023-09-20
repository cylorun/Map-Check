package me.cylorun;

import javax.swing.*;
import java.util.ArrayList;



public class Main {
    private static JFrame frame = new JFrame();
    private static Panel panel = new Panel();
    private static final String version = "0.1";

    public static void main(String[] args) {
        frame.setVisible(true);
        frame.setTitle("Map Check v"+version);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        FileUtil.instancePaths.add("C:\\Users\\alfgr\\Desktop\\mcsr\\MultiMC\\instances\\autoresetter1\\.minecraft\\saves");
        FileUtil.instancePaths.add("C:\\Users\\alfgr\\Desktop\\mcsr\\MultiMC\\instances\\autoresetter2\\.minecraft\\saves");



    }
}