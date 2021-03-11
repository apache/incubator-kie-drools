/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.mvel.compiler.util.debug;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;

import org.drools.core.util.debug.SessionInspector;
import org.drools.core.util.debug.StatefulKnowledgeSessionInfo;
import org.drools.mvel.SessionReporter;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Cheesery;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.Worker;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class SessionInspectorTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public SessionInspectorTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testGetSessionInfo() {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration,
                                                                     "org/drools/mvel/integrationtests/test_SubNetworks.drl",
                                                                     "org/drools/mvel/integrationtests/test_AccumulateWithFromChaining.drl",
                                                                     "org/drools/mvel/integrationtests/test_CollectResultsBetaConstraint.drl",
                                                                     "org/drools/mvel/integrationtests/test_QueryMemoryLeak.drl");
        KieSession ksession = kbase.newKieSession();

        ksession.setGlobal( "results", new ArrayList<Object>() );

        ksession.insert( new Dimension( 100, 50 ) );
        ksession.insert( new Dimension( 130, 80 ) );
        ksession.insert( new Dimension( 50, 40 ) );
        ksession.insert( new Dimension( 50, 40 ) );
        Cheesery cheesery = new Cheesery();
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        ksession.insert( cheesery );
        ksession.insert( new Person( "Bob", "muzzarella") );
        ksession.insert( new Person( "Mark", "brie") );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "Stilton", 10 ) );
        ksession.insert( new Cheese( "Stilton", 10 ) );
        ksession.insert( new Cheese( "Stilton", 10 ) );
        ksession.insert( new Double( 10 ) );
        ksession.insert( new Double( 11 ) );
        ksession.insert( new Double( 12 ) );
        ksession.insert( new Double( 13 ) );
        ksession.insert( new Double( 14 ) );
        ksession.insert( new Integer( 15 ) );
        ksession.insert( new Integer( 16 ) );
        ksession.insert( new Integer( 17 ) );
        ksession.insert( new Integer( 18 ) );
        FactHandle handle = ksession.insert( new Worker( ) );
        
        ksession.retract( handle );
        
        SessionInspector inspector = new SessionInspector( ksession );
        
        StatefulKnowledgeSessionInfo info = inspector.getSessionInfo();
        
        String report = SessionReporter.generateReport( "simple", info, null );
        
        assertNotNull( report );
    }
    
    @Test
    public void testGetSessionInfoWithCustomTemplate() {
        if ( System.getProperty("java.vendor").toUpperCase().contains("IBM") ) {
            return; //Does not work in the IBM JDK due to a bug in MVEL
        }

        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(this.getClass(), kieBaseTestConfiguration,
                                                                     "org/drools/mvel/integrationtests/test_SubNetworks.drl",
                                                                     "org/drools/mvel/integrationtests/test_AccumulateWithFromChaining.drl",
                                                                     "org/drools/mvel/integrationtests/test_CollectResultsBetaConstraint.drl",
                                                                     "org/drools/mvel/integrationtests/test_QueryMemoryLeak.drl");
        KieSession ksession = kbase.newKieSession();

        ksession.setGlobal( "results", new ArrayList<Object>() );

        ksession.insert( new Dimension( 100, 50 ) );
        ksession.insert( new Dimension( 130, 80 ) );
        ksession.insert( new Dimension( 50, 40 ) );
        ksession.insert( new Dimension( 50, 40 ) );
        Cheesery cheesery = new Cheesery();
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "brie", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "muzzarella", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        cheesery.addCheese( new Cheese( "stilton", 10 ));
        ksession.insert( cheesery );
        ksession.insert( new Person( "Bob", "muzzarella") );
        ksession.insert( new Person( "Mark", "brie") );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "brie", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "muzzarella", 10 ) );
        ksession.insert( new Cheese( "Stilton", 10 ) );
        ksession.insert( new Cheese( "Stilton", 10 ) );
        ksession.insert( new Cheese( "Stilton", 10 ) );
        ksession.insert( new Double( 10 ) );
        ksession.insert( new Double( 11 ) );
        ksession.insert( new Double( 12 ) );
        ksession.insert( new Double( 13 ) );
        ksession.insert( new Double( 14 ) );
        ksession.insert( new Integer( 15 ) );
        ksession.insert( new Integer( 16 ) );
        ksession.insert( new Integer( 17 ) );
        ksession.insert( new Integer( 18 ) );
        FactHandle handle = ksession.insert( new Worker( ) );
        
        ksession.retract( handle );
        
        SessionInspector inspector = new SessionInspector( ksession );
        
        StatefulKnowledgeSessionInfo info = inspector.getSessionInfo();

        SessionReporter.addNamedTemplate( "topten", getClass().getResourceAsStream( "customreports.mvel" ) );
        String report = SessionReporter.generateReport( "topten", info, null );
        
        assertNotNull( report );
        
    }

}
