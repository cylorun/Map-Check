package me.cylorun;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {
    public static JFrame frame = new JFrame();
    public static void main(String[] args) {
        //TODO can only download once per session
        //TODO boat enter prac doesnt work(all assets put into a zip, not the traditional all assets in a folder>zip)
        if (!new File("maps.json").exists()) {
            try {
                URL url = new URL("https://cdn.discordapp.com/attachments/1087349364348956682/1157074195017125988/maps.json");
                try (InputStream in = url.openStream()) {
                    Path outputPath = Path.of("maps.json");
                    Files.copy(in, outputPath);
                } catch (IOException e) {
                }
            } catch (MalformedURLException e) {
            }
        }
        try {
            UIManager.setLookAndFeel(new FlatDarculaLaf());
        } catch (UnsupportedLookAndFeelException e) {
            throw new RuntimeException(e);
        }
            frame.add(new Panel());

            frame.setVisible(true);
            frame.setTitle("Map-Check, by cylorun");
            frame.pack();

            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }