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
package org.jbpm.test.functional.subprocess;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.drools.persistence.jta.JtaTransactionManager;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CallActivityWithReadOnlyProcessInstanceTest extends JbpmTestCase {

    @Parameters(name = "Strategy : {0}")
    public static Collection<Object[]> parameters() {
        Object[][] locking = new Object[][]{
            {Strategy.SINGLETON},
            {Strategy.PROCESS_INSTANCE},
            {Strategy.REQUEST},
        };
        return Arrays.asList(locking);
    };

    private final Strategy strategy;

    public CallActivityWithReadOnlyProcessInstanceTest(Strategy runtimeStrategy) {
        super(true, true);
        this.strategy = runtimeStrategy;
    }

    @Test
    public void testCallActivityWithReadOnlyProcessInstance() throws Exception {
        // JBPM-6434
        InitialContext context = new InitialContext();
        UserTransaction ut = (UserTransaction) context.lookup(JtaTransactionManager.DEFAULT_USER_TRANSACTION_NAME);

        RuntimeManager manager = createRuntimeManager(strategy, (String) null, "org/jbpm/test/functional/subprocess/CallActivityWithVar-Main.bpmn2", "org/jbpm/test/functional/subprocess/CallActivityWithVar-Sub.bpmn2");

        RuntimeEngine runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get());
        
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();

        ut.begin();

        ProcessInstance processInstance = ksession.startProcess("helloMain", null);
        long parentProcessInstanceId = processInstance.getId();

        ut.commit();
        manager.disposeRuntimeEngine(runtimeEngine);
        runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get(parentProcessInstanceId));
        ksession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();
        ut.begin();

        WorkflowProcessInstance readOnlyParentProcessInstance = (WorkflowProcessInstance) ksession.getProcessInstance(parentProcessInstanceId, true);
        assertEquals(0, readOnlyParentProcessInstance.getVariable("Var1"));

        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        Long taskId = tasks.get(0).getId();

        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);

        ut.commit();
        manager.disposeRuntimeEngine(runtimeEngine);
        runtimeEngine = getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));
        ksession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();
        ut.begin();

        readOnlyParentProcessInstance = (WorkflowProcessInstance) ksession.getProcessInstance(parentProcessInstanceId, true);
        assertEquals(1, readOnlyParentProcessInstance.getVariable("Var1"));

        tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        taskId = tasks.get(0).getId();

        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);

        ut.commit();
        ut.begin();

        assertProcessInstanceCompleted(parentProcessInstanceId);

        ut.commit();
        manager.disposeRuntimeEngine(runtimeEngine);

        activeEngines.clear(); // avoid unnecessary WARN for PerRequest
    }

}
