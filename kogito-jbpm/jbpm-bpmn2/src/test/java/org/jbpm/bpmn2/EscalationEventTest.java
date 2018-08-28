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

package org.jbpm.bpmn2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class EscalationEventTest extends JbpmBpmn2TestCase {

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };

    private Logger logger = LoggerFactory
            .getLogger(EscalationEventTest.class);

    private KieSession ksession;
    
    public EscalationEventTest(boolean persistence) {
        super(persistence);
    }

    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
    }

    @After
    public void dispose() {
        if (ksession != null) {
            ksession.dispose();
            ksession = null;
        }
    }

    private ProcessEventListener LOGGING_EVENT_LISTENER = new DefaultProcessEventListener() {

        @Override
        public void afterNodeLeft(ProcessNodeLeftEvent event) {
            logger.info("After node left {}", event.getNodeInstance().getNodeName());
        }

        @Override
        public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
            logger.info("After node triggered {}", event.getNodeInstance().getNodeName());
        }

        @Override
        public void beforeNodeLeft(ProcessNodeLeftEvent event) {
            logger.info("Before node left {}", event.getNodeInstance().getNodeName());
        }

        @Override
        public void beforeNodeTriggered(ProcessNodeTriggeredEvent event) {
            logger.info("Before node triggered {}", event.getNodeInstance().getNodeName());
        }

    };
    
    @Test
    public void testEventSubprocessEscalation() throws Exception {
        KieBase kbase = createKnowledgeBase("escalation/BPMN2-EventSubprocessEscalation.bpmn2");
        final List<Long> executednodes = new ArrayList<Long>();
        ProcessEventListener listener = new DefaultProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add(event.getNodeInstance().getId());
                }
            }

        };
        ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        ProcessInstance processInstance = ksession
                .startProcess("BPMN2-EventSubprocessEscalation");
        assertProcessInstanceActive(processInstance);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(listener);

        WorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
        assertNodeTriggered(processInstance.getId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertEquals(1, executednodes.size());

    }

    @Test
    public void testEscalationBoundaryEvent() throws Exception {
        KieBase kbase = createKnowledgeBase("escalation/BPMN2-EscalationBoundaryEvent.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("EscalationBoundaryEvent");
        assertProcessInstanceCompleted(processInstance);
    }
    
    @Test
    public void testEscalationBoundaryEventInterrupting() throws Exception {
        KieBase kbase = createKnowledgeBase("escalation/BPMN2-EscalationBoundaryEventInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", handler);
        ProcessInstance processInstance = ksession.startProcess("EscalationBoundaryEvent");
        assertProcessInstanceCompleted(processInstance);
    }
    
    @Test
    @Ignore( "Escalation does not cancel work items yet.")
    // TODO: make escalation interrupt tasks -- or look more closely at the spec to make sure that's the case? 
    public void testEscalationBoundaryEventInterruptsTask() throws Exception {
        KieBase kbase = createKnowledgeBase("escalation/BPMN2-EscalationBoundaryEventInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", handler);
        ProcessInstance processInstance = ksession.startProcess("EscalationBoundaryEvent");
        assertProcessInstanceCompleted(processInstance);
        
        // Check for cancellation of task
        assertEquals( "WorkItem was not cancelled!", WorkItem.ABORTED, handler.getWorkItem().getState());
    }

    @Test
    public void testEscalationIntermediateThrowEventProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("escalation/BPMN2-IntermediateThrowEventEscalation.bpmn2");
        ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("EscalationIntermediateThrowEvent");
        assertProcessInstanceAborted(processInstance);
    } 
    

    @Test
    @Ignore( "General escalation is not yet supported.")
    // TODO: implement general escalation
    // TODO: implement asynchronous escalation
    public void testGeneralEscalationBoundaryEventWithTask() throws Exception {
        KieBase kbase = createKnowledgeBase("escalation/BPMN2-EscalationBoundaryEventWithTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "0");
        ProcessInstance processInstance = ksession.startProcess("non-interrupting-escalation", params);
       
        ksession.getWorkItemManager().completeWorkItem(handler.getWorkItem().getId(), null);
        assertProcessInstanceCompleted(processInstance);
        // Did escalation fire? 
        assertProcessVarValue(processInstance, "x", "1");
    }
    
    @Test
    public void testInterruptingEscalationBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("escalation/BPMN2-EscalationBoundaryEventOnTaskInterrupting.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ksession.addEventListener(LOGGING_EVENT_LISTENER);
        ProcessInstance processInstance = ksession.startProcess("BPMN2-EscalationBoundaryEventOnTask");

        List<WorkItem> workItems = handler.getWorkItems();
        assertEquals(2, workItems.size());

        WorkItem workItem = workItems.get(0);
        if (!"john".equalsIgnoreCase((String) workItem.getParameter("ActorId"))) {
            workItem = workItems.get(1);
        }

        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceFinished(processInstance, ksession);
    }
    
    @Test
    @Ignore( "Non interrupting escalation has not yet been implemented.")
    // TODO: implement non-interrupting escalation
    public void testNonInterruptingEscalationBoundaryEventOnTask() throws Exception {
        KieBase kbase = createKnowledgeBase("escalation/BPMN2-EscalationBoundaryEventOnTask.bpmn2");
        ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ksession.addEventListener(LOGGING_EVENT_LISTENER);
        ProcessInstance processInstance = ksession.startProcess("non-interrupting-escalation");

        List<WorkItem> workItems = handler.getWorkItems();
        assertEquals(2, workItems.size());

        WorkItem johnsWork = workItems.get(0);
        WorkItem marysWork = workItems.get(1);
        if (!"john".equalsIgnoreCase((String) johnsWork.getParameter("ActorId"))) {
            marysWork = johnsWork;
            johnsWork = workItems.get(1);
        }

        // end event after task triggers escalation 
        ksession.getWorkItemManager().completeWorkItem(johnsWork.getId(), null);
        
        // escalation should have run.. 
        
        // should finish process
        ksession.getWorkItemManager().completeWorkItem(marysWork.getId(), null);
        assertProcessInstanceCompleted(processInstance);
    }
    
    @Test
    public void testEscalationEndEventProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("escalation/BPMN2-EscalationEndEvent.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("EscalationEndEvent");
        assertProcessInstanceAborted(processInstance.getId(), ksession);
    }
    
    @Test
    public void testEscalationBoundaryEventAndIntermediate() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("escalation/BPMN2-EscalationWithDataMapping.bpmn2");
        ksession = createKnowledgeSession(kbase);
        Map<String, Object> sessionArgs = new HashMap<String, Object>();
        sessionArgs.put("Property_2", new java.lang.RuntimeException());
        ProcessInstance processInstance = ksession.startProcess("BPMN2BoundaryEscalationEventOnTask", sessionArgs);
        assertProcessInstanceCompleted(processInstance);
        assertProcessVarValue(processInstance, "Property_3", "java.lang.RuntimeException");
    }
    
    @Test
    public void testHandledEscalationEndEventProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("escalation/BPMN2-EscalationEndEventHandling.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("hello", 70);
        ProcessInstance processInstance = ksession.startProcess("helloWorld.Escalation", parameters);
        assertProcessInstanceFinished(processInstance, ksession);
    }
}
