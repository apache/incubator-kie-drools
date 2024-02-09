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
package org.drools.decisiontable.project;

import org.junit.After;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.KieResources;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class MultiKieBaseTest {
    
    private KieSession ksession;
    
    @After
    public void tearDown() {

        if (ksession != null) {
            ksession.dispose();
        }
    }

    @Test
    public void testOK() {
        KieServices ks = KieServices.get();
        KieResources kr = ks.getResources();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write("src/main/resources/org/drools/decisiontable/project/rules/Sample.drl",
                        kr.newFileSystemResource("src/test/resources/org/drools/decisiontable/project/rules/Sample.drl"))
                .write("src/main/resources/org/drools/decisiontable/project/dtable/CanDrink.drl.xls",
                        kr.newFileSystemResource("src/test/resources/org/drools/decisiontable/project/dtable/CanDrink.drl.xls"));

        KieModuleModel kproj = ks.newKieModuleModel();
        kproj.newKieBaseModel("rulesKB")
                .addPackage("org.drools.decisiontable.project.rules")
                .newKieSessionModel("rules");
        kproj.newKieBaseModel("dtblaleKB")
                .addPackage("org.drools.decisiontable.project.dtable")
                .newKieSessionModel("dtable");

        kfs.writeKModuleXML(kproj.toXML());

        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        KieContainer kc = ks.newKieContainer(kb.getKieModule().getReleaseId());

        ksession = kc.newKieSession("rules");
        Result res1 = new Result();
        ksession.insert(res1);
        ksession.insert(new Person("Mario", 45));
        ksession.fireAllRules();
        
        assertThat(res1.toString()).isEqualTo("Hello Mario");

        ksession = kc.newKieSession("dtable");
        Result res2 = new Result();
        ksession.insert(res2);
        ksession.insert(new Person("Mario", 45));
        
        ksession.fireAllRules();
        
        assertThat(res2.toString()).isEqualTo("Mario can drink");
    }

    @Test
    public void testWrongFolder() {
        KieServices ks = KieServices.get();
        KieResources kr = ks.getResources();

        KieFileSystem kfs = ks.newKieFileSystem()
                .write("src/main/resources/org/drools/decisiontable/projectwrong/rules/Sample.drl",
                        kr.newFileSystemResource("src/test/resources/org/drools/decisiontable/project/rules/Sample.drl"))
                .write("src/main/resources/org/drools/decisiontable/projectwrong/dtable/CanDrink.drl.xls",
                        kr.newFileSystemResource("src/test/resources/org/drools/decisiontable/project/dtable/CanDrink.drl.xls"));

        KieModuleModel kproj = ks.newKieModuleModel();
        kproj.newKieBaseModel("rulesKB")
                .addPackage("org.drools.decisiontable.projectwrong.rules")
                .newKieSessionModel("rules");
        kproj.newKieBaseModel("dtblaleKB")
                .addPackage("org.drools.decisiontable.projectwrong.dtable")
                .newKieSessionModel("dtable");

        kfs.writeKModuleXML(kproj.toXML());

        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        KieContainer kc = ks.newKieContainer(kb.getKieModule().getReleaseId());

        ksession = kc.newKieSession("rules");
        Result res1 = new Result();
        ksession.insert(res1);
        ksession.insert(new Person("Mario", 45));
        
        ksession.fireAllRules();
        
        assertThat(res1.toString()).isNull();

        ksession = kc.newKieSession("dtable");
        Result res2 = new Result();
        ksession.insert(res2);
        ksession.insert(new Person("Mario", 45));
        
        ksession.fireAllRules();
        
        assertThat(res2.toString()).isNull();
    }
}
