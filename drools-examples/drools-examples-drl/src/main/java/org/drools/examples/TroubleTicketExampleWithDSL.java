package org.drools.examples;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
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

        final StatefulSession session = ruleBase.newStatefulSession();

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
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

        final FactHandle fa = session.insert( a );
        final FactHandle fb = session.insert( b );
        final FactHandle fc = session.insert( c );
        final FactHandle fd = session.insert( d );

        final FactHandle ft1 = session.insert( t1 );
        final FactHandle ft2 = session.insert( t2 );
        final FactHandle ft3 = session.insert( t3 );
        final FactHandle ft4 = session.insert( t4 );

        session.fireAllRules();

        t3.setStatus( "Done" );

        session.update( ft3,
                        t3 );

        try {
            System.err.println( "[[ Sleeping 5 seconds ]]" );
            Thread.sleep( 5000 );
        } catch ( final InterruptedException e ) {
            e.printStackTrace();
        }

        session.fireAllRules();

        session.dispose();

        logger.writeToDisk();
    }

    private static Reader getDSL() {
        return new InputStreamReader( TroubleTicketExampleWithDSL.class.getResourceAsStream( "ticketing.dsl" ) );

    }

    private static InputStreamReader getSource() {
        return new InputStreamReader( TroubleTicketExampleWithDSL.class.getResourceAsStream( "TroubleTicketWithDSL.dslr" ) );
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
