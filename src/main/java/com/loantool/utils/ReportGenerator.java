package com.loantool.utils;

import com.loantool.models.LoanDecision;
import com.loantool.models.RiskTier;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReportGenerator {

    public static void generateReport(List<LoanDecision> decisions, String filePath)
            throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write("LOAN ELIGIBILITY DECISION REPORT\n");
            writer.write("================================\n\n");
            writer.write("Generated: " + LocalDateTime.now().format(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n\n");

            writer.write("SUMMARY STATISTICS\n");
            writer.write("==================\n");

            int total = decisions.size();
            int approved = 0;
            int lowRisk = 0, mediumRisk = 0, highRisk = 0;
            double totalRequested = 0, totalRecommended = 0;

            for (LoanDecision decision : decisions) {
                if (decision.isApproved())
                    approved++;

                switch (decision.getRiskTier()) {
                    case LOW_RISK:
                        lowRisk++;
                        break;
                    case MEDIUM_RISK:
                        mediumRisk++;
                        break;
                    case HIGH_RISK:
                        highRisk++;
                        break;
                }

                totalRequested += decision.getApplicant().getLoanAmountRequested();
                totalRecommended += decision.getRecommendedLimit();
            }

            writer.write(String.format("Total Applicants: %d\n", total));
            writer.write(String.format("Approved: %d (%.1f%%)\n",
                    approved, (approved * 100.0) / total));
            writer.write(String.format("Low Risk: %d (%.1f%%)\n",
                    lowRisk, (lowRisk * 100.0) / total));
            writer.write(String.format("Medium Risk: %d (%.1f%%)\n",
                    mediumRisk, (mediumRisk * 100.0) / total));
            writer.write(String.format("High Risk: %d (%.1f%%)\n",
                    highRisk, (highRisk * 100.0) / total));
            writer.write(String.format("\nTotal Amount Requested: $%,.2f\n", totalRequested));
            writer.write(String.format("Total Recommended Limit: $%,.2f\n", totalRecommended));

            writer.write("\nDETAILED DECISIONS\n");
            writer.write("===================\n");

            for (LoanDecision decision : decisions) {
                writer.write(String.format(
                        "\nApplicant: %s | Risk: %s | Approved: %s\n",
                        decision.getApplicant().getId(),
                        decision.getRiskTier().getDisplayName(),
                        decision.isApproved() ? "YES" : "NO"));
                writer.write(String.format(
                        "Risk Score: %.1f | Requested: $%,.2f | Recommended: $%,.2f\n",
                        decision.getApplicant().getRiskScore(),
                        decision.getApplicant().getLoanAmountRequested(),
                        decision.getRecommendedLimit()));
                writer.write("Reason: " + decision.getDecisionReason() + "\n");
            }
        }
    }
}