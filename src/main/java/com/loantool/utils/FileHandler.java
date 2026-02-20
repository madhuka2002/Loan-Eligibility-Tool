package com.loantool.utils;

import com.loantool.models.Applicant;
import com.loantool.models.LoanDecision;
import com.loantool.models.RiskTier;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

public class FileHandler {

    public static List<Applicant> loadApplicantsFromCSV(String filePath) throws IOException {
        List<Applicant> applicants = new ArrayList<>();
        List<String> validationErrors = new ArrayList<>();

        List<String> lines = Files.readAllLines(Paths.get(filePath));

        if (lines.isEmpty()) {
            throw new IOException("CSV file is empty");
        }

        // Validate header
        String header = lines.get(0);
        String[] headerParts = header.split(",");
        if (headerParts.length < 6) {
            throw new IOException(
                    "Invalid CSV format. Expected: ID,MonthlyIncome,ExistingDebt,CreditScore,EmploymentMonths,LoanAmountRequested");
        }

        // Process data rows
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);

            // Skip empty lines
            if (line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",");

            // Validate row
            DataValidator.ValidationResult validation = DataValidator.validateCSVRow(parts, i + 1);

            if (!validation.isValid()) {
                validationErrors.add("Line " + (i + 1) + ": " + validation.getErrorMessages());
                continue; // Skip invalid rows
            }

            try {
                String id = parts[0].trim();
                double income = Double.parseDouble(parts[1].trim());
                double debt = Double.parseDouble(parts[2].trim());
                int creditScore = Integer.parseInt(parts[3].trim());
                int employmentMonths = Integer.parseInt(parts[4].trim());
                double loanAmount = Double.parseDouble(parts[5].trim());

                applicants.add(new Applicant(id, income, debt, creditScore,
                        employmentMonths, loanAmount));

                // Log warnings if any
                if (validation.hasWarnings()) {
                    System.out.println("⚠️ Warning for line " + (i + 1) + ": " +
                            validation.getWarningMessages());
                }

            } catch (NumberFormatException e) {
                validationErrors.add("Line " + (i + 1) + ": Invalid number format");
            }
        }

        // Report validation errors
        if (!validationErrors.isEmpty()) {
            System.out.println("\n📋 VALIDATION REPORT:");
            System.out.println("====================");
            System.out.println("Loaded " + applicants.size() + " valid applicants");
            System.out.println("Skipped " + validationErrors.size() + " invalid rows");

            if (applicants.size() > 0) {
                System.out.println("\n❌ Validation Errors:");
                for (String error : validationErrors) {
                    System.out.println(error);
                }
            }
        }

        return applicants;
    }

    public static void saveDecisionsToCSV(List<LoanDecision> decisions, String filePath)
            throws IOException {
        // Create directory if it doesn't exist
        File outputDir = new File(filePath).getParentFile();
        if (outputDir != null && !outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            writer.write("ApplicantID,MonthlyIncome,ExistingDebt,CreditScore," +
                    "EmploymentMonths,LoanAmountRequested,RiskScore,RiskTier," +
                    "Approved,RecommendedLimit,DecisionReason\n");

            // Write data
            for (LoanDecision decision : decisions) {
                Applicant applicant = decision.getApplicant();

                writer.write(String.format("%s,%.2f,%.2f,%d,%d,%.2f,%.1f,%s,%s,%.2f,\"%s\"\n",
                        applicant.getId(),
                        applicant.getMonthlyIncome(),
                        applicant.getExistingDebt(),
                        applicant.getCreditScore(),
                        applicant.getEmploymentDuration(),
                        applicant.getLoanAmountRequested(),
                        applicant.getRiskScore(),
                        decision.getRiskTier().name(),
                        decision.isApproved() ? "YES" : "NO",
                        decision.getRecommendedLimit(),
                        decision.getDecisionReason().replace("\"", "\"\"") // Escape quotes
                ));
            }
        }
    }

    public static void saveDecisionsToJSON(List<LoanDecision> decisions, String filePath)
            throws IOException {
        JSONArray jsonArray = new JSONArray();

        for (LoanDecision decision : decisions) {
            JSONObject obj = new JSONObject();
            Applicant applicant = decision.getApplicant();

            obj.put("applicantId", applicant.getId());
            obj.put("monthlyIncome", applicant.getMonthlyIncome());
            obj.put("existingDebt", applicant.getExistingDebt());
            obj.put("creditScore", applicant.getCreditScore());
            obj.put("employmentMonths", applicant.getEmploymentDuration());
            obj.put("loanAmountRequested", applicant.getLoanAmountRequested());
            obj.put("riskScore", applicant.getRiskScore());
            obj.put("riskTier", decision.getRiskTier().name());
            obj.put("approved", decision.isApproved());
            obj.put("decisionReason", decision.getDecisionReason());
            obj.put("recommendedLimit", decision.getRecommendedLimit());
            obj.put("decisionTime", decision.getDecisionTime().toString());
            obj.put("interestRate", decision.getRiskTier().getBaseInterestRate());

            jsonArray.put(obj);
        }

        // Create directory if it doesn't exist
        File outputDir = new File(filePath).getParentFile();
        if (outputDir != null && !outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (FileWriter file = new FileWriter(filePath)) {
            file.write(jsonArray.toString(2));
        }
    }

    public static List<LoanDecision> loadDecisionsFromJSON(String filePath)
            throws IOException {
        List<LoanDecision> decisions = new ArrayList<>();

        // Check if file exists
        File file = new File(filePath);
        if (!file.exists()) {
            throw new FileNotFoundException("File not found: " + filePath);
        }

        String content = new String(Files.readAllBytes(Paths.get(filePath)));

        // Check if content is empty
        if (content.trim().isEmpty()) {
            return decisions; // Return empty list
        }

        JSONArray jsonArray = new JSONArray(content);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject obj = jsonArray.getJSONObject(i);

            Applicant applicant = new Applicant(
                    obj.getString("applicantId"),
                    obj.getDouble("monthlyIncome"),
                    obj.getDouble("existingDebt"),
                    obj.getInt("creditScore"),
                    obj.getInt("employmentMonths"),
                    obj.getDouble("loanAmountRequested"));

            // Set risk score if it exists in JSON
            if (obj.has("riskScore")) {
                applicant.setRiskScore(obj.getDouble("riskScore"));
            } else {
                // Calculate if not present
                applicant.calculateRiskScore();
            }

            // Get risk tier
            String tierStr = obj.getString("riskTier");
            RiskTier tier;
            try {
                tier = RiskTier.valueOf(tierStr);
            } catch (IllegalArgumentException e) {
                // Default to MEDIUM_RISK if invalid
                tier = RiskTier.MEDIUM_RISK;
            }

            boolean approved = obj.getBoolean("approved");
            String decisionReason = obj.getString("decisionReason");

            // Create LoanDecision
            LoanDecision decision = new LoanDecision(applicant, tier, approved, decisionReason);
            decisions.add(decision);
        }

        return decisions;
    }

    public static void saveTextToFile(String content, String filePath) throws IOException {
        // Create directory if it doesn't exist
        File outputDir = new File(filePath).getParentFile();
        if (outputDir != null && !outputDir.exists()) {
            outputDir.mkdirs();
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(content);
        }
    }

    public static String readTextFromFile(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public static boolean fileExists(String filePath) {
        return new File(filePath).exists();
    }

}