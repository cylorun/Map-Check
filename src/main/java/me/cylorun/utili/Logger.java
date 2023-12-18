package me.cylorun.utili;

import java.time.LocalTime;

public class Logger {
    public static final int INFO = 100;
    public static final int WARNING = 200;
    public static final int ERROR = 300;

    public Logger() {
    }

    public static void log(int level, String msg) {
        LocalTime localTime = LocalTime.now();
        String time = localTime.toString().substring(0, localTime.toString().lastIndexOf('.'));
        switch (level) {
            case INFO -> System.out.printf("[%s/INFO] %s\n", time, msg);
            case WARNING -> System.out.printf("[%s/WARN] %s\n", time, msg);
            case ERROR -> System.err.printf("[%s/ERROR] %s\n", time, msg);
        }
    }
}

