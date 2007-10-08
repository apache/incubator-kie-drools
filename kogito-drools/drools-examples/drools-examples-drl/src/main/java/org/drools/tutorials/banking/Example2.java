package org.drools.tutorials.banking;

public class Example2 {
    public static void main(String[] args) throws Exception {
        Number[] numbers = new Number[] {wrap(3), wrap(1), wrap(4), wrap(1), wrap(5)};
        new RuleRunner().runRules( new String[] { "Example2.drl" },
                                   numbers );
    }
    
    private static Integer wrap(int i) {
        return new Integer(i);
    }
}
