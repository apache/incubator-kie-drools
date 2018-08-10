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

package org.jbpm.test.regression.event;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import qa.tools.ikeeper.annotation.BZ;

public class TimerEventTest extends JbpmTestCase {

    private static final String EXCEPTION_AFTER_TIMER = "org/jbpm/test/regression/event/TimerEvent-exceptionAfter.bpmn2";
    private static final String EXCEPTION_AFTER_TIMER_ID = "org.jbpm.test.regression.event.TimerEvent-exceptionAfter";

    private static final String START_TIMER_CYCLE = "org/jbpm/test/regression/event/TimerEvent-startTimerCycle.bpmn2";
    private static final String START_TIMER_CYCLE_ID = "org.jbpm.test.regression.event.TimerEvent-startTimerCycle";

    private static final String CANCELLED_TIMER = "org/jbpm/test/regression/event/TimerEvent-cancelledTimer.bpmn";
    private static final String CANCELLED_TIMER_ID = "org.jbpm.test.regression.event.TimerEvent-cancelledTimer";

    private static final String TIMER_AND_GATEWAY = "org/jbpm/test/regression/event/TimerEvent-timerAndGateway.bpmn";
    private static final String TIMER_AND_GATEWAY_ID = "org.jbpm.test.regression.event.TimerEvent-timerAndGateway";

    public static final String BOUNDARY_MULTIPLE_INSTANCES =
            "org/jbpm/test/regression/event/TimerEvent-boundaryMultipleInstances.bpmn2";
    public static final String BOUNDARY_MULTIPLE_INSTANCES_ID =
            "org.jbpm.test.regression.event.TimerEvent-boundaryMultipleInstances";

    @Test
    @BZ({"958390", "1167738"})
    public void testRuntimeExceptionAfterTimer() throws InterruptedException {
        KieSession ksession = createKSession(EXCEPTION_AFTER_TIMER);
        ProcessInstance pi = ksession.startProcess(EXCEPTION_AFTER_TIMER_ID);
        Thread.sleep(3000);
        
        assertProcessInstanceActive(pi.getId());
        
        ksession.abortProcessInstance(pi.getId());
        assertProcessInstanceAborted(pi.getId());
    }

    @Test
    @BZ("1104563")
    public void testStartTimerCycle() throws InterruptedException {
        createKSession(START_TIMER_CYCLE); //this starts already the timer
        AuditService auditService = getLogService();

        Thread.sleep(5000);
        Assertions.assertThat(auditService.findProcessInstances()).hasSize(1);

        Thread.sleep(5000);
        Assertions.assertThat(auditService.findProcessInstances()).hasSize(2);

        Thread.sleep(5000);
        Assertions.assertThat(auditService.findProcessInstances()).hasSize(3);
    }

    @Test
    @BZ("1148304")
    public void testCancelledTimerNotScheduled() {
        for (int i = 0; i < 5; i++) {
            createRuntimeManager(Strategy.PROCESS_INSTANCE, (String) null, CANCELLED_TIMER);
            KieSession ksession = getRuntimeEngine().getKieSession();
            TaskService taskService = getRuntimeEngine().getTaskService();

            Map<String, Object> params = new HashMap<String, Object>();
            ProcessInstance pi = ksession.startProcess(CANCELLED_TIMER_ID, params);
            System.out.println("A process instance started : pid = " + pi.getId());

            List<Long> list = taskService.getTasksByProcessInstanceId(pi.getId());
            for (long taskId : list) {
                Task task = taskService.getTaskById(taskId);
                System.out.println("taskId = " + task.getId() + ", status = " + task.getTaskData().getStatus());
            }

            Date before = new Date();

            disposeRuntimeManager();

            // Check if engine did not waited for timer
            Date after = new Date();
            long seconds = (after.getTime() - before.getTime()) / 1000;
            Assertions.assertThat(seconds).as("Cancelled timer has been scheduled").isLessThan(5);
        }
    }

    @Test
    @BZ("1036761")
    public void testTimerAndGateway() throws Exception {
        KieSession ksession = createKSession(TIMER_AND_GATEWAY);
        int sessionId = ksession.getId();

        TestAsyncWorkItemHandler handler1 = new TestAsyncWorkItemHandler();
        TestAsyncWorkItemHandler handler2 = new TestAsyncWorkItemHandler();

        ksession.getWorkItemManager().registerWorkItemHandler("task1", handler1);
        ksession.getWorkItemManager().registerWorkItemHandler("task2", handler2);

        ProcessInstance instance = ksession.createProcessInstance(TIMER_AND_GATEWAY_ID, new HashMap<String, Object>());
        ksession.startProcessInstance(instance.getId());

        WorkItem workItem1 = handler1.getWorkItem();
        Assertions.assertThat(workItem1).isNotNull();
        Assertions.assertThat(handler1.getWorkItem()).isNull();

        // first safe state: task1 completed
        ksession.getWorkItemManager().completeWorkItem(workItem1.getId(), null);

        ksession = restoreKSession(TIMER_AND_GATEWAY);
        ksession.getWorkItemManager().registerWorkItemHandler("task1", handler1);
        ksession.getWorkItemManager().registerWorkItemHandler("task2", handler2);
        // second safe state: timer completed, waiting on task2
        for (int i = 0; i < 7; i++) {
            Thread.sleep(1000);
        }

        WorkItem workItem2 = handler2.getWorkItem();
        // Both sides of the join are completed. But on the process instance, there are two JoinInstance for the same
        // Join, and since it is an AND join, it never reaches task2. It fails after the next assertion
        Assertions.assertThat(workItem2).isNotNull();
        Assertions.assertThat(handler1.getWorkItem()).isNull();
    }

    @Test
    @BZ("1213209")
    public void testBoundaryTimerInMultipleInstancesSubprocess() throws InterruptedException {
        KieSession ksession = createKSession(BOUNDARY_MULTIPLE_INSTANCES);
        ksession.setGlobal("counter", 0);

        Set<Integer> runList = new HashSet<Integer>();
        runList.add(1);
        runList.add(2);
        runList.add(3);

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("runList", runList);

        ksession.startProcess(BOUNDARY_MULTIPLE_INSTANCES_ID, parameters);

        // wait for 3x 50ms timers to be triggered
        Thread.sleep(1000);

        Integer counter = (Integer) ksession.getGlobal("counter");
        Assertions.assertThat(counter).isEqualTo(3);
    }

    private static class TestAsyncWorkItemHandler implements WorkItemHandler {

        private WorkItem workItem;
        private int activations = 0;

        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            System.out.println("Starting call to handler " + workItem.getName());
            this.workItem = workItem;
            this.activations++;
        }

        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            this.workItem = null;
        }

        public WorkItem getWorkItem() {
            WorkItem result = workItem;
            workItem = null;
            return result;
        }

        public int getActivations() {
            return activations;
        }

    }

}
