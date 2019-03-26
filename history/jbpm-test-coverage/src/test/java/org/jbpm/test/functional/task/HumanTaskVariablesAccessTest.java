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

package org.jbpm.test.functional.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class HumanTaskVariablesAccessTest extends JbpmTestCase {

    private static final Logger logger = LoggerFactory.getLogger(HumanTaskVariablesAccessTest.class);

    public HumanTaskVariablesAccessTest() {
        super(true, true);
    }

    @Test
    public void testBeforeEvents() {
        testHumanTaskWithListener(new DefaultTaskEventListener() {

            @Override
            public void beforeTaskStartedEvent(TaskEvent event) {
                assertTaskStartedEvent(event);
            }

            @Override
            public void beforeTaskCompletedEvent(TaskEvent event) {
                assertTaskCompletedEvent(event);
            }

            @Override
            public void beforeTaskAddedEvent(TaskEvent event) {
                assertTaskAddedEvent(event);
            }

        });
    }

    @Test
    public void testAfterEvents() {
        testHumanTaskWithListener(new DefaultTaskEventListener() {

            @Override
            public void afterTaskStartedEvent(TaskEvent event) {
                assertTaskStartedEvent(event);
            }

            @Override
            public void afterTaskCompletedEvent(TaskEvent event) {
                assertTaskCompletedEvent(event);
            }

            @Override
            public void afterTaskAddedEvent(TaskEvent event) {
                assertTaskAddedEvent(event);
            }

        });
    }

    private void testHumanTaskWithListener(TaskLifeCycleEventListener listener) {
        addTaskEventListener(listener);
        createRuntimeManager("org/jbpm/test/functional/task/HumanTaskWithVariables.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processHTInput", "Simple human task input");

        ProcessInstance processInstance = ksession.startProcess("humanTaskWithVariables", params);

        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Start", "Task");

        // let john execute Task
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        logger.info("John is executing task {}", task.getName());

        params.clear();
        params.put("humanTaskOutput", "Simple human task output");
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", params);

        assertNodeTriggered(processInstance.getId(), "End");
        assertProcessInstanceNotActive(processInstance.getId(), ksession);
    }

    private void assertTaskStartedEvent(TaskEvent event) {
        assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
        assertEquals(5, event.getTask().getTaskData().getTaskInputVariables().size());
        assertEquals("Simple human task input", event.getTask().getTaskData().getTaskInputVariables().get("humanTaskInput"));

        assertNull(event.getTask().getTaskData().getTaskOutputVariables());
    }

    // In *TaskCompleted events, the task is already pre-populated with output variables, input variables are accessible after loading.
    private void assertTaskCompletedEvent(TaskEvent event) {
        assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
        assertEquals(5, event.getTask().getTaskData().getTaskInputVariables().size());
        assertEquals("Simple human task input", event.getTask().getTaskData().getTaskInputVariables().get("humanTaskInput"));

        assertNotNull(event.getTask().getTaskData().getTaskOutputVariables());
        assertEquals(1, event.getTask().getTaskData().getTaskOutputVariables().size());
        assertEquals("Simple human task output", event.getTask().getTaskData().getTaskOutputVariables().get("humanTaskOutput"));

        event.getTaskContext().loadTaskVariables(event.getTask());

        assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
        assertEquals(5, event.getTask().getTaskData().getTaskInputVariables().size());
        assertEquals("Simple human task input", event.getTask().getTaskData().getTaskInputVariables().get("humanTaskInput"));

        assertNotNull(event.getTask().getTaskData().getTaskOutputVariables());
        assertEquals(1, event.getTask().getTaskData().getTaskOutputVariables().size());
        assertEquals("Simple human task output", event.getTask().getTaskData().getTaskOutputVariables().get("humanTaskOutput"));
    }

    // In *TaskAdded events, the task is already pre-populated with input variables, output variables are not accessible even after loading.
    private void assertTaskAddedEvent(TaskEvent event) {
        assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
        assertEquals(5, event.getTask().getTaskData().getTaskInputVariables().size());
        assertEquals("Simple human task input", event.getTask().getTaskData().getTaskInputVariables().get("humanTaskInput"));
        assertEquals(5, event.getTask().getTaskData().getTaskInputVariables().size());
        assertEquals("Simple human task input", event.getTask().getTaskData().getTaskInputVariables().get("humanTaskInput"));

        assertNull(event.getTask().getTaskData().getTaskOutputVariables());

        event.getTaskContext().loadTaskVariables(event.getTask());

        assertNotNull(event.getTask().getTaskData().getTaskInputVariables());
        assertEquals(5, event.getTask().getTaskData().getTaskInputVariables().size());
        assertEquals("Simple human task input", event.getTask().getTaskData().getTaskInputVariables().get("humanTaskInput"));

        assertNull(event.getTask().getTaskData().getTaskOutputVariables());
    }

}
