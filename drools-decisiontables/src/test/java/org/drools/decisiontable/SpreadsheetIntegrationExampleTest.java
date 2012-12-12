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

import org.acme.insurance.launcher.PricingRuleLauncher;
import org.junit.Test;
import org.kie.KieServices;
import org.kie.builder.DecisionTableConfiguration;
import org.kie.builder.DecisionTableInputType;
import org.kie.builder.KieBuilder;
import org.kie.builder.KieFileSystem;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.io.Resource;
import org.kie.io.ResourceFactory;
import org.kie.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SpreadsheetIntegrationExampleTest {

    @Test
    public void testExecuteUsingKieAPI() throws Exception {
        // get the resource
        Resource dt = ResourceFactory.newClassPathResource( "/data/IntegrationExampleTest.xls", getClass() );
        
        // create the builder
        KieSession ksession = getKieSession( dt );

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

    private KieSession getKieSession(Resource dt) {
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write( dt );
        KieBuilder kb = ks.newKieBuilder( kfs ).buildAll();
        assertTrue( kb.getResults().getMessages().isEmpty() );

        // get the session
        KieSession ksession = ks.newKieContainer(ks.getRepository().getDefaultGAV()).newKieSession();
        return ksession;
    }

    @Test
    public void testExecuteJBRULES3005() throws Exception {
        Resource dt = ResourceFactory.newClassPathResource( "/data/IntegrationExampleTest.xls", getClass() );
        KieSession ksession = getKieSession( dt );

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
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType( DecisionTableInputType.XLS );
        dtconf.setWorksheetName( "Tables_2" );

        Resource dt = ResourceFactory.newClassPathResource( "/data/IntegrationExampleTest.xls", getClass() )
                                     .setConfiguration( dtconf );
        KieSession ksession = getKieSession( dt );

        //ASSERT AND FIRE
        ksession.insert( new Cheese( "cheddar",
                                    42 ) );
        ksession.insert( new Person( "michael",
                                    "stilton",
                                    25 ) );
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                           list );
        ksession.fireAllRules();
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
