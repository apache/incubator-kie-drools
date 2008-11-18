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

        final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        builder.addResource( getDSL(),
                             KnowledgeType.DSL );
        builder.addResource( getSource(),
                             KnowledgeType.DSLR );

        final KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( builder.getKnowledgePackages() );

        final StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();

        final WorkingMemoryFileLogger logger = new WorkingMemoryFileLogger( session );
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

        System.err.println( "[[ awake ]]" );

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

}
