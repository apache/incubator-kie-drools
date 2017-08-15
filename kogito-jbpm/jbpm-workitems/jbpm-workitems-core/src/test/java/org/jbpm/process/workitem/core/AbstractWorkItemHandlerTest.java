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

package org.jbpm.process.workitem.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.*;

public class AbstractWorkItemHandlerTest {

    @Test
    public void testServiceInvocationWithMultipleIntParams() throws Exception {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                                                              new CustomWorkItemHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("HR.test",
                                                                                                  params);

        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
    }

    private static KieBase readKnowledgeBase() throws Exception {
        ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-TwoUserTasks.bpmn2"),
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

    private class CustomWorkItemHandler extends AbstractWorkItemHandler {

        public CustomWorkItemHandler(KieSession ksession) {
            super(ksession);
        }

        @Override
        public void executeWorkItem(WorkItem workItem,
                                    WorkItemManager manager) {
            NodeInstance nodeInstance = getNodeInstance(workItem);
            System.out.println("Node instance " + nodeInstance + " for work item " + workItem);
            manager.completeWorkItem(workItem.getId(),
                                     null);
        }

        @Override
        public void abortWorkItem(WorkItem workItem,
                                  WorkItemManager manager) {

        }
    }
}
