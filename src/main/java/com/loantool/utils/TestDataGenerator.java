package com.loantool.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TestDataGenerator {

    public static void generateCSV(String filePath, int numberOfRecords) throws IOException {
        try (FileWriter writer = new FileWriter(filePath)) {

            writer.write("ID,MonthlyIncome,ExistingDebt,CreditScore,EmploymentMonths,LoanAmountRequested\n");

            Random random = new Random(42); // Fixed seed for reproducibility

            for (int i = 1; i <= numberOfRecords; i++) {
                String id = String.format("APP%04d", i);

                // Generate realistic data
                double income = 1500 + random.nextDouble() * 8500; // $1500 - $10000
                double debt = random.nextDouble() * income * 0.8; // Up to 80% of income
                int creditScore = 350 + random.nextInt(500); // 350-850
                int employmentMonths = random.nextInt(60); // Up to 5 years
                double loanAmount = 1000 + random.nextDouble() * 19000; // $1000 - $20000

                writer.write(String.format("%s,%.2f,%.2f,%d,%d,%.2f\n",
                        id, income, debt, creditScore, employmentMonths, loanAmount));
            }
        }

        System.out.println("✅ Generated " + numberOfRecords + " test records to " + filePath);
    }

    public static void main(String[] args) {
        try {
            // Generate sample dataset
            generateCSV("data/input/sample_large.csv", 1000);

            // Generate small test dataset
            generateCSV("data/input/test_data.csv", 50);

        } catch (IOException e) {
            System.err.println("Error generating test data: " + e.getMessage());
        }
    }
}