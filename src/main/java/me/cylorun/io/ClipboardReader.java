package me.cylorun.io;

import me.cylorun.utili.BrainBorException;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClipboardReader extends Thread{
    private String lastClipboardString;
    private final List<ClipboardListener> listeners;
    private final Clipboard clipboard;

    public ClipboardReader() {
        clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        this.lastClipboardString = "";
        this.listeners = new ArrayList<>();
        this.start();
    }

    public String getClipboardContent() {
        return lastClipboardString;
    }

    public void setClipboardContent(String clipboardString) {
        if (!lastClipboardString.equals(clipboardString)) {
            lastClipboardString = clipboardString;
            notifyListeners();
        }
    }

    public void addListener(ClipboardListener listener) {
        listeners.add(listener);
    }


    private void notifyListeners() {
        for (ClipboardListener listener : listeners) {
            listener.onClipboarcChanged();
        }
    }

    @Override
    public void run() {
        while (true) {
            String clipboardString = null;
            try {
                clipboardString = (String) clipboard.getData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException | IllegalStateException | IOException ignored) {
            }
            if (clipboardString != null && !this.lastClipboardString.equals(clipboardString)) {
                setClipboardContent(clipboardString);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new BrainBorException(e);
            }
        }
    }
}