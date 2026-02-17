package com.loantool.models;

public class Applicant implements Comparable<Applicant> {
    private String id;
    private String name;
    private double salary;
    private double loanAmount;
    private int creditScore;
    private boolean eligible;

    public Applicant(String id, String name, double salary, double loanAmount, int creditScore) {
        this.id = id;
        this.name = name;
        this.salary = salary;
        this.loanAmount = loanAmount;
        this.creditScore = creditScore;
        this.eligible = false;
    }

    @Override
    public int compareTo(Applicant other) {
        // Compare by credit score in descending order
        return Integer.compare(other.creditScore, this.creditScore);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getSalary() {
        return salary;
    }

    public double getLoanAmount() {
        return loanAmount;
    }

    public int getCreditScore() {
        return creditScore;
    }

    public int getRiskScore() {
        return (int) (1 - (creditScore / 850.0)); // Normalize credit score to a risk score between 0 and 1
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }

    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }

    @Override
    public String toString() {
        return "Applicant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", loanAmount=" + loanAmount +
                ", creditScore=" + creditScore +
                ", eligible=" + eligible +
                '}';
    }

}