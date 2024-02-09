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
package org.drools.decisiontable;

import java.util.ArrayList;
import java.util.List;

import org.acme.insurance.launcher.PricingRuleLauncher;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

import static org.assertj.core.api.Assertions.assertThat;

public class SpreadsheetIntegrationExampleTest {
    
    private KieSession ksession;

    @After
    public void tearDown() {
        if (ksession != null) {
            ksession.dispose();
        }
    }

    @Test
    public void testExecuteUsingKieAPI() throws Exception {
        // get the resource
        Resource dt = ResourceFactory.newClassPathResource("/data/IntegrationExampleTest.drl.xls", getClass());
        
        // create the builder
        ksession = getKieSession(dt);

        ksession.insert(new Cheese("stilton", 42));
        ksession.insert(new Person("michael", "stilton", 42));
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        
        ksession.fireAllRules();
       
        assertThat(list).hasSize(1).containsExactly("Old man stilton");
    }

    private KieSession getKieSession(Resource dt) {
        KieServices ks = KieServices.Factory.get();

        KieFileSystem kfs = ks.newKieFileSystem().write(dt);
        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        
        assertThat(kb.getResults().getMessages()).isEmpty();

        // get the session
        return ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
    }

    @Test
    public void testExecuteJBRULES3005() throws Exception {
        Resource dt = ResourceFactory.newClassPathResource("/data/IntegrationExampleTest.drl.xls", getClass());
        ksession = getKieSession(dt);

        //ASSERT AND FIRE
        ksession.insert(new Cheese("stilton", 42));
        ksession.insert(new Person("michael", "stilton", 42));
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        
        ksession.fireAllRules();
        
        assertThat(list).hasSize(1).containsExactly("Old man stilton");
    }
    
    @Test 
    public void testNamedWorksheet() throws Exception {
        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType(DecisionTableInputType.XLS);
        dtconf.setWorksheetName("Tables_2");

        Resource dt = ResourceFactory.newClassPathResource("/data/IntegrationExampleTest.drl.xls", getClass())
                                     .setConfiguration(dtconf);
        ksession = getKieSession(dt);

        //ASSERT AND FIRE
        ksession.insert(new Cheese("cheddar", 42));
        ksession.insert(new Person("michael", "stilton", 25));
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();
        
        assertThat(list).hasSize(1).containsExactly("Young man cheddar");
    }

    /**
     * A smoke test mainly.
     */
    @Test
    public void testInsuranceExample() throws Exception {
        PricingRuleLauncher launcher = new PricingRuleLauncher();
        assertThat(launcher.executeExample()).isEqualTo(120);
    }

    @Test
    public void testBooleanField() throws Exception {
        Resource dt = ResourceFactory.newClassPathResource("/data/ShopRules.drl.xls", getClass());
        ksession = getKieSession(dt);

        Person p = new Person("michael", "stilton", 42);
        ksession.insert(p);

        ksession.fireAllRules();

        assertThat(p.getCanBuyAlcohol()).isTrue();
    }

    @Test
    public void testHeadingWhitespace() throws Exception {
        System.setProperty("drools.trimCellsInDTable", "false");
        try {
            Resource dt = ResourceFactory.newClassPathResource("/data/HeadingWhitespace.drl.xls", getClass());
            ksession = getKieSession(dt);

            Person p = new Person(" me");
            ksession.insert(p);

            ksession.fireAllRules();

            assertThat(p.getCanBuyAlcohol()).isTrue();
        } finally {
            System.clearProperty("drools.trimCellsInDTable");
        }
    }

    @Test
    public void testPackageName() throws Exception {
        // DROOLS-4967
        KieServices ks = KieServices.get();

        KieModuleModel kmodel = ks.newKieModuleModel();
        kmodel.newKieBaseModel("kbase1")
                .addPackage("org.drools.simple.candrink")
                .setDefault(true);

        KieBase kbase = new KieHelper().setKieModuleModel(kmodel)
                .addResource(ks.getResources().newClassPathResource("/data/CanNotDrink2.drl.xls", getClass()), ResourceType.DTABLE)
                .build();

        assertThat(kbase.getKiePackage("org.drools.simple.candrink").getRules()).hasSize(2);
    }
}
