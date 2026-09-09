package me.cylorun;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;


public class MapCheck {
    public static final String VERSION = "4.4.0";

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatDarculaLaf());
        try {
            MapCheckFrame.getInstance();
        } catch (IllegalStateException e) {
            MapCheckFrame.showError(e.getMessage());
            System.exit(1);
        }
    }
}
