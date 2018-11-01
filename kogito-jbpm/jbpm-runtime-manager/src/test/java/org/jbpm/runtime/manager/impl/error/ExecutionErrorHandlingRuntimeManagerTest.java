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

package org.jbpm.runtime.manager.impl.error;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.jbpm.services.task.exception.TaskExecutionException;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.error.ExecutionErrorManager;
import org.kie.internal.runtime.error.ExecutionErrorStorage;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.EventService;
import org.kie.internal.task.api.UserGroupCallback;

@RunWith(Parameterized.class)
public class ExecutionErrorHandlingRuntimeManagerTest extends AbstractBaseTest {
    
    @Parameters(name = "Strategy : {0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {     
                 {"singleton"}, 
                 {"request"},
                 {"processinstance"},
                 {"case"}
           });
    }
    
    private String strategy;
       
    public ExecutionErrorHandlingRuntimeManagerTest(String strategy) {
        this.strategy = strategy;
    }
    
    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;
    private EntityManagerFactory emf;
    private RuntimeManager manager;
    
    @Rule 
    public TestName testName = new TestName();
    
    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.jpa");
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        
        createRuntimeManager();
    }
    
    @After
    public void teardown() {
        if (manager != null) {
            manager.close();
        }
        EntityManagerFactoryManager.get().clear();
        pds.close();
    }
    
    private void createRuntimeManager() {
        RuntimeEnvironment environment = createEnvironment();        
        if ("singleton".equals(strategy)) {
            manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "first");
        } else if ("processinstance".equals(strategy)) {
            manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment, "first");
        } else if ("request".equals(strategy)) {
            manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment, "first");
        } else if ("case".equals(strategy)) {
            manager = RuntimeManagerFactory.Factory.get().newPerCaseRuntimeManager(environment, "first");
        }
        assertNotNull(manager);        
    }
    
    @Test
    public void testBasicScriptFailure() {
            
        RuntimeEngine runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        assertNotNull(ksession1);                 
        
        try {
            ksession1.startProcess("BrokenScriptTask");
            fail("Start process should fail due to broken script");
        } catch (Throwable e) {
            // expected
        }
        manager.disposeRuntimeEngine(runtime1);
       
        ExecutionErrorManager errorManager = ((AbstractRuntimeManager) manager).getExecutionErrorManager();
        ExecutionErrorStorage storage = errorManager.getStorage();
        
        List<ExecutionError> errors = storage.list(0, 10);
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertExecutionError(errors.get(0), "Process", "BrokenScriptTask", "Hello");
      
    }
    
    @Test
    public void testScriptFailureAfterUserTask() {
        
        RuntimeEngine runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        assertNotNull(ksession1);                 
        
        ProcessInstance pi = ksession1.startProcess("UserTaskWithRollback");
        
        manager.disposeRuntimeEngine(runtime1);
        
        runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi.getId()));
        ksession1 = runtime1.getKieSession();
        
        TaskService taskService = runtime1.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        taskService.start(taskId, "john");
        
        Map<String, Object> results = new HashMap<>();
        results.put("output1", "rollback");
        
        try {
            taskService.complete(taskId, "john", results);
            fail("Complete task should fail due to broken script");
        } catch (Throwable e) {
            // expected
        }
                 
        manager.disposeRuntimeEngine(runtime1);
       
        ExecutionErrorManager errorManager = ((AbstractRuntimeManager) manager).getExecutionErrorManager();
        ExecutionErrorStorage storage = errorManager.getStorage();
        
        List<ExecutionError> errors = storage.list(0, 10);
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertExecutionError(errors.get(0), "Process", "UserTaskWithRollback", "Script Task 1");
   
    }
    
    

    @SuppressWarnings("unchecked")
    @Test
    public void testUserTaskFailure() {
        
        RuntimeEngine runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        assertNotNull(ksession1);                 
        
        ksession1.startProcess("UserTaskWithRollback");
        
        TaskService taskService = runtime1.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        try {
            ((EventService<TaskLifeCycleEventListener>)taskService).registerTaskEventListener(new DefaultTaskEventListener(){

                @Override
                public void afterTaskStartedEvent(TaskEvent event) {
                    throw new TaskExecutionException("On purpose");
                }                
            });            
            
            taskService.start(taskId, "john");
            fail("Start task should fail due to broken script");
        } catch (Throwable e) {
            // expected
        }
                 
        manager.disposeRuntimeEngine(runtime1);
       
        ExecutionErrorManager errorManager = ((AbstractRuntimeManager) manager).getExecutionErrorManager();
        ExecutionErrorStorage storage = errorManager.getStorage();
        
        List<ExecutionError> errors = storage.list(0, 10);
        assertNotNull(errors);
        assertEquals(1, errors.size());        
        assertExecutionError(errors.get(0), "Task", "UserTaskWithRollback", "Hello");

    }
        
    @Test
    public void testDataBaseFailureInMemoryStorage() {
            
        RuntimeEngine runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        assertNotNull(ksession1);                 
        
        ksession1.addEventListener(new DefaultProcessEventListener(){

            @Override
            public void afterProcessStarted(ProcessStartedEvent event) {
                pds.close();
            }
            
        });
        
        try {
            ksession1.startProcess("UserTaskWithRollback");
            fail("Start process should fail due to data base error");
        } catch (Throwable e) {
            // expected
        }
        int expectedErrors = 1;
        try {
            manager.disposeRuntimeEngine(runtime1);
        } catch (Exception e) {
            // expected to fail for some strategies due to data source being down
            expectedErrors++;
        }
       
        ExecutionErrorManager errorManager = ((AbstractRuntimeManager) manager).getExecutionErrorManager();
        ExecutionErrorStorage storage = errorManager.getStorage();
        
        List<ExecutionError> errors = storage.list(0, 10);
        assertNotNull(errors);
        assertTrue(errors.size() >= expectedErrors);
        assertExecutionError(errors.get(0), "DB", "UserTaskWithRollback", "Hello");      

    }
    
    @Test
    public void testFailureAfterUserTaskNoWorkItemHandler() {
        
        RuntimeEngine runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        assertNotNull(ksession1);                 
        
        ProcessInstance pi = ksession1.startProcess("UserTaskWithCustomTask");
        
        manager.disposeRuntimeEngine(runtime1);
        
        runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi.getId()));
        ksession1 = runtime1.getKieSession();
        
        TaskService taskService = runtime1.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        taskService.start(taskId, "john");
        
        Map<String, Object> results = new HashMap<>();
        results.put("output1", "rollback");
        
        try {
            taskService.complete(taskId, "john", results);
            fail("Complete task should fail due to no work item handler found error");
        } catch (Throwable e) {
            // expected
        }
                 
        manager.disposeRuntimeEngine(runtime1);
       
        ExecutionErrorManager errorManager = ((AbstractRuntimeManager) manager).getExecutionErrorManager();
        ExecutionErrorStorage storage = errorManager.getStorage();
        
        List<ExecutionError> errors = storage.list(0, 10);
        assertNotNull(errors);
        assertEquals(1, errors.size());
        assertExecutionError(errors.get(0), "Process", "UserTaskWithCustomTask", "Manual Task 2");
        String errorMessage = errors.get(0).getErrorMessage();
        assertTrue(errorMessage.contains("Could not find work item handler for Manual Task"));
    }
    
    private RuntimeEnvironment createEnvironment() {
        
        ExecutionErrorStorage storage = new ExecutionErrorStorage() {
            
            private List<ExecutionError> errors = new ArrayList<>();
            @Override
            public ExecutionError store(ExecutionError error) {
                this.errors.add(error);
                return error;
            }
            
            @Override
            public List<ExecutionError> listByProcessInstance(Long processInstanceId, Integer page, Integer pageSize) {
                return errors;
            }
            
            @Override
            public List<ExecutionError> listByDeployment(String deploymentId, Integer page, Integer pageSize) {
                return errors;
            }
            
            @Override
            public List<ExecutionError> listByActivity(String activityName, Integer page, Integer pageSize) {
                return errors;
            }
            
            @Override
            public List<ExecutionError> list(Integer page, Integer pageSize) {
                return errors;
            }
            
            @Override
            public ExecutionError get(String errorId) {                
                return errors.stream().filter(err -> err.getErrorId().equals(errorId)).findFirst().get();
            }
            
            @Override
            public void acknowledge(String user, String... errorIds) {
                for (String errorId : errorIds) {
                    ExecutionError error = get(errorId);
                    error.setAcknowledged(true);
                    error.setAcknowledgedBy(user);
                    error.setAcknowledgedAt(new Date());
                }
            }
        };
        
        RuntimeEnvironmentBuilder environmentBuilder = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .entityManagerFactory(emf)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-BrokenScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTaskWithRollback.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTaskCustomTask.bpmn2"), ResourceType.BPMN2);
        
        if (testName.getMethodName().contains("InMemoryStorage")) {
            environmentBuilder.addEnvironmentEntry("ExecutionErrorStorage", storage);
        }
        
        return environmentBuilder.get();
    }
    
    private void assertExecutionError(ExecutionError error, String type, String processId, String activityName) {        
        assertNotNull(error);
        assertEquals(type, error.getType());
        assertEquals(processId, error.getProcessId());
        assertEquals(activityName, error.getActivityName());
        assertEquals(manager.getIdentifier(), error.getDeploymentId());
        assertNotNull(error.getError());
        assertNotNull(error.getErrorMessage());
        assertNotNull(error.getActivityId());
        assertNotNull(error.getProcessInstanceId());
        
        assertNull(error.getAcknowledgedAt());
        assertNull(error.getAcknowledgedBy());
        assertFalse(error.isAcknowledged());
    }
}
