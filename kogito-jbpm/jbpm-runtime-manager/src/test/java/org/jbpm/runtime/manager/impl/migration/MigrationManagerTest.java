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

package org.jbpm.runtime.manager.impl.migration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.UserGroupCallback;

public class MigrationManagerTest extends AbstractBaseTest {

    private PoolingDataSource pds;
    private EntityManagerFactory emf;
    private UserGroupCallback userGroupCallback;  
    private RuntimeManager managerV1;
    private RuntimeManager managerV2;
    
    // general info
    private static final String DEPLOYMENT_ID_V1 = "managerV1";
    private static final String DEPLOYMENT_ID_V2 = "managerV2";
    
    private static final String USER_JOHN = "john";
    
    // simple user task process
    private static final String PROCESS_ID_V1 = "UserTask-1";
    private static final String PROCESS_ID_V2 = "UserTask-2";
    
    private static final String PROCESS_NAME_V1 = "User Task v1";
    private static final String PROCESS_NAME_V2 = "User Task v2";
    
    private static final String TASK_NAME_V1 = "Hello v1";
    private static final String TASK_NAME_V2 = "Hello v2";
    
    private static final String ADDTASKAFTERACTIVE_ID_V1 = "process-migration-testv1.AddTaskAfterActive";
    private static final String ADDTASKAFTERACTIVE_ID_V2 = "process-migration-testv2.AddTaskAfterActive";
    
    private static final String ADDTASKBEFOREACTIVE_ID_V1 = "process-migration-testv1.AddTaskBeforeActive";
    private static final String ADDTASKBEFOREACTIVE_ID_V2 = "process-migration-testv2.AddTaskBeforeActive";
    
    private static final String REMOVEACTIVETASK_ID_V1 = "process-migration-testv1.RemoveActiveTask";
    private static final String REMOVEACTIVETASK_ID_V2 = "process-migration-testv2.RemoveActiveTask";
    
    private static final String REMOVENONACTIVETASK_ID_V1 = "process-migration-testv1.RemoveNonActiveTask";
    private static final String REMOVENONACTIVETASK_ID_V2 = "process-migration-testv2.RemoveNonActiveTask";
    
    private static final String REPLACEACTIVETASK_ID_V1 = "process-migration-testv1.ReplaceActiveTask";
    private static final String REPLACEACTIVETASK_ID_V2 = "process-migration-testv2.ReplaceActiveTask";
    
    private static final String REMOVENONACTIVEBEFORETASK_ID_V1 = "process-migration-testv1.RemoveNonActiveBeforeTask";
    private static final String REMOVENONACTIVEBEFORETASK_ID_V2 = "process-migration-testv2.RemoveNonActiveBeforeTask";
    
    private static final String MULTIINSTANCE_ID_V1 = "MultiInstance-1";
    private static final String MULTIINSTANCE_ID_V2 = "MultiInstance-2";

