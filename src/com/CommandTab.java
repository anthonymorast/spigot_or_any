package com;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;

public class CommandTab extends JPanel implements ActionListener {
    private String baseCmd = "cmd.exe /c start cmd.exe /k plink -ssh ";
    private StringBuilder cmdStringBuilder = new StringBuilder(baseCmd);

    private JLabel passwordLabel = new JLabel("Password (cannot be stored): ");
    private JTextField passwordField = new JTextField(20);
    private JButton startButton = new JButton("Start Server");
    private JButton stopButton = new JButton("Stop Server");
    private JButton restartButton = new JButton("Restart Server");
    private JLabel customScriptLabel = new JLabel("Custom Script: ");
    private DefaultComboBoxModel<String> customComboBoxModel = new DefaultComboBoxModel<>();
    private JButton customScriptButton = new JButton("Run Custom Script");
    private JTextArea outputTextArea = new JTextArea();

    private ServerConfig currentConfig = new ServerConfig();

    CommandTab() {
        init();
        createGui();
    }

    private void init() {
        setLayout(new BorderLayout());
    }

    private void createGui() {
        JPanel buttonPwdPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JPanel passwordPanel = new JPanel(new FlowLayout());
        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);

        buttonPwdPanel.add(passwordPanel);

        JPanel buttonPanel = new JPanel(new BorderLayout());
        JPanel topButtonPanel = new JPanel(new FlowLayout());
        JPanel bottomButtonPanel = new JPanel(new FlowLayout());
        JComboBox<String> customScriptCombo = new JComboBox<>(customComboBoxModel);
        setActionListeners();

        topButtonPanel.add(startButton);
        topButtonPanel.add(stopButton);
        topButtonPanel.add(restartButton);
        topButtonPanel.add(customScriptButton);

        bottomButtonPanel.add(customScriptLabel);
        bottomButtonPanel.add(customScriptCombo);

        buttonPanel.add(topButtonPanel, BorderLayout.NORTH);
        buttonPanel.add(bottomButtonPanel, BorderLayout.CENTER);

        buttonPwdPanel.add(buttonPanel);
        buttonPwdPanel.setPreferredSize(new Dimension(200, 125));

        add(buttonPwdPanel, BorderLayout.NORTH);

        JPanel textAreaPanel = new JPanel(new BorderLayout());
        outputTextArea.setEditable(false);
        outputTextArea.setLineWrap(true);
        outputTextArea.setWrapStyleWord(true);
        outputTextArea.setMargin(new Insets(5, 5, 5,5));

        JScrollPane scrollPane = new JScrollPane(outputTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        DefaultCaret caret = (DefaultCaret)outputTextArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        outputTextArea.setCaret(caret);

        textAreaPanel.add(scrollPane, BorderLayout.CENTER);
        textAreaPanel.setBorder(new EmptyBorder(15, 15, 0, 15));

        add(textAreaPanel, BorderLayout.CENTER);
    }

    private void setActionListeners() {
        startButton.addActionListener(this);
        stopButton.addActionListener(this);
        restartButton.addActionListener(this);
        customScriptButton.addActionListener(this);
    }

    void setCurrentConfig(ServerConfig config) {
        currentConfig = config;
        updateCustomCombo();
    }

    private void updateCustomCombo() {
        customComboBoxModel.removeAllElements();
        for(String[] command : currentConfig.getCustomScripts()) {
            customComboBoxModel.addElement(command[0]);
        }
    }

    private void startServer() {
        setupCmdString();
        cmdStringBuilder.append("./").append(currentConfig.getStartScriptName()).append("\"");
        writeToTextArea("Executing Command:");
        writeToTextArea("    " + cmdStringBuilder.toString());

        executeCommand();

        cmdStringBuilder = new StringBuilder(baseCmd);
        writeToTextArea("");
    }

    private void stopServer() {
        setupCmdString();
        cmdStringBuilder.append("./").append(currentConfig.getStopScriptName()).append("\"");
        writeToTextArea("Executing Command:");
        writeToTextArea("    " + cmdStringBuilder.toString());

        executeCommand();

        cmdStringBuilder = new StringBuilder(baseCmd);
        writeToTextArea("");
    }

    private void customCommand() {
        if(customComboBoxModel.getSize() <= 0) {
            return;
        }
        String cmdName = (String)customComboBoxModel.getSelectedItem();
        String customCommand = "";
        for(String[] cmd : currentConfig.getCustomScripts()) {
            if(cmd[0].equalsIgnoreCase(cmdName)){
                customCommand = cmd[1];
            }
        }

        setupCmdString();
        cmdStringBuilder.append("./").append(customCommand).append("\"");
        writeToTextArea("Executing Command:");
        writeToTextArea("    " + cmdStringBuilder.toString());

        executeCommand();

        cmdStringBuilder = new StringBuilder(baseCmd);
        writeToTextArea("");
    }

    private void setupCmdString() {
        cmdStringBuilder.append(currentConfig.getUsername()).append("@").append(currentConfig.getAddress()).append(" ")
                .append("-pw").append(" ").append(passwordField.getText()).append(" \"")
                .append("cd ").append(currentConfig.getServerAndScriptDirectory()).append(" ")
                .append("&& ");
    }

    private void writeToTextArea(String message) {
        outputTextArea.append(message);
        outputTextArea.append("\n");
    }

    private void executeCommand() {
        // executes the command currently stored in cmdStringBuilder which is built by specific callbacks
        try {
            Process proc = Runtime.getRuntime().exec(cmdStringBuilder.toString());
            BufferedReader stdin = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stderr = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while(stdin.ready()) {
                writeToTextArea(stdin.readLine());
            }
            while(stderr.ready()) {
                writeToTextArea(stderr.readLine());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error running start command.", "Error!", JOptionPane.ERROR_MESSAGE);
            writeToTextArea(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if((e.getSource() == startButton || e.getSource() == stopButton ||
            e.getSource() == restartButton || e.getSource() == customScriptButton) && passwordField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required.", "Error!", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if(e.getSource() == startButton) {
            startServer();
        } else if(e.getSource() == stopButton) {
            stopServer();
        } else if (e.getSource() == restartButton) {
            stopServer();
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            startServer();
        } else if(e.getSource() == customScriptButton) {
            customCommand();
        }
    }
}
