package org.drools.examples.troubleticket;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class TroubleTicketExampleWithDSL {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.addResource( getDSL(),
                             KnowledgeType.DSL );
        kbuilder.addResource( getSource(),
                             KnowledgeType.DSLR );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( ksession );
        logger.setFileName( "log/state" );

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

        final FactHandle fa = ksession.insert( a );
        final FactHandle fb = ksession.insert( b );
        final FactHandle fc = ksession.insert( c );
        final FactHandle fd = ksession.insert( d );

        final FactHandle ft1 = ksession.insert( t1 );
        final FactHandle ft2 = ksession.insert( t2 );
        final FactHandle ft3 = ksession.insert( t3 );
        final FactHandle ft4 = ksession.insert( t4 );

        ksession.fireAllRules();

        t3.setStatus( "Done" );

        ksession.update( ft3,
                        t3 );

        try {
            System.err.println( "[[ Sleeping 5 seconds ]]" );
            Thread.sleep( 5000 );
        } catch ( final InterruptedException e ) {
            e.printStackTrace();
        }

        System.err.println( "[[ awake ]]" );

        ksession.fireAllRules();

        ksession.dispose();

        logger.writeToDisk();
    }

    private static Reader getDSL() {
        return new InputStreamReader( TroubleTicketExampleWithDSL.class.getResourceAsStream( "ticketing.dsl" ) );

    }

    private static InputStreamReader getSource() {
        return new InputStreamReader( TroubleTicketExampleWithDSL.class.getResourceAsStream( "TroubleTicketWithDSL.dslr" ) );
    }

}
