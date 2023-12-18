package me.cylorun.win32;

import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.win32.StdCallLibrary;

public interface PsApi extends StdCallLibrary {

    int GetModuleFileNameExA(WinNT.HANDLE process, WinNT.HANDLE module ,
                            byte[] name, int i);

}