package me.cylorun.utili;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.*;
import com.sun.jna.ptr.IntByReference;
import me.cylorun.win32.PsApi;

import java.util.ArrayList;

public class ProcessUtil {
    private static final byte[] executablePathBuffer = new byte[1024];

    public static int getPidFromHwnd(WinDef.HWND hwnd) {
        final IntByReference pidPointer = new IntByReference();
        User32.INSTANCE.GetWindowThreadProcessId(hwnd, pidPointer);
        return pidPointer.getValue();
    }


    public static String getProcessExecutable(int processId) {
        WinNT.HANDLE process = Kernel32.INSTANCE.OpenProcess(0x0400 | 0x0010, false, processId);
        StringBuilder out = new StringBuilder();
        synchronized (executablePathBuffer) {
            Psapi.INSTANCE.GetModuleFileNameExA(process, null, executablePathBuffer, 1024);
            for (byte a : executablePathBuffer) {
                if (a == 0) {
                    break;
                }
                out.append((char) a);
            }
        }
        return out.toString();
    }


    public static String getActiveWinName(){
        me.cylorun.win32.User32 user32 = me.cylorun.win32.User32.INSTANCE;

        WinDef.HWND foregroundWindow = user32.GetForegroundWindow();

        byte[] windowText = new byte[512];
        user32.GetWindowTextA(foregroundWindow, windowText, 512);

        String title = new String(windowText).trim();
        return title;

    }
    public static WinDef.HWND getFocusWindowHwnd(){
        return User32.INSTANCE.GetForegroundWindow();
    }
    public static ArrayList<WinDef.HWND> hwndFromTitle(String title) {
        ArrayList<WinDef.HWND> openList = new ArrayList<>();
        User32.INSTANCE.EnumWindows((hwnd, lParam) -> {
            char[] buffer = new char[1024];
            WinDef.HWND hwndRef = new WinDef.HWND(hwnd.getPointer());
            if (User32.INSTANCE.IsWindowVisible(hwndRef)) {
                int length = User32.INSTANCE.GetWindowText(hwndRef, buffer, buffer.length);
                String winTitle = new String(buffer, 0, length);
                if (winTitle.contains(title)) {
                    openList.add(hwnd);

                }
            }
            return true;
        }, null);

        return openList;
    }

}


