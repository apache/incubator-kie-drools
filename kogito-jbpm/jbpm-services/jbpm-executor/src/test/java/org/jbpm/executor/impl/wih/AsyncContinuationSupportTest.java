/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.jbpm.executor.impl.wih;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.jbpm.test.util.ExecutorTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.executor.ExecutorService;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class AsyncContinuationSupportTest extends AbstractExecutorBaseTest {

    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;  
    private RuntimeManager manager;
    private ExecutorService executorService;
    private EntityManagerFactory emf = null;
    
    private long delay = 1000;
    @Before
    public void setup() {
        ExecutorTestUtil.cleanupSingletonSessionId();
        pds = ExecutorTestUtil.setupPoolingDataSource();
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        executorService = buildExecutorService();
    }
    
    @After
    public void teardown() {
        executorService.destroy();
        if (manager != null) {
            RuntimeManagerRegistry.get().remove(manager.getIdentifier());
            manager.close();
        }
        if (emf != null) {
        	emf.close();
        }
        pds.close();
    }

    @Test
    public void testAsyncScriptTask() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncScriptTask.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("AsyncScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNotNull(processInstance);
        
        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);
        
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(8, logs.size());
    } 
    
    @Test
    public void testNoAsyncServiceAvilableScriptTask() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncScriptTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("AsyncScriptTask");
        long processInstanceId = processInstance.getId();
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(8, logs.size());
    } 
    
    @Test
    public void testAsyncServiceTask() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ServiceProcess.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        handlers.put("Service Task", new ServiceTaskHandler());
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);   
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        
        ProcessInstance processInstance = ksession.startProcess("ServiceProcess", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(6, logs.size());
    } 
    
    @Test
    public void testAsyncMIUserTask() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-MultiInstanceLoopCharacteristicsTask.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);   
        
        ArrayList<String> items = new ArrayList<String>();
        items.add("one");
        items.add("two");
        items.add("three");
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", items);
        
        ProcessInstance processInstance = ksession.startProcess("MultiInstanceLoopCharacteristicsTask", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(delay);
        
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        Thread.sleep(delay);
        
        tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(2, tasks.size());
    
        Thread.sleep(delay);
        
        tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(3, tasks.size());
        
        for (TaskSummary task : tasks) {
            runtime.getTaskService().start(task.getId(), "john");
            runtime.getTaskService().complete(task.getId(), "john", null);
        }
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(6, logs.size());
    } 
    
    @Test
    public void testAsyncMISubProcess() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-MultiInstanceLoopCharacteristicsProcess.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);   
        
        ArrayList<String> items = new ArrayList<String>();
        items.add("one");
        items.add("two");
        items.add("three");
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", items);
        
        ProcessInstance processInstance = ksession.startProcess("MultiInstanceLoopCharacteristicsProcess", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
    
        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(26, logs.size());
    } 
    
    @Test
    public void testAsyncSubProcess() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-SubProcess.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);   
  
        
        Map<String, Object> params = new HashMap<String, Object>();  
        
        ProcessInstance processInstance = ksession.startProcess("SubProcess", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(delay);        
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(18, logs.size());
    } 
    
    @Test
    public void testSubProcessWithAsyncNodes() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-SubProcessAsyncNodes.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);   
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        ProcessInstance processInstance = ksession.startProcess("SubProcess", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
    
        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
    
        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(18, logs.size());
    } 
    
    @Test
    public void testSubProcessWithSomeAsyncNodes() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-SubProcessSomeAsyncNodes.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);   
        
        Map<String, Object> params = new HashMap<String, Object>();
        
        ProcessInstance processInstance = ksession.startProcess("SubProcess", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        Thread.sleep(delay);       
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(18, logs.size());
    } 
    
    @Test
    public void testAsyncCallActivityTask() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivity.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("ParentProcess");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(6, logs.size());
    } 
    
    
    @Test
    public void testAsyncAndSyncServiceTasks() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncServiceTask.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        handlers.put("Service Task", new ServiceTaskHandler());
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);     
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "john");
        
        ProcessInstance processInstance = ksession.startProcess("async-cont.async-service-task", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        
        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Thread.sleep(delay);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(14, logs.size());
    }
      
    
    private ExecutorService buildExecutorService() {        
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.setInterval((int)delay);
        executorService.setTimeunit(TimeUnit.MILLISECONDS);
        executorService.init();

        // let the executor start worker threads
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
        }
        
        return executorService;
    }
}
