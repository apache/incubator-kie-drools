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
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.Results;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.builder.InternalKieBuilder;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;

public class BusinessRuleTaskTest {

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
    public void testBusinessRuleTaskProcess() throws Exception {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = createSession(kbase);

        BusinessRuleTaskHandler handler = new BusinessRuleTaskHandler(GROUP_ID,
                                                                      ARTIFACT_ID,
                                                                      VERSION);
        ksession.getWorkItemManager().registerWorkItemHandler("BusinessRuleTask",
                                                              handler);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person",
                   new org.jbpm.process.workitem.bpmn2.objects.Person("john"));

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("evaluation.ruletask",
                                                                                                  params);
        org.jbpm.process.workitem.bpmn2.objects.Person variable = (org.jbpm.process.workitem.bpmn2.objects.Person) processInstance.getVariable("person");
        assertEquals("john",
                     variable.getName());
        assertEquals(35,
                     variable.getAge().intValue());
        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
    }

    @Test
    public void testDecisionTaskProcess() throws Exception {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = createSession(kbase);

        BusinessRuleTaskHandler handler = new BusinessRuleTaskHandler(GROUP_ID,
                                                                      ARTIFACT_ID,
                                                                      VERSION);
        ksession.getWorkItemManager().registerWorkItemHandler("DecisionTask",
                                                              handler);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("age",
                   16);
        params.put("yearsOfService",
                   1);

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("BPMN2-BusinessRuleTask",
                                                                                                  params);
        BigDecimal variable = (BigDecimal) processInstance.getVariable("vacationDays");

        assertEquals(27,
                     variable.intValue());
        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
    }

    private static KieBase readKnowledgeBase() throws Exception {
        ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("businessRuleTaskProcess.bpmn2"),
                     ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("businessRuleTaskDMN.bpmn2"),
                     ResourceType.BPMN2);
        return kbuilder.newKieBase();
    }

    private static KieSession createSession(KieBase kbase) {
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory",
                       "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory",
                       "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        return kbase.newKieSession(config,
                                   EnvironmentFactory.newEnvironment());
    }

    private byte[] createAndDeployJar(KieServices ks,
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

