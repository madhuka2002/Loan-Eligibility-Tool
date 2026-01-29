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

}
