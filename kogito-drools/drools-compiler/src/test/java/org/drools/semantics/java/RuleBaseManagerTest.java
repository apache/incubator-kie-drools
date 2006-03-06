package org.drools.semantics.java;

import org.drools.WorkingMemory;

import junit.framework.TestCase;

public class RuleBaseManagerTest extends TestCase {
    public void testDrl() throws Exception {
        RuleBaseManager manager = new RuleBaseManager();
        manager.addDrl( getClass().getResourceAsStream( "test1.drl" ) );                        
        
        org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
        ruleBase.addPackage( manager.getPackage( "org.drools.test" ) );
        WorkingMemory workingMemory = ruleBase.newWorkingMemory();
        
        Cheese stilton = new Cheese("stilton", 5);
        workingMemory.assertObject( stilton );
        workingMemory.fireAllRules();
    }
    
    public static class Cheese {
        private String type;
        private int    price;

        public Cheese(String type,
                      int price) {
            super();
            this.type = type;
            this.price = price;
        }

        public int getPrice() {
            return price;
        }

        public String getType() {
            return type;
        }
    }    
}
