package org.drools.impact.analysis.integrationtests.kogito.domain;

public class LoanApplication {

    private String id;

    private Applicant applicant;

    private int amount;

    private int deposit;

    private boolean approved = false;

    public LoanApplication() {
    }

    public LoanApplication(String id, Applicant applicant, int amount, int deposit) {
        this.id = id;
        this.applicant = applicant;
        this.amount = amount;
        this.deposit = deposit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Applicant getApplicant() {
        return applicant;
    }

    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }

}
