package com.loantool.ui;

import com.loantool.algorithms.EligibilityEngine;
import com.loantool.config.RulesConfig;
import com.loantool.models.Applicant;
import com.loantool.utils.FileHandler;
import com.loantool.utils.TestDataGenerator;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FairnessAnalysisPanel extends JPanel {
    private JSpinner datasetSizeSpinner;
    private JRadioButton existingDataRadio;
    private JRadioButton newDataRadio;
    private JButton analyzeButton;
    private JTextArea resultsArea;
    private JPanel chartPanel;

    public FairnessAnalysisPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 248, 250));
        createPanel();
    }

    private void createPanel() {
        // Control Panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(new Color(243, 229, 245)); // Light purple
        TitledBorder controlBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(123, 31, 162), 2),
                "Analysis Parameters");
        controlBorder.setTitleColor(new Color(123, 31, 162));
        controlBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        controlPanel.setBorder(controlBorder);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0; gbc.gridy = 0;
        controlPanel.add(new JLabel("Dataset Size:"), gbc);
        gbc.gridx = 1;
        datasetSizeSpinner = new JSpinner(new SpinnerNumberModel(50, 10, 10000, 10));
        controlPanel.add(datasetSizeSpinner, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        existingDataRadio = new JRadioButton("Use Existing Dataset", true);
        newDataRadio = new JRadioButton("Generate New Dataset");
        ButtonGroup dataGroup = new ButtonGroup();
        dataGroup.add(existingDataRadio);
        dataGroup.add(newDataRadio);
        controlPanel.add(existingDataRadio, gbc);
        gbc.gridx = 1;
        controlPanel.add(newDataRadio, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        analyzeButton = new JButton("Run Fairness Analysis");
        analyzeButton.setFont(new Font("Arial", Font.BOLD, 14));
        analyzeButton.setBackground(new Color(123, 31, 162)); // Purple
        analyzeButton.setForeground(Color.WHITE);
        analyzeButton.setFocusPainted(false);
        analyzeButton.addActionListener(e -> runAnalysis());
        controlPanel.add(analyzeButton, gbc);

        // Results Area
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(new Color(255, 245, 238)); // Light peach
        TitledBorder resultsBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(237, 108, 2), 2),
                "Analysis Results");
        resultsBorder.setTitleColor(new Color(237, 108, 2));
        resultsBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        resultsPanel.setBorder(resultsBorder);

        resultsArea = new JTextArea(10, 50);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        resultsArea.setEditable(false);
        resultsArea.setBackground(Color.WHITE);
        resultsArea.setForeground(new Color(30, 30, 30));
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // Chart Panel
        chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBackground(new Color(220, 237, 253)); // Light blue
        TitledBorder chartBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(25, 118, 210), 2),
                "Visualization");
        chartBorder.setTitleColor(new Color(25, 118, 210));
        chartBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        chartPanel.setBorder(chartBorder);
        chartPanel.setPreferredSize(new Dimension(600, 400));
        JLabel chartLabel = new JLabel("Run analysis to see visualization", JLabel.CENTER);
        chartLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        chartLabel.setForeground(new Color(100, 100, 100));
        chartPanel.add(chartLabel, BorderLayout.CENTER);

        // Layout
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(controlPanel, BorderLayout.NORTH);
        leftPanel.add(resultsPanel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(chartPanel, BorderLayout.CENTER);
    }

    private void runAnalysis() {
        analyzeButton.setEnabled(false);
        resultsArea.setText("Running analysis...\n");

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            @Override
            protected Void doInBackground() throws Exception {
                int datasetSize = (Integer) datasetSizeSpinner.getValue();

                List<Applicant> applicants;

                if (newDataRadio.isSelected()) {
                    publish("Generating " + datasetSize + " sample applicants...");
                    TestDataGenerator.generateCSV("data/input/analysis_data.csv", datasetSize);
                    applicants = FileHandler.loadApplicantsFromCSV("data/input/analysis_data.csv");
                } else {
                    if (datasetSize <= 100) {
                        applicants = FileHandler.loadApplicantsFromCSV("data/input/applicants.csv");
                    } else {
                        TestDataGenerator.generateCSV("data/input/large_dataset.csv", datasetSize);
                        applicants = FileHandler.loadApplicantsFromCSV("data/input/large_dataset.csv");
                    }
                }

                publish("Loaded " + applicants.size() + " applicants\n");
                publish("Analyzing income threshold impact...\n");
                publish("════════════════════════════════════════════════════════\n");
                publish(" Threshold | Approval Rate | High Risk % | Impact\n");
                publish("════════════════════════════════════════════════════════\n");

                RulesConfig config = RulesConfig.getInstance();
                double originalThreshold = config.getMinIncomeThreshold();

                List<Double> thresholds = new ArrayList<>();
                List<Double> approvalRates = new ArrayList<>();
                List<Double> highRiskRates = new ArrayList<>();

                double[] testThresholds = {1500, 1800, 2000, 2200, 2500, 3000};

                for (double threshold : testThresholds) {
                    config.setMinIncomeThreshold(threshold);
                    EligibilityEngine engine = new EligibilityEngine();
                    int eligibleCount = 0;
                    int highRiskCount = 0;
                    int approvedCount = 0;

                    for (Applicant applicant : applicants) {
                        if (engine.isEligible(applicant)) {
                            eligibleCount++;
                            applicant.calculateRiskScore();
                            if (applicant.getRiskScore() < 50) {
                                highRiskCount++;
                            }
                            if (applicant.getRiskScore() >= 50) {
                                approvedCount++;
                            }
                        }
                    }

                    double approvalRate = (eligibleCount * 100.0) / applicants.size();
                    double highRiskRate = eligibleCount > 0 ? (highRiskCount * 100.0) / eligibleCount : 0;

                    thresholds.add(threshold);
                    approvalRates.add(approvalRate);
                    highRiskRates.add(highRiskRate);

                    String impact;
                    if (approvalRate > 80 && highRiskRate < 10) {
                        impact = "✅ Excellent";
                    } else if (approvalRate > 60 && highRiskRate < 20) {
                        impact = "👍 Good";
                    } else if (approvalRate > 40 && highRiskRate < 30) {
                        impact = "⚠️  Moderate";
                    } else {
                        impact = "❌ High Risk";
                    }

                    publish(String.format(" $%-9.0f| %-13.1f%%| %-12.1f%%| %s\n",
                            threshold, approvalRate, highRiskRate, impact));
                }

                config.setMinIncomeThreshold(originalThreshold);

                publish("\n════════════════════════════════════════════════════════\n");
                publish("💡 Recommendation:\n");
                publish("A threshold of $2000-$2200 provides a good balance for this dataset.\n");

                // Create chart
                SwingUtilities.invokeLater(() -> createChart(thresholds, approvalRates, highRiskRates));

                return null;
            }

            @Override
            protected void process(List<String> chunks) {
                for (String message : chunks) {
                    resultsArea.append(message);
                }
                resultsArea.setCaretPosition(resultsArea.getDocument().getLength());
            }

            @Override
            protected void done() {
                analyzeButton.setEnabled(true);
                try {
                    get();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(FairnessAnalysisPanel.this,
                            "Error running analysis: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void createChart(List<Double> thresholds, List<Double> approvalRates, List<Double> highRiskRates) {
        chartPanel.removeAll();

        XYChart chart = new XYChartBuilder()
                .width(600)
                .height(400)
                .title("Fairness vs Risk Analysis")
                .xAxisTitle("Income Threshold ($)")
                .yAxisTitle("Percentage (%)")
                .theme(Styler.ChartTheme.Matlab)
                .build();

        chart.addSeries("Approval Rate", thresholds, approvalRates);
        chart.addSeries("High Risk Rate", thresholds, highRiskRates);

        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setXAxisDecimalPattern("#");
        chart.getStyler().setYAxisDecimalPattern("#.##");

        JPanel chartWrapper = new XChartPanel<>(chart);
        chartPanel.add(chartWrapper, BorderLayout.CENTER);
        chartPanel.revalidate();
        chartPanel.repaint();
    }
}