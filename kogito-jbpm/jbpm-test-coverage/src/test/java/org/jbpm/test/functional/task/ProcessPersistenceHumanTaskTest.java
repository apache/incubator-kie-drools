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

import java.util.List;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * This is a sample file to test a process.
 */
public class ProcessPersistenceHumanTaskTest extends JbpmTestCase {

    private static final Logger logger = LoggerFactory.getLogger(ProcessPersistenceHumanTaskTest.class);

    public ProcessPersistenceHumanTaskTest() {
        super(true, true);
    }

    @Test
    public void testProcess() throws Exception {
        createRuntimeManager("org/jbpm/test/functional/task/humantask.bpmn");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();


        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");

        assertProcessInstanceActive(processInstance.getId());
        assertNodeTriggered(processInstance.getId(), "Start", "Task 1");

        // simulating a system restart
        logger.debug("Reloading the environemnt to simulate system restart");
        disposeRuntimeManager();
        createRuntimeManager("org/jbpm/test/functional/task/humantask.bpmn");
        runtimeEngine = getRuntimeEngine();
        ksession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();

        // let john execute Task 1
        String taskGroup = "en-UK";
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", taskGroup);
        TaskSummary task = list.get(0);
        logger.debug("John is executing task " + task.getName());
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);

        assertNodeTriggered(processInstance.getId(), "Task 2");

        // simulating a system restart
        logger.debug("Reloading the environemnt to simulate system restart once again");
        disposeRuntimeManager();
        createRuntimeManager("org/jbpm/test/functional/task/humantask.bpmn");
        runtimeEngine = getRuntimeEngine();
        ksession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();

        // let mary execute Task 2
        String taskUser = "mary";
        list = taskService.getTasksAssignedAsPotentialOwner(taskUser, taskGroup);
        assertTrue("No tasks found for potential owner " + taskUser + "/" + taskGroup, list.size() > 0);
        task = list.get(0);
        logger.debug("Mary is executing task " + task.getName());
        taskService.start(task.getId(), "mary");
        taskService.complete(task.getId(), "mary", null);

        assertNodeTriggered(processInstance.getId(), "End");
        assertProcessInstanceCompleted(processInstance.getId());
    }

    @Test
    public void testTransactions() throws Exception {
        createRuntimeManager("org/jbpm/test/functional/task/humantask.bpmn");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();

        long processId;
        UserTransaction ut = (UserTransaction) new InitialContext().lookup("java:comp/UserTransaction");
        try {
            ut.begin();
            ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
            processId = processInstance.getId();
        } finally {
            ut.rollback();
        }

        assertNull(ksession.getProcessInstance(processId));
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(0, list.size());
    }

}
