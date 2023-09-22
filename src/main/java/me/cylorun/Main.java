package me.cylorun;

import javax.swing.*;

public class Main {
    private static JFrame frame = new JFrame();
    private static Panel panel = new Panel();

    public static void main(String[] args) {
        frame.setVisible(true);
        frame.setTitle("Map-Check");
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
    }
}