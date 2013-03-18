/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.bpmn2.persistence;

import java.util.*;

import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.bpmn2.SimpleBPMNProcessTest;
import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.api.io.ResourceType;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkflowProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPMN2ProcessTestWithPersistence extends JbpmBpmn2TestCase {

    private Logger logger = LoggerFactory.getLogger(SimpleBPMNProcessTest.class);
    
    public void testCallActivityWithHistoryLog() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
                .newKnowledgeBuilder();
        kbuilder.add(ResourceFactory
                .newClassPathResource("BPMN2-CallActivity.bpmn2"),
                ResourceType.BPMN2);
        kbuilder.add(ResourceFactory
                .newClassPathResource("BPMN2-CallActivitySubProcess.bpmn2"),
                ResourceType.BPMN2);
        if (!kbuilder.getErrors().isEmpty()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                logger.error(error.toString());
            }
            throw new IllegalArgumentException(
                    "Errors while parsing knowledge base");
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "oldValue");
        ProcessInstance processInstance = ksession.startProcess(
                "ParentProcess", params);
        assertTrue(processInstance.getState() == ProcessInstance.STATE_COMPLETED);
        assertEquals("new value",
                ((WorkflowProcessInstance) processInstance).getVariable("y"));

        List<ProcessInstanceLog> subprocesses = JPAProcessInstanceDbLog.findSubProcessInstances(processInstance.getId());
        assertNotNull(subprocesses);
        assertEquals(1, subprocesses.size());
    }
}
