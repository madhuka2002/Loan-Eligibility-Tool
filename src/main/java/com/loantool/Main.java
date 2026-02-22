package com.loantool;

import com.loantool.models.Applicant;
import com.loantool.models.LoanDecision;
import com.loantool.algorithms.EligibilityEngine;
import com.loantool.algorithms.MergeSorter;
import com.loantool.algorithms.QuickSorter;
import com.loantool.algorithms.RiskClassifier;
import com.loantool.models.RiskTier;
import com.loantool.utils.FileHandler;
import com.loantool.utils.ProcessedDataContext;
import com.loantool.utils.ReportGenerator;
import com.loantool.utils.TestDataGenerator;
import com.loantool.algorithms.HeapSorter;
import com.loantool.config.RulesConfig;
import com.loantool.ui.LoanEligibilityGUI;

import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Check if CLI mode is explicitly requested
        if (args.length > 0 && (args[0].equals("--cli") || args[0].equals("-c"))) {
            runCLI();
            return;
        }

        // Default to GUI mode
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                new LoanEligibilityGUI().setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error starting GUI: " + e.getMessage());
                System.err.println("Falling back to CLI mode...\n");
                runCLI();
            }
        });
    }

    private static void runCLI() {
        System.out.println("========================================================");
        System.out.println("🏦 LOAN ELIGIBILITY & RISK TIER ENGINE v1.0");
        System.out.println("========================================================");
        System.out.println("A Rule-Based Credit Decision Tool");
        System.out.println("Developed for Academic Project\n");
        System.out.println("💡 Tip: Run without --cli flag to launch GUI interface\n");

        Scanner scanner = new Scanner(System.in);
        runCLILoop(scanner);
    }

    private static void runCLILoop(Scanner scanner) {
        while (true) {
            displayMainMenu();

            int choice = getMenuChoice(scanner);

            switch (choice) {
                case 1:
                    processCSVFile(scanner);
                    break;
                case 2:
                    processManualInput(scanner);
                    break;
                case 3:
                    runFairnessAnalysis(scanner);
                    break;
                case 4:
                    displayRulesConfiguration();
                    break;
                case 5:
                    generateReport();
                    break;
                case 6:
                    compareSortingAlgorithms();
                    break;
                case 7:
                    updateRulesConfiguration(scanner);
                    break;
                case 8:
                    generateSampleData(scanner);
                    break;
                case 9:
                    System.out.println("\n👋 Thank you for using Loan Eligibility Tool!");
                    System.out.println("Exiting... Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("❌ Invalid option! Please try again.");
            }

            System.out.println("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("                         MAIN MENU");
        System.out.println("════════════════════════════════════════════════════════");
        System.out.println("1. 📊 Process CSV File");
        System.out.println("2. 📝 Process Single Applicant (Manual Input)");
        System.out.println("3. 🔍 Run Fairness Analysis");
        System.out.println("4. ⚙️  View Rules Configuration");
        System.out.println("5. 📄 Generate Detailed Report");
        System.out.println("6. ⚡ Compare Sorting Algorithms");
        System.out.println("7. 🔧 Update Rules Configuration");
        System.out.println("8. 📋 Generate Sample Data");
        System.out.println("9. 🚪 Exit");
        System.out.println("════════════════════════════════════════════════════════");
        System.out.print("\nSelect option (1-9): ");
    }

    private static int getMenuChoice(Scanner scanner) {
        try {
            return scanner.nextInt();
        } catch (Exception e) {
            scanner.nextLine(); // Clear invalid input
            return -1;
        } finally {
            scanner.nextLine(); // Always consume newline
        }
    }

    private static void processCSVFile(Scanner scanner) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("                     PROCESS CSV FILE");
        System.out.println("════════════════════════════════════════════════════════");

        System.out.print("Enter CSV file path [Press Enter for default: data/input/applicants.csv]: ");
        String filePath = scanner.nextLine().trim();

        if (filePath.isEmpty()) {
            filePath = "data/input/applicants.csv";
        }

        try {
            System.out.println("\n📂 Loading applicants from: " + filePath);
            List<Applicant> applicants = FileHandler.loadApplicantsFromCSV(filePath);

            if (applicants.isEmpty()) {
                System.out.println("❌ No valid applicants found in the file.");
                return;
            }

            System.out.println("✅ Successfully loaded " + applicants.size() + " applicants");

            // Step 1: Eligibility Check using Decision Tree
            System.out.println("\n════════════════════════════════════════════════════════");
            System.out.println("                 ELIGIBILITY EVALUATION");
            System.out.println("════════════════════════════════════════════════════════");
            EligibilityEngine engine = new EligibilityEngine();
            List<Applicant> eligibleApplicants = engine.evaluateEligibility(applicants);
            System.out.println("📊 Results:");
            System.out.println("  • Total applicants: " + applicants.size());
            System.out.println("  • Eligible applicants: " + eligibleApplicants.size());
            System.out.println("  • Rejected applicants: " + (applicants.size() - eligibleApplicants.size()));

            if (eligibleApplicants.isEmpty()) {
                System.out.println("⚠️  No eligible applicants found!");
                return;
            }

            // Step 2: Calculate Risk Scores
            System.out.println("\n════════════════════════════════════════════════════════");
            System.out.println("                 RISK SCORE CALCULATION");
            System.out.println("════════════════════════════════════════════════════════");
            System.out.println("Calculating risk scores for eligible applicants...");
            for (Applicant applicant : eligibleApplicants) {
                applicant.calculateRiskScore();
            }
            System.out.println("✅ Risk scores calculated successfully");

            // Step 3: Sort by Risk Score (Higher = Better)
            System.out.println("\n════════════════════════════════════════════════════════");
            System.out.println("                 APPLICANT RANKING");
            System.out.println("════════════════════════════════════════════════════════");
            System.out.print("Choose sorting algorithm [1-Merge Sort, 2-Quick Sort, 3-Heap Sort]: ");
            String sortChoice = scanner.nextLine().trim();

            List<Applicant> sortedApplicants;
            long startTime = System.currentTimeMillis();

            if (sortChoice.equals("2")) {
                System.out.println("Using Quick Sort algorithm...");
                QuickSorter quickSorter = new QuickSorter();
                sortedApplicants = quickSorter.sort(eligibleApplicants);
            } else if (sortChoice.equals("3")) {
                System.out.println("Using Heap Sort algorithm...");
                HeapSorter heapSorter = new HeapSorter();
                sortedApplicants = heapSorter.sort(eligibleApplicants);
            } else {
                System.out.println("Using Merge Sort algorithm...");
                MergeSorter mergeSorter = new MergeSorter();
                sortedApplicants = mergeSorter.sort(eligibleApplicants);
            }

            long sortTime = System.currentTimeMillis() - startTime;
            System.out.println("✅ Applicants sorted by risk score");
            System.out.println("⏱️  Sorting completed in " + sortTime + " ms");

            // Step 4: Classify Risk Tiers using Binary Search
            System.out.println("\n════════════════════════════════════════════════════════");
            System.out.println("                 RISK TIER CLASSIFICATION");
            System.out.println("════════════════════════════════════════════════════════");
            RiskClassifier classifier = new RiskClassifier();
            List<LoanDecision> decisions = classifier.classify(sortedApplicants);
            System.out.println("✅ Risk classification completed");

            // Step 5: Save Results
            System.out.println("\n════════════════════════════════════════════════════════");
            System.out.println("                  SAVING RESULTS");
            System.out.println("════════════════════════════════════════════════════════");

            // Save as JSON
            FileHandler.saveDecisionsToJSON(decisions, "data/output/results.json");
            System.out.println("💾 Results saved to: data/output/results.json");

            // Save as CSV
            FileHandler.saveDecisionsToCSV(decisions, "data/output/results.csv");
            System.out.println("💾 Results saved to: data/output/results.csv");

            // Build BST and Linked List (data structures)
            ProcessedDataContext.getInstance().setProcessedData(sortedApplicants, decisions);
            System.out.println("📊 Data structures built: BST ("
                    + ProcessedDataContext.getInstance().getApplicantBST().size() + " nodes), Linked List ("
                    + ProcessedDataContext.getInstance().getDecisionLinkedList().size() + " decisions)");

            // Display summary
            displayDetailedSummary(decisions);

        } catch (Exception e) {
            System.err.println("\n❌ ERROR: " + e.getMessage());
            e.printStackTrace();
            System.out.println("\n🔧 Troubleshooting:");
            System.out.println("1. Check if the file exists at: " + filePath);
            System.out.println("2. Ensure the CSV format is correct");
            System.out.println("3. Create the directory: mkdir -p data/input");
        }
    }

    private static void processManualInput(Scanner scanner) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("                 MANUAL APPLICANT ENTRY");
        System.out.println("════════════════════════════════════════════════════════");

        System.out.println("Enter applicant details:");
        System.out.println("────────────────────────────────────────────────────────");

        System.out.print("Applicant ID (format: APP001): ");
        String id = scanner.nextLine().trim();

        double income = getValidDoubleInput(scanner, "Monthly Income ($): ", 0, 1000000);
        double debt = getValidDoubleInput(scanner, "Existing Debt ($): ", 0, 500000);
        int creditScore = getValidIntInput(scanner, "Credit Score (300-850): ", 300, 850);
        int employmentMonths = getValidIntInput(scanner, "Employment Duration (months): ", 0, 600);
        double loanAmount = getValidDoubleInput(scanner, "Loan Amount Requested ($): ", 100, 1000000);

        Applicant applicant = new Applicant(id, income, debt, creditScore, employmentMonths, loanAmount);

        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("                 EVALUATION RESULTS");
        System.out.println("════════════════════════════════════════════════════════");

        EligibilityEngine engine = new EligibilityEngine();
        RiskClassifier classifier = new RiskClassifier();

        if (engine.isEligible(applicant)) {
            applicant.calculateRiskScore();
            LoanDecision decision = classifier.classifySingle(applicant);

            System.out.println("✅ ELIGIBLE FOR LOAN");
            System.out.println("────────────────────────────────────────────────────────");
            System.out.printf("Applicant ID:      %s\n", applicant.getId());
            System.out.printf("Risk Score:        %.1f/100\n", applicant.getRiskScore());
            System.out.printf("Risk Tier:         %s\n", decision.getRiskTier().getDisplayName());
            System.out.printf("Interest Rate:     %.1f%%\n", decision.getRiskTier().getBaseInterestRate() * 100);
            System.out.printf("Requested Amount:  $%,.2f\n", applicant.getLoanAmountRequested());
            System.out.printf("Recommended Limit: $%,.2f\n", decision.getRecommendedLimit());
            System.out.printf("Decision:          %s\n", decision.getDecisionReason());

            if (decision.isApproved()) {
                System.out.println("\n🎉 Congratulations! Loan application is APPROVED!");
            } else {
                System.out.println("\n⚠️  Application requires further review.");
            }
        } else {
            System.out.println("❌ NOT ELIGIBLE FOR LOAN");
            System.out.println("────────────────────────────────────────────────────────");
            System.out.println("Rejection Reason: " + engine.getRejectionReason(applicant));
            System.out.println("\n💡 Suggestions:");
            System.out.println("1. Improve credit score");
            System.out.println("2. Reduce existing debt");
            System.out.println("3. Maintain employment for longer duration");
        }
    }

    private static void runFairnessAnalysis(Scanner scanner) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("                 FAIRNESS VS RISK ANALYSIS");
        System.out.println("════════════════════════════════════════════════════════");

        System.out.println("This analysis shows how changing thresholds affects:");
        System.out.println("  • Approval rates (Fairness)");
        System.out.println("  • High-risk loan percentages (Risk)\n");

        System.out.print("Enter dataset size for analysis [10, 50, 100, 1000] or press Enter for default (50): ");
        String sizeInput = scanner.nextLine().trim();
        int datasetSize = sizeInput.isEmpty() ? 50 : Integer.parseInt(sizeInput);

        System.out.print("Use existing dataset or generate new? [E=Existing, N=New, press Enter for Existing]: ");
        String choice = scanner.nextLine().trim().toUpperCase();

        List<Applicant> applicants;

        try {
            if (choice.equals("N")) {
                System.out.println("\n📊 Generating " + datasetSize + " sample applicants...");
                TestDataGenerator.generateCSV("data/input/analysis_data.csv", datasetSize);
                applicants = FileHandler.loadApplicantsFromCSV("data/input/analysis_data.csv");
            } else {
                if (datasetSize <= 100) {
                    applicants = FileHandler.loadApplicantsFromCSV("data/input/applicants.csv");
                } else {
                    // Generate larger dataset
                    TestDataGenerator.generateCSV("data/input/large_dataset.csv", datasetSize);
                    applicants = FileHandler.loadApplicantsFromCSV("data/input/large_dataset.csv");
                }
            }

            System.out.println("✅ Loaded " + applicants.size() + " applicants for analysis");

            RulesConfig config = RulesConfig.getInstance();
            double originalThreshold = config.getMinIncomeThreshold();

            System.out.println("\n📈 ANALYZING INCOME THRESHOLD IMPACT");
            System.out.println("════════════════════════════════════════════════════════");
            System.out.println("Current minimum income threshold: $" + originalThreshold);
            System.out.println("\nTesting different thresholds:");
            System.out.println("────────────────────────────────────────────────────────");
            System.out.println(" Threshold | Approval Rate | High Risk % | Impact");
            System.out.println("────────────────────────────────────────────────────────");

            double[] thresholds = { 1500, 1800, 2000, 2200, 2500, 3000 };

            for (double threshold : thresholds) {
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
                        // Consider approved if not high risk
                        if (applicant.getRiskScore() >= 50) {
                            approvedCount++;
                        }
                    }
                }

                double approvalRate = (eligibleCount * 100.0) / applicants.size();
                double highRiskRate = eligibleCount > 0 ? (highRiskCount * 100.0) / eligibleCount : 0;

                // Determine impact
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

                System.out.printf(" $%-9.0f| %-13.1f%%| %-12.1f%%| %s\n",
                        threshold, approvalRate, highRiskRate, impact);
            }

            // Reset to original
            config.setMinIncomeThreshold(originalThreshold);

            System.out.println("\n📊 ANALYSIS SUMMARY");
            System.out.println("════════════════════════════════════════════════════════");
            System.out.println("Lower thresholds increase accessibility but may increase risk.");
            System.out.println("Higher thresholds reduce risk but may exclude worthy applicants.");
            System.out.println("\n💡 Recommendation:");
            System.out.println("A threshold of $2000-$2200 provides a good balance for this dataset.");

        } catch (Exception e) {
            System.err.println("\n❌ Error in fairness analysis: " + e.getMessage());
        }
    }

    private static void displayRulesConfiguration() {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("             CURRENT RULES CONFIGURATION");
        System.out.println("════════════════════════════════════════════════════════");

        RulesConfig config = RulesConfig.getInstance();
        config.displayRules();

        // Show Rules Flow Graph (small graph usage)
        System.out.println("\n📊 RULES FLOW GRAPH (Directed Graph - BFS):");
        com.loantool.datastructures.RulesFlowGraph graph = new com.loantool.datastructures.RulesFlowGraph();
        System.out.println(graph.getFlowPaths());
        System.out.println(graph.bfsTraversal());

        System.out.println("\n🔍 ALGORITHMIC DECISION TREE RULES:");
        System.out.println("────────────────────────────────────────────────────────");
        System.out.println("1. Income Check: Monthly income ≥ minimum threshold");
        System.out.println("2. Debt Ratio: Debt-to-income ratio ≤ maximum limit");
        System.out.println("3. Credit Score: Credit score ≥ minimum requirement");
        System.out.println("4. Employment: Employment duration ≥ minimum months");
        System.out.println("5. Loan Amount: Loan-to-income ratio ≤ maximum limit");
        System.out.println("\nIf ALL rules pass → ELIGIBLE");
        System.out.println("If ANY rule fails → NOT ELIGIBLE");
    }

    private static void generateReport() {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("                 GENERATE DETAILED REPORT");
        System.out.println("════════════════════════════════════════════════════════");

        try {
            List<LoanDecision> decisions = FileHandler.loadDecisionsFromJSON("data/output/results.json");

            System.out.println("📊 Creating comprehensive report...");
            ReportGenerator.generateReport(decisions, "data/output/detailed_report.txt");
            System.out.println("✅ Report generated: data/output/detailed_report.txt");

            // Also generate executive summary
            generateExecutiveSummary(decisions);

        } catch (Exception e) {
            System.err.println("\n❌ Error generating report: " + e.getMessage());
            System.out.println("💡 Try processing a CSV file first to generate results.");
        }
    }

    private static void compareSortingAlgorithms() {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("         SORTING ALGORITHM PERFORMANCE COMPARISON");
        System.out.println("════════════════════════════════════════════════════════");

        try {
            System.out
                    .print("Enter dataset size for comparison [100, 1000, 10000] or press Enter for default (1000): ");
            Scanner tempScanner = new Scanner(System.in);
            String sizeInput = tempScanner.nextLine().trim();
            int datasetSize = sizeInput.isEmpty() ? 1000 : Integer.parseInt(sizeInput);

            // Generate or load dataset
            String dataFile;
            if (datasetSize <= 100) {
                dataFile = "data/input/applicants.csv";
            } else {
                dataFile = "data/input/comparison_data.csv";
                System.out.println("📊 Generating " + datasetSize + " records for comparison...");
                TestDataGenerator.generateCSV(dataFile, datasetSize);
            }

            List<Applicant> applicants = FileHandler.loadApplicantsFromCSV(dataFile);
            List<Applicant> eligibleApplicants = new EligibilityEngine().evaluateEligibility(applicants);

            for (Applicant applicant : eligibleApplicants) {
                applicant.calculateRiskScore();
            }

            System.out.println("\n📈 PERFORMANCE TESTING ON " + eligibleApplicants.size() + " ELIGIBLE APPLICANTS");
            System.out.println("────────────────────────────────────────────────────────");

            // Test Merge Sort
            System.out.println("\n1. MERGE SORT TEST");
            System.out.println("   - Time Complexity: O(n log n)");
            System.out.println("   - Space Complexity: O(n)");
            System.out.println("   - Characteristics: Stable, predictable performance");

            MergeSorter mergeSorter = new MergeSorter();
            long mergeStartTime = System.nanoTime();
            List<Applicant> mergeSorted = mergeSorter.sort(new ArrayList<>(eligibleApplicants));
            long mergeTime = System.nanoTime() - mergeStartTime;

            // Test Quick Sort
            System.out.println("\n2. QUICK SORT TEST");
            System.out.println("   - Time Complexity: O(n log n) average, O(n²) worst");
            System.out.println("   - Space Complexity: O(log n)");
            System.out.println("   - Characteristics: In-place, cache-friendly");

            QuickSorter quickSorter = new QuickSorter();
            long quickStartTime = System.nanoTime();
            List<Applicant> quickSorted = quickSorter.sort(new ArrayList<>(eligibleApplicants));
            long quickTime = System.nanoTime() - quickStartTime;

            // Test Heap Sort
            System.out.println("\n3. HEAP SORT TEST");
            System.out.println("   - Time Complexity: O(n log n)");
            System.out.println("   - Space Complexity: O(n) - uses Max Heap");
            HeapSorter heapSorter = new HeapSorter();
            long heapStartTime = System.nanoTime();
            List<Applicant> heapSorted = heapSorter.sort(new ArrayList<>(eligibleApplicants));
            long heapTime = System.nanoTime() - heapStartTime;

            System.out.println("\n════════════════════════════════════════════════════════");
            System.out.println("                 PERFORMANCE RESULTS");
            System.out.println("════════════════════════════════════════════════════════");
            System.out.printf("Merge Sort Execution Time:  %,.3f ms\n", mergeTime / 1_000_000.0);
            System.out.printf("Quick Sort Execution Time:  %,.3f ms\n", quickTime / 1_000_000.0);
            System.out.printf("Heap Sort Execution Time:   %,.3f ms\n", heapTime / 1_000_000.0);

            // Verify correctness (all three)
            boolean identical = true;
            for (int i = 0; i < mergeSorted.size(); i++) {
                if (mergeSorted.get(i).getRiskScore() != quickSorted.get(i).getRiskScore()
                        || mergeSorted.get(i).getRiskScore() != heapSorted.get(i).getRiskScore()) {
                    identical = false;
                    break;
                }
            }

            System.out.println("\n✅ Sorting results (Merge/Quick/Heap) are " + (identical ? "IDENTICAL" : "DIFFERENT"));

            // Show top results
            System.out.println("\n🏆 TOP 5 APPLICANTS (Highest Risk Scores):");
            System.out.println("────────────────────────────────────────────────────────");
            for (int i = 0; i < Math.min(5, mergeSorted.size()); i++) {
                Applicant app = mergeSorted.get(i);
                System.out.printf("%d. %s - Score: %.1f | Income: $%,.0f | Credit: %d\n",
                        i + 1, app.getId(), app.getRiskScore(),
                        app.getMonthlyIncome(), app.getCreditScore());
            }

            tempScanner.close();

        } catch (Exception e) {
            System.err.println("\n❌ Error in comparison: " + e.getMessage());
        }
    }

    private static void updateRulesConfiguration(Scanner scanner) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("             UPDATE RULES CONFIGURATION");
        System.out.println("════════════════════════════════════════════════════════");

        RulesConfig config = RulesConfig.getInstance();

        System.out.println("Current configuration:");
        config.displayRules();

        System.out.println("\nSelect parameter to update:");
        System.out.println("1. Minimum Income Threshold");
        System.out.println("2. Maximum Debt-to-Income Ratio");
        System.out.println("3. Minimum Credit Score");
        System.out.println("4. Minimum Employment Months");
        System.out.println("5. Maximum Loan-to-Income Ratio");
        System.out.println("6. Risk Score Thresholds");
        System.out.println("7. Reset to Defaults");
        System.out.println("8. Cancel");
        System.out.print("\nChoice: ");

        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("New minimum income threshold ($): ");
                    double income = scanner.nextDouble();
                    config.setMinIncomeThreshold(income);
                    System.out.println("✅ Minimum income threshold updated to $" + income);
                    break;
                case 2:
                    System.out.print("New maximum debt-to-income ratio (0.0-1.0): ");
                    double ratio = scanner.nextDouble();
                    config.setMaxDebtToIncomeRatio(ratio);
                    System.out.println("✅ Maximum debt-to-income ratio updated to " + (ratio * 100) + "%");
                    break;
                case 3:
                    System.out.print("New minimum credit score (300-850): ");
                    int creditScore = scanner.nextInt();
                    config.setMinCreditScore(creditScore);
                    System.out.println("✅ Minimum credit score updated to " + creditScore);
                    break;
                case 4:
                    System.out.print("New minimum employment months: ");
                    int months = scanner.nextInt();
                    config.setMinEmploymentMonths(months);
                    System.out.println("✅ Minimum employment months updated to " + months);
                    break;
                case 5:
                    System.out.print("New maximum loan-to-income ratio: ");
                    double loanRatio = scanner.nextDouble();
                    config.setMaxLoanToIncomeRatio(loanRatio);
                    System.out.println("✅ Maximum loan-to-income ratio updated to " + loanRatio);
                    break;
                case 6:
                    System.out.print("Low Risk threshold (≥): ");
                    int lowThreshold = scanner.nextInt();
                    System.out.print("Medium Risk threshold (≥): ");
                    int mediumThreshold = scanner.nextInt();
                    config.setRiskThresholds(new int[] { lowThreshold, mediumThreshold });
                    System.out.println("✅ Risk thresholds updated");
                    break;
                case 7:
                    config.resetToDefaults();
                    System.out.println("✅ Configuration reset to defaults");
                    break;
                case 8:
                    System.out.println("Update cancelled");
                    break;
                default:
                    System.out.println("❌ Invalid choice");
            }

        } catch (Exception e) {
            System.err.println("❌ Error updating configuration: " + e.getMessage());
            scanner.nextLine(); // Clear buffer
        }
    }

    private static void generateSampleData(Scanner scanner) {
        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("                 GENERATE SAMPLE DATA");
        System.out.println("════════════════════════════════════════════════════════");

        System.out.print("Enter number of sample records to generate (10-10000): ");
        try {
            int count = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (count < 10 || count > 10000) {
                System.out.println("❌ Please enter a value between 10 and 10000");
                return;
            }

            System.out.print("Enter filename [sample_data.csv]: ");
            String filename = scanner.nextLine().trim();
            if (filename.isEmpty()) {
                filename = "sample_data.csv";
            }

            String filePath = "data/input/" + filename;
            TestDataGenerator.generateCSV(filePath, count);

            System.out.println("\n✅ Generated " + count + " sample records");
            System.out.println("📁 Location: " + filePath);
            System.out.println("\n📋 Sample records include:");
            System.out.println("  • Realistic income levels ($1,500 - $10,000)");
            System.out.println("  • Varied credit scores (350-850)");
            System.out.println("  • Employment durations (0-60 months)");
            System.out.println("  • Diverse debt-to-income ratios");

        } catch (Exception e) {
            System.err.println("❌ Error generating sample data: " + e.getMessage());
            scanner.nextLine(); // Clear buffer
        }
    }

    private static void displayDetailedSummary(List<LoanDecision> decisions) {
        int lowRisk = 0, mediumRisk = 0, highRisk = 0;
        int approved = 0;
        double totalRequested = 0, totalRecommended = 0;

        for (LoanDecision decision : decisions) {
            // Count risk tiers using if-else (Java 11 compatible)
            RiskTier tier = decision.getRiskTier();
            if (tier == RiskTier.LOW_RISK) {
                lowRisk++;
            } else if (tier == RiskTier.MEDIUM_RISK) {
                mediumRisk++;
            } else { // HIGH_RISK
                highRisk++;
            }

            if (decision.isApproved()) {
                approved++;
            }

            totalRequested += decision.getApplicant().getLoanAmountRequested();
            totalRecommended += decision.getRecommendedLimit();
        }

        System.out.println("\n════════════════════════════════════════════════════════");
        System.out.println("                 DECISION SUMMARY");
        System.out.println("════════════════════════════════════════════════════════");
        System.out.printf("📊 Total Applicants Processed:  %d\n", decisions.size());
        System.out.printf("✅ Approved Applications:        %d (%.1f%%)\n",
                approved, (approved * 100.0) / decisions.size());
        System.out.printf("💰 Total Amount Requested:      $%,.2f\n", totalRequested);
        System.out.printf("💳 Total Recommended Limit:     $%,.2f\n", totalRecommended);

        System.out.println("\n📈 RISK DISTRIBUTION:");
        System.out.println("────────────────────────────────────────────────────────");
        System.out.printf("🔵 Low Risk:       %d (%.1f%%)\n",
                lowRisk, (lowRisk * 100.0) / decisions.size());
        System.out.printf("🟡 Medium Risk:    %d (%.1f%%)\n",
                mediumRisk, (mediumRisk * 100.0) / decisions.size());
        System.out.printf("🔴 High Risk:      %d (%.1f%%)\n",
                highRisk, (highRisk * 100.0) / decisions.size());

        System.out.println("\n💡 RECOMMENDATIONS:");
        System.out.println("────────────────────────────────────────────────────────");
        if (highRisk > decisions.size() * 0.3) {
            System.out.println("⚠️  High proportion of high-risk applicants.");
            System.out.println("    Consider tightening eligibility criteria.");
        }

        if (totalRecommended < totalRequested * 0.7) {
            System.out.println("⚠️  Recommended limits are significantly lower than requested.");
            System.out.println("    Applicants may be requesting too much relative to income.");
        }

        if (approved < decisions.size() * 0.5) {
            System.out.println("⚠️  Low approval rate.");
            System.out.println("    Consider reviewing fairness of eligibility rules.");
        }
    }

    private static void generateExecutiveSummary(List<LoanDecision> decisions) {
        try {
            StringBuilder summary = new StringBuilder();

            int lowRisk = 0, mediumRisk = 0, highRisk = 0;
            int approved = 0;
            double totalRequested = 0, totalRecommended = 0;

            for (LoanDecision decision : decisions) {
                RiskTier tier = decision.getRiskTier();
                if (tier == RiskTier.LOW_RISK) {
                    lowRisk++;
                } else if (tier == RiskTier.MEDIUM_RISK) {
                    mediumRisk++;
                } else {
                    highRisk++;
                }

                if (decision.isApproved()) {
                    approved++;
                }

                totalRequested += decision.getApplicant().getLoanAmountRequested();
                totalRecommended += decision.getRecommendedLimit();
            }

            summary.append("EXECUTIVE SUMMARY\n");
            summary.append("=================\n\n");
            summary.append(String.format("Total Applications: %d\n", decisions.size()));
            summary.append(String.format("Approval Rate: %.1f%%\n", (approved * 100.0) / decisions.size()));
            summary.append(String.format("Total Exposure: $%,.2f\n", totalRecommended));
            summary.append(String.format("Risk Distribution - Low: %.1f%%, Medium: %.1f%%, High: %.1f%%\n",
                    (lowRisk * 100.0) / decisions.size(),
                    (mediumRisk * 100.0) / decisions.size(),
                    (highRisk * 100.0) / decisions.size()));

            // Save summary
            FileHandler.saveTextToFile(summary.toString(), "data/output/executive_summary.txt");
            System.out.println("✅ Executive summary saved: data/output/executive_summary.txt");

        } catch (Exception e) {
            System.err.println("Error generating executive summary: " + e.getMessage());
        }
    }

    private static double getValidDoubleInput(Scanner scanner, String prompt, double min, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = scanner.nextDouble();
                scanner.nextLine(); // Consume newline

                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("❌ Please enter a value between %.2f and %.2f\n", min, max);
                }
            } catch (Exception e) {
                System.out.println("❌ Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }

    private static int getValidIntInput(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("❌ Please enter a value between %d and %d\n", min, max);
                }
            } catch (Exception e) {
                System.out.println("❌ Invalid input. Please enter a valid integer.");
                scanner.nextLine(); // Clear invalid input
            }
        }
    }
}
