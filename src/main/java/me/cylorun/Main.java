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
        if (!new File("maps.json").exists()) {
            try {
                URL url = new URL("https://cdn.discordapp.com/attachments/1087349364348956682/1157074195017125988/maps.json");
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
