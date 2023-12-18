package me.cylorun.utili;

import me.cylorun.io.ClipboardReader;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Player {
    public Player() {
        // bad class name
    }



    public static int[] getLocation(ClipboardReader clipboard) { // x, z, yaw
        String clipboardContent = clipboard.getClipboardContent();
        int[] locations = new int[3];

        if (clipboardContent.contains("/execute in minecraft:")) {
            String[] f3info = clipboardContent.replaceAll("\\D+ ", "").split("\\s+");
            locations[0] = (int) Double.parseDouble(f3info[0]);
            locations[1] = (int) Double.parseDouble(f3info[2]);
            locations[2] = (int) Double.parseDouble(f3info[3]);
            return locations;
        }
        return null;
    }

    public static MCDimension getDimension(ClipboardReader clipboard) {
        String clipboardContent = clipboard.getClipboardContent();
        if (clipboardContent.contains("minecraft:the_nether")) return MCDimension.NETHER;
        if (clipboardContent.contains("minecraft:overworld")) return MCDimension.OVERWORLD;
        return MCDimension.END;

    }

}
