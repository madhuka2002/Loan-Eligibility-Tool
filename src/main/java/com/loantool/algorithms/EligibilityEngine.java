package com.loantool.algorithms;

import com.loantool.models.Applicant;
import com.loantool.config.RulesConfig;

import java.util.ArrayList;
import java.util.List;

public class EligibilityEngine {
    private final RulesConfig config;
    private String rejectionReason;

    public EligibilityEngine() {
        this.config = RulesConfig.getInstance();
    }

    public boolean isEligible(Applicant applicant) {
        // Decision Tree Rules
        if (applicant.getMonthlyIncome() < config.getMinIncomeThreshold()) {
            rejectionReason = "Income below minimum threshold";
            return false;
        }

        double debtRatio = applicant.getDebtToIncomeRatio();
        if (debtRatio > config.getMaxDebtToIncomeRatio()) {
            rejectionReason = String.format("Debt ratio (%.1f%%) exceeds limit", debtRatio * 100);
            return false;
        }

        if (applicant.getCreditScore() < config.getMinCreditScore()) {
            rejectionReason = "Credit score too low";
            return false;
        }

        if (applicant.getEmploymentDuration() < config.getMinEmploymentMonths()) {
            rejectionReason = "Employment duration too short";
            return false;
        }

        double loanToIncome = applicant.getLoanAmountRequested() / applicant.getMonthlyIncome();
        if (loanToIncome > config.getMaxLoanToIncomeRatio()) {
            rejectionReason = "Loan amount too high relative to income";
            return false;
        }

        return true;
    }

    public List<Applicant> evaluateEligibility(List<Applicant> applicants) {
        List<Applicant> eligibleApplicants = new ArrayList<>();

        for (Applicant applicant : applicants) {
            if (isEligible(applicant)) {
                applicant.setEligible(true);
                eligibleApplicants.add(applicant);
            }
        }

        return eligibleApplicants;
    }

    public String getRejectionReason(Applicant applicant) {
        // Force evaluation to set rejection reason
        isEligible(applicant);
        return rejectionReason;
    }
}