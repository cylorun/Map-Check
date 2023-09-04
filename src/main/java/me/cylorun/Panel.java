package me.cylorun;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Panel extends JPanel {
    private JButton download = new JButton("Download");
    private JButton selectInstances = new JButton("Select instances");
    private JCheckBox bastion = new JCheckBox("LLamas bastion practice");
    private JCheckBox zero = new JCheckBox("Zero Cycle");
    private JCheckBox end = new JCheckBox("Ryguy2k4 End practice");
    private JCheckBox blaze = new JCheckBox("Blaze practice");
    private JCheckBox end_portal_fill = new JCheckBox("End portal fill");
    private JCheckBox portal = new JCheckBox("Semperzz portal practice");
    private JCheckBox crafting = new JCheckBox("Crafting v2");
    private JCheckBox ow = new JCheckBox("7rowl OW practice");
    private JCheckBox zero_prep = new JCheckBox("Zero prep");
    Panel(){
        setPreferredSize(new Dimension(500,500));
        setLayout(null);
        add(bastion);
        add(download);
        add(selectInstances);
        add(zero);
        add(end);
        add(blaze);
        add(end_portal_fill);
        add(portal);
        add(crafting);
        add(ow);
        add(zero_prep);



        download.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (FileUtil.instancePaths != null) {
                    FileUtil.getMaps();
                } else {
                    JOptionPane.showMessageDialog(new JFrame(),"No instances selected");
                }
            }
        });
        selectInstances.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new InstanceSelect();
            }
        });

        bastion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (bastion.isSelected()){
                    FileUtil.maps.add("https://github.com/LlamaPag/bastion/releases/download/3.14.0/LBP_3.14.0.zip");
                }else {
                    FileUtil.maps.remove("https://github.com/LlamaPag/bastion/releases/download/3.14.0/LBP_3.14.0.zip");
                }
            }
        });
        zero.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (zero.isSelected()){
                    FileUtil.maps.add("https://zerocycle.repl.co/_zero_cycle_practice_astraf_nayoar.zip");
                } else {
                    FileUtil.maps.remove("https://zerocycle.repl.co/_zero_cycle_practice_astraf_nayoar.zip");
                }
            }
        });
        end.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (end.isSelected()){
                    FileUtil.maps.add("https://github.com/ryguy2k4/ryguy2k4endpractice/releases/download/v3.4.0/_Ryguy2k4_End_Practice_v3.4.0-1.16.1.zip");
                } else {
                    FileUtil.maps.remove("https://github.com/ryguy2k4/ryguy2k4endpractice/releases/download/v3.4.0/_Ryguy2k4_End_Practice_v3.4.0-1.16.1.zip");
                }
            }
        });
        blaze.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (blaze.isSelected()){
                    FileUtil.maps.add("https://github.com/Semperzz/Blaze-Practice/releases/download/v1.3/Blaze.Practice.zip");
                } else {
                    FileUtil.maps.remove("https://github.com/Semperzz/Blaze-Practice/releases/download/v1.3/Blaze.Practice.zip");
                }
            }
        });
        end_portal_fill.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (end_portal_fill.isSelected()){
                    FileUtil.maps.add("https://github.com/cylorun/End-Portal-Fill/releases/download/Minecraft/EndPortal.v2.zip");
                } else {
                    FileUtil.maps.remove("https://github.com/cylorun/End-Portal-Fill/releases/download/Minecraft/EndPortal.v2.zip");
                }
            }
        });
        portal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (portal.isSelected()){
                    FileUtil.maps.add("https://github.com/Semperzz/Portal-Practice/releases/download/v2.8/Portal.Practice.v2.zip");
                } else {
                    FileUtil.maps.remove("https://github.com/Semperzz/Portal-Practice/releases/download/v2.8/Portal.Practice.v2.zip");
                }
            }
        });
        crafting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (crafting.isSelected()){
                    FileUtil.maps.add(" https://github.com/Semperzz/Crafting-Practice-v2/releases/download/v2.1/Crafting.Practice.v2.zip");
                } else {
                    FileUtil.maps.remove(" https://github.com/Semperzz/Crafting-Practice-v2/releases/download/v2.1/Crafting.Practice.v2.zip");
                }
            }
        });
        ow.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (ow.isSelected()){
                    FileUtil.maps.add("https://github.com/7rowl/OWPractice/releases/download/v2.0/OW.Practice.v2.0.zip");
                } else {
                    FileUtil.maps.remove("https://github.com/7rowl/OWPractice/releases/download/v2.0/OW.Practice.v2.0.zip");
                }
            }
        });
        zero_prep.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (zero_prep.isSelected()){
                    FileUtil.maps.add("https://github.com/Semperzz/Zero-Sorting-Practice/releases/download/v1.5/Zero.Sorting.zip");
                } else {
                    FileUtil.maps.remove("https://github.com/Semperzz/Zero-Sorting-Practice/releases/download/v1.5/Zero.Sorting.zip");
                }
            }
        });


    download.setBounds(300,450,100,40);
    selectInstances.setBounds(100,20,150,40);
    bastion.setBounds(50,70,200,20);
    zero.setBounds(50,100,200,20);
    end.setBounds(50,130,200,20);
    blaze.setBounds(50,160,200,20);
    end_portal_fill.setBounds(50,190,200,20);
    portal.setBounds(50,220,200,20);
    crafting.setBounds(50,250,200,20);
    ow.setBounds(50,280,200,20);
    zero_prep.setBounds(50,310,200,20);




    }

}
