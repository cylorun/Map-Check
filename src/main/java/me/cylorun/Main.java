package me.cylorun;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static String VERSION = "4.0.3";
    public static void main(String[] args) throws UnsupportedLookAndFeelException, IOException {
        UIManager.setLookAndFeel(new FlatDarculaLaf());
        new MapCheckFrame();
    }
}
