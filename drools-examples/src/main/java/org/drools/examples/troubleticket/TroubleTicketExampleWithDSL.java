package org.drools.examples.troubleticket;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TroubleTicketExampleWithDSL {

    private static final Logger LOG = LoggerFactory.getLogger(TroubleTicketExampleWithDSL.class);

    public static void main(final String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        execute( kc );
    }

    public static void execute( KieContainer kc ) {
        KieSession ksession = kc.newKieSession( "TroubleTicketWithDTKS" );

        final Customer a = new Customer( "A",
                                         "Drools",
                                         "Gold" );
        final Customer b = new Customer( "B",
                                         "Drools",
                                         "Platinum" );
        final Customer c = new Customer( "C",
                                         "Drools",
                                         "Silver" );
        final Customer d = new Customer( "D",
                                         "Drools",
                                         "Silver" );

        final Ticket t1 = new Ticket( a );
        final Ticket t2 = new Ticket( b );
        final Ticket t3 = new Ticket( c );
        final Ticket t4 = new Ticket( d );

        ksession.insert( a );
        ksession.insert( b );
        ksession.insert( c );
        ksession.insert( d );

        ksession.insert( t1 );
        ksession.insert( t2 );
        final FactHandle ft3 = ksession.insert( t3 );
        ksession.insert( t4 );

        ksession.fireAllRules();

        t3.setStatus( "Done" );

        ksession.update( ft3,
                         t3 );

        try {
            System.err.println( "[[ Sleeping 5 seconds ]]" );
            Thread.sleep( 5000 );
        } catch ( final InterruptedException e ) {
            LOG.error("Exception", e);
        }

        System.err.println( "[[ awake ]]" );

        ksession.fireAllRules();

        ksession.dispose();
    }
}
