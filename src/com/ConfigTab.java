package com;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ConfigTab extends JPanel implements ActionListener, TableModelListener {
    private JLabel nameLabel = new JLabel("Server Name");
    private JTextField nameField = new JTextField(20);

    private JLabel addressLabel = new JLabel("Server Address*");
    private JTextField addressField = new JTextField(20);

    private JLabel usernameLabel = new JLabel("Username*");
    private JTextField usernameField = new JTextField(20);

    private JLabel serverScriptLabel = new JLabel("Server and Script Directory*");
    private JTextField serverScriptField = new JTextField(35);

    private JLabel serverStartLabel = new JLabel("Start Script Name");
    private JTextField serverStartField = new JTextField(20);

    private JLabel serverStopLabel = new JLabel("Start Script Name");
    private JTextField serverStopField = new JTextField(20);

    private JButton addButton = new JButton("Add Row");
    private JButton deleteButton = new JButton("Delete Row");
    private JTable customScriptTable;
    private DefaultTableModel model;

    private JButton saveConfigButton = new JButton("Save Config");
    private JButton deleteConfigButton = new JButton("Delete Config");
    private JButton newConfigButton = new JButton("New Config");

    private GridBagConstraints c = new GridBagConstraints();
    private ServerConfig currentConfig = new ServerConfig();

    ConfigTab() {
        init();
        createGui();
    }

    void setCurrentConfig(ServerConfig config) {
        this.currentConfig = config;
        updateFields();
        updateTable();
    }

    private void updateTable() {
        while(model.getRowCount() > 0) {
            model.removeRow(0);
        }
        for(String[] customScript : currentConfig.getCustomScripts()) {
            model.addRow(new Object[]{customScript[0], customScript[1]});
        }
    }

    private void updateFields() {
        nameField.setText(currentConfig.getName());
        addressField.setText(currentConfig.getAddress());
        usernameField.setText(currentConfig.getUsername());
        serverScriptField.setText(currentConfig.getServerAndScriptDirectory());
        serverStartField.setText(currentConfig.getStartScriptName());
        serverStopField.setText(currentConfig.getStopScriptName());
    }

    private void init() {
        setLayout(new BorderLayout());
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
    }

    private void createGui() {
        JPanel fieldPanel = new JPanel(new GridBagLayout());
        c.gridy = 0;
        c.insets = new Insets(10, 0, 15, 10);
        fieldPanel.add(nameLabel, c);
        c.gridy = 0;
        c.gridx = 1;
        c.insets = new Insets(10, 10, 15, 0);
        fieldPanel.add(nameField, c);

        c.gridx = 0;
        c.gridy = 1;
        c.insets = new Insets(0, 0, 15, 10);
        fieldPanel.add(addressLabel, c);
        c.gridy = 1;
        c.gridx = 1;
        c.insets = new Insets(0, 10, 15, 0);
        fieldPanel.add(addressField, c);

        c.gridx = 0;
        c.gridy = 2;
        c.insets = new Insets(0, 0, 15, 10);
        fieldPanel.add(usernameLabel, c);
        c.gridy = 2;
        c.gridx = 1;
        c.insets = new Insets(0, 10, 15, 0);
        fieldPanel.add(usernameField, c);

        c.gridx = 0;
        c.gridy = 3;
        c.insets = new Insets(0, 0, 15, 10);
        fieldPanel.add(serverScriptLabel, c);
        c.gridy = 3;
        c.gridx = 1;
        c.insets = new Insets(0, 10, 15, 0);
        fieldPanel.add(serverScriptField, c);

        c.gridx = 0;
        c.gridy = 4;
        c.insets = new Insets(0, 0, 15, 10);
        fieldPanel.add(serverStartLabel, c);
        c.gridy = 4;
        c.gridx = 1;
        c.insets = new Insets(0, 10, 15, 0);
        fieldPanel.add(serverStartField, c);

        c.gridx = 0;
        c.gridy = 5;
        c.insets = new Insets(0, 0, 15, 10);
        fieldPanel.add(serverStopLabel, c);
        c.gridy = 5;
        c.gridx = 1;
        c.insets = new Insets(0, 10, 15, 0);
        fieldPanel.add(serverStopField, c);

        JPanel tablePanel = new JPanel(new BorderLayout());
        JScrollPane tableScroll = createCustomScriptsTable();

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel scriptButtonPanel = new JPanel();
        JPanel configButtonPanel = new JPanel();

        newConfigButton.addActionListener(this);
        saveConfigButton.addActionListener(this);
        deleteConfigButton.addActionListener(this);
        addButton.addActionListener(this);
        deleteButton.addActionListener(this);

        configButtonPanel.add(newConfigButton);
        configButtonPanel.add(saveConfigButton);
        configButtonPanel.add(deleteConfigButton);

        scriptButtonPanel.add(addButton);
        scriptButtonPanel.add(deleteButton);

        buttonPanel.add(scriptButtonPanel, BorderLayout.EAST);
        buttonPanel.add(configButtonPanel, BorderLayout.WEST);

        tablePanel.add(tableScroll, BorderLayout.CENTER);
        tablePanel.add(buttonPanel, BorderLayout.SOUTH);

        add(fieldPanel, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.SOUTH);
    }

    private JScrollPane createCustomScriptsTable() {
        String[] cols = new String[] {"Name", "File"};
        model = new DefaultTableModel(cols, 0);
        model.addTableModelListener(this);
        for(String[] customScript : currentConfig.getCustomScripts()) {
            model.addColumn(new Object[]{customScript[0], customScript[1]});
        }
        customScriptTable = new JTable(model);
        customScriptTable.setPreferredScrollableViewportSize(new Dimension(100, 70));
        customScriptTable.setFillsViewportHeight(true);
        customScriptTable.setAutoCreateRowSorter(true);
        return new JScrollPane(customScriptTable);
    }

    private void setCurrentConfigFields() {
        currentConfig.setName(nameField.getText());
        currentConfig.setAddress(addressField.getText());
        currentConfig.setUsername(usernameField.getText());
        currentConfig.setServerAndScriptDirectory(serverScriptField.getText());
        currentConfig.setStartScriptName(serverStartField.getText());
        currentConfig.setStopScriptName(serverStopField.getText());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == addButton) {
            model.addRow(new String[]{"", ""});
            currentConfig.getCustomScripts().add(new String[]{"", ""});
        } else if (e.getSource() == deleteButton) {
            int idx = customScriptTable.convertRowIndexToModel(customScriptTable.getSelectedRow());
            model.removeRow(idx);
            currentConfig.getCustomScripts().remove(idx);
        } else if (e.getSource() == deleteConfigButton) {
            MinecraftManager.deleteConfig(currentConfig);
            if(MinecraftManager.getConfigurations().isEmpty()) {
                currentConfig = new ServerConfig();
            } else {
                currentConfig = MinecraftManager.getConfigurations().get(0);
            }
            updateTable();
        } else if (e.getSource() == saveConfigButton) {
            String filename = JOptionPane.showInputDialog(this, "Enter config file name");
            if(!filename.endsWith(".json")){
                filename += ".json";
            }

            if (filename.isEmpty()) {
                JOptionPane.showMessageDialog(this, "File name is required, configuration not saved.",
                        "Error!", JOptionPane.ERROR_MESSAGE);
            } else {
                File f = new File(MinecraftManager.getConfigDirectory()+filename);
                if(f.exists()) {
                    String[] options = new String[]{"Overwrite", "Cancel"};
                    int response = JOptionPane.showOptionDialog(this,
                            "File already exists. What do you want to do?", "File Exists",
                            JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[1]);
                    if (response == 1) {
                        return;
                    }
                }
                if (addressField.getText().isEmpty() || usernameField.getText().isEmpty()
                        || serverScriptField.getText().isEmpty() || nameField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "Server Name, Address, Username, and Server and Script Directory are required fields.",
                            "Error!", JOptionPane.ERROR_MESSAGE);
                    return;
                } else if (MinecraftManager.nameExists(nameField.getText())) {
                    JOptionPane.showMessageDialog(this,
                            "Please enter a distinct server name.",
                            "Error!", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if(f.exists()) {
                    f.delete();
                }
                currentConfig.setFilename(MinecraftManager.getConfigDirectory() + filename);
                setCurrentConfigFields();
                currentConfig.writeJsonFile();
                MinecraftManager.updateConfigCombo();
            }
        }else if (e.getSource() == newConfigButton) {
            setCurrentConfig(new ServerConfig());
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if(e.getSource() == model) {
            if(customScriptTable.getSelectedRow() > 0) {
                int idx = customScriptTable.convertRowIndexToModel(customScriptTable.getSelectedRow());
                currentConfig.getCustomScripts().set(idx,
                        new String[]{model.getValueAt(idx, 0).toString(), model.getValueAt(idx, 0).toString()});
            }
        }
    }
}
