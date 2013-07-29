package org.jbpm.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a sample file to test a process.
 */
public class ProcessPersistenceHumanTaskOnLaneTest extends JbpmJUnitBaseTestCase {

    private static final Logger logger = LoggerFactory.getLogger(ProcessPersistenceHumanTaskOnLaneTest.class);

    public ProcessPersistenceHumanTaskOnLaneTest() {
        super(true, true);
        
    }

    @Test 
    public void testProcess() throws Exception {
        createRuntimeManager("HumanTaskOnLane.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();

        ProcessInstance processInstance = ksession.startProcess("UserTask");

        assertProcessInstanceActive(processInstance.getId(), ksession);
        

        // simulating a system restart
        logger.debug("Reloading the environemnt to simulate system restart");
        disposeRuntimeManager();
        createRuntimeManager("HumanTaskOnLane.bpmn2");
        runtimeEngine = getRuntimeEngine();
        ksession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();

        // let john execute Task 1
        String taskUser = "john";
        String locale = "en-UK";
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(taskUser, locale);
        assertEquals(1, list.size());
        
        TaskSummary task = list.get(0);
        taskService.claim(task.getId(), taskUser);
        taskService.start(task.getId(), taskUser);
        taskService.complete(task.getId(), taskUser, null);

        // simulating a system restart
        logger.debug("Reloading the environemnt to simulate system restart once again");
        disposeRuntimeManager();
        createRuntimeManager("HumanTaskOnLane.bpmn2");
        runtimeEngine = getRuntimeEngine();
        ksession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();
        
        
        List<Status> reservedOnly = new ArrayList<Status>();
        reservedOnly.add(Status.Reserved);
        
        list = taskService.getTasksAssignedAsPotentialOwnerByStatus(taskUser, reservedOnly, locale);
        assertEquals(1, list.size());
        
        task = list.get(0);
        taskService.start(task.getId(), taskUser);
        taskService.complete(task.getId(), taskUser, null);


        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }


}
