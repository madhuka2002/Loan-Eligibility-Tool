package com.loantool.config;

public class RulesConfig {
    private static RulesConfig instance;

    // Default rules
    private double minIncomeThreshold = 2000.0;
    private double maxDebtToIncomeRatio = 0.4;
    private int minCreditScore = 600;
    private int minEmploymentMonths = 6;
    private double maxLoanToIncomeRatio = 2.0;
    private int[] riskThresholds = { 80, 50 }; // [Low, Medium]

    private RulesConfig() {
        // Private constructor for singleton
    }

    public static synchronized RulesConfig getInstance() {
        if (instance == null) {
            instance = new RulesConfig();
        }
        return instance;
    }

    public void displayRules() {
        System.out.println("\n📋 CURRENT RULES CONFIGURATION");
        System.out.println("==============================");
        System.out.printf("Minimum Income: $%.2f\n", minIncomeThreshold);
        System.out.printf("Max Debt-to-Income Ratio: %.1f%%\n", maxDebtToIncomeRatio * 100);
        System.out.printf("Minimum Credit Score: %d\n", minCreditScore);
        System.out.printf("Minimum Employment: %d months\n", minEmploymentMonths);
        System.out.printf("Max Loan-to-Income Ratio: %.1f\n", maxLoanToIncomeRatio);
        System.out.println("Risk Thresholds: Low≥" + riskThresholds[0] +
                ", Medium≥" + riskThresholds[1] + ", High<" + riskThresholds[1]);
    }

    public void resetToDefaults() {
        minIncomeThreshold = 2000.0;
        maxDebtToIncomeRatio = 0.4;
        minCreditScore = 600;
        minEmploymentMonths = 6;
        maxLoanToIncomeRatio = 2.0;
        riskThresholds = new int[] { 80, 50 };
    }

    // Getters and Setters
    public double getMinIncomeThreshold() {
        return minIncomeThreshold;
    }

    public void setMinIncomeThreshold(double threshold) {
        this.minIncomeThreshold = threshold;
    }

    public double getMaxDebtToIncomeRatio() {
        return maxDebtToIncomeRatio;
    }

    public void setMaxDebtToIncomeRatio(double ratio) {
        this.maxDebtToIncomeRatio = ratio;
    }

    public int getMinCreditScore() {
        return minCreditScore;
    }

    public void setMinCreditScore(int score) {
        this.minCreditScore = score;
    }

    public int getMinEmploymentMonths() {
        return minEmploymentMonths;
    }

    public void setMinEmploymentMonths(int months) {
        this.minEmploymentMonths = months;
    }

    public double getMaxLoanToIncomeRatio() {
        return maxLoanToIncomeRatio;
    }

    public void setMaxLoanToIncomeRatio(double ratio) {
        this.maxLoanToIncomeRatio = ratio;
    }

    public int[] getRiskThresholds() {
        return riskThresholds.clone();
    }

    public void setRiskThresholds(int[] thresholds) {
        this.riskThresholds = thresholds.clone();
    }
}