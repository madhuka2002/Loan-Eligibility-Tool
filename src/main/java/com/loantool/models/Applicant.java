package com.loantool.models;

public class Applicant implements Comparable<Applicant> {
    private String id;
    private double monthlyIncome;
    private double existingDebt;
    private int creditScore;
    private int employmentDuration; // months
    private double loanAmountRequested;
    private double riskScore;
    private boolean eligible;

    public Applicant(String id, double monthlyIncome, double existingDebt,
                     int creditScore, int employmentDuration, double loanAmountRequested) {
        this.id = id;
        this.monthlyIncome = monthlyIncome;
        this.existingDebt = existingDebt;
        this.creditScore = creditScore;
        this.employmentDuration = employmentDuration;
        this.loanAmountRequested = loanAmountRequested;
    }

    public void calculateRiskScore() {
        // Weighted scoring algorithm
        double incomeWeight = (monthlyIncome / 5000) * 30; // Max 30 points
        double debtRatio = (existingDebt / monthlyIncome) * 100;
        double debtWeight = Math.max(0, 30 - (debtRatio * 0.3)); // Max 30 points

        double creditWeight = (creditScore - 300) / 5.5; // 300-850 → 0-100 points
        creditWeight = Math.min(40, Math.max(0, creditWeight)); // Max 40 points

        this.riskScore = incomeWeight + debtWeight + creditWeight;
    }

    public double getDebtToIncomeRatio() {
        return monthlyIncome > 0 ? existingDebt / monthlyIncome : Double.MAX_VALUE;
    }

    @Override
    public int compareTo(Applicant other) {
        // Higher risk score = better (lower risk)
        return Double.compare(other.riskScore, this.riskScore);
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public double getMonthlyIncome() {
        return monthlyIncome;
    }

    public double getExistingDebt() {
        return existingDebt;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public int getEmploymentDuration() {
        return employmentDuration;
    }

    public double getLoanAmountRequested() {
        return loanAmountRequested;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
    }

    @Override
    public String toString() {
        return String.format("Applicant %s: Income=$%.2f, Debt=$%.2f, Credit=%d, Risk=%.1f",
                id, monthlyIncome, existingDebt, creditScore, riskScore);
    }
}