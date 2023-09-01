/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests;

import java.io.FileWriter;
import java.util.Collection;
import java.util.HashMap;

import org.drools.core.SessionConfiguration;
import org.drools.kiesession.debug.SessionInspector;
import org.drools.mvel.SessionReporter;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.KeepReferenceOption;
import org.kie.api.runtime.rule.FactHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.fail;

/** Run all the tests with the ReteOO engine implementation */
@RunWith(Parameterized.class)
public class OutOfMemoryTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public OutOfMemoryTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private static Logger logger = LoggerFactory.getLogger(OutOfMemoryTest.class);
    
    /**
     * This test can take a while (> 1 minute).
     * @throws Exception
     */
    @Test
    @Ignore
    public void testStatefulSessionsCreation() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_OutOfMemoryError.drl");

        int i = 0;

        SessionConfiguration conf = KieServices.get().newKieSessionConfiguration().as(SessionConfiguration.KEY);
        conf.setKeepReference( true ); // this is just for documentation purposes, since the default value is "true"
        try {
            for ( i = 0; i < 300000; i++ ) {
                KieSession ksession = kbase.newKieSession( conf, null );
                ksession.dispose();
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
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_OutOfMemoryError.drl");

        KieSession ksession = kbase.newKieSession();

        ksession.insert( new Cheese( "stilton",
                                          1 ) );

        ksession.fireAllRules( 3000000 );

        // just for profiling
        //Thread.currentThread().wait();
    }
    
    @Test
    @Ignore("dump_tuples.mvel no longer seems to work")
    public void testMemoryLeak() {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_MemoryLeak.drl");
        KieSession ksession = kbase.newKieSession();

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
