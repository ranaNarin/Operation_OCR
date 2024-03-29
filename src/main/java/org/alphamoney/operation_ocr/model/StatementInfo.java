package org.alphamoney.operation_ocr.model;

public class StatementInfo {
    private Double salary;
    private Double totalCredits;
    private Double totalDebits;

    // Constructors, getters, and setters

    public StatementInfo() {
    }

    public StatementInfo(Double salary, Double totalCredits, Double totalDebits) {
        this.salary = salary;
        this.totalCredits = totalCredits;
        this.totalDebits = totalDebits;
    }

    // Getters and setters
    public Double getSalary() {
        return salary;
    }

    public void setSalary(Double salary) {
        this.salary = salary;
    }

    public Double getTotalCredits() {
        return totalCredits;
    }

    public void setTotalCredits(Double totalCredits) {
        this.totalCredits = totalCredits;
    }

    public Double getTotalDebits() {
        return totalDebits;
    }

    public void setTotalDebits(Double totalDebits) {
        this.totalDebits = totalDebits;
    }
}
