package me.cylorun;

import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;


public class MapCheck {
    public static final String VERSION = "4.2.0";
    public static URL MAPS_URL;

    static {
        try {
            MAPS_URL = new URL("https://cylorun.com/api/maps");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatDarculaLaf());
        MapCheckFrame.getInstance();
    }
}