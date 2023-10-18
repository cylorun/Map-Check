package me.cylorun;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static me.cylorun.Util.*;

public class MapCheckPanel extends JPanel {
    private JButton addButton;
    private JLabel fieldLabel;
    private JButton selectInstances;
    private JButton downloadButton;
    private JTextField urlField;

    public MapCheckPanel() {
        initializeGuiComponents();
        initializeActionListeners();
    }

    private void initializeGuiComponents() {
        this.removeAll();
        Map<JCheckBox, String> checkBoxes = new HashMap<>();
        JsonNode rootNode;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            rootNode = objectMapper.readTree(new File("maps.json"));

            for (JsonNode node : rootNode) {
                String label = node.get("label").asText();
                String url = node.get("url").asText();
                checkBoxes.put(new JCheckBox(label), url);
            }
        } catch (IOException e) {
            errorPane(e);
        }

        int height = 170 + (checkBoxes.size() * 30);

        setPreferredSize(new Dimension(300, height));
        setLayout(null);

        int yPosition = 70;
        for (JCheckBox checkBox : checkBoxes.keySet()) {
            checkBox.setBounds(50, yPosition, 200, 20);
            add(checkBox);
            yPosition += 30;
        }

        addButton = new JButton("Add");
        fieldLabel = new JLabel("Custom map URL");
        selectInstances = new JButton("Select instances");
        downloadButton = new JButton("Download");
        urlField = new JTextField();

        downloadButton.setBounds(150, 20, 150, 40);
        selectInstances.setBounds(0, 20, 150, 40);
        urlField.setBounds(10, height - 70, 270, 40);
        fieldLabel.setBounds(10, height - 100, 100, 30);
        addButton.setBounds(10, height - 30, 60, 30);

        add(downloadButton);
        add(selectInstances);
        add(urlField);
        add(fieldLabel);
        add(addButton);

        for (JCheckBox c : checkBoxes.keySet()) {
            c.addActionListener(e -> {
                if (c.isSelected()) {
                    Util.mapUrls.add(checkBoxes.get(c));
                } else {
                    Util.mapUrls.remove(checkBoxes.get(c));
                }
            });
        }
        Main.frame.pack();
    }

    private void initializeActionListeners() {
        addButton.addActionListener(e -> {
            String url = urlField.getText();
            String label = url.substring(url.lastIndexOf('/') + 1);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

            ArrayNode rootNode;

            try {
                rootNode = (ArrayNode) objectMapper.readTree(new File("maps.json"));
                if (url.endsWith(".zip")) {
                    Map<String, String> map = Map.of(
                            "label", label,
                            "url", url
                    );
                    JsonNode mapNode = objectMapper.valueToTree(map);
                    rootNode.add(mapNode);

                    objectMapper.writeValue(new File("maps.json"), rootNode);
                    JOptionPane.showMessageDialog(null, String.format("Added the map %s with the link: \n %s", label.replace(".zip", ""), url));
                    initializeGuiComponents();

                } else {
                    JOptionPane.showMessageDialog(null, String.format("Invalid URL \n %s ", url), "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException ex) {
                Util.errorPane(ex);
            }
        });

        downloadButton.addActionListener(e -> {
            if (!instancePaths.isEmpty()) {
                if (!mapUrls.isEmpty()) {
                    Util.downloadMaps(instancePaths.get(0));
                    for (int i = 1; i < instancePaths.size(); i++) {
                        String instance = instancePaths.get(i);
                        for (String map : mapPaths) {
                            map = map.replace(".zip", "");
                            Util.copyFolder(new File(map), new File(instance));
                        }
                    }
                    JOptionPane.showMessageDialog(null, "Finished downloading", "hanbani", JOptionPane.INFORMATION_MESSAGE);
                    Util.log(0, "Finished downloading!");
                    mapPaths.clear();

                } else JOptionPane.showMessageDialog(null, "No maps selected", "lol", JOptionPane.WARNING_MESSAGE);

            } else
                JOptionPane.showMessageDialog(null, "No instances selected", "UNLUCKY!", JOptionPane.WARNING_MESSAGE);

        });

        selectInstances.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home"), "/Desktop"));
            fileChooser.setMultiSelectionEnabled(true);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int response = fileChooser.showOpenDialog(null);

            if (response == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();

                for (File file : selectedFiles) {
                    File savesFolder = new File(file, "/.minecraft/saves");
                    log(0, "Instance path " + file);

                    if (savesFolder.exists()) {
                        instancePaths.add(savesFolder.getAbsolutePath());
                    } else {
                        if (savesFolder.toPath().getParent().toFile().exists()) {
                            savesFolder.mkdir();
                            instancePaths.add(savesFolder.getAbsolutePath());
                        } else {
                            log(1, "Invalid mc dir " + file + "\n(no .minecraft or saves folder found)");
                            int choice = JOptionPane.showConfirmDialog(
                                    null,
                                    file.getAbsolutePath() + "\n is not a Minecraft directory \n Would you still like to add it?",
                                    "Invalid Directory",
                                    JOptionPane.YES_NO_OPTION
                            );

                            if (choice == JOptionPane.YES_OPTION) {
                                instancePaths.add(file.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        });
    }
}

