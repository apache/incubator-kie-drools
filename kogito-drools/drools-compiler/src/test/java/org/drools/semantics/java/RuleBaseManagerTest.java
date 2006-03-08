package org.drools.semantics.java;

import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;

import junit.framework.TestCase;

public class RuleBaseManagerTest extends TestCase {
    public void testDrl() throws Exception {
        PackageBuilder builder = new PackageBuilder();
        //manager.addDrl( getClass().getResourceAsStream( "test1.drl" ) );                        
        
        org.drools.reteoo.RuleBaseImpl ruleBase = new org.drools.reteoo.RuleBaseImpl();
        ruleBase.addPackage( builder.getPackage( ) );
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
