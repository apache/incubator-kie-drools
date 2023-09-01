package org.drools.compiler.integrationtests.ruleunits.decisiontables;

import org.drools.ruleunits.api.DataSource;
import org.drools.ruleunits.api.DataStore;
import org.drools.ruleunits.api.RuleUnitData;

public class LoanUnit implements RuleUnitData {

    private int maxAmount;

    private DataStore<LoanApplication> loanApplications;

    public LoanUnit() {
        this(DataSource.createStore());
    }

    public LoanUnit(DataStore<LoanApplication> loanApplications) {
        this.loanApplications = loanApplications;
    }

    public DataStore<LoanApplication> getLoanApplications() {
        return loanApplications;
    }

    public void setLoanApplications(DataStore<LoanApplication> loanApplications) {
        this.loanApplications = loanApplications;
    }

    public int getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(int maxAmount) {
        this.maxAmount = maxAmount;
    }

}
