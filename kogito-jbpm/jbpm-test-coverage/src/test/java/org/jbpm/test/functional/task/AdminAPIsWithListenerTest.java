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
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.admin.listener.TaskCleanUpProcessEventListener;
import org.jbpm.services.task.identity.DefaultUserInfo;
import org.jbpm.test.JbpmTestCase;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.logger.KnowledgeRuntimeLoggerFactory;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.EventService;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.internal.task.api.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class AdminAPIsWithListenerTest extends JbpmTestCase {

    private static final Logger logger = LoggerFactory.getLogger(AdminAPIsWithListenerTest.class);

    private EntityManagerFactory emfTasks;
    protected UserInfo userInfo;
    protected Properties conf;

    public AdminAPIsWithListenerTest() {
        super(true, true);
    }

    
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        emfTasks = Persistence.createEntityManagerFactory("org.jbpm.services.task");
        userInfo = new DefaultUserInfo(null);
    }
    

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        if (emfTasks != null && emfTasks.isOpen()) {
            emfTasks.close();
        }
    }

 

    @SuppressWarnings("unchecked")
	@Test
    public void automaticCleanUpTest() throws Exception {

        createRuntimeManager("org/jbpm/test/functional/task/patient-appointment.bpmn");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();

        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);

        ksession.addEventListener(new TaskCleanUpProcessEventListener(taskService));
        
        // let check how many listeners we have
        assertEquals(2, ((EventService<TaskLifeCycleEventListener>)taskService).getTaskEventListeners().size());
        assertEquals(2, ((EventService<TaskLifeCycleEventListener>)taskService).getTaskEventListeners().size());

        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();

        ProcessInstance process = ksession.startProcess("org.jbpm.PatientAppointment", parameters);

        //The process is in the first Human Task waiting for its completion
        assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());

        //gets frontDesk's tasks
        List<TaskSummary> frontDeskTasks = taskService.getTasksAssignedAsPotentialOwner("frontDesk", "en-UK");
        assertEquals(1, frontDeskTasks.size());

        //doctor doesn't have any task
        List<TaskSummary> doctorTasks = taskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        Assert.assertTrue(doctorTasks.isEmpty());

        //manager doesn't have any task
        List<TaskSummary> managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());


        taskService.start(frontDeskTasks.get(0).getId(), "frontDesk");

        taskService.complete(frontDeskTasks.get(0).getId(), "frontDesk", null);

        //Now doctor has 1 task
        doctorTasks = taskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        assertEquals(1, doctorTasks.size());

        //No tasks for manager yet
        managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());


        taskService.start(doctorTasks.get(0).getId(), "doctor");

        taskService.complete(doctorTasks.get(0).getId(), "doctor", null);

        // tasks for manager 
        managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        assertEquals(1, managerTasks.size());
        taskService.start(managerTasks.get(0).getId(), "manager");

        taskService.complete(managerTasks.get(0).getId(), "manager", null);

        // since persisted process instance is completed it should be null
        process = ksession.getProcessInstance(process.getId());
        Assert.assertNull(process);


        final EntityManager em = emfTasks.createEntityManager();       
        assertEquals(0, em.createQuery("select t from TaskImpl t").getResultList().size());
        assertEquals(0, em.createQuery("select i from I18NTextImpl i").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_BAs").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_ExclOwners").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_PotOwners").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Recipients").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Stakeholders").getResultList().size());
        assertEquals(0, em.createQuery("select c from ContentImpl c").getResultList().size());
        em.close();
    }

    @Test
    public void automaticCleanUpTestAbortProcess() throws Exception {
    	
        createRuntimeManager("org/jbpm/test/functional/task/patient-appointment.bpmn");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();
        
        KnowledgeRuntimeLoggerFactory.newConsoleLogger(ksession);

        ksession.addEventListener(new TaskCleanUpProcessEventListener(taskService));

        logger.info("### Starting process ###");
        Map<String, Object> parameters = new HashMap<String, Object>();

        ProcessInstance process = ksession.startProcess("org.jbpm.PatientAppointment", parameters);
        long processInstanceId = process.getId();

        //The process is in the first Human Task waiting for its completion
        assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());

        //gets frontDesk's tasks
        List<TaskSummary> frontDeskTasks = taskService.getTasksAssignedAsPotentialOwner("frontDesk", "en-UK");
        assertEquals(1, frontDeskTasks.size());

        //doctor doesn't have any task
        List<TaskSummary> doctorTasks = taskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        Assert.assertTrue(doctorTasks.isEmpty());

        //manager doesn't have any task
        List<TaskSummary> managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());


        taskService.start(frontDeskTasks.get(0).getId(), "frontDesk");

        taskService.complete(frontDeskTasks.get(0).getId(), "frontDesk", null);

        //Now doctor has 1 task
        doctorTasks = taskService.getTasksAssignedAsPotentialOwner("doctor", "en-UK");
        assertEquals(1, doctorTasks.size());

        //No tasks for manager yet
        managerTasks = taskService.getTasksAssignedAsPotentialOwner("manager", "en-UK");
        Assert.assertTrue(managerTasks.isEmpty());


        taskService.start(doctorTasks.get(0).getId(), "doctor");

        taskService.complete(doctorTasks.get(0).getId(), "doctor", null);

        // abort process instance
        ksession.abortProcessInstance(processInstanceId);
        // since persisted process instance is completed it should be null
        process = ksession.getProcessInstance(process.getId());
        Assert.assertNull(process);


        final EntityManager em = emfTasks.createEntityManager();

        assertEquals(0, em.createQuery("select t from TaskImpl t").getResultList().size());
        assertEquals(0, em.createQuery("select i from I18NTextImpl i").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_BAs").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_ExclOwners").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_PotOwners").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Recipients").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Stakeholders").getResultList().size());
        assertEquals(0, em.createQuery("select c from ContentImpl c").getResultList().size());
        em.close();
    }
    
    @Test
    public void automaticCleanUpWitCallActivityTest() throws Exception {

        createRuntimeManager("org/jbpm/test/functional/task/CallActivity.bpmn2", 
                "org/jbpm/test/functional/task/CallActivity2.bpmn2", 
                "org/jbpm/test/functional/task/CallActivitySubProcess.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();        

        ksession.addEventListener(new TaskCleanUpProcessEventListener(taskService));
        
        ProcessInstance processInstance = ksession.startProcess("ParentProcessCA");
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1,  tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        taskService.start(taskId, "john");
        taskService.complete(taskId, "john", null);
        
        assertProcessInstanceCompleted(processInstance.getId());

        final EntityManager em = emfTasks.createEntityManager();

        assertEquals(0, em.createQuery("select t from TaskImpl t").getResultList().size());
        assertEquals(0, em.createQuery("select i from I18NTextImpl i").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_BAs").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_ExclOwners").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_PotOwners").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Recipients").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Stakeholders").getResultList().size());
        assertEquals(0, em.createQuery("select c from ContentImpl c").getResultList().size());
        em.close();

    }
    
    @Test
    public void automaticCleanUpForSubProcessWithSingletonStrategy() throws Exception {

        TaskCleanUpProcessEventListener taskCleanUpProcessEventListener = new TaskCleanUpProcessEventListener(null);
        this.addProcessEventListener(taskCleanUpProcessEventListener);

        RuntimeManager manager = createRuntimeManager("org/jbpm/test/functional/task/ht-main.bpmn", "org/jbpm/test/functional/task/ht-sub.bpmn");
        RuntimeEngine runtime = getRuntimeEngine(ProcessInstanceIdContext.get());
        taskCleanUpProcessEventListener.setTaskService((InternalTaskService) runtime.getTaskService());
        KieSession ksession = runtime.getKieSession();

        // start a new process instance
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance pi = ksession.startProcess("com.mycompany.sample", params);

        // obtain the task service
        TaskService taskService = runtime.getTaskService();

        List<TaskSummary> tasks1 = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertFalse(tasks1.isEmpty());
        TaskSummary task1 = tasks1.get(0);
        System.out.println("Sales-rep executing task " + task1.getName() + "(" + task1.getId() + ": " + task1.getDescription() + ")");
        taskService.start(task1.getId(), "john");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("comment", "Agreed, existing laptop needs replacing");
        results.put("outcome", "Accept");
        
        // complete the human task of the main process
        taskService.complete(task1.getId(), "john", results);

        // abort the process instance
        ksession.abortProcessInstance(pi.getId());

        // main process instance shall be aborted
        assertProcessInstanceAborted(pi.getId());
        
        
        AuditService logService = runtime.getAuditService();

        List<? extends ProcessInstanceLog> logs = logService.findProcessInstances("com.mycompany.sample");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        
        assertEquals(ProcessInstance.STATE_ABORTED, logs.get(0).getStatus().intValue());
        
        logs = logService.findProcessInstances("com.mycompany.sample.subprocess");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        
        assertEquals(ProcessInstance.STATE_ABORTED, logs.get(0).getStatus().intValue());
        manager.close();
        
        final EntityManager em = emfTasks.createEntityManager();

        assertEquals(0, em.createQuery("select t from TaskImpl t").getResultList().size());
        assertEquals(0, em.createQuery("select i from I18NTextImpl i").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_BAs").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_ExclOwners").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_PotOwners").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Recipients").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Stakeholders").getResultList().size());
        assertEquals(0, em.createQuery("select c from ContentImpl c").getResultList().size());
        em.close();
    }

    @Test
    public void automaticCleanUpForSubProcessWithPerProcessInstanceStrategy() throws Exception {

        TaskCleanUpProcessEventListener taskCleanUpProcessEventListener = new TaskCleanUpProcessEventListener(null);
        this.addProcessEventListener(taskCleanUpProcessEventListener);

        RuntimeManager manager = createRuntimeManager(Strategy.PROCESS_INSTANCE, "com.mycompany.sample", "org/jbpm/test/functional/task/ht-main.bpmn", "org/jbpm/test/functional/task/ht-sub.bpmn");
        RuntimeEngine runtime = getRuntimeEngine(ProcessInstanceIdContext.get());
        taskCleanUpProcessEventListener.setTaskService((InternalTaskService) runtime.getTaskService());
        KieSession ksession = runtime.getKieSession();

        // start a new process instance
        Map<String, Object> params = new HashMap<String, Object>();
        ProcessInstance pi = ksession.startProcess("com.mycompany.sample", params);

        // obtain the task service
        TaskService taskService = runtime.getTaskService();

        List<TaskSummary> tasks1 = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertFalse(tasks1.isEmpty());
        TaskSummary task1 = tasks1.get(0);
        System.out.println("Sales-rep executing task " + task1.getName() + "(" + task1.getId() + ": " + task1.getDescription() + ")");
        taskService.start(task1.getId(), "john");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("comment", "Agreed, existing laptop needs replacing");
        results.put("outcome", "Accept");

        // complete the human task of the main process
        taskService.complete(task1.getId(), "john", results);

        // abort the process instance
        ksession.abortProcessInstance(pi.getId());

        // main process instance shall be aborted
        assertProcessInstanceAborted(pi.getId());


        AuditService logService = runtime.getAuditService();

        List<? extends ProcessInstanceLog> logs = logService.findProcessInstances("com.mycompany.sample");
        assertNotNull(logs);
        assertEquals(1, logs.size());

        assertEquals(ProcessInstance.STATE_ABORTED, logs.get(0).getStatus().intValue());

        logs = logService.findProcessInstances("com.mycompany.sample.subprocess");
        assertNotNull(logs);
        assertEquals(1, logs.size());

        assertEquals(ProcessInstance.STATE_ABORTED, logs.get(0).getStatus().intValue());
        manager.close();

        final EntityManager em = emfTasks.createEntityManager();

        assertEquals(0, em.createQuery("select t from TaskImpl t").getResultList().size());
        assertEquals(0, em.createQuery("select i from I18NTextImpl i").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_BAs").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_ExclOwners").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_PotOwners").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Recipients").getResultList().size());
        assertEquals(0, em.createNativeQuery("select * from PeopleAssignments_Stakeholders").getResultList().size());
        assertEquals(0, em.createQuery("select c from ContentImpl c").getResultList().size());
        em.close();
    }
}
