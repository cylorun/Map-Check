package me.cylorun;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Panel extends JPanel {
    private JButton finishButton = new JButton("Download");
    private JCheckBox bastion = new JCheckBox();
    Panel(){
        setPreferredSize(new Dimension(500,500));
        setLayout(new FlowLayout());
        add(bastion);
        add(finishButton);
        finishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                FileUtil.getMaps();
            }
        });
        bastion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bastion.isSelected()){
                    FileUtil.maps.add("https://github.com/LlamaPag/bastion/releases/download/3.14.0/LBP_3.14.0.zip");
                }
            }
        });






    }
}
