package com.loantool.models;

public enum RiskTier {
    LOW_RISK("Low Risk", "Best rates, most favorable terms", 0.03),
    MEDIUM_RISK("Medium Risk", "Standard rates and terms", 0.07),
    HIGH_RISK("High Risk", "Higher rates, stricter terms", 0.12);

    private final String displayName;
    private final String description;
    private final double baseInterestRate;

    RiskTier(String displayName, String description, double baseInterestRate) {
        this.displayName = displayName;
        this.description = description;
        this.baseInterestRate = baseInterestRate;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public double getBaseInterestRate() {
        return baseInterestRate;
    }

    public static RiskTier fromScore(double riskScore) {
        if (riskScore >= 80) {
            return LOW_RISK;
        } else if (riskScore >= 50) {
            return MEDIUM_RISK;
        } else {
            return HIGH_RISK;
        }
    }

    public static RiskTier fromString(String name) {
        try {
            return RiskTier.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            return MEDIUM_RISK; // Default
        }
    }

    @Override
    public String toString() {
        return displayName;
    }
}