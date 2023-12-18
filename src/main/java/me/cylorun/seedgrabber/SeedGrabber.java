package me.cylorun.seedgrabber;

import com.sun.jna.platform.win32.Win32VK;
import com.sun.jna.platform.win32.WinDef;
import kaptainwutax.featureutils.structure.generator.piece.stronghold.PortalRoom;
import me.cylorun.io.BrainBorOptions;
import me.cylorun.utili.*;
import net.querz.nbt.io.NBTUtil;
import net.querz.nbt.io.NamedTag;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeedGrabber {
    public SeedGrabber() {

    }

    public static long getSeed(Mode mode, BrainBorOptions options) {
        switch (mode) {
            case RSG -> {
                return rsg();
            }
            case RANKED -> {
                try {
                    return ranked(options);
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return 1557;

    }

    private static long rsg() {
        if (ProcessUtil.getActiveWinName().contains("Minecraft*")) {
            WinDef.HWND hwnd = ProcessUtil.getFocusWindowHwnd();
            String savesPath;
            try {
                savesPath = MCUtil.getPath(ProcessUtil.getPidFromHwnd(hwnd)) + "\\saves";
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return readSeedFromDat(MCUtil.latestSave(savesPath) + "\\level.dat");
        }
        return 1447;

    }

    private static long ranked(BrainBorOptions options) throws InterruptedException, IOException {
        List<WinDef.HWND> list = ProcessUtil.hwndFromTitle("MCSR Ranked");
//        if (list.size() < 2) {
            List<MinecraftInstance> instances = new ArrayList<>();
            for (WinDef.HWND hwnd : list) {
                String path = MCUtil.getPath(ProcessUtil.getPidFromHwnd(hwnd));
                int role = options.mainInstPath.equals(path) ? 0 : 1;
                instances.add(new MinecraftInstance(path,
                        ProcessUtil.getPidFromHwnd(hwnd),
                        hwnd,
                        role
                ));
            }
            instances.sort(Comparator.comparing(MinecraftInstance::getRole));


            WinDef.HWND secondInstHwnd = instances.get(1).getHwnd();
            copyLevel(instances);
            enterWorld(secondInstHwnd);

            while (!instances.get(1).wpState().contains("inworld")) {
                Thread.sleep(500);
            }
            copySeed(secondInstHwnd);
            Thread.sleep(200);
            exitWorld(secondInstHwnd);
            return getSeedFromLog(instances.get(1).getPath() + "\\logs\\latest.log");

//        }
//        throw new BrainBorException(new Exception("1 Or less MCSR Ranked instances were detected, 2 are required."));

    }


    private static long readSeedFromDat(String levelDat) {
        NamedTag tag;
        try {
            tag = NBTUtil.read(levelDat);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String seed = tag.getTag().toString().split("\"seed\":\\{\"type\":\"LongTag\",\"value\":")[1].split("\\}")[0];
        return Long.parseLong(seed);
    }

    private static void copyLevel(List<MinecraftInstance> instances) {
        if (new File(instances.get(1).latestWorld() + "\\level.dat").delete()) {
            try {
                Files.copy(Path.of(instances.get(0).latestWorld() + "\\level.dat"), Path.of(instances.get(1).latestWorld() + "\\level.dat"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void enterWorld(WinDef.HWND hwnd) throws InterruptedException {
        Thread.sleep(500);
        KeyboardUtil.sendKeyToHwnd(hwnd, Win32VK.VK_TAB);
        KeyboardUtil.sendKeyToHwnd(hwnd, Win32VK.VK_RETURN);
        Thread.sleep(500);
        KeyboardUtil.sendKeyToHwnd(hwnd, Win32VK.VK_TAB);
        KeyboardUtil.sendKeyToHwnd(hwnd, Win32VK.VK_TAB);
        KeyboardUtil.sendKeyToHwnd(hwnd, Win32VK.VK_RETURN);
    }

    private static void copySeed(WinDef.HWND hwnd) throws InterruptedException {
        KeyboardUtil.sendKeyToHwnd(hwnd, Win32VK.VK_T);
        Thread.sleep(100);
        KeyboardUtil.sendStringToHwnd(hwnd, "/seed");
        Thread.sleep(100);
        KeyboardUtil.sendKeyToHwnd(hwnd, Win32VK.VK_RETURN);
    }

    private static void exitWorld(WinDef.HWND hwnd) {
        KeyboardUtil.sendKeyToHwnd(hwnd, Win32VK.VK_ESCAPE);
        KeyboardUtil.sendKeyDownToHwnd(hwnd, Win32VK.VK_RSHIFT);
        KeyboardUtil.sendKeyToHwnd(hwnd, Win32VK.VK_TAB);
        KeyboardUtil.sendKeyUpToHwnd(hwnd, Win32VK.VK_RSHIFT);
        KeyboardUtil.sendKeyToHwnd(hwnd, Win32VK.VK_RETURN);
    }

    public static long getSeedFromLog(String logFile) throws IOException {
        LinkedList<String> lines = new LinkedList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(logFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.addFirst(line);
            }
        }

        for (String line : lines) {

            Pattern pattern = Pattern.compile(".*\\[CHAT].*:\\s*\\[(\\d+)]");
            Matcher matcher = pattern.matcher(line);
            if (matcher.find()) {
                return Long.parseLong(matcher.group(1));
            }

        }
        return -1337;
    }

}
