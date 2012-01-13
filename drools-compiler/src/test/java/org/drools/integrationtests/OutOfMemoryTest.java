/*
 * Copyright 2005 JBoss Inc
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

package org.drools.integrationtests;

import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.drools.Cheese;
import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.compiler.PackageBuilder;
import org.drools.core.util.debug.SessionInspector;
import org.drools.core.util.debug.SessionReporter;
import org.drools.io.ResourceFactory;
import org.drools.rule.Package;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Run all the tests with the ReteOO engine implementation */
public class OutOfMemoryTest extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger(OutOfMemoryTest.class);
    
    /**
     * This test can take a while (> 1 minute).
     * @throws Exception
     */
    @Test
    @Ignore
    public void testStatefulSessionsCreation() throws Exception {

        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_OutOfMemoryError.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );

        int i = 0;

        SessionConfiguration conf = new SessionConfiguration();
        conf.setKeepReference( true ); // this is just for documentation purposes, since the default value is "true"
        try {
            for ( i = 0; i < 300000; i++ ) {
                final StatefulSession session = ruleBase.newStatefulSession( conf,
                                                                             null );
                session.dispose();
            }
        } catch ( Throwable e ) {
            logger.info( "Error at: " + i );
            e.printStackTrace();
            fail( "Should not raise any error or exception." );
        }

    }

    @Test
    @Ignore
    public void testAgendaLoop() throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( getClass().getResourceAsStream( "test_OutOfMemory.drl" ) ) );
        final Package pkg = builder.getPackage();

        RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        ruleBase = SerializationHelper.serializeObject( ruleBase );
        final WorkingMemory workingMemory = ruleBase.newStatefulSession();

        workingMemory.insert( new Cheese( "stilton",
                                          1 ) );

        workingMemory.fireAllRules( 3000000 );

        // just for profiling
        //Thread.currentThread().wait();
    }
    
    @Test
    @Ignore
    public void testMemoryLeak() {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "test_MemoryLeak.drl",
                                                            OutOfMemoryTest.class ),
                      ResourceType.DRL );
        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        final KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        final StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        final int pcount = 5;
        Person[] persons = new Person[pcount];
        FactHandle[] pHandles = new FactHandle[pcount];
        for ( int i = 0; i < persons.length; i++ ) {
            persons[i] = new Person( "person-0-" + i );
            pHandles[i] = ksession.insert( persons[i] );
        }

        Cheese[] cheeses = new Cheese[pcount];
        FactHandle[] cHandles = new FactHandle[pcount];
        for ( int i = 0; i < cheeses.length; i++ ) {
            cheeses[i] = new Cheese( "cheese-0-" + i );
            cHandles[i] = ksession.insert( cheeses[i] );
        }

        ksession.fireAllRules();

        for ( int j = 1; j <= 5; j++ ) {
            for ( int i = 0; i < pcount; i++ ) {
                cheeses[i].setType( "cheese-" + j + "-" + i );
                ksession.update( cHandles[i],
                                 cheeses[i] );
                persons[i].setName( "person-" + j + "-" + i );
                ksession.update( pHandles[i],
                                 persons[i] );
            }
            ksession.fireAllRules();
            logger.info( "DONE" );
        }

        SessionInspector inspector = new SessionInspector( ksession );
        SessionReporter.addNamedTemplate( "dump_tuples",
                                          getClass().getResourceAsStream( "/org/drools/core/util/debug/dump_tuples.mvel" ) );
        String report = SessionReporter.generateReport( "dump_tuples",
                                                        inspector.getSessionInfo(),
                                                        new HashMap<String, Object>() );
        try {
            FileWriter out = new FileWriter( "tupleDump.txt" );
            out.write( report );
            out.close();
            logger.info( report );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        //        logicals = getLogicallyInserted( ksession );
        //        assertEquals( pcount, logicals.size() );
    }

}
