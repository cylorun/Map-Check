package me.cylorun.io;

import me.cylorun.utili.BrainBorException;
import me.cylorun.win32.User32;

import java.util.ArrayList;
import java.util.List;

public class HotkeyListener extends Thread {
    private final Hotkey hk;
    private List<HotkeyAction> listeners;

    public HotkeyListener(Hotkey hk) {
        listeners = new ArrayList<>();
        this.hk = hk;
        this.start();
    }

    public void setHotkey(int nhk) {
        this.hk.setKey(nhk);
    }

    @Override
    public void run() {
        while (true) {
            if (User32.INSTANCE.GetAsyncKeyState(hk.getKeyCode()) != 0) {
                notifyListeners();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new BrainBorException(e);
            }
        }
    }

    public void addListener(HotkeyAction ha) {
        listeners.add(ha);
    }

    private void notifyListeners() {
        for (HotkeyAction ha : listeners) {
            ha.onKeyPressed();
        }

    }
}
