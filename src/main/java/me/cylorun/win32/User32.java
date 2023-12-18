package me.cylorun.win32;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.WinDef;

public interface User32 extends Library {
    User32 INSTANCE = Native.load("user32", User32.class);
    int GetAsyncKeyState(int vKey);

    WinDef.HWND GetForegroundWindow();

    int GetWindowTextA(WinDef.HWND hWnd, byte[] lpString, int nMaxCount);
    WinDef.UINT MapVirtualKeyA(WinDef.UINT uCode, WinDef.UINT uMapType);
    boolean PostMessageA(WinDef.HWND hWnd, WinDef.UINT Msg, WinDef.WPARAM wParam, WinDef.LPARAM lParam);
}

