package org.jbpm.test;

import java.util.List;

import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.EmptyContext;

public class JbpmJUnitBaseTestCaseTest extends JbpmJUnitBaseTestCase {

    public JbpmJUnitBaseTestCaseTest() {
        // This test aims general usages --- persistence
        super(true, true);
    }
    

    @Test
    public void testAssertNodeActive() throws Exception {
        // JBPM-4846
        RuntimeManager manager = createRuntimeManager("humantask.bpmn");
        RuntimeEngine engine = getRuntimeEngine(EmptyContext.get());
        KieSession ksession = engine.getKieSession();
        TaskService taskService = engine.getTaskService();
        
        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");
        long processInstanceId = processInstance.getId();
        
        assertProcessInstanceActive(processInstanceId);
        assertNodeTriggered(processInstanceId, "Start", "Task 1");
        assertNodeActive(processInstanceId, ksession, "Task 1");
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        
        taskService.start(taskSummary.getId(), "john");
        taskService.complete(taskSummary.getId(), "john", null);
        
        assertNodeTriggered(processInstanceId, "Start", "Task 1", "Task 2");
        assertNodeActive(processInstanceId, ksession, "Task 2");
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        assertEquals(1, tasks.size());
        taskSummary = tasks.get(0);
        
        taskService.start(taskSummary.getId(), "mary");
        taskService.complete(taskSummary.getId(), "mary", null);
        
        assertProcessInstanceCompleted(processInstanceId);
    }
}
