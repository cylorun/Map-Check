package me.cylorun.io;

import java.awt.event.KeyEvent;

public class Hotkey {
    private int key;

    public Hotkey(int key) {
        this.key = key;
    }

    public String getKey() {
        return KeyEvent.getKeyText(this.key);
    }

    public int getKeyCode() {
        return this.key;
    }
    public void setKey(int keyCode){
        this.key = keyCode;

    }

}
