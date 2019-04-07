/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.workitem.bpmn2;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.bpmn2.objects.Person;
import org.jbpm.process.workitem.core.TestWorkItemManager;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.event.kiebase.AfterRuleAddedEvent;
import org.kie.api.event.kiebase.DefaultKieBaseEventListener;
import org.kie.api.io.Resource;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;

public class BusinessRuleTaskHandlerTest {

    private static final String GROUP_ID = "org.jbpm";
    private static final String ARTIFACT_ID = "test-kjar";
    private static final String VERSION = "1.0";

    private KieServices ks = KieServices.Factory.get();

    @Before
    public void setup() throws Exception {

        createAndDeployJar(ks,
                           ks.newReleaseId(GROUP_ID,
                                           ARTIFACT_ID,
                                           VERSION),
                           ks.getResources().newClassPathResource("businessRule.drl"),
                           ks.getResources().newClassPathResource("0020-vacation-days.dmn"));
    }

    @Test
    public void testDrlStatefulBusinessRuleTaskNoScanner() {
        TestWorkItemManager manager = new TestWorkItemManager();
        BusinessRuleTaskHandler handler = new BusinessRuleTaskHandler(GROUP_ID,
                                                                      ARTIFACT_ID,
                                                                      VERSION);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setId(999);

        Person person = new Person("john");
        workItem.setParameter("person",
                              person);
        workItem.setParameter("KieSessionType",
                              BusinessRuleTaskHandler.STATEFULL_TYPE);

        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = manager.getResults(workItem.getId());
        assertNotNull(results);
        assertEquals(1,
                     results.size());
        assertEquals(35,
                     ((Person) results.get("person")).getAge().intValue());
    }

    @Test
    public void testDrlStatelessBusinessRuleTaskNoScanner() {
        TestWorkItemManager manager = new TestWorkItemManager();
        BusinessRuleTaskHandler handler = new BusinessRuleTaskHandler(GROUP_ID,
                                                                      ARTIFACT_ID,
                                                                      VERSION);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setId(999);

        Person person = new Person("john");
        workItem.setParameter("person",
                              person);

        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = manager.getResults(workItem.getId());
        assertNotNull(results);
        assertEquals(1,
                     results.size());
        assertEquals(35,
                     ((Person) results.get("person")).getAge().intValue());
    }

    @Test
    public void testDmnBusinessRuleTaskNoScanner() {
        TestWorkItemManager manager = new TestWorkItemManager();
        BusinessRuleTaskHandler handler = new BusinessRuleTaskHandler(GROUP_ID,
                                                                      ARTIFACT_ID,
                                                                      VERSION);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setId(999);

        workItem.setParameter("Language",
                              BusinessRuleTaskHandler.DMN_LANG);

        workItem.setParameter("Namespace",
                              "https://www.drools.org/kie-dmn");
        workItem.setParameter("Model",
                              "0020-vacation-days");

        workItem.setParameter("Age",
                              16);
        workItem.setParameter("Years of Service",
                              1);

        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = manager.getResults(workItem.getId());
        assertNotNull(results);
        assertEquals(7,
                     results.size());
        assertEquals(27,
                     ((BigDecimal) results.get("Total Vacation Days")).intValue());
    }

    @Ignore("ignored as it is unstable on jenkins for unknown reason")
    @Test
    public void testDrlStatefulBusinessRuleTaskWithScanner() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        BusinessRuleTaskHandler handler = new BusinessRuleTaskHandler(GROUP_ID,
                                                                      ARTIFACT_ID,
                                                                      VERSION,
                                                                      2000);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setId(999);

        Person person = new Person("john");
        workItem.setParameter("person",
                              person);
        workItem.setParameter("KieSessionType",
                              BusinessRuleTaskHandler.STATEFULL_TYPE);

        handler.executeWorkItem(workItem,
                                manager);

        Map<String, Object> results = manager.getResults(workItem.getId());
        assertNotNull(results);
        assertEquals(1,
                     results.size());
        assertEquals(35,
                     ((Person) results.get("person")).getAge().intValue());

        // build and deploy new version
        createAndDeployJar(ks,
                           ks.newReleaseId(GROUP_ID,
                                           ARTIFACT_ID,
                                           VERSION),
                           ks.getResources().newClassPathResource("businessRule2.drl"));
        // setup waiting mechanism to wait for scanner update
        CountDownLatch latch = new CountDownLatch(1);
        handler.getKieContainer().getKieBase().addEventListener(new DefaultKieBaseEventListener() {

            @Override
            public void afterRuleAdded(AfterRuleAddedEvent event) {
                latch.countDown();
            }
        });

        latch.await(10,
                    TimeUnit.SECONDS);

        person = new Person("john");
        workItem.setParameter("person",
                              person);
        workItem.setParameter("KieSessionType",
                              BusinessRuleTaskHandler.STATEFULL_TYPE);

        handler.executeWorkItem(workItem,
                                manager);

        results = manager.getResults(workItem.getId());
        assertNotNull(results);
        assertEquals(1,
                     results.size());
        assertEquals(45,
                     ((Person) results.get("person")).getAge().intValue());
    }

    /*
     * Helper methods
     */

    protected byte[] createAndDeployJar(KieServices ks,
                                        ReleaseId releaseId,
                                        Resource... resources) throws Exception {
        KieFileSystem kfs = ks.newKieFileSystem().generateAndWritePomXML(releaseId);
        for (int i = 0; i < resources.length; i++) {
            if (resources[i] != null) {
                kfs.write(resources[i]);
            }
        }
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        ((InternalKieBuilder) kieBuilder).buildAll(o -> true);
        Results results = kieBuilder.getResults();
        if (results.hasMessages(Message.Level.ERROR)) {
            throw new IllegalStateException(results.getMessages(Message.Level.ERROR).toString());
        }
        InternalKieModule kieModule = (InternalKieModule) ks.getRepository().getKieModule(releaseId);
        byte[] pomXmlContent = IOUtils.toByteArray(kieModule.getPomAsStream());
        File pom = new File("target",
                            UUID.randomUUID().toString());
        Files.write(pom.toPath(),
                    pomXmlContent);
        KieMavenRepository.getKieMavenRepository().installArtifact(releaseId,
                                                                   kieModule,
                                                                   pom);

        byte[] jar = kieModule.getBytes();
        return jar;
    }
}

