package me.cylorun.gui;

import me.cylorun.io.*;
import me.cylorun.seedgrabber.SeedGrabber;
import me.cylorun.utili.*;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class MainPanel extends JPanel {
    public static HotkeyListener reloadKey;
    private static JPanel topPanel;
    private static JButton seedCopyButton;
    private static JButton settingsButton;
    private static JLabel netherCoords;
    private static JLabel overworldCoords;
    private static SeedInfo seedInfo;
    private static JPanel strongholdPanel;
    private static JPanel seedInfoPanel;
    private static JLabel strongholdDistance;
    private static JLabel strongholdAngle;
    private static BrainBorOptions options;

    public MainPanel(BrainBorOptions options) {
        MainPanel.options = options;
        this.setPreferredSize(new Dimension(400, 250));
        this.setLayout(new GridLayout(3, 1));
        seedInfo = new SeedInfo();

        createTopPanel();
        createStrongholdPanel();
        createSeedInfoPanel();

        topPanel.setLayout(new GridLayout(1, 2));
        topPanel.add(seedCopyButton);
        topPanel.add(settingsButton);


        settingsButton.addActionListener(e -> {
            JFrame settingsFrame = new JFrame("Settings");
            settingsFrame.setVisible(true);
            settingsFrame.setResizable(false);
            settingsFrame.add(new SettingsPanel(new BrainBorOptions()));
            settingsFrame.setAlwaysOnTop(true);
            settingsFrame.pack();
        });


        reloadKey = new HotkeyListener(new Hotkey(options.reloadKey));
        reloadKey.addListener(MainPanel::reload);
        ClipboardReader clipboardReader = new ClipboardReader();
        clipboardReader.addListener(() -> MainPanel.onPlayerUpdate(clipboardReader));
        setStrongholdInfo();
        setSeedInfo();
    }

    private void createTopPanel() {
        topPanel = new JPanel();
        seedCopyButton = new JButton();
        settingsButton = new JButton("Settings");
        this.add(topPanel);

    }

    private void createStrongholdPanel() {
        strongholdPanel = new JPanel();
        strongholdAngle = new JLabel();
        strongholdDistance = new JLabel();
        overworldCoords = new JLabel();
        netherCoords = new JLabel();
        strongholdPanel.setLayout(new GridLayout(2, 4));
        strongholdPanel.add(new JLabel("    Location"));
        strongholdPanel.add(new JLabel("Distance"));
        strongholdPanel.add(new JLabel("Nether"));
        strongholdPanel.add(new JLabel("Angle"));


        strongholdPanel.add(overworldCoords);
        strongholdPanel.add(strongholdDistance);
        strongholdPanel.add(netherCoords);
        strongholdPanel.add(strongholdAngle);
        this.add(strongholdPanel);
    }

    private void createSeedInfoPanel() {
        seedInfoPanel = new JPanel();
        this.add(seedInfoPanel);
    }

    private static void setStrongholdInfo() {
        overworldCoords.setText(seedInfo.coordsStringOverworld());
        netherCoords.setText(seedInfo.coordsStringNether());
        strongholdDistance.setText(seedInfo.strongholdDistString(new int[]{0, 0}, MCDimension.OVERWORLD)); // defaults to 0,0 for the players position
        strongholdAngle.setText(seedInfo.strongholdAngleString(new int[]{0, 0}, MCDimension.OVERWORLD)); // defaults to 0,0 for the players position
    }

    private static void setSeedInfo() {
        seedCopyButton.setText("Seed: " + seedInfo.getSeed());
        seedCopyButton.addActionListener(e -> {
            copyToClipboard(String.valueOf(seedInfo.getSeed()));
        });
    }

    public static void reload() {
        Logger.log(Logger.INFO,"Reload triggered.");
        Mode mode = options.rsg_mode ? Mode.RSG : Mode.RANKED;
        long newSeed = SeedGrabber.getSeed(mode,options);
        if (seedInfo.getSeed() != newSeed) {
            seedInfo.setSeed(newSeed);
            setStrongholdInfo();
            setSeedInfo();
        }
    }

    private static void copyToClipboard(String content) {
        StringSelection stringSelection = new StringSelection(String.valueOf(content));
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static void onPlayerUpdate(ClipboardReader clipboard) {
        int[] playerLocation;
        MCDimension playerDim = Player.getDimension(clipboard);
        try {
            playerLocation = Player.getLocation(clipboard);
        } catch (Exception e) {
            System.err.println("playerloc is null");
            e.printStackTrace();
            return;
        }


        if (playerLocation != null) {
            strongholdAngle.setText(seedInfo.strongholdAngleString(playerLocation, playerDim));
            strongholdDistance.setText(seedInfo.strongholdDistString(playerLocation, playerDim));
        }
    }
}
