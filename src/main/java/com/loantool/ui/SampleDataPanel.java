package com.loantool.ui;

import com.loantool.utils.FileHandler;
import com.loantool.utils.TestDataGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SampleDataPanel extends JPanel {
    private JSpinner recordCountSpinner;
    private JTextField filenameField;
    private JButton generateButton;
    private JTextArea infoArea;

    public SampleDataPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 248, 250));
        createPanel();
    }

    private void createPanel() {
        // Input Panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(new Color(220, 237, 253)); // Light blue
        TitledBorder inputBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(104, 159, 56), 2),
                "Sample Data Generation");
        inputBorder.setTitleColor(new Color(104, 159, 56));
        inputBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.setBorder(inputBorder);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Record Count
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(new JLabel("Number of Records (10-10000):"), gbc);
        gbc.gridx = 1;
        recordCountSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 10000, 10));
        inputPanel.add(recordCountSpinner, gbc);

        // Filename
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(new JLabel("Filename:"), gbc);
        gbc.gridx = 1;
        filenameField = new JTextField(20);
        filenameField.setText("sample_data.csv");
        inputPanel.add(filenameField, gbc);

        // Generate Button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        generateButton = new JButton("Generate Sample Data");
        generateButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateButton.setBackground(new Color(104, 159, 56)); // Light green
        generateButton.setForeground(Color.WHITE);
        generateButton.setFocusPainted(false);
        generateButton.addActionListener(e -> generateData());
        inputPanel.add(generateButton, gbc);

        // Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(255, 245, 238)); // Light peach
        TitledBorder infoBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(237, 108, 2), 2),
                "Information");
        infoBorder.setTitleColor(new Color(237, 108, 2));
        infoBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.setBorder(infoBorder);

        infoArea = new JTextArea(
                "Sample Data Generator\n" +
                        "═══════════════════════\n\n" +
                        "This tool generates sample CSV files with realistic applicant data.\n\n" +
                        "Generated records include:\n" +
                        "• Realistic income levels ($1,500 - $10,000)\n" +
                        "• Varied credit scores (350-850)\n" +
                        "• Employment durations (0-60 months)\n" +
                        "• Diverse debt-to-income ratios\n" +
                        "• Various loan amount requests\n\n" +
                        "Files are saved to: data/input/\n\n" +
                        "You can use these files to test the CSV processing functionality.");
        infoArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        infoArea.setEditable(false);
        infoArea.setBackground(Color.WHITE);
        infoArea.setForeground(new Color(30, 30, 30));
        infoArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        JScrollPane infoScroll = new JScrollPane(infoArea);

        infoPanel.add(infoScroll, BorderLayout.CENTER);

        // Layout
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(inputPanel, BorderLayout.NORTH);
        leftPanel.add(infoPanel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.CENTER);
    }

    private void generateData() {
        try {
            int count = (Integer) recordCountSpinner.getValue();

            if (count < 10 || count > 10000) {
                JOptionPane.showMessageDialog(this,
                        "Please enter a value between 10 and 10000.",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            String filename = filenameField.getText().trim();
            if (filename.isEmpty()) {
                filename = "sample_data.csv";
                filenameField.setText(filename);
            }

            if (!filename.endsWith(".csv")) {
                filename += ".csv";
                filenameField.setText(filename);
            }

            String filePath = "data/input/" + filename;

            generateButton.setEnabled(false);
            generateButton.setText("Generating...");

            SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
                @Override
                protected Void doInBackground() throws Exception {
                    FileHandler.createDirectoryIfNotExists("data/input");
                    TestDataGenerator.generateCSV(filePath, count);
                    return null;
                }

                @Override
                protected void done() {
                    generateButton.setEnabled(true);
                    generateButton.setText("Generate Sample Data");

                    try {
                        get();
                        JOptionPane.showMessageDialog(SampleDataPanel.this,
                                String.format(
                                        "Successfully generated %d sample records!\n\n" +
                                                "Location: %s\n\n" +
                                                "You can now use this file in the CSV processing panel.",
                                        count, filePath),
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(SampleDataPanel.this,
                                "Error generating sample data: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
