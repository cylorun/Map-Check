package me.cylorun;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.formdev.flatlaf.FlatDarculaLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.print.Book;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static me.cylorun.FileUtil.instancePaths;
import static me.cylorun.FileUtil.mapPaths;

public class Panel extends JPanel {
    private final Map<JCheckBox, String> checkBoxes = new HashMap<>();



    public Panel()  {

        JsonNode rootNode = null;
        try {
        UIManager.setLookAndFeel(new FlatDarculaLaf());


        ObjectMapper objectMapper = new ObjectMapper();
        rootNode = objectMapper.readTree(new File("C:\\Users\\alfgr\\Desktop\\maps.json"));
        } catch (UnsupportedLookAndFeelException | IOException e) {
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
            setBackground(new Color(49, 53, 66));

            int yPosition = 70;
            for (JCheckBox checkBox : checkBoxes.keySet()) {
                checkBox.setBounds(50, yPosition, 200, 20);
                add(checkBox);
                yPosition += 30;
            }
                JButton fieldButton = new JButton("Add");
                JLabel fieldLabel = new JLabel("Custom map URL");
                JButton selectInstances = new JButton("Select instances");
                JButton download = new JButton("Download");
                JTextField field = new JTextField();


                download.setBounds(150, 20, 150, 40);
                selectInstances.setBounds(0, 20, 150, 40);
                field.setBounds(10, height-70, 270, 40);
                fieldLabel.setBounds(10, height-100, 100, 30);
                fieldButton.setBounds(10, height-30, 60, 30);


                add(download);
                add(selectInstances);
                add(field);
                add(fieldLabel);
                add(fieldButton);


                fieldButton.addActionListener(e -> {
                    String text = field.getText();

                    //TODO fix path, reload window to show new map (or add reload button)
                    if (text.endsWith(".zip")) {

                        // Create a map for each object
                        Map<String, String> item = Map.of(
                                "label", "Zero Sorting",
                                "url", "https://github.com/Semperzz/Zero-Sorting-Practice/releases/download/v1.5/Zero.Sorting.zip"
                        );

                        // Add the map to the list

                        // Create an ObjectMapper to write JSON
                        ObjectMapper objectMapper = new ObjectMapper();

                        // Enable pretty printing (optional)
                        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

                        // Specify the file path where you want to save the JSON
                        File jsonFile = new File("output.json");

                            // Write the JSON data to the file
                        try {
                            objectMapper.writeValue(jsonFile, item);
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        System.out.println("JSON data written to " + jsonFile.getAbsolutePath());
                    } else {
                        JOptionPane.showMessageDialog(null, "Invalid URL", "Error", JOptionPane.ERROR_MESSAGE);

                    }
                });

                download.addActionListener(e -> {
                    if (!instancePaths.isEmpty()) {
                        FileUtil fu = new FileUtil();
                        fu.downloadMaps(instancePaths.get(0));
                        instancePaths.remove(0);

                        for (String instance : instancePaths) {
                            try {
                                if (!mapPaths.isEmpty()) {
                                    for (String map : mapPaths) {
                                        map = map.replace(".zip", "");
                                        fu.copyFolder(new File(map), new File(instance));
                                    }
                                }
                            } catch (IOException ex) {
                                throw new RuntimeException(ex);
                            }
                        }

                        JOptionPane.showMessageDialog(null, "Finished downloading");
                    } else {
                        JOptionPane.showMessageDialog(new JFrame(), "No instances selected");
                    }
                });

                selectInstances.addActionListener(e -> {
                    JFileChooser fileChooser = new JFileChooser();

                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home"), "\\Desktop"));
                    fileChooser.setMultiSelectionEnabled(true);
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    int response = fileChooser.showOpenDialog(null);

                    if (response == JFileChooser.APPROVE_OPTION) {
                        File[] selectedFiles = fileChooser.getSelectedFiles();

                        for (File file : selectedFiles) {
                            File savesFolder = new File(file + "\\.minecraft\\saves");

                            if (savesFolder.exists()) {
                                instancePaths.add(savesFolder.getAbsolutePath());
                            } else {
                                int choice = JOptionPane.showConfirmDialog(
                                        null,
                                        file.getAbsolutePath() + "\n is not a Minecraft directory \n Would you still like to add it?",
                                        "Invalid Directory",
                                        JOptionPane.YES_NO_OPTION
                                );

                                if (choice == JOptionPane.YES_OPTION) {
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
                                        instancePaths.add(file.getAbsolutePath());
                                    }
                                }
                            }
                        }
                    }
                });

                for (JCheckBox c : checkBoxes.keySet()) {
                    c.addActionListener(e -> {
                        if (c.isSelected()) {
                            FileUtil.maps.add(checkBoxes.get(c));
                        } else {
                            FileUtil.maps.remove(checkBoxes.get(c));
                        }
                    });
                }
            }
    }

