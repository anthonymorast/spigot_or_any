package com;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class MinecraftManager{
    private static MainPanel mainPanel = new MainPanel();
    private static String workingDirectory;
    private static ArrayList<ServerConfig> configurations;

    public static void main(String[] args) {
        new MinecraftManager();
    }

    private MinecraftManager() {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error setting application LookAndFeel.",
                    "Error!", JOptionPane.ERROR_MESSAGE);
        }
        createGui();
        init();
    }

    private void init() {
        configurations = new ArrayList<>();
        workingDirectory = System.getProperty("user.dir");
        findAndReadConfigFiles();
        mainPanel.updateConfigCombo();
    }

    private static void findAndReadConfigFiles() {
        File dir = new File(workingDirectory+"/config/");
        File[] files = dir.listFiles((dir1, name) -> name.endsWith(".json"));
        assert files != null;
        for(File f : files) {
            parseJson(f);
        }
    }

    private static void parseJson(File f) {
        try {
            JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(f.getAbsolutePath()));
            ServerConfig config = new ServerConfig();
            config.setFilename(f.getAbsolutePath());
            config.setName((String)json.get("name"));
            config.setAddress((String)json.get("address"));
            config.setUsername((String)json.get("username"));
            config.setServerAndScriptDirectory((String)json.get("serverAndScriptDirectory"));
            config.setStartScriptName((String)json.get("startScriptName"));
            config.setStopScriptName((String)json.get("stopScriptName"));

            JSONArray customScriptsJson = (JSONArray) json.get("customScripts");
            Iterator itr = customScriptsJson.iterator();
            ArrayList<String[]> customScripts = new ArrayList<>();
            while(itr.hasNext()) {
                Iterator scriptValues = ((Map)itr.next()).entrySet().iterator();
                while(scriptValues.hasNext()) {
                    Map.Entry scriptName = (Map.Entry) scriptValues.next();
                    Map.Entry scriptNickName = (Map.Entry) scriptValues.next();
                    String[] customScript = new String[]{scriptName.getValue().toString(), scriptNickName.getValue().toString()};
                    customScripts.add(customScript);
                }
            }
            config.setCustomScripts(customScripts);

            configurations.add(config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getWorkingDirectory() {
        return workingDirectory;
    }

    static String getConfigDirectory() {
        return workingDirectory+"/config/";
    }

    static ArrayList<ServerConfig> getConfigurations() {
        return configurations;
    }

    static void deleteConfig(ServerConfig config) {
        configurations.remove(config);
        File f = new File(config.getFilename());
        f.delete();
        updateConfigCombo();
    }

    static void updateConfigCombo() {
        configurations.clear();
        findAndReadConfigFiles();
        mainPanel.updateConfigCombo();
    }

    static boolean nameExists(String name) {
        return mainPanel.nameExists(name);
    }

    private void createGui() {
        JFrame mainFrame = new JFrame("MinecraftManager");
        mainFrame.setLayout(new GridBagLayout());
        mainFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        GridBagConstraints c = new GridBagConstraints();
        Dimension resolution = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = 600;
        int xLocation = (int)((resolution.getWidth() / 2) - (screenWidth / 2));
        int screenHeight = 600;
        int yLocation = (int)((resolution.getHeight() / 2) - (screenHeight /2 ));
        mainFrame.setLocation(xLocation, yLocation);

        c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridheight = 3;
        c.gridwidth = 2;
        c.weightx  = 1;
        c.weighty = 1;
        mainFrame.add(mainPanel, c);

        int width = screenWidth;
        int height = screenHeight;
        if (resolution.getWidth() < screenWidth) {
            width = ((Double)resolution.getWidth()).intValue() - 50;
        }
        if (resolution.getHeight() < screenHeight) {
            height = ((Double)resolution.getHeight()).intValue() - 50;
        }
        mainFrame.setMinimumSize(new Dimension(width, height));
        mainFrame.setPreferredSize(new Dimension(width, height));
        mainFrame.pack();
        mainFrame.setVisible(true);
        SwingUtilities.updateComponentTreeUI(mainFrame);
        mainFrame.setResizable(false);
    }
}