package com.loantool.algorithms;

import com.loantool.models.Applicant;
import com.loantool.models.LoanDecision;
import com.loantool.models.RiskTier;
import com.loantool.config.RulesConfig;

import java.util.ArrayList;
import java.util.List;

public class RiskClassifier {
    private final RulesConfig config;

    public RiskClassifier() {
        this.config = RulesConfig.getInstance();
    }

    public LoanDecision classifySingle(Applicant applicant) {
        double score = applicant.getRiskScore();
        RiskTier tier = binarySearchRiskTier(score);
        boolean approved = tier != RiskTier.HIGH_RISK || score >= 40;

        String reason = String.format("Risk Score: %.1f → %s", score, tier.getDisplayName());

        return new LoanDecision(applicant, tier, approved, reason);
    }

    public List<LoanDecision> classify(List<Applicant> applicants) {
        List<LoanDecision> decisions = new ArrayList<>();

        for (Applicant applicant : applicants) {
            decisions.add(classifySingle(applicant));
        }

        return decisions;
    }

    private RiskTier binarySearchRiskTier(double score) {
        int[] thresholds = config.getRiskThresholds();

        int left = 0;
        int right = thresholds.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;

            if (score >= thresholds[mid]) {
                if (mid == 0 || score < thresholds[mid - 1]) {
                    return getTierForThreshold(mid);
                }
                right = mid - 1;
            } else {
                left = mid + 1;
            }
        }

        return RiskTier.HIGH_RISK;
    }

    private RiskTier getTierForThreshold(int index) {
        switch (index) {
            case 0:
                return RiskTier.LOW_RISK;
            case 1:
                return RiskTier.MEDIUM_RISK;
            default:
                return RiskTier.HIGH_RISK;
        }
    }

}
