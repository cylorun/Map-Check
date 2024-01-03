package me.cylorun;

import com.google.gson.*;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapCheckFrame extends JFrame {
    private JPanel mainPanel;
    private JButton downloadButton;
    private JButton addMapButton;
    private JTextField urlField;
    private JButton instSelectButton;
    private JButton selectAllButton;
    private JButton deSelectAllButton;
    private static JProgressBar progressBar;
    private static List<String> selectedMaps = new ArrayList<>();
    private static List<String> instancePaths = new ArrayList<>();
    private Map<JCheckBox, String> checkBoxes;
    private static int currentStep = 0;

    public MapCheckFrame() throws IOException {
        mainPanel = new JPanel();

        downloadMapInfo();
        initializeMainPanel();
        initializeActionListeners();
        this.setVisible(true);
        this.setTitle("Map-Check v" + Main.VERSION);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.add(mainPanel);
        this.pack();
    }

    private void reloadUI() {
        mainPanel.removeAll();
        try {
            initializeMainPanel();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.pack();
    }

    private void downloadMapInfo() {
        if (!new File("maps.json").exists()) {
            try {
                URL url = new URL("https://gist.github.com/cylorun/3cd5d459d9adc9ad28608e8ed606aadb/raw/2f393a01f9cb847e222c8b9f0b1dd5f222d1e563/maps.json");
                Files.copy(url.openStream(), Path.of("maps.json"));
            } catch (IOException e) {
                exceptionPane(e);
            }
        }
    }

    private void addMapToJson(String path, String label, String url) {
        JsonArray jsonArray;
        try {
            jsonArray = JsonParser.parseString(new String(Files.readAllBytes(Paths.get(path)))).getAsJsonArray();
        } catch (IOException e) {
            exceptionPane(e);
            throw new RuntimeException(e);
        }
        label = label.replace(".zip", "");
        JsonObject obj = new JsonObject();
        obj.addProperty("label", label);
        obj.addProperty("url", url);
        jsonArray.add(obj);
        try (FileWriter fileWriter = new FileWriter("maps.json")) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            gson.toJson(jsonArray, fileWriter);
        } catch (IOException e) {
            exceptionPane(e);
        }
        JOptionPane.showMessageDialog(null, String.format("Added the map %s with the link: \n %s", label, url));
        reloadUI();
    }

    private void initializeMainPanel() throws IOException {
        int height = 220 + (countMaps() * 30);

        downloadButton = new JButton("Download");
        addMapButton = new JButton("Add");
        urlField = new JTextField();
        instSelectButton = new JButton("Select Instances");
        progressBar = new JProgressBar(0, 100);
        selectAllButton = new JButton("Select All");
        deSelectAllButton = new JButton("Deselect All");

        progressBar.setStringPainted(true);

        mainPanel.setLayout(null);
        mainPanel.setPreferredSize(new Dimension(300, height));
        mainPanel.add(downloadButton);
        mainPanel.add(addMapButton);
        mainPanel.add(urlField);
        mainPanel.add(instSelectButton);
        mainPanel.add(progressBar);
        mainPanel.add(deSelectAllButton);
        mainPanel.add(selectAllButton);
        addCheckBoxes();

        downloadButton.setBounds(150, 20, 150, 40);
        instSelectButton.setBounds(0, 20, 150, 40);
        selectAllButton.setBounds(0, 62, 150, 40);
        deSelectAllButton.setBounds(150, 62, 150, 40);
        urlField.setBounds(10, height - 70, 270, 40);
        addMapButton.setBounds(10, height - 30, 60, 30);
        progressBar.setBounds(12, height - 100, 265, 30);

    }

    private Map<JCheckBox, String> getCheckBoxes() {
        Map<JCheckBox, String> checkBoxes = new HashMap<>();
        String jsonContent = null;
        try {
            jsonContent = new String(Files.readAllBytes(Paths.get("maps.json")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        JsonArray ja = JsonParser.parseString(jsonContent).getAsJsonArray();
        for (JsonElement element : ja) {
            JsonObject jsonObject = element.getAsJsonObject();
            String label = jsonObject.get("label").getAsString();
            String url = jsonObject.get("url").getAsString();
            checkBoxes.put(new JCheckBox(label), url);

        }
        return checkBoxes;
    }

    private void addCheckBoxes() {
        checkBoxes = getCheckBoxes();
        int yPos = 120;
        for (JCheckBox c : checkBoxes.keySet()) {
            c.addActionListener(e -> {
                if (c.isSelected()) {
                    selectedMaps.add(checkBoxes.get(c));
                } else {
                    selectedMaps.remove(checkBoxes.get(c));
                }
            });
            c.setBounds(50, yPos, 200, 20);
            mainPanel.add(c);
            yPos += 30;

        }
    }

    private int countMaps() throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get("maps.json")));
        JsonArray ja = JsonParser.parseString(jsonContent).getAsJsonArray();
        return ja.size();
    }

    private void initializeActionListeners() {
        addMapButton.addActionListener(e -> {
            String url = urlField.getText();
            String mapName = "Unknown Map";
            if (url.endsWith(".zip") || url.endsWith(".rar")) {
                String[] split = url.split("/");
                if (split.length > 1) {
                    mapName = split[split.length - 1].replace(".zip", "").replace(".rar", "");
                }
            } else {
                mapName = "Unknown Name";
            }
            if (url.endsWith(".zip") || url.endsWith(".rar")) {
                addMapToJson("maps.json", mapName, url);
            } else {
                int choice = JOptionPane.showConfirmDialog(null, String.format("Invalid URL \n %s \nwould you still like to add it? ", url), "Invalid Map URL", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    addMapToJson("maps.json", mapName, url);
                }
            }
        });

        downloadButton.addActionListener(e -> {
            new Thread(this::downloadMaps).start();

        });

        instSelectButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            int response = fileChooser.showOpenDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();
                for (File file : selectedFiles) {
                    File mcDir = new File(file, "/.minecraft");
                    String savesPath = Paths.get(mcDir.toString()).resolve("saves").toString();
                    if (mcDir.exists()) {
                        instancePaths.add(savesPath);
                    } else {

                        int choice = JOptionPane.showConfirmDialog(
                                null,
                                file.getAbsolutePath() + "\n is not a Minecraft directory \n Would you still like to add it?",
                                "Invalid Directory",
                                JOptionPane.YES_NO_OPTION
                        );

                        if (choice == JOptionPane.YES_OPTION) {
                            instancePaths.add(mcDir.getParent());
                        }
                    }
                }
            }
        });

        selectAllButton.addActionListener(a -> {
            for (JCheckBox box : checkBoxes.keySet()) {
                box.setSelected(true);
                ActionEvent e = new ActionEvent(box, ActionEvent.ACTION_PERFORMED, "select");
                for (ActionListener listener : box.getActionListeners()) {
                    listener.actionPerformed(e);
                }
            }
            System.out.println(selectedMaps);
        });
        deSelectAllButton.addActionListener(a -> {
            for (JCheckBox box : checkBoxes.keySet()) {
                box.setSelected(false);
                ActionEvent e = new ActionEvent(box, ActionEvent.ACTION_PERFORMED, "select");
                for (ActionListener listener : box.getActionListeners()) {
                    listener.actionPerformed(e);
                }
            }

        });
    }

    private void exceptionPane(Exception e) {
        JOptionPane.showMessageDialog(null, "Error occured" + e.toString());
    }

    public static void updateProgressBar() {
        currentStep++;
        int progress = (int) ((double) currentStep / ((instancePaths.size() * selectedMaps.size()) + (selectedMaps.size() * 2)) * 100);
        SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
//        System.out.println("Progress set to: " + progress);
    }

    private void resetProgressBar() {
        currentStep = 0;
        SwingUtilities.invokeLater(() -> progressBar.setValue(0));
    }

    private void downloadMaps() {
        resetProgressBar();
        if (!instancePaths.isEmpty() || !selectedMaps.isEmpty()) {
            List<String> downloadedMapsPaths = FileUtil.downloadToTemp(selectedMaps);
            FileUtil.copyFromTemp(instancePaths, downloadedMapsPaths);
            try {
                FileUtils.deleteDirectory(new File(Path.of(System.getProperty("user.dir"), "mc_temp").toString()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Finished downloading", "Download Status", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "No maps or instances selected", "Download Status", JOptionPane.WARNING_MESSAGE);

        }
    }
}
