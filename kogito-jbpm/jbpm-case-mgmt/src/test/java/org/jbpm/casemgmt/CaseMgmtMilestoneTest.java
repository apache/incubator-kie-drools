package org.jbpm.casemgmt;

import java.util.List;

import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.junit.Test;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;

public class CaseMgmtMilestoneTest extends JbpmJUnitBaseTestCase {
    
    public CaseMgmtMilestoneTest() {
        super(true, true);
    }
    
    @Test
    public void testMilestones() {
        addWorkItemHandler("Milestone", new SystemOutWorkItemHandler());
        createRuntimeManager("CaseUserTask.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        CaseMgmtService caseMgmtService = new CaseMgmtUtil(runtimeEngine);
        
        String[] milestones = caseMgmtService.getMilestoneNames("CaseUserTask");
        prettyPrintMilestones(milestones);
        assertEquals(2, milestones.length);
        
        ProcessInstance processInstance = runtimeEngine.getKieSession().startProcess("CaseUserTask");
        
        milestones = caseMgmtService.getAchievedMilestones(processInstance.getId());
        assertEquals(0, milestones.length);
        
        runtimeEngine.getKieSession().signalEvent("Milestone1", null, processInstance.getId());
        milestones = caseMgmtService.getAchievedMilestones(processInstance.getId());
        prettyPrintAchievedMilestones(milestones);
        assertEquals(1, milestones.length);
        
        TaskService taskService = runtimeEngine.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("krisv", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        taskService.start(task.getId(), "krisv");
        taskService.complete(task.getId(), "krisv", null);
        assertProcessInstanceCompleted(processInstance.getId());
        
        milestones = caseMgmtService.getAchievedMilestones(processInstance.getId());
        prettyPrintAchievedMilestones(milestones);
        assertEquals(1, milestones.length);
    }
    
    public void prettyPrintMilestones(String[] milestones) {
        System.out.println("***** Milestones: *****");
        for (String milestone: milestones) {
            System.out.println(milestone);
        }
    }
    
    public void prettyPrintAchievedMilestones(String[] milestones) {
        System.out.println("***** Achieved milestones: *****");
        for (String milestone: milestones) {
            System.out.println(milestone);
        }
    }
    

}
