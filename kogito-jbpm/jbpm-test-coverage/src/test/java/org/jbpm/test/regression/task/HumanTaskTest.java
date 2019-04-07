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

package org.jbpm.test.regression.task;

import static org.jbpm.test.tools.TrackingListenerAssert.assertTriggered;
import static org.jbpm.test.tools.TrackingListenerAssert.assertTriggeredAndLeft;
import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.TrackingProcessEventListener;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

import qa.tools.ikeeper.annotation.BZ;

public class HumanTaskTest extends JbpmTestCase {

    private static final String BOUNDARY_TIMER = "org/jbpm/test/regression/task/HumanTask-boundaryTimer.bpmn2";
    private static final String BOUNDARY_TIMER_ID = "org.jbpm.test.regression.task.HumanTask-boundaryTimer";

    private static final String COMPLETION_ROLLBACK = "org/jbpm/test/regression/task/HumanTask-completionRollback.bpmn2";
    private static final String COMPLETION_ROLLBACK_ID = "org.jbpm.test.regression.task.HumanTask-completionRollback";

    private static final String ON_ENTRY_SCRIPT_EXCEPTION = "org/jbpm/test/regression/task/HumanTask-onEntryScriptException.bpmn2";
    private static final String ON_ENTRY_SCRIPT_EXCEPTION_ID = "org.jbpm.test.regression.task.HumanTask-onEntryScriptException";

    private static final String ON_EXIT_SCRIPT_EXCEPTION = "org/jbpm/test/regression/task/HumanTask-onExitScriptException.bpmn2";
    private static final String ON_EXIT_SCRIPT_EXCEPTION_ID = "org.jbpm.test.regression.task.HumanTask-onExitScriptException";

    private static final String ABORT_WORKITEM_TASK_STATUS = "org/jbpm/test/regression/task/HumanTask-abortWorkItemTaskStatus.bpmn2";
    private static final String ABORT_WORKITEM_TASK_STATUS_ID = "org.jbpm.test.regression.task.HumanTask-abortWorkItemTaskStatus";

    private static final String LOCALE = "org/jbpm/test/regression/task/HumanTask-locale.bpmn2";
    private static final String LOCALE_ID = "org.jbpm.test.regression.task.HumanTask-locale";

    private static final String INPUT_TRANSFORMATION = "org/jbpm/test/regression/task/HumanTask-inputTransformation.bpmn2";
    private static final String INPUT_TRANSFORMATION_ID = "org.jbpm.test.regression.task.HumanTask-inputTransformation";

    @Test
    @BZ("958397")
    public void testBoundaryTimer() throws Exception {
        createRuntimeManager(BOUNDARY_TIMER);
        KieSession ksession = getRuntimeEngine().getKieSession();
        TaskService taskService = getRuntimeEngine().getTaskService();

        TrackingProcessEventListener tpel = new TrackingProcessEventListener();
        ksession.addEventListener(tpel);
        ProcessInstance pi = ksession.startProcess(BOUNDARY_TIMER_ID);

        // wait for timer
        String endNodeName = "End1";
        assertTrue( "Node '" + endNodeName + "' was not triggered on time!", tpel.waitForNodeTobeTriggered(endNodeName, 2000));

        assertTriggeredAndLeft(tpel, "Script1");
        assertTriggered(tpel, endNodeName);

        long taskId = taskService.getTasksByProcessInstanceId(pi.getId()).get(0);
        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);

        assertTriggeredAndLeft(tpel, "Script2");
        assertTriggered(tpel, "End2");

