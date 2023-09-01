package org.drools.examples.banking;

public class BankingExample1 {
    public static void main(String[] args) {
        new RuleRunner().runRules( new String[] { "Example1.drl" },
                                   new Object[0] );
    }
}
