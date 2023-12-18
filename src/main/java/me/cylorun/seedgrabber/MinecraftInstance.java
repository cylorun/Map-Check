package me.cylorun.seedgrabber;

import com.sun.jna.platform.win32.WinDef.HWND;
import me.cylorun.utili.Logger;
import me.cylorun.utili.MCUtil;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

public class MinecraftInstance {
    private String path;
    private int pid;
    private HWND hwnd;
    private int role; // 0 main, 1 cheat

    public MinecraftInstance(String path, int pid, HWND hwnd, int role) {
        this.path = path;
        this.pid = pid;
        this.hwnd = hwnd;
        this.role = role; // make automatic
        Logger.log(Logger.INFO,"New MC instance detected!\n" + this);

    }

    public String getPath() {
        return path;
    }

    public int getPid() {
        return pid;
    }

    public HWND getHwnd() {
        return hwnd;
    }

    public int getRole() {
        return role;
    }

    public String latestWorld() {
        return MCUtil.latestSave(this.path + "\\saves");
    }

    public String wpState() {
        BufferedReader reader;
        String line;
        try {
            reader = new BufferedReader(new FileReader(this.path + "\\wpstateout.txt"));
            line = reader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return line;
    }


    @Override
    public String toString() {
        return String.format("Path: %s\nPid: %s\nHwnd: %s\nRole: %s\n", path, pid, hwnd, role == 0 ? "Main" : "Second");
    }
}
