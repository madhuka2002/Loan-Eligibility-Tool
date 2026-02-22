package com.loantool.ui;

import com.loantool.algorithms.EligibilityEngine;
import com.loantool.algorithms.HeapSorter;
import com.loantool.algorithms.MergeSorter;
import com.loantool.algorithms.QuickSorter;
import com.loantool.models.Applicant;
import com.loantool.utils.FileHandler;
import com.loantool.utils.TestDataGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class SortingComparisonPanel extends JPanel {
    private JSpinner datasetSizeSpinner;
    private JButton compareButton;
    private JTextArea resultsArea;
    private JTable topApplicantsTable;

    public SortingComparisonPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(new Color(245, 248, 250));
        createPanel();
    }

    private void createPanel() {
        // Control Panel
        JPanel controlPanel = new JPanel(new GridBagLayout());
        controlPanel.setBackground(new Color(220, 237, 253)); // Light blue
        TitledBorder controlBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0, 151, 167), 2),
                "Comparison Parameters");
        controlBorder.setTitleColor(new Color(0, 151, 167));
        controlBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        controlPanel.setBorder(controlBorder);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        controlPanel.add(new JLabel("Dataset Size:"), gbc);
        gbc.gridx = 1;
        datasetSizeSpinner = new JSpinner(new SpinnerNumberModel(1000, 100, 100000, 100));
        controlPanel.add(datasetSizeSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        compareButton = new JButton("Compare Sorting Algorithms");
        compareButton.setFont(new Font("Arial", Font.BOLD, 14));
        compareButton.setBackground(new Color(0, 151, 167)); // Cyan
        compareButton.setForeground(Color.WHITE);
        compareButton.setFocusPainted(false);
        compareButton.addActionListener(e -> compareAlgorithms());
        controlPanel.add(compareButton, gbc);

        // Results Area
        JPanel resultsPanel = new JPanel(new BorderLayout());
        resultsPanel.setBackground(new Color(255, 245, 238)); // Light peach
        TitledBorder resultsBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(237, 108, 2), 2),
                "Performance Comparison");
        resultsBorder.setTitleColor(new Color(237, 108, 2));
        resultsBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        resultsPanel.setBorder(resultsBorder);

        resultsArea = new JTextArea(12, 50);
        resultsArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        resultsArea.setEditable(false);
        resultsArea.setBackground(Color.WHITE);
        resultsArea.setForeground(new Color(30, 30, 30));
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        resultsPanel.add(scrollPane, BorderLayout.CENTER);

        // Top Applicants Table
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(new Color(243, 229, 245)); // Light purple
        TitledBorder tableBorder = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(123, 31, 162), 2),
                "Top 10 Applicants (Highest Risk Scores)");
        tableBorder.setTitleColor(new Color(123, 31, 162));
        tableBorder.setTitleFont(new Font("Arial", Font.BOLD, 14));
        tablePanel.setBorder(tableBorder);

        String[] columnNames = { "Rank", "Applicant ID", "Risk Score", "Income", "Credit Score" };
        javax.swing.table.DefaultTableModel tableModel = new javax.swing.table.DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        topApplicantsTable = new JTable(tableModel);
        JScrollPane tableScroll = new JScrollPane(topApplicantsTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        // Layout
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(controlPanel, BorderLayout.NORTH);
        leftPanel.add(resultsPanel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(tablePanel, BorderLayout.CENTER);
    }

    private void compareAlgorithms() {
        compareButton.setEnabled(false);
        resultsArea.setText("Running comparison...\n");

        SwingWorker<Void, String> worker = new SwingWorker<Void, String>() {
            private List<Applicant> sortedApplicants;

            @Override
            protected Void doInBackground() throws Exception {
                int datasetSize = (Integer) datasetSizeSpinner.getValue();

                String dataFile;
                if (datasetSize <= 100) {
                    dataFile = "data/input/applicants.csv";
                } else {
                    dataFile = "data/input/comparison_data.csv";
                    publish("Generating " + datasetSize + " records for comparison...\n");
                    TestDataGenerator.generateCSV(dataFile, datasetSize);
                }

                publish("Loading applicants...\n");
                List<Applicant> applicants = FileHandler.loadApplicantsFromCSV(dataFile);
                List<Applicant> eligibleApplicants = new EligibilityEngine().evaluateEligibility(applicants);

                publish("Calculating risk scores...\n");
                for (Applicant applicant : eligibleApplicants) {
                    applicant.calculateRiskScore();
                }

                publish("\n════════════════════════════════════════════════════════\n");
                publish("PERFORMANCE TESTING ON " + eligibleApplicants.size() + " ELIGIBLE APPLICANTS\n");
                publish("════════════════════════════════════════════════════════\n\n");

                // Test Merge Sort
                publish("1. MERGE SORT TEST\n");
                publish("   - Time Complexity: O(n log n)\n");
                publish("   - Space Complexity: O(n)\n");
                publish("   - Characteristics: Stable, predictable performance\n\n");

                MergeSorter mergeSorter = new MergeSorter();
                long mergeStartTime = System.nanoTime();
                List<Applicant> mergeSorted = mergeSorter.sort(new ArrayList<>(eligibleApplicants));
                long mergeTime = System.nanoTime() - mergeStartTime;

                // Test Quick Sort
                publish("2. QUICK SORT TEST\n");
                publish("   - Time Complexity: O(n log n) average, O(n²) worst\n");
                publish("   - Space Complexity: O(log n)\n");
                publish("   - Characteristics: In-place, cache-friendly\n\n");

                QuickSorter quickSorter = new QuickSorter();
                long quickStartTime = System.nanoTime();
                List<Applicant> quickSorted = quickSorter.sort(new ArrayList<>(eligibleApplicants));
                long quickTime = System.nanoTime() - quickStartTime;

                // Test Heap Sort
                publish("3. HEAP SORT TEST\n");
                publish("   - Time Complexity: O(n log n)\n");
                publish("   - Space Complexity: O(n) - uses Max Heap\n");
                publish("   - Characteristics: In-place heap operations, Priority-queue based\n\n");

                HeapSorter heapSorter = new HeapSorter();
                long heapStartTime = System.nanoTime();
                List<Applicant> heapSorted = heapSorter.sort(new ArrayList<>(eligibleApplicants));
                long heapTime = System.nanoTime() - heapStartTime;

                publish("════════════════════════════════════════════════════════\n");
                publish("PERFORMANCE RESULTS\n");
                publish("════════════════════════════════════════════════════════\n");
                publish(String.format("Merge Sort Execution Time:  %,.3f ms\n", mergeTime / 1_000_000.0));
                publish(String.format("Quick Sort Execution Time:  %,.3f ms\n", quickTime / 1_000_000.0));
                publish(String.format("Heap Sort Execution Time:   %,.3f ms\n", heapTime / 1_000_000.0));

                double diffQuick = (quickTime - mergeTime) / 1_000_000.0;
                double pctQuick = mergeTime > 0 ? ((double) (quickTime - mergeTime) / mergeTime) * 100 : 0;
                publish(String.format("Quick vs Merge: %,.3f ms (%+.1f%%)\n", diffQuick, pctQuick));

                double diffHeap = (heapTime - mergeTime) / 1_000_000.0;
                double pctHeap = mergeTime > 0 ? ((double) (heapTime - mergeTime) / mergeTime) * 100 : 0;
                publish(String.format("Heap vs Merge:  %,.3f ms (%+.1f%%)\n\n", diffHeap, pctHeap));

                // Verify correctness (all three match)
                boolean identical = true;
                for (int i = 0; i < mergeSorted.size(); i++) {
                    if (mergeSorted.get(i).getRiskScore() != quickSorted.get(i).getRiskScore()
                            || mergeSorted.get(i).getRiskScore() != heapSorted.get(i).getRiskScore()) {
                        identical = false;
                        break;
                    }
                }

                publish("✅ Sorting results (Merge/Quick/Heap) are " + (identical ? "IDENTICAL" : "DIFFERENT") + "\n");
                publish("\n( Top 10 below extracted via Max Heap - extractMax K times )\n");

                // Top 10 via Heap (demonstrates heap for priority extraction)
                sortedApplicants = heapSorter.getTopK(new ArrayList<>(eligibleApplicants), 10);
                if (sortedApplicants.size() < 10) {
                    sortedApplicants = heapSorted; // fallback to full sorted list
                }

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
                compareButton.setEnabled(true);
                try {
                    get();

                    if (sortedApplicants != null) {
                        displayTopApplicants(sortedApplicants);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(SortingComparisonPanel.this,
                            "Error comparing algorithms: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        worker.execute();
    }

    private void displayTopApplicants(List<Applicant> applicants) {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) topApplicantsTable.getModel();
        model.setRowCount(0);

        int count = Math.min(10, applicants.size());
        for (int i = 0; i < count; i++) {
            Applicant app = applicants.get(i);
            model.addRow(new Object[] {
                    i + 1,
                    app.getId(),
                    String.format("%.1f", app.getRiskScore()),
                    String.format("$%,.0f", app.getMonthlyIncome()),
                    app.getCreditScore()
            });
        }
    }
}
