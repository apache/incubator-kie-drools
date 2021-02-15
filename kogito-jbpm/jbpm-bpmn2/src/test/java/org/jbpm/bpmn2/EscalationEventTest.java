/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.bpmn2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoNodeInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class EscalationEventTest extends JbpmBpmn2TestCase {

    private KogitoProcessEventListener LOGGING_EVENT_LISTENER = new DefaultKogitoProcessEventListener() {

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
        kruntime = createKogitoProcessRuntime("escalation/BPMN2-EventSubprocessEscalation.bpmn2");
        final List<String> executednodes = new ArrayList<>();
        KogitoProcessEventListener listener = new DefaultKogitoProcessEventListener() {

            @Override
            public void afterNodeLeft(ProcessNodeLeftEvent event) {
                if (event.getNodeInstance().getNodeName()
                        .equals("Script Task 1")) {
                    executednodes.add( (( KogitoNodeInstance ) event.getNodeInstance()).getStringId());
                }
            }

        };

        kruntime.getProcessEventManager().addEventListener(listener);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task",
                workItemHandler);
        KogitoProcessInstance processInstance = kruntime
                .startProcess("BPMN2-EventSubprocessEscalation");
        assertProcessInstanceActive(processInstance);
        kruntime.getProcessEventManager().addEventListener(listener);

        KogitoWorkItem workItem = workItemHandler.getWorkItem();
        assertNotNull(workItem);
        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
        assertNodeTriggered(processInstance.getStringId(), "start", "User Task 1",
                "end", "Sub Process 1", "start-sub", "Script Task 1", "end-sub");
        assertEquals(1, executednodes.size());

    }

    @Test
    public void testEscalationBoundaryEvent() throws Exception {
        kruntime = createKogitoProcessRuntime("escalation/BPMN2-EscalationBoundaryEvent.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("EscalationBoundaryEvent");
        assertProcessInstanceCompleted(processInstance);
    }
    
    @Test
    public void testEscalationBoundaryEventInterrupting() throws Exception {
        kruntime = createKogitoProcessRuntime("escalation/BPMN2-EscalationBoundaryEventInterrupting.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("EscalationBoundaryEvent");
        assertProcessInstanceCompleted(processInstance);
    }
    
    @Test
    @Disabled( "Escalation does not cancel work items yet.")
    // TODO: make escalation interrupt tasks -- or look more closely at the spec to make sure that's the case? 
    public void testEscalationBoundaryEventInterruptsTask() throws Exception {
        kruntime = createKogitoProcessRuntime("escalation/BPMN2-EscalationBoundaryEventInterrupting.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("MyTask", handler);
        KogitoProcessInstance processInstance = kruntime.startProcess("EscalationBoundaryEvent");
        assertProcessInstanceCompleted(processInstance);
        
        // Check for cancellation of task
        assertEquals(KogitoWorkItem.ABORTED, handler.getWorkItem().getState(), "WorkItem was not cancelled!");
    }

    @Test
    public void testEscalationIntermediateThrowEventProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("escalation/BPMN2-IntermediateThrowEventEscalation.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("EscalationIntermediateThrowEvent");
        assertProcessInstanceAborted(processInstance);
    } 
    

    @Test
    @Disabled("General escalation is not yet supported.")
    // TODO: implement general escalation
    // TODO: implement asynchronous escalation
    public void testGeneralEscalationBoundaryEventWithTask() throws Exception {
        kruntime = createKogitoProcessRuntime("escalation/BPMN2-EscalationBoundaryEventWithTask.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        
        Map<String, Object> params = new HashMap<>();
        params.put("x", "0");
        KogitoProcessInstance processInstance = kruntime.startProcess("non-interrupting-escalation", params);

        kruntime.getWorkItemManager().completeWorkItem(handler.getWorkItem().getStringId(), null);
        assertProcessInstanceCompleted(processInstance);
        // Did escalation fire? 
        assertProcessVarValue(processInstance, "x", "1");
    }
    
    @Test
    public void testInterruptingEscalationBoundaryEventOnTask() throws Exception {
        kruntime = createKogitoProcessRuntime("escalation/BPMN2-EscalationBoundaryEventOnTaskInterrupting.bpmn2");

        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        kruntime.getProcessEventManager().addEventListener(LOGGING_EVENT_LISTENER);
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2-EscalationBoundaryEventOnTask");

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertEquals(2, workItems.size());

        KogitoWorkItem workItem = workItems.get(0);
        if (!"john".equalsIgnoreCase((String) workItem.getParameter("ActorId"))) {
            workItem = workItems.get(1);
        }

        kruntime.getWorkItemManager().completeWorkItem(workItem.getStringId(), null);
        assertProcessInstanceFinished(processInstance, kruntime);
    }
    
    @Test
    @Disabled("Non interrupting escalation has not yet been implemented.")
    // TODO: implement non-interrupting escalation
    public void testNonInterruptingEscalationBoundaryEventOnTask() throws Exception {
        kruntime = createKogitoProcessRuntime("escalation/BPMN2-EscalationBoundaryEventOnTask.bpmn2");
        TestWorkItemHandler handler = new TestWorkItemHandler();
        kruntime.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        kruntime.getProcessEventManager().addEventListener(LOGGING_EVENT_LISTENER);
        KogitoProcessInstance processInstance = kruntime.startProcess("non-interrupting-escalation");

        List<KogitoWorkItem> workItems = handler.getWorkItems();
        assertEquals(2, workItems.size());

        KogitoWorkItem johnsWork = workItems.get(0);
        KogitoWorkItem marysWork = workItems.get(1);
        if (!"john".equalsIgnoreCase((String) johnsWork.getParameter("ActorId"))) {
            marysWork = johnsWork;
            johnsWork = workItems.get(1);
        }

        // end event after task triggers escalation 
        kruntime.getWorkItemManager().completeWorkItem(johnsWork.getStringId(), null);
        
        // escalation should have run.. 
        
        // should finish process
        kruntime.getWorkItemManager().completeWorkItem(marysWork.getStringId(), null);
        assertProcessInstanceCompleted(processInstance);
    }
    
    @Test
    public void testEscalationEndEventProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("escalation/BPMN2-EscalationEndEvent.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("EscalationEndEvent");
        assertProcessInstanceAborted(processInstance.getStringId(), kruntime);
    }
    
    @Test
    public void testEscalationBoundaryEventAndIntermediate() throws Exception {
        kruntime = createKogitoProcessRuntime("escalation/BPMN2-EscalationWithDataMapping.bpmn2");
        Map<String, Object> sessionArgs = new HashMap<>();
        sessionArgs.put("Property_2", new java.lang.RuntimeException());
        KogitoProcessInstance processInstance = kruntime.startProcess("BPMN2BoundaryEscalationEventOnTask", sessionArgs);
        assertProcessInstanceCompleted(processInstance);
        assertProcessVarValue(processInstance, "Property_3", "java.lang.RuntimeException");
    }
    
    @Test
    public void testHandledEscalationEndEventProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("escalation/BPMN2-EscalationEndEventHandling.bpmn2");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("hello", 70);
        KogitoProcessInstance processInstance = kruntime.startProcess("helloWorld.Escalation", parameters);
        assertProcessInstanceFinished(processInstance, kruntime);
    }
}
