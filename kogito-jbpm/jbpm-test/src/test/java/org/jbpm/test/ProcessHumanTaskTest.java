package org.jbpm.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;


/**
 * This is a sample file to test a process.
 */
public class ProcessHumanTaskTest extends JbpmJUnitTestCase {
	
	public ProcessHumanTaskTest() {
		super(true);
	}

	@Test
	public void testProcess() {
		KieSession ksession = createKnowledgeSession("humantask.bpmn");
        TaskService taskService = getTaskService();
		
		ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello");

		assertProcessInstanceActive(processInstance.getId(), ksession);
		assertNodeTriggered(processInstance.getId(), "Start", "Task 1");
		
		// let john execute Task 1
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
		TaskSummary task = list.get(0);
		System.out.println("John is executing task " + task.getName());
		taskService.start(task.getId(), "john");
		taskService.complete(task.getId(), "john", null);

		assertNodeTriggered(processInstance.getId(), "Task 2");
		
		// let mary execute Task 2
		list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
		task = list.get(0);
		System.out.println("Mary is executing task " + task.getName());
		taskService.start(task.getId(), "mary");
		taskService.complete(task.getId(), "mary", null);

		assertNodeTriggered(processInstance.getId(), "End");
		assertProcessInstanceCompleted(processInstance.getId(), ksession);
	}
	
    @Test @Ignore //-> task CreatedBy in summary is not filled ??
    public void testProcessWithCreatedBy() {
        KieSession ksession = createKnowledgeSession("humantaskwithcreatedby.bpmn");
        TaskService taskService = getTaskService();
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", "krisv");
        ProcessInstance processInstance = ksession.startProcess("com.sample.bpmn.hello.createdby", params);

        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Start", "Task 1");
        
        // let john execute Task 1
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        assertEquals("mary", task.getCreatedBy().getId());
        System.out.println("John is executing task " + task.getName());
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);

        assertNodeTriggered(processInstance.getId(), "Task 2");
        
        // let mary execute Task 2
        list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        task = list.get(0);
        assertEquals("krisv", task.getCreatedBy().getId());
        System.out.println("Mary is executing task " + task.getName());
        taskService.start(task.getId(), "mary");
        taskService.complete(task.getId(), "mary", null);

        assertNodeTriggered(processInstance.getId(), "End");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

}
