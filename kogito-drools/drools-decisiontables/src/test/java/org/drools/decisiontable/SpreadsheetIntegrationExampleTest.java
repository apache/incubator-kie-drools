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

package org.drools.decisiontable;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.acme.insurance.launcher.PricingRuleLauncher;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;

public class SpreadsheetIntegrationExampleTest {

    @Test
    public void testExecute() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType( DecisionTableInputType.XLS );

        kbuilder.add( ResourceFactory.newClassPathResource( "/data/IntegrationExampleTest.xls", getClass() ),
                              ResourceType.DTABLE,
                              dtconf );

        assertFalse( kbuilder.hasErrors() );

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        //ASSERT AND FIRE
        session.insert( new Cheese( "stilton",
                                    42 ) );
        session.insert( new Person( "michael",
                                    "stilton",
                                    42 ) );
        final List<String> list = new ArrayList<String>();
        session.setGlobal( "list",
                           list );
        session.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertEquals( "Old man stilton",
                      list.get( 0 ) );
    }

    @Test
    public void testExecuteJBRULES3005() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.add( ResourceFactory.newClassPathResource( "/data/IntegrationExampleTest.xls", getClass() ),
                              ResourceType.DTABLE );

        assertFalse( kbuilder.hasErrors() );

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        //ASSERT AND FIRE
        session.insert( new Cheese( "stilton",
                                    42 ) );
        session.insert( new Person( "michael",
                                    "stilton",
                                    42 ) );
        final List<String> list = new ArrayList<String>();
        session.setGlobal( "list",
                           list );
        session.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertEquals( "Old man stilton",
                      list.get( 0 ) );
    }
    
    @Test
    public void testNamedWorksheet() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType( DecisionTableInputType.XLS );
        dtconf.setWorksheetName( "Tables_2" );

        kbuilder.add( ResourceFactory.newClassPathResource( "/data/IntegrationExampleTest.xls", getClass() ),
                              ResourceType.DTABLE,
                              dtconf );

        assertFalse( kbuilder.hasErrors() );

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        //ASSERT AND FIRE
        session.insert( new Cheese( "cheddar",
                                    42 ) );
        session.insert( new Person( "michael",
                                    "stilton",
                                    25 ) );
        final List<String> list = new ArrayList<String>();
        session.setGlobal( "list",
                           list );
        session.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertEquals( "Young man cheddar",
                      list.get( 0 ) );
    }

    /**
     * A smoke test mainly.
     */
    @Test
    public void testInsuranceExample() throws Exception {
        PricingRuleLauncher launcher = new PricingRuleLauncher();
        assertEquals( 120,
                      launcher.executeExample() );
    }

}
