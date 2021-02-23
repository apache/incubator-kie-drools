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

import java.awt.*;
import java.util.ArrayList;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.util.debug.SessionInspector;
import org.drools.mvel.SessionReporter;
import org.drools.core.util.debug.StatefulKnowledgeSessionInfo;
import org.drools.mvel.CommonTestMethodBase;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Cheesery;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.Worker;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class SessionInspectorTest extends CommonTestMethodBase {

    @Test
    public void testGetSessionInfo() {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/mvel/integrationtests/test_SubNetworks.drl" ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource("org/drools/mvel/integrationtests/test_AccumulateWithFromChaining.drl"),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/mvel/integrationtests/test_CollectResultsBetaConstraint.drl" ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/mvel/integrationtests/test_QueryMemoryLeak.drl" ),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        
        KieSession ksession = createKnowledgeSession(kbase);
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/mvel/integrationtests/test_SubNetworks.drl" ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource("org/drools/mvel/integrationtests/test_AccumulateWithFromChaining.drl"),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource( "org/drools/mvel/integrationtests/test_CollectResultsBetaConstraint.drl" ),
                      ResourceType.DRL );
        kbuilder.add( ResourceFactory.newClassPathResource("org/drools/mvel/integrationtests/test_QueryMemoryLeak.drl"),
                      ResourceType.DRL );

        assertFalse( kbuilder.getErrors().toString(),
                     kbuilder.hasErrors() );

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages( kbuilder.getKnowledgePackages() );
        
        KieSession ksession = createKnowledgeSession(kbase);
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
