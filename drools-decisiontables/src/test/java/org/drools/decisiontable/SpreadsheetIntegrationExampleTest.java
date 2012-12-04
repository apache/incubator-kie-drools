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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.acme.insurance.launcher.PricingRuleLauncher;
import org.junit.Test;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.DecisionTableConfiguration;
import org.kie.builder.DecisionTableInputType;
import org.kie.builder.KieFactory;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KieServices;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.builder.Results;
import org.kie.io.Resource;
import org.kie.io.ResourceFactory;
import org.kie.runtime.KieSession;
import org.kie.runtime.StatefulKnowledgeSession;

public class SpreadsheetIntegrationExampleTest {

    @Test
    public void testExecuteUsingKieAPI() throws Exception {
        // get the resource
        Resource dt = ResourceFactory.newClassPathResource( "/data/IntegrationExampleTest.xls", getClass() );
        
        // create the builder
        KieSession ksession = getKieSession( "src/main/resources/IntegrationExampleTest.xls", dt );

        ksession.insert( new Cheese( "stilton",
                                    42 ) );
        ksession.insert( new Person( "michael",
                                    "stilton",
                                    42 ) );
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                           list );
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertEquals( "Old man stilton",
                      list.get( 0 ) );
    }

    private KieSession getKieSession(String name, Resource dt) {
        KieServices ks = KieServices.Factory.get();
        KieFactory kf = KieFactory.Factory.get();
        
        KieFileSystem kfs = kf.newKieFileSystem().write( name, dt );
        Results results = ks.newKieBuilder( kfs ).build();
        assertTrue( results.getInsertedMessages().isEmpty() );

        // get the session
        KieSession ksession = ks.getKieContainer(ks.getKieRepository().getDefaultGAV()).getKieSession();
        return ksession;
    }

    @Test
    public void testExecuteJBRULES3005() throws Exception {
        Resource dt = ResourceFactory.newClassPathResource( "/data/IntegrationExampleTest.xls", getClass() );
        KieSession ksession = getKieSession( "src/main/resources/IntegrationExampleTest.xls", dt );

        //ASSERT AND FIRE
        ksession.insert( new Cheese( "stilton",
                                    42 ) );
        ksession.insert( new Person( "michael",
                                    "stilton",
                                    42 ) );
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                           list );
        ksession.fireAllRules();
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