        assertProcessInstanceCompleted(pi.getId());
    }

    @Test
    @BZ("1004681")
    public void testCompletionRollback() {
        createRuntimeManager(COMPLETION_ROLLBACK);
        TaskService taskService = getRuntimeEngine().getTaskService();
        KieSession ksession = getRuntimeEngine().getKieSession();

        ProcessInstance pi = ksession.startProcess(COMPLETION_ROLLBACK_ID);
        logger.debug("Process with id = " + pi.getId() + " has just been started.");

        List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        long taskId = taskList.get(0).getId();
        taskService.start(taskId, "john");

        Task task = taskService.getTaskById(taskId);
        logger.debug("Actual task status: " + task.getTaskData().getStatus());

        try {
            taskService.complete(taskId, "john", null);
            Assertions.fail("Exception should have been thrown from the process script task.");
        } catch (Exception ex) {
            // exception thrown in process script task is intentional
        }
        disposeRuntimeManager();

        createRuntimeManager(COMPLETION_ROLLBACK);
        taskService = getRuntimeEngine().getTaskService();
        Status status = taskService.getTaskById(taskId).getTaskData().getStatus();
        Assertions.assertThat(status).as("Task completion has not been rolled back!").isEqualTo(Status.InProgress);
    }

    @Test
    @BZ("1120122")
    public void testOnEntryScriptException() {
        createRuntimeManager(ON_ENTRY_SCRIPT_EXCEPTION);
        KieSession ksession = getRuntimeEngine().getKieSession();
        TaskService taskService = getRuntimeEngine().getTaskService();

        long pid = ksession.startProcess(ON_ENTRY_SCRIPT_EXCEPTION_ID).getId();

        List<Long> tasks = taskService.getTasksByProcessInstanceId(pid);
        Assertions.assertThat(tasks).hasSize(1);

        Task task = taskService.getTaskById(tasks.get(0));
        Assertions.assertThat(task.getNames().get(0).getText()).isEqualTo("Human task 2");
    }

    @Test
    @BZ("1120122")
    public void testOnExitScriptException() {
        createRuntimeManager(ON_EXIT_SCRIPT_EXCEPTION);
        KieSession ksession = getRuntimeEngine().getKieSession();
        TaskService taskService = getRuntimeEngine().getTaskService();

        long pid = ksession.startProcess(ON_EXIT_SCRIPT_EXCEPTION_ID).getId();

        List<Long> tasks = taskService.getTasksByProcessInstanceId(pid);
        Assertions.assertThat(tasks).hasSize(1);
        long taskId = tasks.get(0);

        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);

        tasks = taskService.getTasksByProcessInstanceId(pid);
        Assertions.assertThat(tasks).hasSize(2);

        Task task1 = taskService.getTaskById(Math.min(tasks.get(0), tasks.get(1)));
        Assertions.assertThat(task1.getNames().get(0).getText()).isEqualTo("Human task 1");
        Assertions.assertThat(task1.getTaskData().getStatus()).isEqualTo(Status.Completed);

        Task task2 = taskService.getTaskById(Math.max(tasks.get(0), tasks.get(1)));
        Assertions.assertThat(task2.getNames().get(0).getText()).isEqualTo("Human task 2");
        Assertions.assertThat(task2.getTaskData().getStatus()).isEqualTo(Status.Reserved);
    }

    @Test
    @BZ("1145046")
    public void testAbortWorkItemTaskStatus() {
        for (int i = 0; i < 5; i++) {
            createRuntimeManager(Strategy.PROCESS_INSTANCE, "abortWorkItemTaskStatus" + i, ABORT_WORKITEM_TASK_STATUS);
            RuntimeEngine runtime = getRuntimeEngine();
            KieSession ksession = runtime.getKieSession();

            Map<String, Object> params = new HashMap<String, Object>();
            ProcessInstance pi = ksession.startProcess(ABORT_WORKITEM_TASK_STATUS_ID, params);

            TaskService taskService = runtime.getTaskService();
            List<Long> list = taskService.getTasksByProcessInstanceId(pi.getId());
            for (long taskId : list) {
                Task task = taskService.getTaskById(taskId);
                Assertions.assertThat(task.getTaskData().getStatus()).isEqualTo(Status.Exited);
            }
            disposeRuntimeManager();
        }
    }

    @Test
    @BZ("1139496")
    public void testLocale() {
        KieSession ksession = createKSession(LOCALE);

        ProcessInstance pi = ksession.startProcess(LOCALE_ID);
        RuntimeEngine engine = getRuntimeEngine();
        TaskService taskService = engine.getTaskService();
        List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("john", "ja_JP");
        TaskSummary task = taskList.get(0);
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);
        EntityManagerFactory emf = getEmf();

        assertProcessInstanceCompleted(pi.getId());
        String language = emf.createEntityManager()
                .createNativeQuery("SELECT language from I18NTEXT WHERE shorttext='空手'")
                .getSingleResult().toString();
        Assertions.assertThat(language).isEqualTo("ja_JP");
    }

    @Test
    @BZ("1081508")
    public void testInputTransformation() {
        KieSession ksession = createKSession(INPUT_TRANSFORMATION);

        ProcessInstance pi = ksession.startProcess(INPUT_TRANSFORMATION_ID);
        RuntimeEngine engine = getRuntimeEngine();
        TaskService taskService = engine.getTaskService();

        List<TaskSummary> taskList = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        Long taskId = taskList.get(0).getId();

        taskService.start(taskId, "john");

        Map<String, Object> taskById = taskService.getTaskContent(taskId);
        Assertions.assertThat(taskById).containsEntry("Input", "Transformed String");

        taskService.complete(taskId, "john", null);

        assertProcessInstanceCompleted(pi.getId());
    }

}
