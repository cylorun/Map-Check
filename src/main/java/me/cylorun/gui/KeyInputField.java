package me.cylorun.gui;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class KeyInputField extends JButton implements KeyListener {
    private boolean recording;
    private int recordedKey;
    private final String keyText;
    private List<KeyInputListener> listeners = new ArrayList<>();

    public KeyInputField(String keyText, String value) {
        super(keyText + ": " + value);
        this.keyText = keyText;
        this.addKeyListener(this);

        this.addActionListener(e -> {
            if (!recording) {
                setText(keyText + ": ...");
                recording = true;
            } else {
                setText(KeyEvent.getKeyText(recordedKey));
                recording = false;
            }
        });
    }

    public void addListener(KeyInputListener listener) {
        listeners.add(listener);
    }

    private void notifyListeners() {
        for (KeyInputListener listener : listeners) {
            listener.onKeyChanged();
        }
    }


    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (recording) {
            recordedKey = e.getKeyCode();
            setText(keyText + ": " + KeyEvent.getKeyText(recordedKey));
            recording = false;
            notifyListeners();
        }
    }

    public int getRecordedKey() {
        return recordedKey;
    }
}

