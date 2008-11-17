package org.drools.examples.troubleticket;

import java.io.InputStreamReader;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class TroubleTicketExample {

    /**
     * @param args
     */
    public static void main(final String[] args) throws Exception {

        final KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.addResource( new InputStreamReader( TroubleTicketExample.class.getResourceAsStream( "TroubleTicket.drl" ) ),
                             KnowledgeType.DRL );

        final KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( builder.getKnowledgePackages() );

        final StatefulKnowledgeSession session = knowledgeBase.newStatefulKnowledgeSession();

        //        ThreadedWorkingMemoryFileLogger logger = new ThreadedWorkingMemoryFileLogger(session);
        //        logger.setFileName( "log/trouble_ticket" );
        //        logger.start(1000);

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

        session.insert( a );
        session.insert( b );
        session.insert( c );
        session.insert( d );

        session.insert( t1 );
        session.insert( t2 );
        final FactHandle ft3 = session.insert( t3 );
        session.insert( t4 );

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

        session.dispose();

        //        logger.stop();
        //        logger.writeToDisk();
    }

}
