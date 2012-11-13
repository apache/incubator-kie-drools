/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.droolsjbpm.services.test;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import org.kie.definition.process.Process;
import org.kie.logger.KnowledgeRuntimeLoggerFactory;

import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.api.KnowledgeDomainService;
import org.droolsjbpm.services.impl.model.NodeInstanceDesc;
import org.droolsjbpm.services.impl.model.ProcessInstanceDesc;
import org.droolsjbpm.services.impl.model.VariableStateDesc;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.query.TaskSummary;
import org.junit.Test;
import static org.junit.Assert.*;

public abstract class DomainKnowledgeServiceBaseTest {

    @Inject
    protected KnowledgeDomainService knowledgeService;
    @Inject
    protected TaskServiceEntryPoint taskService;
    @Inject
    protected KnowledgeDataService dataService;
    @Inject
    protected KnowledgeAdminDataService adminDataService;
    
    @Test
    public void testSimpleProcess() throws Exception {
        
        
        
        StatefulKnowledgeSession ksession = knowledgeService.getSessionByBusinessKey("default");
        
        assertEquals(1, dataService.getProcesses().size());
        
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);
        Collection<Process> processes = ksession.getKnowledgeBase().getProcesses();

        assertEquals(1, processes.size());
        ProcessInstance processInstance = ksession.startProcess("org.jbpm.writedocument", null);
        ProcessInstanceDesc processInstanceById = dataService.getProcessInstanceById(0, processInstance.getId());
        assertEquals(ProcessInstance.STATE_PENDING, processInstanceById.getState());
        Collection<ProcessInstanceDesc> processInstancesDesc = dataService.getProcessInstances();
        assertEquals(1, processInstancesDesc.size());
        // I'm not using a persistent session here
        Collection<NodeInstanceDesc> processInstanceHistory = dataService.getProcessInstanceHistory(0, processInstance.getId());
        Iterator<NodeInstanceDesc> iterator = processInstanceHistory.iterator();
        assertEquals(2, processInstanceHistory.size());
        assertEquals("Start", iterator.next().getName());
        assertEquals("Write a Document", iterator.next().getName());
        Collection<NodeInstanceDesc> processInstanceActiveNodes = dataService.getProcessInstanceActiveNodes(0, processInstance.getId());
        assertEquals(1, processInstanceActiveNodes.size());
        assertEquals("Write a Document", processInstanceActiveNodes.iterator().next().getName());

        List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        Collection<ProcessInstance> processInstances = ksession.getProcessInstances();

        assertEquals(1, processInstances.size());

        assertEquals(1, tasksAssignedAsPotentialOwner.size());
        Collection<VariableStateDesc> variablesCurrentState = dataService.getVariablesCurrentState(processInstance.getId());
        assertEquals(0, variablesCurrentState.size());
        // Get Twice to test duplicated items
        tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");

        assertEquals(1, tasksAssignedAsPotentialOwner.size());


        TaskSummary task = tasksAssignedAsPotentialOwner.get(0);


        taskService.start(task.getId(), "salaboy");

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Result", "Initial Document");
        taskService.complete(task.getId(), "salaboy", result);



        

        List<TaskSummary> translatorTasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertEquals(1, translatorTasks.size());
        
        variablesCurrentState = dataService.getVariablesCurrentState(processInstance.getId());
        assertEquals(1, variablesCurrentState.size());
        assertEquals("Initial Document", variablesCurrentState.iterator().next().getNewValue());
        
        processInstanceHistory = dataService.getProcessInstanceHistory(0, processInstance.getId());
        assertEquals(5, processInstanceHistory.size());
        iterator = processInstanceHistory.iterator();
        assertEquals("Start", iterator.next().getName());
        assertEquals("Write a Document", iterator.next().getName());
        assertEquals("Translate and Review", iterator.next().getName());
        String taskName = iterator.next().getName();
        assertTrue(("Translate Document".equals(taskName) || "Review Document".equals(taskName)));
        taskName = iterator.next().getName();
        assertTrue(("Translate Document".equals(taskName) || "Review Document".equals(taskName)));
        
        processInstanceActiveNodes = dataService.getProcessInstanceActiveNodes(0, processInstance.getId());
        assertEquals(2, processInstanceActiveNodes.size());
        Iterator<NodeInstanceDesc> iteratorActiveNodes = processInstanceActiveNodes.iterator();
        String nodeName = iteratorActiveNodes.next().getName();
        assertTrue(("Translate Document".equals(nodeName) || "Review Document".equals(nodeName)));
        nodeName = iteratorActiveNodes.next().getName();
        assertTrue(("Translate Document".equals(nodeName) || "Review Document".equals(nodeName)));
        
        
        List<TaskSummary> reviewerTasks = taskService.getTasksAssignedAsPotentialOwner("reviewer", "en-UK");
        assertEquals(1, reviewerTasks.size());

        taskService.start(reviewerTasks.get(0).getId(), "reviewer");
        result = new HashMap<String, Object>();
        result.put("Result", "Reviewed Document");
        taskService.complete(reviewerTasks.get(0).getId(), "reviewer", result);

