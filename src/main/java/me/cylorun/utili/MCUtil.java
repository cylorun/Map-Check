package me.cylorun.utili;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Objects;

public class MCUtil {
    public MCUtil(){

    }
    public static String getPath(int pid) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("wmic", "process", "where", "processId=\""+pid+"\"", "get", "CommandLine");
        Process process = processBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line;
        int lineCount = 0;
        while ((line = reader.readLine()) != null) {
            lineCount++;
            if (lineCount == 3){
                line = line.split("path=")[1].split("\\s+")[0].replace("natives",".minecraft");
                break;
            }

        }
        return line;

    }
    public static String latestSave(String savesDir){
        String latest = "";
        long lm = 0;
        for (File w : new File(savesDir).listFiles()){
            if (w.lastModified() > lm){
                lm = w.lastModified();
                latest = w.getAbsolutePath();
            }

        }
        return latest;

    }
    public static String getInstanceName(String path){
        String instName = path;
        if (instName != null && instName.split("\\\\").length > 2) {
            String[] s = instName.split("\\\\");
            instName = s[s.length-1];
        } else {
            instName = "None";
        }
        return instName;
    }
}
