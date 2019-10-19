package com;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ServerConfig {
    private String filename;
    private String name;
    private String address;
    private String username;
    private String serverAndScriptDirectory;
    private String startScriptName;
    private String stopScriptName;
    private ArrayList<String[]> customScripts;

    public ServerConfig() {
        customScripts = new ArrayList<>();
    }

    public ArrayList<String[]> getCustomScripts() {
        return customScripts;
    }

    public void setCustomScripts(ArrayList<String[]> customScriptNames) {
        this.customScripts = customScriptNames;
    }

    public String getStartScriptName() {
        return startScriptName;
    }

    public void setStartScriptName(String startScriptName) {
        this.startScriptName = startScriptName;
    }

    public String getStopScriptName() {
        return stopScriptName;
    }

    public void setStopScriptName(String stopScriptName) {
        this.stopScriptName = stopScriptName;
    }

    public String getServerAndScriptDirectory() {
        return serverAndScriptDirectory;
    }

    public void setServerAndScriptDirectory(String serverAndScriptDirectory) {
        this.serverAndScriptDirectory = serverAndScriptDirectory;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void writeJsonFile() {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("address", address);
        json.put("username", username);
        json.put("serverAndScriptDirectory", serverAndScriptDirectory);
        json.put("startScriptName", startScriptName);
        json.put("stopScriptName", stopScriptName);

        JSONArray customScriptArray = new JSONArray();
        for(String[] customScript : customScripts) {
            if(customScript[0].isEmpty() && customScript[1].isEmpty()) {
                // if both fields are empty, just ignore.
                continue;
            }
            JSONObject scriptObject = new JSONObject();
            scriptObject.put("scriptName", customScript[0]);
            scriptObject.put("scriptNickname", customScript[1]);
            customScriptArray.add(scriptObject);
        }

        json.put("customScripts", customScriptArray);
        File f = new File(filename);
        try {
            if(!f.createNewFile()) {
                return;
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Error creating file, check the filename and try again.",
                    "Error!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        try (FileWriter fileWriter = new FileWriter(f)){
            fileWriter.write(json.toJSONString());
            fileWriter.flush();
            JOptionPane.showMessageDialog(null,
                    "Config file successfully created.",
                    "Success!", JOptionPane.PLAIN_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error writing file, more details can be seen if running from command line.",
                    "Error!", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