        processInstanceHistory = dataService.getProcessInstanceHistory(0, processInstance.getId());
        assertEquals(6, processInstanceHistory.size());
        iterator = processInstanceHistory.iterator();
        assertEquals("Start", iterator.next().getName());
        assertEquals("Write a Document", iterator.next().getName());
        assertEquals("Translate and Review", iterator.next().getName());
        taskName = iterator.next().getName();
        assertTrue(("Translate Document".equals(taskName) || "Review Document".equals(taskName)));
        taskName = iterator.next().getName();
        assertTrue("Translate Document".equals(taskName) || "Review Document".equals(taskName));
        
        assertEquals("Waiting for Translation and Revision", iterator.next().getName());

        variablesCurrentState = dataService.getVariablesCurrentState(processInstance.getId());
        assertEquals(2, variablesCurrentState.size());
        Iterator<VariableStateDesc> variableIterator = variablesCurrentState.iterator();
        assertEquals("Initial Document", variableIterator.next().getNewValue());
        assertEquals("Reviewed Document", variableIterator.next().getNewValue());
        
        translatorTasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertEquals(1, translatorTasks.size());

        taskService.start(translatorTasks.get(0).getId(), "translator");
        result = new HashMap<String, Object>();
        result.put("Result", "Translated Document");
        taskService.complete(translatorTasks.get(0).getId(), "translator", result);

        processInstanceHistory = dataService.getProcessInstanceHistory(0, processInstance.getId());
        assertEquals(9, processInstanceHistory.size());
        iterator = processInstanceHistory.iterator();
        assertEquals("Start", iterator.next().getName());
        assertEquals("Write a Document", iterator.next().getName());
        assertEquals("Translate and Review", iterator.next().getName());
        taskName = iterator.next().getName();
        assertTrue("Translate Document".equals(taskName) || "Review Document".equals(taskName));
        taskName = iterator.next().getName();
        assertTrue("Translate Document".equals(taskName) || "Review Document".equals(taskName));
        assertEquals("Waiting for Translation and Revision", iterator.next().getName());
        assertEquals("Waiting for Translation and Revision", iterator.next().getName());
        assertEquals("Report", iterator.next().getName());
        assertEquals("End", iterator.next().getName());

        variablesCurrentState = dataService.getVariablesCurrentState(processInstance.getId());
        assertEquals(3, variablesCurrentState.size());
        variableIterator = variablesCurrentState.iterator();
        assertEquals("Initial Document", variableIterator.next().getNewValue());
        assertEquals("Reviewed Document", variableIterator.next().getNewValue());
        assertEquals("Translated Document", variableIterator.next().getNewValue());
        
        
        processInstanceHistory = dataService.getProcessInstanceFullHistory(0, processInstance.getId());
        assertEquals(18, processInstanceHistory.size());

       

    }
    
    @Test
    public void testMultiProcessInstances(){
        StatefulKnowledgeSession ksession = knowledgeService.getSessionByBusinessKey("default");
         Collection<Process> processes = ksession.getKnowledgeBase().getProcesses();

        assertEquals(1, processes.size());
        ProcessInstance processInstance = ksession.startProcess("org.jbpm.writedocument", null);
        ProcessInstanceDesc processInstanceById = dataService.getProcessInstanceById(0, processInstance.getId());
        assertEquals(ProcessInstance.STATE_PENDING, processInstanceById.getState());
        
          processInstance = ksession.startProcess("org.jbpm.writedocument", null);
        processInstanceById = dataService.getProcessInstanceById(0, processInstance.getId());
        assertEquals(ProcessInstance.STATE_PENDING, processInstanceById.getState());
        
         Collection<ProcessInstanceDesc> processInstancesDesc = dataService.getProcessInstances();
        assertEquals(2, processInstancesDesc.size());
        
        
       


        List<TaskSummary> tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        Collection<ProcessInstance> processInstances = ksession.getProcessInstances();

       

        // Get Twice to test duplicated items
        tasksAssignedAsPotentialOwner = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");

        assertEquals(2, tasksAssignedAsPotentialOwner.size());


        TaskSummary task = tasksAssignedAsPotentialOwner.get(0);


        taskService.start(task.getId(), "salaboy");

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("Result", "Initial Document");
        taskService.complete(task.getId(), "salaboy", result);





        List<TaskSummary> translatorTasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertEquals(1, translatorTasks.size());

        



        List<TaskSummary> reviewerTasks = taskService.getTasksAssignedAsPotentialOwner("reviewer", "en-UK");
        assertEquals(1, reviewerTasks.size());

        taskService.start(reviewerTasks.get(0).getId(), "reviewer");

        taskService.complete(reviewerTasks.get(0).getId(), "reviewer", null);

     

        


        translatorTasks = taskService.getTasksAssignedAsPotentialOwner("translator", "en-UK");
        assertEquals(1, translatorTasks.size());

        taskService.start(translatorTasks.get(0).getId(), "translator");

        taskService.complete(translatorTasks.get(0).getId(), "translator", null);

        
       
        processInstance = ksession.startProcess("org.jbpm.writedocument", null);
        processInstanceById = dataService.getProcessInstanceById(0, processInstance.getId());
        assertEquals(ProcessInstance.STATE_PENDING, processInstanceById.getState());
        ProcessInstanceDesc next = dataService.getProcessInstances().iterator().next();
        assertTrue(next instanceof ProcessInstanceDesc);
        assertEquals(3, dataService.getProcessInstances().size());
        
    }
    
}
