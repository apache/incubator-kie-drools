package org.drools.tutorials.banking;

public class Example5 {    
    public static void main(String[] args) throws Exception {      
        Object[] cashflows = {
            new TypedCashflow(new SimpleDate("01/01/2007"),    
                              TypedCashflow.CREDIT, 300.00),
            new TypedCashflow(new SimpleDate("05/01/2007"),
                              TypedCashflow.CREDIT, 100.00),
            new TypedCashflow(new SimpleDate("11/01/2007"),
                              TypedCashflow.CREDIT, 500.00),
            new TypedCashflow(new SimpleDate("07/01/2007"),
                              TypedCashflow.DEBIT, 800.00),
            new TypedCashflow(new SimpleDate("02/01/2007"),
                              TypedCashflow.DEBIT, 400.00),
        };
        
        new RuleRunner().runRules( new String[] { "Example5.drl" },
                                   cashflows );
    }
}
