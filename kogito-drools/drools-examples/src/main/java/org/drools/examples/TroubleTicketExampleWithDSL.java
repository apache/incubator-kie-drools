package org.drools.examples;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.compiler.PackageBuilder;

public class TroubleTicketExampleWithDSL {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( getSource(),
                                   getDSL() );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        final WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( workingMemory );
        logger.setFileName( "log/state" );

        final Customer a = new Customer( "A",
                                   "Gold" );
        final Customer b = new Customer( "B",
                                   "Platinum" );
        final Customer c = new Customer( "C",
                                   "Silver" );
        final Customer d = new Customer( "D",
                                   "Silver" );

        final Ticket t1 = new Ticket( a );
        final Ticket t2 = new Ticket( b );
        final Ticket t3 = new Ticket( c );
        final Ticket t4 = new Ticket( d );

        final FactHandle fa = workingMemory.assertObject( a );
        final FactHandle fb = workingMemory.assertObject( b );
        final FactHandle fc = workingMemory.assertObject( c );
        final FactHandle fd = workingMemory.assertObject( d );

        final FactHandle ft1 = workingMemory.assertObject( t1 );
        final FactHandle ft2 = workingMemory.assertObject( t2 );
        final FactHandle ft3 = workingMemory.assertObject( t3 );
        final FactHandle ft4 = workingMemory.assertObject( t4 );

        workingMemory.fireAllRules();

        t3.setStatus( "Done" );

        workingMemory.modifyObject( ft3,
                                    t3 );

        try {
            System.err.println( "[[ Sleeping 5 seconds ]]" );
            Thread.sleep( 5000 );
        } catch ( final InterruptedException e ) {
            e.printStackTrace();
        }

        workingMemory.fireAllRules();

        logger.writeToDisk();
    }

    private static Reader getDSL() {
        return new InputStreamReader( TroubleTicketExampleWithDSL.class.getResourceAsStream( "/ticketing.dsl" ) );

    }

    private static InputStreamReader getSource() {
        return new InputStreamReader( TroubleTicketExampleWithDSL.class.getResourceAsStream( "/TroubleTicketWithDSL.drl" ) );
    }

    public static class Customer {
        private String name;
        private String subscription;

        public Customer() {
        	
        }
        
        public Customer(final String name,
                        final String subscription) {
            super();
            this.name = name;
            this.subscription = subscription;
        }

        public String getName() {
            return this.name;
        }

        public String getSubscription() {
            return this.subscription;
        }

        public String toString() {
            return "[Customer " + this.name + " : " + this.subscription + "]";
        }

    }

    public static class Ticket {
        private Customer customer;
        private String   status;

        public Ticket() {
        	
        }
        
        public Ticket(final Customer customer) {
            super();
            this.customer = customer;
            this.status = "New";
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(final String status) {
            this.status = status;
        }

        public Customer getCustomer() {
            return this.customer;
        }

        public String toString() {
            return "[Ticket " + this.customer + " : " + this.status + "]";
        }

    }

}
