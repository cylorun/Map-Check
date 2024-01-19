package me.cylorun;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.io.IOException;

public class Main {
    public static String VERSION = "4.0.2";
    public static void main(String[] args) throws UnsupportedLookAndFeelException, IOException {
        // if u add a map the program will die lmao
        UIManager.setLookAndFeel(new FlatDarculaLaf());
        new MapCheckFrame();
    }
}
