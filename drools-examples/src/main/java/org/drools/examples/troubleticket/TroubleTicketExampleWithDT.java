/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.examples.troubleticket;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.logger.KnowledgeRuntimeLogger;
import org.drools.logger.KnowledgeRuntimeLoggerFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

/**
 * This shows off a decision table.
 */
public class TroubleTicketExampleWithDT {

    public static final void main(String[] args) {
        TroubleTicketExampleWithDT launcher = new TroubleTicketExampleWithDT();
        launcher.executeExample();
    }

    public void executeExample() {

        final DecisionTableConfiguration dtableconfiguration = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtableconfiguration.setInputType( DecisionTableInputType.XLS );

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(  ResourceFactory.newClassPathResource( "TroubleTicket.xls", TroubleTicketExampleWithDT.class ),
                              ResourceType.DTABLE,
                              dtableconfiguration );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // typical decision tables are used statelessly
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

//        KnowledgeRuntimeLogger logger = KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "log/trouble_ticket.log");

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
            e.printStackTrace();
        }

        System.err.println( "[[ awake ]]" );

        ksession.dispose();

//        logger.close();
    }

}
