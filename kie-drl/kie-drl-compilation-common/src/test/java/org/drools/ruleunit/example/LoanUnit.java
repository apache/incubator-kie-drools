package org.drools.ruleunit.example;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class LoanUnit implements RuleUnitData {

    private int maxAmount;

    private DataStore<LoanApplication> loanApplications;
    private DataStore<AllAmounts> allAmounts;

    public LoanUnit() {
        this(DataSource.createStore(), DataSource.createStore());
    }

    public LoanUnit(DataStore<LoanApplication> loanApplications, DataStore<AllAmounts> allAmounts) {
        this.loanApplications = loanApplications;
        this.allAmounts = allAmounts;
    }

    public DataStore<LoanApplication> getLoanApplications() {
        return loanApplications;
    }

    public void setLoanApplications(DataStore<LoanApplication> loanApplications) {
        this.loanApplications = loanApplications;
    }

    public DataStore<AllAmounts> getAllAmounts() {
        return allAmounts;
    }

    public void setAllAmounts(DataStore<AllAmounts> allAmounts) {
        this.allAmounts = allAmounts;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

}
