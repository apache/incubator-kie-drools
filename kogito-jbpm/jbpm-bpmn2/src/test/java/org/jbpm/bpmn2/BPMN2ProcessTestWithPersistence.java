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
package org.jbpm.bpmn2;

import java.util.HashMap;
import java.util.Map;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderError;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.kie.io.ResourceFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkflowProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BPMN2ProcessTestWithPersistence extends JbpmBpmn2TestCase {

    private Logger logger = LoggerFactory.getLogger(SimpleBPMNProcessTest.class);
    
    public BPMN2ProcessTestWithPersistence() {
        super(true);
    }

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
//        enable this as soon as pull request #98 is in as it fixes subprocess instance creation          
//        List<ProcessInstanceLog> subprocesses = JPAProcessInstanceDbLog.findSubProcessInstances(processInstance.getId());
//        assertNotNull(subprocesses);
//        assertEquals(1, subprocesses.size());
    }
}
