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

import static me.cylorun.FileUtil.*;

public class Panel extends JPanel {
    JButton fieldButton;
    JLabel fieldLabel;
    JButton selectInstances;
    JButton download;
    JTextField field;

    public Panel() {
        initializeGuiComponents();
        initializeActionListeners();
    }

    private void initializeGuiComponents() {
        removeAll();
        Map<JCheckBox, String> checkBoxes = new HashMap<>();
        JsonNode rootNode;


        try {

            ObjectMapper objectMapper = new ObjectMapper();
            rootNode = objectMapper.readTree(new File("maps.json"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (JsonNode node : rootNode) {
            String label = node.get("label").asText();
            String url = node.get("url").asText();
            checkBoxes.put(new JCheckBox(label), url);
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

        fieldButton = new JButton("Add");
        fieldLabel = new JLabel("Custom map URL");
        selectInstances = new JButton("Select instances");
        download = new JButton("Download");
        field = new JTextField();

        download.setBounds(150, 20, 150, 40);
        selectInstances.setBounds(0, 20, 150, 40);
        field.setBounds(10, height - 70, 270, 40);
        fieldLabel.setBounds(10, height - 100, 100, 30);
        fieldButton.setBounds(10, height - 30, 60, 30);

        add(download);
        add(selectInstances);
        add(field);
        add(fieldLabel);
        add(fieldButton);

        for (JCheckBox c : checkBoxes.keySet()) {
            c.addActionListener(e -> {
                if (c.isSelected()) {
                    FileUtil.maps.add(checkBoxes.get(c));
                } else {
                    FileUtil.maps.remove(checkBoxes.get(c));
                }
            });
        }
        Main.frame.pack();
    }
    private void initializeActionListeners(){
        fieldButton.addActionListener(e -> {
            String url = field.getText();
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
                    JOptionPane.showMessageDialog(null, String.format("Added the map %s with the link: \n %s",label.replace(".zip",""),url));
                    initializeGuiComponents();

                } else {
                    JOptionPane.showMessageDialog(null, String.format("Invalid URL \n %s ",url), "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        download.addActionListener(e -> {
            if (!instancePaths.isEmpty()) {
                if (!maps.isEmpty()) {
                    FileUtil.downloadMaps(instancePaths.get(0));
                    for (int i = 1; i < instancePaths.size(); i++) {
                        String instance = instancePaths.get(i);
                        for (String map : mapPaths) {
                            map = map.replace(".zip", "");
                            FileUtil.copyFolder(new File(map), new File(instance));
                        }
                        mapPaths.clear();
                        JOptionPane.showMessageDialog(null, "Finished downloading","hanbani",JOptionPane.INFORMATION_MESSAGE);
                    }

                } else JOptionPane.showMessageDialog(null,"No maps selected","lol",JOptionPane.WARNING_MESSAGE);
                
            } else JOptionPane.showMessageDialog(null, "No instances selected","UNLUCKY!",JOptionPane.WARNING_MESSAGE);

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

                    if (savesFolder.exists()) {
                        instancePaths.add(savesFolder.getAbsolutePath());
                    } else {
                        boolean matchFound = false;
                        for (File f : file.listFiles()) {
                            if (f.getName().contains(".minecraft")) {
                                savesFolder.mkdir();
                                instancePaths.add(savesFolder.getAbsolutePath());
                                matchFound = true;
                                break;
                            }
                        }
                        if (!matchFound) {
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

