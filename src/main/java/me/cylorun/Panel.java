package me.cylorun;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static me.cylorun.FileUtil.instancePaths;
import static me.cylorun.FileUtil.mapPaths;

public class Panel extends JPanel {
    private final JButton download = new JButton("Download");
    private final JButton selectInstances = new JButton("Select instances");
    public JProgressBar bar = new JProgressBar();
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

        download.setBounds(150, 20, 150, 40);
        selectInstances.setBounds(0, 20, 150, 40);
        bar.setBounds(0,400,300,30);
        add(download);
        add(selectInstances);

        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                   if (!instancePaths.isEmpty()) {
                       FileUtil fu = new FileUtil();
                       fu.downloadMaps(instancePaths.get(0));
                       instancePaths.remove(0);
                       for (String instance : instancePaths) {
                           try {
                               if (!mapPaths.isEmpty()) {
                                   for (String map : mapPaths) {
                                       map = map.replace(".zip","");
                                       fu.copyFolder(new File(map), new File(instance));
                                        }
                                    }
                               } catch(IOException ex){
                                   throw new RuntimeException(ex);
                               }
                       }
                   JFrame frame = new JFrame();
                   frame.setAlwaysOnTop(true);
                   JOptionPane.showMessageDialog(frame, "Finished downloading");
                   frame.dispose();
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
