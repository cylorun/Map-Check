package me.cylorun;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

public class Panel extends JPanel {
    private final JButton download = new JButton("Download");
    private final JButton selectInstances = new JButton("Select instances");
    private final Map<JCheckBox, String> checkBoxes = new HashMap<>();

    public Panel() {
        setPreferredSize(new Dimension(300, 450));
        setLayout(null);

        checkBoxes.put(new JCheckBox("Llamas bastion practice"), "https://github.com/LlamaPag/bastion/releases/download/3.14.0/LBP_3.14.0.zip");
        checkBoxes.put(new JCheckBox("Zero Cycle"), "https://zerocycle.repl.co/_zero_cycle_practice_astraf_nayoar.zip");
        checkBoxes.put(new JCheckBox("Ryguy2k4 End practice"), "https://github.com/ryguy2k4/ryguy2k4endpractice/releases/download/v3.4.0/_Ryguy2k4_End_Practice_v3.4.0-1.16.1.zip");
        checkBoxes.put(new JCheckBox("Blaze practice"), "https://github.com/Semperzz/Blaze-Practice/releases/download/v1.3/Blaze.Practice.zip");
        checkBoxes.put(new JCheckBox("End portal fill"), "https://github.com/cylorun/End-Portal-Fill/releases/download/Minecraft/EndPortal.v2.zip");
        checkBoxes.put(new JCheckBox("Semperzz portal practice"), "https://github.com/Semperzz/Portal-Practice/releases/download/v2.8/Portal.Practice.v2.zip");
        checkBoxes.put(new JCheckBox("Crafting v2"), "https://github.com/Semperzz/Crafting-Practice-v2/releases/download/v2.1/Crafting.Practice.v2.zip");
        checkBoxes.put(new JCheckBox("7rowl OW practice"), "https://github.com/7rowl/OWPractice/releases/download/v2.0/OW.Practice.v2.0.zip");
        checkBoxes.put(new JCheckBox("Zero prep"), "https://github.com/Semperzz/Zero-Sorting-Practice/releases/download/v1.5/Zero.Sorting.zip");

        int yPosition = 70;
        for (JCheckBox checkBox : checkBoxes.keySet()) {
            checkBox.setBounds(50, yPosition, 200, 20);
            add(checkBox);
            yPosition += 30;
        }

        download.setBounds(130, 400, 100, 40);
        selectInstances.setBounds(100, 20, 150, 40);
        add(download);
        add(selectInstances);

        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!FileUtil.instancePaths.isEmpty()) {
                    FileUtil fu = new FileUtil(); // Create a single instance of FileUtil
                    for (int i = 0; i < FileUtil.instancePaths.size(); i++) {
                        System.out.println(FileUtil.instancePaths.size());
                        fu.downloadMaps(FileUtil.instancePaths.get(i));

                        for (int a = 0; a < FileUtil.mapPaths.size(); a++) { // Use 'a' for the inner loop
                            System.out.println(FileUtil.mapPaths.size());
                            fu.getMaps(FileUtil.mapPaths.get(a));
                        }
                    }

                    JOptionPane.showMessageDialog(new JFrame(), "Finished downloading");
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "No instances selected");
                }
            }
        });

        selectInstances.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InstanceSelect();
            }
        });

        for (JCheckBox checkBox : checkBoxes.keySet()) {
            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (checkBox.isSelected()) {
                        FileUtil.maps.add(checkBoxes.get(checkBox));
                    } else {
                        FileUtil.maps.remove(checkBoxes.get(checkBox));
                    }
                }
            });
        }
    }
}
