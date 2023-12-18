package me.cylorun.utili;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class BrainBorException extends RuntimeException {
    public BrainBorException(Exception e) {
        Logger.log(Logger.ERROR, e.toString());
        String errorMessage = String.format("<html>An error has occured<br> <font color='orange'>%s</font> </html>\n", e);

        JPanel panel = new JPanel(new FlowLayout());
        JButton copyButton = new JButton("Copy");

        panel.add(new JLabel(errorMessage));
        panel.add(copyButton);

        copyButton.setPreferredSize(new Dimension(73, 35));
        copyButton.addActionListener(event -> {
            StringSelection stringSelection = new StringSelection(e.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            JOptionPane.showMessageDialog(null,
                    "Copied error to clipboard",
                    "Unfortunate Occurrence",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        JOptionPane.showMessageDialog(
                null,
                panel,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

}
