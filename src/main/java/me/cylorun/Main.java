package me.cylorun;

import com.formdev.flatlaf.FlatDarculaLaf;
import me.cylorun.gui.MainPanel;
import me.cylorun.io.BrainBorOptions;

import javax.swing.*;

public class Main {
    // /execute in minecraft:the_nether run tp @s 1000 43.00 -25.50 113.63 69.31
    //                                              x    y     z    yaw      pitch
    public final static String VERSION = "2.4.3";

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatDarculaLaf());
        JFrame frame = new JFrame("Brain-bor v" + VERSION);
        BrainBorOptions bo = new BrainBorOptions();
        frame.add(new MainPanel(bo));

        frame.setAlwaysOnTop(bo.onTop);
        frame.setVisible(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();

        ImageIcon icon = new ImageIcon(Main.class.getClassLoader().getResource("icon.png"));
        frame.setIconImage(icon.getImage());

    }

}
