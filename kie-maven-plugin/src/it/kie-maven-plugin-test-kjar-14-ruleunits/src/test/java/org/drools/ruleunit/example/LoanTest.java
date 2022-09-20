package org.drools.ruleunit.example;

import java.util.List;
import java.util.Map;

import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.junit.Test;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

public class LoanTest {

    @Test
    public void test() {
        LoanUnit loanUnit = new LoanUnit();
        loanUnit.setMaxAmount(5000);

        RuleUnitInstance<LoanUnit> instance = RuleUnitProvider.get().createRuleUnitInstance(loanUnit);

        loanUnit.getLoanApplications().add( new LoanApplication("ABC10002", new Applicant("Paul", 25), 5000, 100) );
        loanUnit.getLoanApplications().add( new LoanApplication("ABC10001", new Applicant("John", 45), 2000, 100) );
        loanUnit.getLoanApplications().add( new LoanApplication("ABC10015", new Applicant("George", 12), 1000, 100) );

        List<LoanApplication> results = instance.executeQuery("FindApproved").stream().map(this::toResult).collect(toList());

        assertEquals(1, results.size());
        assertEquals("ABC10001", results.get(0).getId());
        assertEquals("John", results.get(0).getApplicant().getName());
    }

    private LoanApplication toResult(Map<String, Object> tuple) {
        return (LoanApplication) tuple.get("$l");
    }
}
