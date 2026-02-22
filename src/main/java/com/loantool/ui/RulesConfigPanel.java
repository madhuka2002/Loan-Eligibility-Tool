package com.loantool.ui;

import com.loantool.config.RulesConfig;
import com.loantool.datastructures.RulesFlowGraph;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RulesConfigPanel extends JPanel {
    private JTextField minIncomeField;
    private JTextField maxDebtRatioField;
    private JTextField minCreditScoreField;
    private JTextField minEmploymentField;
    private JTextField maxLoanRatioField;
    private JTextField lowRiskThresholdField;
    private JTextField mediumRiskThresholdField;
    private JButton saveButton;
    private JButton resetButton;
    private JTextArea rulesDisplayArea;

    public RulesConfigPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 248, 250));
        createPanel();
        loadCurrentConfig();
    }

    private void createPanel() {
        // Rules Display Panel
        JPanel displayPanel = new JPanel(new BorderLayout());
        displayPanel.setBackground(new Color(255, 245, 238)); // Light peach
        TitledBorder displayBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(237, 108, 2), 2),
                "Current Rules Configuration");
        displayBorder.setTitleColor(new Color(237, 108, 2));
        displayBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        displayPanel.setBorder(displayBorder);

        rulesDisplayArea = new JTextArea(8, 50);
        rulesDisplayArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        rulesDisplayArea.setEditable(false);
        rulesDisplayArea.setBackground(Color.WHITE);
        rulesDisplayArea.setForeground(new Color(30, 30, 30));
        JScrollPane displayScroll = new JScrollPane(rulesDisplayArea);

        displayPanel.add(displayScroll, BorderLayout.CENTER);

        // Edit Panel
        JPanel editPanel = new JPanel(new GridBagLayout());
        editPanel.setBackground(new Color(220, 237, 253)); // Light blue
        TitledBorder editBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(25, 118, 210), 2),
                "Edit Configuration");
        editBorder.setTitleColor(new Color(25, 118, 210));
        editBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        editPanel.setBorder(editBorder);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Minimum Income
        gbc.gridx = 0;
        gbc.gridy = 0;
        editPanel.add(new JLabel("Minimum Income Threshold ($):"), gbc);
        gbc.gridx = 1;
        minIncomeField = new JTextField(15);
        editPanel.add(minIncomeField, gbc);

        // Max Debt-to-Income Ratio
        gbc.gridx = 0;
        gbc.gridy = 1;
        editPanel.add(new JLabel("Max Debt-to-Income Ratio (0.0-1.0):"), gbc);
        gbc.gridx = 1;
        maxDebtRatioField = new JTextField(15);
        editPanel.add(maxDebtRatioField, gbc);

        // Minimum Credit Score
        gbc.gridx = 0;
        gbc.gridy = 2;
        editPanel.add(new JLabel("Minimum Credit Score (300-850):"), gbc);
        gbc.gridx = 1;
        minCreditScoreField = new JTextField(15);
        editPanel.add(minCreditScoreField, gbc);

        // Minimum Employment Months
        gbc.gridx = 0;
        gbc.gridy = 3;
        editPanel.add(new JLabel("Minimum Employment Months:"), gbc);
        gbc.gridx = 1;
        minEmploymentField = new JTextField(15);
        editPanel.add(minEmploymentField, gbc);

        // Max Loan-to-Income Ratio
        gbc.gridx = 0;
        gbc.gridy = 4;
        editPanel.add(new JLabel("Max Loan-to-Income Ratio:"), gbc);
        gbc.gridx = 1;
        maxLoanRatioField = new JTextField(15);
        editPanel.add(maxLoanRatioField, gbc);

        // Risk Thresholds
        gbc.gridx = 0;
        gbc.gridy = 5;
        editPanel.add(new JLabel("Low Risk Threshold (≥):"), gbc);
        gbc.gridx = 1;
        lowRiskThresholdField = new JTextField(15);
        editPanel.add(lowRiskThresholdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        editPanel.add(new JLabel("Medium Risk Threshold (≥):"), gbc);
        gbc.gridx = 1;
        mediumRiskThresholdField = new JTextField(15);
        editPanel.add(mediumRiskThresholdField, gbc);

        // Buttons
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JPanel buttonPanel = new JPanel(new FlowLayout());

        saveButton = new JButton("Save Changes");
        saveButton.setBackground(new Color(46, 125, 50)); // Green
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveConfig());
        resetButton = new JButton("Reset to Defaults");
        resetButton.setBackground(new Color(211, 47, 47)); // Red
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.addActionListener(e -> resetConfig());
        JButton refreshButton = new JButton("Refresh Display");
        refreshButton.setBackground(new Color(70, 130, 180)); // Steel blue
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> loadCurrentConfig());

        JButton graphButton = new JButton("View Flow Graph");
        graphButton.setBackground(new Color(0, 151, 167)); // Cyan
        graphButton.setForeground(Color.WHITE);
        graphButton.setFocusPainted(false);
        graphButton.setToolTipText("View eligibility rules as directed graph (BFS)");
        graphButton.addActionListener(e -> showRulesFlowGraph());

        buttonPanel.add(saveButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(graphButton);
        editPanel.add(buttonPanel, gbc);

        // Info Panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(243, 229, 245)); // Light purple
        TitledBorder infoBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(123, 31, 162), 2),
                "Decision Tree Rules");
        infoBorder.setTitleColor(new Color(123, 31, 162));
        infoBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.setBorder(infoBorder);
        JTextArea infoArea = new JTextArea(
                "1. Income Check: Monthly income ≥ minimum threshold\n" +
                        "2. Debt Ratio: Debt-to-income ratio ≤ maximum limit\n" +
                        "3. Credit Score: Credit score ≥ minimum requirement\n" +
                        "4. Employment: Employment duration ≥ minimum months\n" +
                        "5. Loan Amount: Loan-to-income ratio ≤ maximum limit\n\n" +
                        "If ALL rules pass → ELIGIBLE\n" +
                        "If ANY rule fails → NOT ELIGIBLE");
        infoArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        infoArea.setEditable(false);
        infoArea.setBackground(Color.WHITE);
        infoArea.setForeground(new Color(30, 30, 30));
        infoPanel.add(new JScrollPane(infoArea), BorderLayout.CENTER);

        // Layout
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(displayPanel, BorderLayout.NORTH);
        leftPanel.add(infoPanel, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(editPanel, BorderLayout.NORTH);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.EAST);
    }

    private void loadCurrentConfig() {
        RulesConfig config = RulesConfig.getInstance();

        minIncomeField.setText(String.valueOf(config.getMinIncomeThreshold()));
        maxDebtRatioField.setText(String.valueOf(config.getMaxDebtToIncomeRatio()));
        minCreditScoreField.setText(String.valueOf(config.getMinCreditScore()));
        minEmploymentField.setText(String.valueOf(config.getMinEmploymentMonths()));
        maxLoanRatioField.setText(String.valueOf(config.getMaxLoanToIncomeRatio()));

        int[] thresholds = config.getRiskThresholds();
        lowRiskThresholdField.setText(String.valueOf(thresholds[0]));
        mediumRiskThresholdField.setText(String.valueOf(thresholds[1]));

        // Update display
        rulesDisplayArea.setText(
                "Current Rules Configuration\n" +
                        "============================\n" +
                        String.format("Minimum Income: $%.2f\n", config.getMinIncomeThreshold()) +
                        String.format("Max Debt-to-Income Ratio: %.1f%%\n", config.getMaxDebtToIncomeRatio() * 100) +
                        String.format("Minimum Credit Score: %d\n", config.getMinCreditScore()) +
                        String.format("Minimum Employment: %d months\n", config.getMinEmploymentMonths()) +
                        String.format("Max Loan-to-Income Ratio: %.1f\n", config.getMaxLoanToIncomeRatio()) +
                        String.format("Risk Thresholds: Low≥%d, Medium≥%d, High<%d\n",
                                thresholds[0], thresholds[1], thresholds[1]));
    }

    private void saveConfig() {
        try {
            RulesConfig config = RulesConfig.getInstance();

            config.setMinIncomeThreshold(Double.parseDouble(minIncomeField.getText()));
            config.setMaxDebtToIncomeRatio(Double.parseDouble(maxDebtRatioField.getText()));
            config.setMinCreditScore(Integer.parseInt(minCreditScoreField.getText()));
            config.setMinEmploymentMonths(Integer.parseInt(minEmploymentField.getText()));
            config.setMaxLoanToIncomeRatio(Double.parseDouble(maxLoanRatioField.getText()));

            int lowThreshold = Integer.parseInt(lowRiskThresholdField.getText());
            int mediumThreshold = Integer.parseInt(mediumRiskThresholdField.getText());
            config.setRiskThresholds(new int[] { lowThreshold, mediumThreshold });

            loadCurrentConfig();

            JOptionPane.showMessageDialog(this,
                    "Configuration saved successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Please enter valid numeric values for all fields.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error saving configuration: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showRulesFlowGraph() {
        RulesFlowGraph graph = new RulesFlowGraph();
        String bfsResult = graph.bfsTraversal();
        String flowPaths = graph.getFlowPaths();
        String message = flowPaths + "\n" + bfsResult + "\n(Graph: " + graph.getNodeCount() + " nodes, "
                + graph.getEdgeCount() + " edges)";
        JOptionPane.showMessageDialog(this, message, "Rules Flow Graph", JOptionPane.INFORMATION_MESSAGE);
    }

    private void resetConfig() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset all rules to default values?",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            RulesConfig config = RulesConfig.getInstance();
            config.resetToDefaults();
            loadCurrentConfig();

            JOptionPane.showMessageDialog(this,
                    "Configuration reset to defaults!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
