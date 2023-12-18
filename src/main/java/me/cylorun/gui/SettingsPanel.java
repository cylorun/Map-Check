package me.cylorun.gui;

import me.cylorun.io.BrainBorOptions;
import me.cylorun.io.Hotkey;
import me.cylorun.seedgrabber.MinecraftInstance;
import me.cylorun.utili.MCUtil;

import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    private final JPanel generalPanel = new JPanel();
    private final JPanel hotkeysPanel = new JPanel();
    private final JPanel themePanel = new JPanel();
    private final JPanel seedInfoPanel = new JPanel();
    private KeyInputField reloadKey;
    private JCheckBox alwaysOnTopBox;
    private JCheckBox modeBox;
    private final BrainBorOptions options;


    public SettingsPanel(BrainBorOptions options) {
        this.options = options;
        setPreferredSize(new Dimension(350, 300));
        setLayout(new GridLayout(1, 1));

        JTabbedPane tabbedPane = createTabbedPane();
        createGeneralPanel();
        createHotkeysPanel();
        loadPreviousConfig();
        add(tabbedPane);
        addListeners();


    }

    private void loadPreviousConfig() {
        modeBox.setSelected(!options.rsg_mode);
        alwaysOnTopBox.setSelected(options.onTop);


    }

    private void addListeners() {
        modeBox.addActionListener(e -> {
            options.setOption("rsg_mode", String.valueOf(!modeBox.isSelected()));
        });
        alwaysOnTopBox.addActionListener(e -> {
            options.setOption("always_top", String.valueOf(alwaysOnTopBox.isSelected()));
        });
        reloadKey.addListener(() -> {
            Hotkey h = new Hotkey(reloadKey.getRecordedKey());
            options.setOption("reload_key", String.valueOf(h.getKeyCode()));
            MainPanel.reloadKey.setHotkey(h.getKeyCode());
        });

    }

    private JTabbedPane createTabbedPane() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("General", generalPanel);
        tabbedPane.addTab("Hotkeys", hotkeysPanel);
        tabbedPane.addTab("Theme", themePanel);
        tabbedPane.addTab("SeedInfo", seedInfoPanel);
        return tabbedPane;
    }

    private void createHotkeysPanel() {
        reloadKey = new KeyInputField("ReloadKey", new Hotkey(options.reloadKey).getKey());
        hotkeysPanel.add(reloadKey);

    }

    private void createGeneralPanel() {
        modeBox = new JCheckBox("Use Ranked Mode");
        alwaysOnTopBox = new JCheckBox("Always On Top");
        modeBox.setToolTipText("Use if it doesn't work with Ranked");

        generalPanel.setLayout(new GridLayout(3, 2));
        generalPanel.add(alwaysOnTopBox);
        generalPanel.add(modeBox);
        generalPanel.add(createBrowsePanel(options.mainInstPath, 1));
        generalPanel.add(new JLabel());
        generalPanel.add(createBrowsePanel(options.secondInstPath, 2));
    }

    private JPanel createBrowsePanel(String instancePath, int instance) {
        JPanel panel = new JPanel();
        JLabel label = new JLabel(instance == 1 ? "Main Inst: " + MCUtil.getInstanceName(instancePath) : "Second Inst: " + MCUtil.getInstanceName(instancePath));
        JButton button = new JButton("Browse");

        button.addActionListener(e -> openFileDialog(instance));

        panel.add(label);
        panel.add(button);
        return panel;
    }

    private void openFileDialog(int instance) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            switch (instance) {
                case 1 -> options.setOption("main_inst", fileChooser.getSelectedFile().getAbsolutePath());
                case 2 -> options.setOption("second_inst", fileChooser.getSelectedFile().getAbsolutePath());
            }

        }
    }
}