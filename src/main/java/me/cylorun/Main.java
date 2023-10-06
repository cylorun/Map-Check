package me.cylorun;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    private static final UIManager uiManager = new UIManager();
    public static JFrame frame;
    public static void main(String[] args) {
        //TODO boat enter prac doesnt work(all assets put into a zip, not the traditional all assets in a folder>zip)
        //TODO remove maps
        if (!new File("maps.json").exists()) {
            try {
                URL url = new URL("https://gist.githubusercontent.com/cylorun/3cd5d459d9adc9ad28608e8ed606aadb/raw/7a84e9ea0e892af7dfb49e5aae268e35034a66d4/maps.json");
                Files.copy(url.openStream(), Path.of("maps.json"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            try {
                uiManager.setLookAndFeel(new FlatDarculaLaf());

            } catch (UnsupportedLookAndFeelException e) {
                throw new RuntimeException(e);
            }
            frame = new JFrame();
            frame.add(new Panel());

            frame.setVisible(true);
            frame.setTitle("Map-Check");
            frame.pack();

            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }
}
