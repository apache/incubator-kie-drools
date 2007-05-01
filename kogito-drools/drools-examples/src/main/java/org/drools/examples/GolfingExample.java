package org.drools.examples;

import java.io.InputStreamReader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;

public class GolfingExample {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( GolfingExample.class.getResourceAsStream( "golf.drl" ) ) );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        final StatefulSession session = ruleBase.newStatefulSession();
        
        String[] names = new String[] { "Fred", "Joe", "Bob", "Tom" };
        String[] colors = new String[] { "red", "blue", "plaid", "orange" };
        int[] positions = new int[] { 1, 2, 3, 4 };
        
        for ( int n = 0; n < names.length; n++ ) {
            for ( int c = 0; c < colors.length; c++ ) {
                for ( int p = 0; p < positions.length; p++ ) {
                    session.assertObject( new Golfer( names[n], colors[c], positions[p]) );
                }                
            }            
        }

        session.fireAllRules();
        session.dispose();
    }


    public static class Golfer {
        private String name;
        private String color;
        private int position;
        
        public Golfer() {
        	
        }
        
        public Golfer(String name,
                      String color,
                      int position) {
            super();
            this.name = name;
            this.color = color;
            this.position = position;
        }
        /**
         * @return the color
         */
        public String getColor() {
            return this.color;
        }
        /**
         * @return the name
         */
        public String getName() {
            return this.name;
        }
        
        /**
         * @return the name
         */
        public int getPosition() {
            return this.position;
        }        
        
    }
}

