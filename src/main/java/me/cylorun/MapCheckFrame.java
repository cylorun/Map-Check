package me.cylorun;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapCheckFrame extends JFrame {
    private final JPanel mainPanel;
    private JButton downloadButton;
    private JTextField urlField;
    private JButton instSelectButton;
    private JButton selectAllButton;
    private JButton deSelectAllButton;
    private JProgressBar progressBar;
    public List<String> selectedMaps = new ArrayList<>();
    public List<String> instancePaths = new ArrayList<>();
    private Map<JCheckBox, JsonObject> checkBoxes;
    private int currentStep = 0;
    private static MapCheckFrame instance;

    private MapCheckFrame() {
        this.mainPanel = new JPanel();
        this.downloadMapInfo();
        this.initializeMainPanel();
        this.initializeActionListeners();
        this.setupFrame();
    }

    public static synchronized MapCheckFrame getInstance() {
        if (instance == null) {
            instance = new MapCheckFrame();
        }
        return instance;
    }

    private void setupFrame() {
        this.setVisible(true);
        this.setTitle("Map-Check v" + MapCheck.VERSION);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.add(this.mainPanel);
        this.pack();
    }

    private void reload() {
        this.mainPanel.removeAll();
        this.initializeMainPanel();
        this.pack();
    }

    private void downloadMapInfo() {
        try {
            if (Files.exists(Paths.get("maps.json"))) {
                Files.delete(Paths.get("maps.json"));
            }

            URL url = MapCheck.MAPS_URL;
            Files.copy(url.openStream(), Paths.get("maps.json"));
        } catch (IOException e) {
            showError(e);
        }
    }


    private void initializeMainPanel() {
        int totalMaps = 0;
        try {
            totalMaps = this.getMapCount();
        } catch (IOException e) {
            showError(e);
            return;
        }
        int height = 220 + (totalMaps * 30);

        this.downloadButton = new JButton("Download");
        this.urlField = new JTextField();
        this.instSelectButton = new JButton("Select Instances");
        this.progressBar = new JProgressBar(0, 100);
        this.selectAllButton = new JButton("Select All");
        this.deSelectAllButton = new JButton("Deselect All");

        this.progressBar.setStringPainted(true);
        this.progressBar.setFont(new Font("Arial", Font.PLAIN, 15));

        this.mainPanel.setLayout(new GridBagLayout());
        this.mainPanel.setPreferredSize(new Dimension(300, height));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        this.addComponentsToMainPanel(gbc);
    }

    private void addComponentsToMainPanel(GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        this.mainPanel.add(instSelectButton, gbc);

        gbc.gridx = 1;
        this.mainPanel.add(downloadButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        this.mainPanel.add(selectAllButton, gbc);

        gbc.gridx = 1;
        this.mainPanel.add(deSelectAllButton, gbc);


        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        this.mainPanel.add(progressBar, gbc);

        gbc.gridy = 3;
        this.mainPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);

        gbc.gridy = 4;
        this.addCheckBoxes(gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        this.mainPanel.add(new JSeparator(SwingConstants.HORIZONTAL), gbc);
    }

    private Map<JCheckBox, JsonObject> getCheckBoxes() {
        Map<JCheckBox, JsonObject> checkBoxes = new HashMap<>();
        String jsonContent;
        try {
            jsonContent = new String(Files.readAllBytes(Paths.get("maps.json")));
        } catch (FileNotFoundException e) {
            MapCheckFrame.showError("Maps.json file not found, try re-launching map-check");
            System.exit(0);
            throw new RuntimeException(e);
        } catch (IOException e) {
            MapCheckFrame.showError("Something weird happened: " + e);
            System.exit(1);
            throw new RuntimeException(e);
        }

        JsonArray ja = JsonParser.parseString(jsonContent).getAsJsonArray();
        for (JsonElement element : ja) {
            JsonObject jsonObject = element.getAsJsonObject();
            String label = jsonObject.get("label").getAsString();
            String creator = jsonObject.get("creator").getAsString();

            checkBoxes.put(new JCheckBox(String.format("%s (by %s)", label, creator)), jsonObject);
        }
        return checkBoxes;
    }

    private void addCheckBoxes(GridBagConstraints gbc) {
        this.checkBoxes = getCheckBoxes();
        for (JCheckBox c : this.checkBoxes.keySet()) {
            c.addActionListener(e -> {
                if (c.isSelected()) {
                    this.selectedMaps.add(this.checkBoxes.get(c).get("url").getAsString());
                } else {
                    this.selectedMaps.remove(this.checkBoxes.get(c).get("url").getAsString());
                }
            });
            gbc.gridx = 0;
            gbc.gridy++;
            gbc.gridwidth = 2;
            this.mainPanel.add(c, gbc);
        }
    }

    private int getMapCount() throws IOException {
        String jsonContent = new String(Files.readAllBytes(Paths.get("maps.json")));
        JsonArray ja = JsonParser.parseString(jsonContent).getAsJsonArray();
        return ja.size();
    }

    private void initializeActionListeners() {

        this.downloadButton.addActionListener(e -> new Thread(this::downloadMaps).start());

        this.instSelectButton.addActionListener(e -> openFileChooser());

        this.selectAllButton.addActionListener(a -> selectAllCheckBoxes(true));

        this.deSelectAllButton.addActionListener(a -> selectAllCheckBoxes(false));
    }

    private void selectAllCheckBoxes(boolean select) {
        for (JCheckBox box : this.checkBoxes.keySet()) {
            box.setSelected(select);
            ActionEvent e = new ActionEvent(box, ActionEvent.ACTION_PERFORMED, "select");
            for (ActionListener listener : box.getActionListeners()) {
                listener.actionPerformed(e);
            }
        }
    }

    private void openFileChooser() {
        JFileChooser fileChooser = new JFileChooser(new File(System.getProperty("user.home"), "Desktop"));
        fileChooser.setMultiSelectionEnabled(true);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int response = fileChooser.showOpenDialog(null);

        if (response == JFileChooser.APPROVE_OPTION) {
            File[] selectedFiles = fileChooser.getSelectedFiles();
            for (File file : selectedFiles) {
                this.processSelectedDir(file);
            }
        }
    }

    private void processSelectedDir(File file) {
        File mcDir = new File(file, ".minecraft");
        File altMcDir = new File(file, "minecraft");

        String savesPath = null;

        if (mcDir.exists()) {
            savesPath = Paths.get(mcDir.toString()).resolve("saves").toString();
        } else if (altMcDir.exists()) {
            savesPath = Paths.get(altMcDir.toString()).resolve("saves").toString();
        }

        if (savesPath != null) {
            this.instancePaths.add(savesPath);
            System.out.println("Added: " + savesPath);
        } else {
            int choice = JOptionPane.showConfirmDialog(
                    null,
                    file.getAbsolutePath() + "\n is not a Minecraft directory \n Would you still like to add it?",
                    "Invalid Directory",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                instancePaths.add(file.getAbsolutePath());
                System.out.println("Added: " + file.getAbsolutePath());
            }
        }
    }

    public void updateProgressBar() {
        this.currentStep++;
        int progress = (int) ((double) this.currentStep / ((this.instancePaths.size() * this.selectedMaps.size()) + (this.selectedMaps.size() * 2)) * 100);
        SwingUtilities.invokeLater(() -> this.progressBar.setValue(progress));
    }

    private void resetProgressBar() {
        this.currentStep = 0;
        SwingUtilities.invokeLater(() -> this.progressBar.setValue(0));
    }

    private void downloadMaps() {
        resetProgressBar();
        if (!this.instancePaths.isEmpty() && !this.selectedMaps.isEmpty()) {
            List<String> downloadedMapsPaths = FileUtil.downloadToTemp(this.selectedMaps);
            FileUtil.copyFromTemp(this.instancePaths, downloadedMapsPaths);
            try {
                FileUtils.deleteDirectory(new File(Paths.get(System.getProperty("user.dir"), "mc_temp").toString()));
            } catch (IOException e) {
                showError(e);
                return;
            }
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "Finished downloading", "Download Status", JOptionPane.INFORMATION_MESSAGE);
        } else {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "No maps or instances selected", "Download Status", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void showError(Object o) {
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(null, "An error occurred\n" + o.toString());
    }
}


class MapCheck {
    public static String VERSION = "4.2.0";
    public static URL MAPS_URL;

    static {
        try {
            MAPS_URL = new URL("https://cylorun.com/api/maps");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new FlatDarculaLaf());
        MapCheckFrame.getInstance();
    }
}