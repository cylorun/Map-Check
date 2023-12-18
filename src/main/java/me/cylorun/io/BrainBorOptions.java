package me.cylorun.io;

import me.cylorun.utili.BrainBorException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class BrainBorOptions {
    public boolean rsg_mode;
    public String mainInstPath;
    public String secondInstPath;
    public int reloadKey;
    public boolean onTop;
    private static Properties properties;
    private final String[] settings = {"rsg_mode", "reload_key", "main_inst", "second_inst","on_top"};
    private static final String configFile = System.getProperty("user.dir") + "\\config.properties";

    public BrainBorOptions() {
        properties = new Properties();
        if (!new File(configFile).exists()) {
            setDefaultConfig();
        }
        this.loadOptions();
//        System.out.printf("Rsg Mode: %s \nAlways top: %s\nReload Key: %s\nMain Instance: %s\nSecond Instance: %s\n", rsg_mode, onTop,new Hotkey(this.reloadKey).getKey(), mainInstPath, secondInstPath);

    }

    public void loadOptions() {
        this.rsg_mode = Boolean.parseBoolean(getSetting(settings[0]));
        this.reloadKey = Integer.parseInt(getSetting(settings[1]));
        this.mainInstPath = getSetting(settings[2]);
        this.secondInstPath = getSetting(settings[3]);
        this.onTop = Boolean.parseBoolean(getSetting(settings[4]));
    }

    public void setOption(String option, String value) {
        saveSetting(option, value);
        this.loadOptions();
    }

    public static String getSetting(String property) {
        try (FileInputStream fileInputStream = new FileInputStream(configFile)) {
            properties.load(fileInputStream);
            return properties.getProperty(property);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveSetting(String property, String value) {
        properties.setProperty(property, value);
        try (FileOutputStream fileOutputStream = new FileOutputStream(configFile)) {
            properties.store(fileOutputStream, null);
        } catch (IOException e) {
            throw new BrainBorException(e);
        }
    }

    private static void setDefaultConfig() {
        saveSetting("main_inst", "C://");
        saveSetting("second_inst", "C://");
        saveSetting("reload_key", "119"); // F8
        saveSetting("rsg_mode", "true");
        saveSetting("on_top", "true");


    }
}