    private JPAAuditLogService auditService;
    
    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
        
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.jpa");
        
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        
        auditService = new JPAAuditLogService(emf);
    }
    
    @After
    public void teardown() {
        auditService.dispose();
        
        if (managerV1 != null) {
            managerV1.close();
        }
        if (managerV2 != null) {
            managerV2.close();
        }
        EntityManagerFactoryManager.get().clear();
        pds.close();
    }
    
    @Test
    public void testMigrateUserTaskProcessInstance() {
        createRuntimeManagers("migration/v1/BPMN2-UserTask-v1.bpmn2", "migration/v2/BPMN2-UserTask-v2.bpmn2");
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(PROCESS_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState()); 
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(PROCESS_ID_V1, log.getProcessId());
        assertEquals(PROCESS_NAME_V1, log.getProcessName());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
        
        TaskService taskService = runtime.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(USER_JOHN, "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        TaskSummary task = tasks.get(0);
        assertNotNull(task);
        
        assertEquals(PROCESS_ID_V1, task.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, task.getDeploymentId());
        assertEquals(TASK_NAME_V1, task.getName());
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, PROCESS_ID_V2);
        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();
        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(PROCESS_ID_V2, log.getProcessId());
        assertEquals(PROCESS_NAME_V2, log.getProcessName());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        auditService.dispose();
        
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        taskService = runtime.getTaskService();
        
        tasks = taskService.getTasksAssignedAsPotentialOwner(USER_JOHN, "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        task = tasks.get(0);
        assertNotNull(task);
        
        assertEquals(PROCESS_ID_V2, task.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, task.getDeploymentId());
        assertEquals(TASK_NAME_V2, task.getName()); // same name as the node mapping was not given
        
        managerV2.disposeRuntimeEngine(runtime);
    }
    
    @Test
    public void testMigrateUserTaskProcessInstanceWithNodeMapping() {
        createRuntimeManagers("migration/v1/BPMN2-UserTask-v1.bpmn2", "migration/v2/BPMN2-UserTask-v2.bpmn2");
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(PROCESS_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());   
        
        TaskService taskService = runtime.getTaskService();        
        TaskSummary task = getTask(taskService);
        managerV1.disposeRuntimeEngine(runtime);
        
        assertEquals(PROCESS_ID_V1, task.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, task.getDeploymentId());
        assertEquals(TASK_NAME_V1, task.getName());        
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, PROCESS_ID_V2);
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate(Collections.singletonMap("_2", "_2"));        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        assertMigratedProcessInstance(PROCESS_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
                
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        taskService = runtime.getTaskService();        
        assertMigratedTaskAndComplete(taskService, PROCESS_ID_V2, pi1.getId(), TASK_NAME_V2);
        
        assertMigratedProcessInstance(PROCESS_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
        
        managerV2.disposeRuntimeEngine(runtime);
    }
    
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Test
    public void testMigrateUserTaskProcessInstanceWithRollback() {
        createRuntimeManagers("migration/v1/BPMN2-UserTask-v1.bpmn2", "migration/v2/BPMN2-UserTask-v2.bpmn2");
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(PROCESS_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());        
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, PROCESS_ID_V2);        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = null;              
        try {
            // explicitly without generic to cause error (class cast) in migration process to test rollback
            Map erronousMapping = Collections.singletonMap("_2", 2);            
            migrationManager.migrate(erronousMapping);
        } catch (MigrationException e) {
           report = e.getReport(); 
        }        
        assertNotNull(report);
        assertFalse(report.isSuccessful());
        
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(PROCESS_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
        
        auditService.dispose();
    }
    
    @Test
    public void testAddTaskAfterActive() {
        createRuntimeManagers("migration/v1/AddTaskAfterActive-v1.bpmn2", "migration/v2/AddTaskAfterActive-v2.bpmn2");
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(ADDTASKAFTERACTIVE_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());        
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, ADDTASKAFTERACTIVE_ID_V2);        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        assertMigratedProcessInstance(ADDTASKAFTERACTIVE_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
                
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = runtime.getTaskService();        
        assertMigratedTaskAndComplete(taskService, ADDTASKAFTERACTIVE_ID_V2, pi1.getId(), "Active Task");

        managerV2.disposeRuntimeEngine(runtime);        

        assertMigratedProcessInstance(ADDTASKAFTERACTIVE_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
        
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        taskService = runtime.getTaskService();
        assertMigratedTaskAndComplete(taskService, ADDTASKAFTERACTIVE_ID_V2, pi1.getId(), "Added Task");
        
        assertMigratedProcessInstance(ADDTASKAFTERACTIVE_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
        managerV2.disposeRuntimeEngine(runtime);
    }
    
    @Test
    public void testAddTaskBeforeActive() {
        
        String activeNodeId = "_18771A1A-9DB9-4CA1-8C2E-19DEE24A1776";
        String addedNodeId = "_94643E69-BD97-4E4A-8B4A-364FEB95CA3C";
        
        createRuntimeManagers("migration/v1/AddTaskBeforeActive-v1.bpmn2", "migration/v2/AddTaskBeforeActive-v2.bpmn2");
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(ADDTASKBEFOREACTIVE_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());        
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, ADDTASKBEFOREACTIVE_ID_V2);        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate(Collections.singletonMap(activeNodeId, addedNodeId));        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        assertMigratedProcessInstance(ADDTASKBEFOREACTIVE_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
                
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = runtime.getTaskService();        
        assertMigratedTaskAndComplete(taskService, ADDTASKBEFOREACTIVE_ID_V2, pi1.getId(), "Added Task");
        managerV2.disposeRuntimeEngine(runtime);
        
        assertMigratedProcessInstance(ADDTASKBEFOREACTIVE_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
        
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        taskService = runtime.getTaskService();
        assertMigratedTaskAndComplete(taskService, ADDTASKBEFOREACTIVE_ID_V2, pi1.getId(), "Active Task");
        managerV2.disposeRuntimeEngine(runtime);
        
        assertMigratedProcessInstance(ADDTASKBEFOREACTIVE_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);        
    }
    
    @Test
    public void testRemoveActiveTask() {
        
        String activeNodeId = "_ECEDD1CE-7380-418C-B7A6-AF8ECB90B820";
        String nextNodeId = "_9EF3CAE0-D978-4E96-9C00-8A80082EB68E";
        
        createRuntimeManagers("migration/v1/RemoveActiveTask-v1.bpmn2", "migration/v2/RemoveActiveTask-v2.bpmn2");
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(REMOVEACTIVETASK_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());        
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, REMOVEACTIVETASK_ID_V2);        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate(Collections.singletonMap(activeNodeId, nextNodeId));        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        assertMigratedProcessInstance(REMOVEACTIVETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
                
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = runtime.getTaskService();        
        
        assertMigratedTaskAndComplete(taskService, REMOVEACTIVETASK_ID_V2, pi1.getId(), "Mapped Task");
        
        assertMigratedProcessInstance(REMOVEACTIVETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
        
        managerV2.disposeRuntimeEngine(runtime);
    }
    
    @Test
    public void testRemoveNonActiveTask() {
        
        createRuntimeManagers("migration/v1/RemoveNonActiveTask-v1.bpmn2", "migration/v2/RemoveNonActiveTask-v2.bpmn2");
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(REMOVENONACTIVETASK_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());        
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, REMOVENONACTIVETASK_ID_V2);        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        assertMigratedProcessInstance(REMOVENONACTIVETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
                
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = runtime.getTaskService();        

        assertMigratedTaskAndComplete(taskService, REMOVENONACTIVETASK_ID_V2, pi1.getId(), "Active Task");                
        assertMigratedProcessInstance(REMOVENONACTIVETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
        
        managerV2.disposeRuntimeEngine(runtime);
    }
    
    @Test
    public void testReplaceActiveTask() {
        
        String activeNodeId = "_E9140EE9-1B5A-46B1-871E-A735402B69F4";
        String replaceNodeId = "_9B25FCC5-C718-4941-A4AE-DD8D6E368F48";
        
        createRuntimeManagers("migration/v1/ReplaceActiveTask-v1.bpmn2", "migration/v2/ReplaceActiveTask-v2.bpmn2");
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(REPLACEACTIVETASK_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());        
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, REPLACEACTIVETASK_ID_V2);        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate(Collections.singletonMap(activeNodeId, replaceNodeId));        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        assertMigratedProcessInstance(REPLACEACTIVETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
                
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = runtime.getTaskService();        
 
        assertMigratedTaskAndComplete(taskService, REPLACEACTIVETASK_ID_V2, pi1.getId(), "Mapped Task");                
        assertMigratedProcessInstance(REPLACEACTIVETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
              
        managerV2.disposeRuntimeEngine(runtime);
    }
    
    @Test
    public void testRemoveNonActiveBeforeActiveTask() {
        
        createRuntimeManagers("migration/v1/RemoveNonActiveBeforeTask-v1.bpmn2", "migration/v2/RemoveNonActiveBeforeTask-v2.bpmn2");
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(REMOVENONACTIVEBEFORETASK_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());   
        
        assertTaskAndComplete(runtime.getTaskService(), REMOVENONACTIVEBEFORETASK_ID_V1, pi1.getId(), "Active Task"); 
        
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, REMOVENONACTIVEBEFORETASK_ID_V2);        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        MigrationReport report = migrationManager.migrate();        
        assertNotNull(report);
        assertTrue(report.isSuccessful());
        
        assertMigratedProcessInstance(REMOVENONACTIVEBEFORETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_ACTIVE);
                
        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        TaskService taskService = runtime.getTaskService();        

        assertMigratedTaskAndComplete(taskService, REMOVENONACTIVEBEFORETASK_ID_V2, pi1.getId(), "Non-active Task");                
        assertMigratedProcessInstance(REMOVENONACTIVEBEFORETASK_ID_V2, pi1.getId(), ProcessInstance.STATE_COMPLETED);
        
        managerV2.disposeRuntimeEngine(runtime);
    }
    
    @Test
    public void testMigrateUserTaskProcessInstanceDifferentRuntimeManagers() {        
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("migration/v1/BPMN2-UserTask-v1.bpmn2"), ResourceType.BPMN2)
                .get();
        
        managerV1 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, DEPLOYMENT_ID_V1);  
        
        RuntimeEnvironment environment2 = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("migration/v2/BPMN2-UserTask-v2.bpmn2"), ResourceType.BPMN2)
                .get();
        
        managerV2 = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment2, DEPLOYMENT_ID_V2); 
        
        assertNotNull(managerV1);
        assertNotNull(managerV2);
        
        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance pi1 = ksession.startProcess(PROCESS_ID_V1);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState()); 
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(PROCESS_ID_V1, log.getProcessId());
        assertEquals(PROCESS_NAME_V1, log.getProcessName());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
        
        TaskService taskService = runtime.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(USER_JOHN, "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        TaskSummary task = tasks.get(0);
        assertNotNull(task);
        
        assertEquals(PROCESS_ID_V1, task.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, task.getDeploymentId());
        assertEquals(TASK_NAME_V1, task.getName());
        managerV1.disposeRuntimeEngine(runtime);
        
        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, PROCESS_ID_V2);
        
        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        
        try {
            migrationManager.migrate();
        } catch (MigrationException e) {
            assertEquals("Source (org.jbpm.runtime.manager.impl.SingletonRuntimeManager) and target (org.jbpm.runtime.manager.impl.PerProcessInstanceRuntimeManager) deployments are of different type (they represent different runtime strategies)", e.getMessage());
        }
        
        runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        assertNotNull(ksession); 
               
        auditService = new JPAAuditLogService(emf);
        log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(PROCESS_ID_V1, log.getProcessId());
        assertEquals(PROCESS_NAME_V1, log.getProcessName());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());
        
        taskService = runtime.getTaskService();
        tasks = taskService.getTasksAssignedAsPotentialOwner(USER_JOHN, "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        task = tasks.get(0);
        assertNotNull(task);
        
        assertEquals(PROCESS_ID_V1, task.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, task.getDeploymentId());
        assertEquals(TASK_NAME_V1, task.getName());
        managerV1.disposeRuntimeEngine(runtime);
        
    }

    @Test
    public void testMigrateMultiInstance() {
        createRuntimeManagers("migration/v1/BPMN2-MultiInstance-v1.bpmn2", "migration/v2/BPMN2-MultiInstance-v2.bpmn2");
        assertNotNull(managerV1);
        assertNotNull(managerV2);

        RuntimeEngine runtime = managerV1.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        List<String> processVar1 = new ArrayList<String>();
        processVar1.add("one");
        processVar1.add("two");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("processVar1", processVar1);
        ProcessInstance pi1 = ksession.startProcess(MULTIINSTANCE_ID_V1, params);
        assertNotNull(pi1);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        JPAAuditLogService auditService = new JPAAuditLogService(emf);
        ProcessInstanceLog log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(MULTIINSTANCE_ID_V1, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, log.getExternalId());

        TaskService taskService = runtime.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(USER_JOHN, "en-UK");
        assertNotNull(tasks);
        assertEquals(2, tasks.size());

        TaskSummary task = tasks.get(0);
        assertNotNull(task);

        assertEquals(MULTIINSTANCE_ID_V1, task.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, task.getDeploymentId());
        managerV1.disposeRuntimeEngine(runtime);

        MigrationSpec migrationSpec = new MigrationSpec(DEPLOYMENT_ID_V1, pi1.getId(), DEPLOYMENT_ID_V2, MULTIINSTANCE_ID_V2);

        MigrationManager migrationManager = new MigrationManager(migrationSpec);
        Map<String, String> nodeMapping = new HashMap<>();
        nodeMapping.put("_2", "UserTask_1");
        MigrationReport report = migrationManager.migrate(nodeMapping);

        assertNotNull(report);
        assertTrue(report.isSuccessful());

        log = auditService.findProcessInstance(pi1.getId());
        assertNotNull(log);
        assertEquals(MULTIINSTANCE_ID_V2, log.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, log.getExternalId());
        auditService.dispose();

        runtime = managerV2.getRuntimeEngine(EmptyContext.get());
        taskService = runtime.getTaskService();

        tasks = taskService.getTasksAssignedAsPotentialOwner(USER_JOHN, "en-UK");
        assertNotNull(tasks);
        assertEquals(2, tasks.size());

        task = tasks.get(0);
        assertNotNull(task);

        assertEquals(MULTIINSTANCE_ID_V2, task.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, task.getDeploymentId());

        managerV2.disposeRuntimeEngine(runtime);
    }
    
    /*
     * Helper methods
     */
    
    protected TaskSummary getTask(TaskService taskService) {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner(USER_JOHN, "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        TaskSummary task = tasks.get(0);
        assertNotNull(task);
        
        return task;
    }
    
    protected void assertTaskAndComplete(TaskService taskService, String processId, Long processInstanceId, String taskName) {
        List<TaskSummary> tasks = taskService.getTasksByStatusByProcessInstanceId(processInstanceId, Arrays.asList(Status.Reserved), "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        TaskSummary task = tasks.get(0);
        assertNotNull(task);
        assertEquals(processId, task.getProcessId());
        assertEquals(DEPLOYMENT_ID_V1, task.getDeploymentId());
        assertEquals(taskName, task.getName());
        
        taskService.start(task.getId(), USER_JOHN);
        taskService.complete(task.getId(), USER_JOHN, null);
    }
    
    protected void assertMigratedTaskAndComplete(TaskService taskService, String processId, Long processInstanceId, String taskName) {
        List<TaskSummary> tasks = taskService.getTasksByStatusByProcessInstanceId(processInstanceId, Arrays.asList(Status.Reserved), "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());
        
        TaskSummary task = tasks.get(0);
        assertNotNull(task);
        assertEquals(processId, task.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, task.getDeploymentId());
        assertEquals(taskName, task.getName());
        
        taskService.start(task.getId(), USER_JOHN);
        taskService.complete(task.getId(), USER_JOHN, null);
    }

    protected void assertMigratedProcessInstance(String processId, long processInstanceId, int status) {

        ProcessInstanceLog instance = auditService.findProcessInstance(processInstanceId);
        assertNotNull(instance);
        assertEquals(processId, instance.getProcessId());
        assertEquals(DEPLOYMENT_ID_V2, instance.getExternalId());
        assertEquals(status, instance.getStatus().intValue());
    }
    
    protected void createRuntimeManagers(String processV1, String processV2) {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource(processV1), ResourceType.BPMN2)
                .get();
        
        managerV1 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, DEPLOYMENT_ID_V1);  
        
        RuntimeEnvironment environment2 = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource(processV2), ResourceType.BPMN2)
                .get();
        
        managerV2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment2, DEPLOYMENT_ID_V2); 
    }
}
