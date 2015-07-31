package org.jbpm.casemgmt;

import java.util.List;

import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.definition.process.Process;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;

public class CaseMgmtDynamicTest extends JbpmJUnitBaseTestCase {
    
    public CaseMgmtDynamicTest() {
        super(true, true);
    }
    
    @Test
    public void testDynamicTask() {
        createRuntimeManager("EmptyCase.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        CaseMgmtService caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        
        ProcessInstance processInstance = caseMgmtService.startNewCase(null);
        
        List<TaskSummary> tasks = runtimeEngine.getTaskService()
            .getTasksAssignedAsPotentialOwner("krisv", "en-UK");
        assertEquals(0, tasks.size());
        
        caseMgmtService.createDynamicHumanTask(
            processInstance.getId(), "Do something", 
            "krisv", null, "You need to do something", null);
        
        TaskService taskService = runtimeEngine.getTaskService();
        tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals((Long) processInstance.getId(), task.getProcessInstanceId());
        
        Task[] activeTasks = caseMgmtService.getActiveTasks(processInstance.getId());
        prettyPrintActiveTasks(activeTasks);
        assertEquals(1, activeTasks.length);
        
        taskService.start(task.getId(), "krisv");
        taskService.complete(task.getId(), "krisv", null);
        
        activeTasks = caseMgmtService.getActiveTasks(processInstance.getId());
        assertEquals(0, activeTasks.length);
    }
    
    @Test
    public void testDynamicProcess() {
        createRuntimeManager("EmptyCase.bpmn2", "CaseUserTask.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        CaseMgmtService caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        
        ProcessInstance processInstance = caseMgmtService.startNewCase(null);
        
        List<TaskSummary> tasks = runtimeEngine.getTaskService()
            .getTasksAssignedAsPotentialOwner("krisv", "en-UK");
        assertEquals(0, tasks.size());
            
        Process[] processes = caseMgmtService.getAvailableProcesses();
        prettyPrintProcesses(processes);
        assertEquals(2, processes.length);
        
        caseMgmtService.createDynamicProcess(
            processInstance.getId(), "CaseUserTask", null);
        
        ProcessInstance[] activeSubProcesses = caseMgmtService.getActiveSubProcesses(processInstance.getId());
        prettyPrintActiveSubProcesses(activeSubProcesses);
        assertEquals(1, activeSubProcesses.length);

        TaskService taskService = runtimeEngine.getTaskService();
        tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        taskService.start(task.getId(), "krisv");
        taskService.complete(task.getId(), "krisv", null);
        
        activeSubProcesses = caseMgmtService.getActiveSubProcesses(processInstance.getId());
        assertEquals(0, activeSubProcesses.length);
    }
        
    public void prettyPrintActiveTasks(Task[] tasks) {
        System.out.println("***** Active Tasks: *****");
        for (Task task: tasks) {
            System.out.println(task.getName() + " (processInstanceId=" 
                + task.getTaskData().getProcessInstanceId() + ")");
        }
    }
    
    public void prettyPrintProcesses(Process[] processes) {
        System.out.println("***** Available processes: *****");
        for (Process process: processes) {
            System.out.println(process.getName() + " (id=" + process.getId() + ")");
        }
    }
    
    public void prettyPrintActiveSubProcesses(ProcessInstance[] processInstances) {
        System.out.println("***** Active SubProcesses: *****");
        for (ProcessInstance processInstance: processInstances) {
            System.out.println(processInstance.getProcessId() 
                + " (id=" + processInstance.getId() + ")");
        }
    }
    
}
