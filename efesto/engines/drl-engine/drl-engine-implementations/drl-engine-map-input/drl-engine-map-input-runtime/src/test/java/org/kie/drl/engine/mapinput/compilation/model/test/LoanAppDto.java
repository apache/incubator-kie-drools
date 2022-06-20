package org.kie.drl.engine.mapinput.compilation.model.test;

import java.util.List;

public class LoanAppDto {
    private int maxAmount;
    private List<LoanApplication> loanApplications;

    public LoanAppDto(int maxAmount, List<LoanApplication> loanApplications) {
        this.maxAmount = maxAmount;
        this.loanApplications = loanApplications;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

    public List<LoanApplication> getLoanApplications() {
        return loanApplications;
    }

    public void setLoanApplications(List<LoanApplication> loanApplications) {
        this.loanApplications = loanApplications;
    }
}