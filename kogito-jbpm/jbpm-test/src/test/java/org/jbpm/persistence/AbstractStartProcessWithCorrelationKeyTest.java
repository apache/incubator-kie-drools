/*
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.persistence;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.persistence.correlation.JPACorrelationKeyFactory;
import org.jbpm.task.TaskService;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.test.JbpmJUnitTestCase;
import org.junit.Test;
import org.kie.KieInternalServices;
import org.kie.process.CorrelationAwareProcessRuntime;
import org.kie.process.CorrelationKey;
import org.kie.process.CorrelationKeyFactory;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;

public abstract class AbstractStartProcessWithCorrelationKeyTest extends JbpmJUnitTestCase {
    
    private CorrelationKeyFactory factory;
    public AbstractStartProcessWithCorrelationKeyTest(boolean persistence) {
        super(true);
        super.setPersistence(persistence);
        factory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
    }
    
    @Test
    public void testCreateAndStartProcessWithBusinessKey() {
        StatefulKnowledgeSession ksession = createKnowledgeSession("humantask.bpmn");
        TaskService taskService = getTaskService(ksession);
        
        ProcessInstance processInstance = ((CorrelationAwareProcessRuntime)ksession).createProcessInstance("com.sample.bpmn.hello", getCorrelationKey(), null);
        ksession.startProcessInstance(processInstance.getId());
        
        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Start", "Task 1");       
        
        // let john execute Task 1
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        System.out.println("John is executing task " + task.getName());
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);

        assertNodeTriggered(processInstance.getId(), "Task 2");
        
        ProcessInstance processInstanceCopy = ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(getCorrelationKey());
        assertNotNull(processInstanceCopy);
        assertEquals(processInstance.getId(), processInstanceCopy.getId());
        
        // let mary execute Task 2
        list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        task = list.get(0);
        System.out.println("Mary is executing task " + task.getName());
        taskService.start(task.getId(), "mary");
        taskService.complete(task.getId(), "mary", null);

        assertNodeTriggered(processInstance.getId(), "End");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);       
    }
    
    @Test
    public void testProcessWithBusinessKey() {
        StatefulKnowledgeSession ksession = createKnowledgeSession("humantask.bpmn");
        TaskService taskService = getTaskService(ksession);
        
        ProcessInstance processInstance = ((CorrelationAwareProcessRuntime)ksession).startProcess("com.sample.bpmn.hello", getCorrelationKey(), null);

        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Start", "Task 1");       
        
        // let john execute Task 1
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        System.out.println("John is executing task " + task.getName());
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);

        assertNodeTriggered(processInstance.getId(), "Task 2");
        
        ProcessInstance processInstanceCopy = ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(getCorrelationKey());
        assertNotNull(processInstanceCopy);
        assertEquals(processInstance.getId(), processInstanceCopy.getId());
        
        // let mary execute Task 2
        list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        task = list.get(0);
        System.out.println("Mary is executing task " + task.getName());
        taskService.start(task.getId(), "mary");
        taskService.complete(task.getId(), "mary", null);

        assertNodeTriggered(processInstance.getId(), "End");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);       
    }

    @Test
    public void testProcessWithBusinessKeyFailOnDuplicatedBusinessKey() {
        StatefulKnowledgeSession ksession = createKnowledgeSession("humantask.bpmn");
        TaskService taskService = getTaskService(ksession);
        
        ProcessInstance processInstance = ((CorrelationAwareProcessRuntime)ksession)
                .startProcess("com.sample.bpmn.hello", getCorrelationKey(), null);

        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Start", "Task 1");
        
        try {
            ((CorrelationAwareProcessRuntime)ksession).startProcess("com.sample.bpmn.hello", getCorrelationKey(), null);
            fail("Cannot have duplicated business key running at the same time");
        } catch (Exception e) {
            
        }
        
        // let john execute Task 1
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        System.out.println("John is executing task " + task.getName());
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);

        assertNodeTriggered(processInstance.getId(), "Task 2");
        
        ProcessInstance processInstanceCopy = ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(getCorrelationKey());
        assertNotNull(processInstanceCopy);
        assertEquals(processInstance.getId(), processInstanceCopy.getId());
        
        // let mary execute Task 2
        list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        task = list.get(0);
        System.out.println("Mary is executing task " + task.getName());
        taskService.start(task.getId(), "mary");
        taskService.complete(task.getId(), "mary", null);

        assertNodeTriggered(processInstance.getId(), "End");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }
    
    @Test
    public void testProcessesWithSameBusinessKeyNotInParallel() {
        StatefulKnowledgeSession ksession = createKnowledgeSession("humantask.bpmn");
        TaskService taskService = getTaskService(ksession);
        
        ProcessInstance processInstance = ((CorrelationAwareProcessRuntime)ksession).
                startProcess("com.sample.bpmn.hello", getCorrelationKey(), null);

        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Start", "Task 1");     
        
        // let john execute Task 1
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        System.out.println("John is executing task " + task.getName());
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);

        assertNodeTriggered(processInstance.getId(), "Task 2");
        
        ProcessInstance processInstanceCopy = ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(getCorrelationKey());
        assertNotNull(processInstanceCopy);
        assertEquals(processInstance.getId(), processInstanceCopy.getId());
        
        // let mary execute Task 2
        list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        task = list.get(0);
        System.out.println("Mary is executing task " + task.getName());
        taskService.start(task.getId(), "mary");
        taskService.complete(task.getId(), "mary", null);

        assertNodeTriggered(processInstance.getId(), "End");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        
        
        processInstance = ((CorrelationAwareProcessRuntime)ksession).startProcess("com.sample.bpmn.hello", getCorrelationKey(), null);

        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Start", "Task 1");      
        
        // let john execute Task 1
        list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        task = list.get(0);
        System.out.println("John is executing task " + task.getName());
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);

        assertNodeTriggered(processInstance.getId(), "Task 2");
        
        processInstanceCopy = ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(getCorrelationKey());
        assertNotNull(processInstanceCopy);
        assertEquals(processInstance.getId(), processInstanceCopy.getId());
        
        // let mary execute Task 2
        list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        task = list.get(0);
        System.out.println("Mary is executing task " + task.getName());
        taskService.start(task.getId(), "mary");
        taskService.complete(task.getId(), "mary", null);

        assertNodeTriggered(processInstance.getId(), "End");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);     
    }
    
    @Test
    public void testProcessWithMultiValuedBusinessKey() {
        StatefulKnowledgeSession ksession = createKnowledgeSession("humantask.bpmn");
        TaskService taskService = getTaskService(ksession);
        
        ProcessInstance processInstance = ((CorrelationAwareProcessRuntime)ksession).startProcess("com.sample.bpmn.hello", getMultiValuedCorrelationKey(), null);

        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Start", "Task 1");       
        
        // let john execute Task 1
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        System.out.println("John is executing task " + task.getName());
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);

        assertNodeTriggered(processInstance.getId(), "Task 2");
        
        ProcessInstance processInstanceCopy = ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(getMultiValuedCorrelationKey());
        assertNotNull(processInstanceCopy);
        assertEquals(processInstance.getId(), processInstanceCopy.getId());
        
        // let mary execute Task 2
        list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        task = list.get(0);
        System.out.println("Mary is executing task " + task.getName());
        taskService.start(task.getId(), "mary");
        taskService.complete(task.getId(), "mary", null);

        assertNodeTriggered(processInstance.getId(), "End");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);       
    }
    
    @Test
    public void testProcessWithInvalidBusinessKey() {
        StatefulKnowledgeSession ksession = createKnowledgeSession("humantask.bpmn");
        TaskService taskService = getTaskService(ksession);
        
        ProcessInstance processInstance = ((CorrelationAwareProcessRuntime)ksession).startProcess("com.sample.bpmn.hello", getMultiValuedCorrelationKey(), null);

        assertProcessInstanceActive(processInstance.getId(), ksession);
        assertNodeTriggered(processInstance.getId(), "Start", "Task 1");       
        
        // let john execute Task 1
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        TaskSummary task = list.get(0);
        System.out.println("John is executing task " + task.getName());
        taskService.start(task.getId(), "john");
        taskService.complete(task.getId(), "john", null);

        assertNodeTriggered(processInstance.getId(), "Task 2");
        
        // now check if when using invalid correlation it won't be found
        ProcessInstance processInstanceNotFound = ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(getCorrelationKey());
        assertNull(processInstanceNotFound);
        
        ProcessInstance processInstanceCopy = ((CorrelationAwareProcessRuntime)ksession).getProcessInstance(getMultiValuedCorrelationKey());
        assertNotNull(processInstanceCopy);
        assertEquals(processInstance.getId(), processInstanceCopy.getId());
        
        // let mary execute Task 2
        list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
        task = list.get(0);
        System.out.println("Mary is executing task " + task.getName());
        taskService.start(task.getId(), "mary");
        taskService.complete(task.getId(), "mary", null);

        assertNodeTriggered(processInstance.getId(), "End");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);       
    }
    
    private CorrelationKey getCorrelationKey() {
        
        return factory.newCorrelationKey("mybusinesskey");
    }
    
    private CorrelationKey getMultiValuedCorrelationKey() {
        List<String> properties = new ArrayList<String>();
        properties.add("customerid");
        properties.add("orderid");
        return factory.newCorrelationKey(properties);
    }
    
}