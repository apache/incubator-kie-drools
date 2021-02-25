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

import org.assertj.core.api.Assertions;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.kogito.internal.process.event.DefaultKogitoProcessEventListener;
import org.kie.kogito.internal.process.event.KogitoProcessEventListener;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoWorkItem;

public class CompensationTest extends JbpmBpmn2TestCase {

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

    @BeforeEach
    public void prepare() {
        clearHistory();
    }

    /**
     * TESTS
     */

    @Test
    public void compensationViaIntermediateThrowEventProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("compensation/BPMN2-Compensation-IntermediateThrowEvent.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "0");
        KogitoProcessInstance processInstance = kruntime.startProcess("CompensateIntermediateThrowEvent", params);

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getStringId(), null);

        // compensation activity (assoc. with script task) signaled *after* script task
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        assertProcessVarValue(processInstance, "x", "1");
    }

    @Test
    public void compensationTwiceViaSignal() throws Exception {
        kruntime = createKogitoProcessRuntime("compensation/BPMN2-Compensation-IntermediateThrowEvent.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "0");
        String processId = "CompensateIntermediateThrowEvent";
        KogitoProcessInstance processInstance = kruntime.startProcess(processId, params);

        // twice
        kruntime.signalEvent("Compensation", CompensationScope.IMPLICIT_COMPENSATION_PREFIX + processId, processInstance.getStringId());
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getStringId(), null);

        // compensation activity (assoc. with script task) signaled *after* script task
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        assertProcessVarValue(processInstance, "x", "2");
    }

    @Test
    public void compensationViaEventSubProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("compensation/BPMN2-Compensation-EventSubProcess.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "0");
        KogitoProcessInstance processInstance = kruntime.startProcess("CompensationEventSubProcess", params);

        assertProcessInstanceActive(processInstance.getStringId(), kruntime);
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getStringId(), null);
        assertProcessVarValue(processInstance, "x", "1");
    }

    @Test
    public void compensationOnlyAfterAssociatedActivityHasCompleted() throws Exception {
        kruntime = createKogitoProcessRuntime("compensation/BPMN2-Compensation-UserTaskBeforeAssociatedActivity.bpmn2");
        kruntime.getProcessEventManager().addEventListener(LOGGING_EVENT_LISTENER);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "0");
        KogitoProcessInstance processInstance = kruntime.startProcess("CompensateIntermediateThrowEvent", params);

        // should NOT cause compensation since compensated activity has not yet completed (or started)! 
        kruntime.signalEvent("Compensation", "_3", processInstance.getStringId());

        // user task -> script task (associated with compensation) --> intermeidate throw compensation event
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getStringId(), null);
        // compensation activity (assoc. with script task) signaled *after* to-compensate script task
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        assertProcessVarValue(processInstance, "x", "1");
    }

    @Test
    public void orderedCompensation() throws Exception {
        kruntime = createKogitoProcessRuntime("compensation/BPMN2-Compensation-ParallelOrderedCompensation-IntermediateThrowEvent.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "");
        KogitoProcessInstance processInstance = kruntime.startProcess("CompensateParallelOrdered", params);
        List<KogitoWorkItem> workItems = workItemHandler.getWorkItems();
        List<String> workItemIds = new ArrayList<>();
        for (KogitoWorkItem workItem : workItems) {
            if ("Thr".equals(workItem.getParameter("NodeName"))) {
                workItemIds.add(workItem.getStringId());
            }
        }
        for (KogitoWorkItem workItem : workItems) {
            if ("Two".equals(workItem.getParameter("NodeName"))) {
                workItemIds.add(workItem.getStringId());
            }
        }
        for (KogitoWorkItem workItem : workItems) {
            if ("One".equals(workItem.getParameter("NodeName"))) {
                workItemIds.add(workItem.getStringId());
            }
        }
        for (String id : workItemIds) {
            kruntime.getKogitoWorkItemManager().completeWorkItem(id, null);
        }

        // user task -> script task (associated with compensation) --> intermeidate throw compensation event
        String xVal = getProcessVarValue(processInstance, "x");
        // Compensation happens in the *REVERSE* order of completion
        // Ex: if the order is 3, 17, 282, then compensation should happen in the order of 282, 17, 3
        // Compensation did not fire in the same order as the associated activities completed.
        Assertions.assertThat(xVal).isEqualTo("_171:_131:_141:_151:");
    }

    @Test
    public void compensationInSubSubProcesses() throws Exception {
        kruntime = createKogitoProcessRuntime("compensation/BPMN2-Compensation-InSubSubProcess.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "0");
        KogitoProcessInstance processInstance = kruntime.startProcess("CompensateSubSubSub", params);

        kruntime.signalEvent("Compensation", "_C-2", processInstance.getStringId());

        kruntime.getKogitoWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getStringId(), null);
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getStringId(), null);
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getStringId(), null);

        // compensation activity (assoc. with script task) signaled *after* script task
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        assertProcessVarValue(processInstance, "x", "2");
    }

    @Test
    public void specificCompensationOfASubProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("compensation/BPMN2-Compensation-ThrowSpecificForSubProcess.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", 1);
        KogitoProcessInstance processInstance = kruntime.startProcess("CompensationSpecificSubProcess", params);

        // compensation activity (assoc. with script task) signaled *after* to-compensate script task
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);

        assertProcessVarValue(processInstance, "x", null);
    }

    @Test
    @Disabled
    public void compensationViaCancellation() throws Exception {
        kruntime = createKogitoProcessRuntime("compensation/BPMN2-Compensation-IntermediateThrowEvent.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        Map<String, Object> params = new HashMap<>();
        params.put("x", "0");
        KogitoProcessInstance processInstance = kruntime.startProcess("CompensateIntermediateThrowEvent", params);

        kruntime.signalEvent("Cancel", null, processInstance.getStringId());
        kruntime.getKogitoWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getStringId(), null);

        // compensation activity (assoc. with script task) signaled *after* script task
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        assertProcessVarValue(processInstance, "x", "1");
    }

    @Test
    public void compensationInvokingSubProcess() throws Exception {
        kruntime = createKogitoProcessRuntime("compensation/BPMN2-UserTaskCompensation.bpmn2");
        kruntime.getKogitoWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<>();
        params.put("compensation", "True");
        KogitoProcessInstance processInstance = kruntime.startProcess("UserTaskCompensation", params);

        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
        assertProcessVarValue(processInstance, "compensation", "compensation");
    }

    /**
     * Test to demonstrate that Compensation Events work with Reusable
     * Subprocesses
     *
     * @throws Exception
     */
    @Test
    public void compensationWithReusableSubprocess() throws Exception {
        kruntime = createKogitoProcessRuntime("compensation/BPMN2-Booking.bpmn2",
                "compensation/BPMN2-BookResource.bpmn2", "compensation/BPMN2-CancelResource.bpmn2");
        KogitoProcessInstance processInstance = kruntime.startProcess("Booking");
        assertProcessInstanceCompleted(processInstance.getStringId(), kruntime);
    }

}
