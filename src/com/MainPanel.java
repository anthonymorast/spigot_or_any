package com;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class MainPanel extends JPanel implements ActionListener {
    private JComboBox<String> configCombo;
    DefaultComboBoxModel<String> comboBoxModel;
    private JTabbedPane controlPane = new JTabbedPane();
    private CommandTab cmdTab = new CommandTab();
    private ConfigTab configTab = new ConfigTab();

    private GridBagConstraints c = new GridBagConstraints();

    public MainPanel() {
        init();
        createGui();
    }

    private void init() {
        setLayout(new GridBagLayout());
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
    }

    private void createGui() {
        controlPane.add("Commands", cmdTab);
        controlPane.add("Configuration", configTab);

        comboBoxModel = new DefaultComboBoxModel<>();
        configCombo = new JComboBox<>(comboBoxModel);
        configCombo.addActionListener(this);
        JPanel configPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        configPanel.add(new JLabel("Current Configuration:"));
        configPanel.add(new JLabel(""));
        configPanel.add(configCombo);

        c.gridy = 0;
        c.insets = new Insets(0, 5, 25, 0);
        add(configPanel, c);

        c.gridx = 0;
        c.gridy = 2;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1.0;
        c.insets = new Insets(0, 0, 0, 0);
        add(controlPane, c);
    }

    void updateConfigCombo() {
        String selectedName = (String) comboBoxModel.getSelectedItem();
        boolean contains = false;
        comboBoxModel.removeAllElements();
        for(ServerConfig config : MinecraftManager.getConfigurations()) {
            comboBoxModel.addElement(config.getName());
            if(config.getName().equalsIgnoreCase(selectedName)) {
                contains = true;
            }
        }
        if(contains) {
            comboBoxModel.setSelectedItem(selectedName);
        } else if (comboBoxModel.getSize() > 0){
            comboBoxModel.setSelectedItem(comboBoxModel.getElementAt(0));
        }
    }

    boolean nameExists(String name) {
        return comboBoxModel.getIndexOf(name) > -1;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == configCombo) {
            String selectedValue = (String)comboBoxModel.getSelectedItem();
            for(ServerConfig config : MinecraftManager.getConfigurations()) {
                if(config.getName().equalsIgnoreCase(selectedValue)) {
                    configTab.setCurrentConfig(config);
                    cmdTab.setCurrentConfig(config);
                    return;
                }
            }
            configTab.setCurrentConfig(new ServerConfig());
        }
    }
}
