package com.sample;

import java.io.InputStreamReader;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.reteoo.WorkingMemoryFileLogger;

public class TroubleTicketExample {

    /**
     * @param args
     */
    public static void main(String[] args) throws Exception {

        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( TroubleTicketExample.class.getResourceAsStream( "/TroubleTicket.drl" ) ) );

        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        WorkingMemory workingMemory = ruleBase.newWorkingMemory();

        WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( workingMemory );
        logger.setFileName( "log/state" );

        Customer a = new Customer( "A", "Gold" );
        Customer b = new Customer( "B", "Platinum" );
        Customer c = new Customer( "C", "Silver" );        
        Customer d = new Customer( "D", "Silver" );
        
        Ticket t1 = new Ticket( a );
        Ticket t2 = new Ticket( b );
        Ticket t3 = new Ticket( c );        
        Ticket t4 = new Ticket( d );        
        
        FactHandle fa = workingMemory.assertObject( a );
        FactHandle fb = workingMemory.assertObject( b );
        FactHandle fc = workingMemory.assertObject( c );
        FactHandle fd = workingMemory.assertObject( d );        

        FactHandle ft1 = workingMemory.assertObject( t1 );
        FactHandle ft2 = workingMemory.assertObject( t2 );
        FactHandle ft3 = workingMemory.assertObject( t3 );
        FactHandle ft4 = workingMemory.assertObject( t4 );        
        
        workingMemory.fireAllRules();
        
        t3.setStatus( "Done" );
        
        workingMemory.modifyObject( ft3, t3 );

        try
        {
            System.err.println( "[[ Sleeping 5 seconds ]]" );
            Thread.sleep( 5000 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace( );
        }        

        workingMemory.fireAllRules();
        
        logger.writeToDisk();
    }

    public static class Customer {
        private String name;
        private String subscription;

        public Customer(String name,
                        String subscription) {
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

        public Ticket(Customer customer) {
            super();
            this.customer = customer;
            this.status = "New";
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String status) {
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
