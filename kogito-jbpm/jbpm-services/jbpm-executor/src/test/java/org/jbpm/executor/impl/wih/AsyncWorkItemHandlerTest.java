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

package org.jbpm.executor.impl.wih;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.drools.core.command.runtime.process.SetProcessInstanceVariablesCommand;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.commands.error.JobAutoAckErrorCommand;
import org.jbpm.executor.commands.error.ProcessAutoAckErrorCommand;
import org.jbpm.executor.commands.error.TaskAutoAckErrorCommand;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.objects.IncrementService;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.process.core.async.AsyncExecutionMarker;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.jbpm.services.task.exception.TaskExecutionException;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.jbpm.test.util.ExecutorTestUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
import org.kie.api.executor.STATUS;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.error.ExecutionErrorManager;
import org.kie.internal.runtime.error.ExecutionErrorStorage;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.EventService;

public class AsyncWorkItemHandlerTest extends AbstractExecutorBaseTest {

    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;  
    private RuntimeManager manager;
    private ExecutorService executorService;
    private EntityManagerFactory emf = null;
    
    private EntityManagerFactory emfErrors = null;
    @Before
    public void setup() {
        ExecutorTestUtil.cleanupSingletonSessionId();
        pds = ExecutorTestUtil.setupPoolingDataSource();
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
        executorService = buildExecutorService();
        
        emfErrors = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.complete");
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
        if (emfErrors != null) {
            emfErrors.close();
        }
        pds.close();
    }

    @Test(timeout=10000)
    public void testRunProcessWithAsyncHandler() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Hello", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        Thread.sleep(3000);
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
    
    @Test(timeout=10000)
    public void testRunProcessWithAsyncHandlerWithAbort() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTaskWithParams.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("delayAsync", "5s");
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", parameters);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        
        runtime.getKieSession().abortProcessInstance(processInstance.getId());
        
        countDownListener.waitTillCompleted();
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
    
    @Test(timeout=10000)
    public void testRunProcessWithAsyncHandlerDuplicatedRegister() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        countDownListener.waitTillCompleted();
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        manager.close();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);

    }
    
    @Test(timeout=10000)
    public void testRunProcessWithAsyncHandlerDelayed() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTaskWithParams.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("delayAsync", "4s");
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        countDownListener.waitTillCompleted();
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
    
    @Test(timeout=10000)
    public void testRunProcessWithAsyncHandlerAndReturnNullCommand() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.test.ReturnNullCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    } 
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        countDownListener.waitTillCompleted();
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
    
    @Test
    public void testRunProcessWithAsyncHandlerWithBusinessKey() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTaskWithBusinessKey.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        String businessKey = UUID.randomUUID().toString();
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("businessKey", businessKey);
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        Thread.sleep(3000);  
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        List<RequestInfo> jobRequest = executorService.getRequestsByBusinessKey(businessKey, new QueryContext());
        assertNotNull(jobRequest);
        assertEquals(1, jobRequest.size());
        assertEquals(businessKey, jobRequest.get(0).getKey());
        assertEquals(STATUS.DONE, jobRequest.get(0).getStatus());
    }
    
    @Test
    public void testRunProcessWithAsyncHandlerWithBusinessKeyAbort() throws Exception {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTaskWithBusinessKey.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        String businessKey = UUID.randomUUID().toString();
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("businessKey", businessKey);
        params.put("delay", "5s");
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        runtime.getKieSession().abortProcessInstance(processInstance.getId());
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        List<RequestInfo> jobRequest = executorService.getRequestsByBusinessKey(businessKey, new QueryContext());
        assertNotNull(jobRequest);
        assertEquals(1, jobRequest.size());
        assertEquals(businessKey, jobRequest.get(0).getKey());
        assertEquals(STATUS.CANCELLED, jobRequest.get(0).getStatus());
    }
    
    @Test(timeout=10000)
    public void testRunProcessWithAsyncHandlerProritizedJobs() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-PrioritizedAsyncTasks.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }

                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("async-examples.priority-jobs");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        List<RequestInfo> delayedPrintOuts = executorService.getRequestsByCommand("org.jbpm.executor.test.CustomCommand", new QueryContext());
        List<RequestInfo> printOuts = executorService.getRequestsByCommand("org.jbpm.executor.commands.PrintOutCommand", new QueryContext());
        
        assertEquals(1, delayedPrintOuts.size());
        assertEquals(1, printOuts.size());
        
        assertEquals(STATUS.QUEUED, delayedPrintOuts.get(0).getStatus());
        assertEquals(STATUS.QUEUED, printOuts.get(0).getStatus());
        
        countDownListener.waitTillCompleted();
        
        delayedPrintOuts = executorService.getRequestsByCommand("org.jbpm.executor.test.CustomCommand", new QueryContext());
        printOuts = executorService.getRequestsByCommand("org.jbpm.executor.commands.PrintOutCommand", new QueryContext());
        
        assertEquals(1, delayedPrintOuts.size());
        assertEquals(1, printOuts.size());
        
        assertEquals(STATUS.DONE, delayedPrintOuts.get(0).getStatus());
        assertEquals(STATUS.QUEUED, printOuts.get(0).getStatus());
        
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        
        delayedPrintOuts = executorService.getRequestsByCommand("org.jbpm.executor.test.CustomCommand", new QueryContext());
        printOuts = executorService.getRequestsByCommand("org.jbpm.executor.commands.PrintOutCommand", new QueryContext());
        
        assertEquals(1, delayedPrintOuts.size());
        assertEquals(1, printOuts.size());
        
        assertEquals(STATUS.DONE, delayedPrintOuts.get(0).getStatus());
        assertEquals(STATUS.DONE, printOuts.get(0).getStatus());
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
    
    @Test(timeout=10000)
    public void testRunProcessWithAsyncHandlerCallbackErrorRetry() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTaskWithError.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        assertEquals(0, IncrementService.get());
        
        Map<String, Object> params = new HashMap<String, Object>(); 
        params.put("retryAsync", "1s, 2s, 4s");
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        countDownListener.waitTillCompleted(2000);
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("x", "should be fixed now");
        ksession.execute(new SetProcessInstanceVariablesCommand(processInstance.getId(), variables));
        
        countDownListener.waitTillCompleted();
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        assertEquals(1, IncrementService.get());
    }
    
    @Test(timeout=10000)
    public void testRunProcessWithAsyncHandlerWthSecurityManager() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.commands.PrintOutCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment); 
        assertNotNull(manager);
        final AtomicBoolean active = new AtomicBoolean(false);
        ((InternalRuntimeManager) manager).setSecurityManager(new org.kie.internal.runtime.manager.SecurityManager() {
            
            @Override
            public void checkPermission() throws SecurityException {
                if (active.get() && !AsyncExecutionMarker.isAsync()) {
                    throw new SecurityException("Only async allowed");
                }
            }
            
        });
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        manager.disposeRuntimeEngine(runtime);
        // activate security manager to enforce checks for async only
        active.set(true);
        
        countDownListener.waitTillCompleted();
        // reset the security manager again...
        active.set(false);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        manager.disposeRuntimeEngine(runtime);
    }
    
    @Test(timeout=20000)
    public void testRunProcessWithAsyncHandlerCallbackErrorRetryUpdateJobData() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTaskWithRetryParam.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.test.MissingDataCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession); 
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("retryAsync", "1s, 2s, 3s");
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", params);
        Long processInstanceId = processInstance.getId();
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        countDownListener.waitTillCompleted(2000);
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        List<RequestInfo> requests = executorService.getRequestsByProcessInstance(processInstanceId, Arrays.asList(STATUS.RETRYING), new QueryContext());
        assertEquals(1, requests.size());
        
        Long requestId = requests.get(0).getId();
        
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("amount", 200);
        executorService.updateRequestData(requestId, variables);
        
        countDownListener.waitTillCompleted();
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertNull(processInstance);
        
        requests = executorService.getRequestsByProcessInstance(processInstanceId, Arrays.asList(STATUS.DONE), new QueryContext());
        assertEquals(1, requests.size());
    }
    
    @Test(timeout=10000)
    public void testRunProcessWithAsyncHandlerRecordExecutionError() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        ((ExecutorServiceImpl) executorService).setRetries(0);
        
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.ThrowExceptionCommand"));
                        return handlers;
                    }
              
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        countDownListener.waitTillCompleted();
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        List<RequestInfo> errorJobs = executorService.getInErrorRequests(new QueryContext());
        assertEquals(1, errorJobs.size());
        
        RequestInfo errorJob = errorJobs.get(0);
        assertEquals(errorJob.getProcessInstanceId().longValue(), processInstance.getId());
        
        ExecutionErrorManager errorManager = ((AbstractRuntimeManager) manager).getExecutionErrorManager();
        assertNotNull("ErrorManager is null", errorManager);
        ExecutionErrorStorage errorStorage = errorManager.getStorage();
        assertNotNull("ErrorStorage is null", errorStorage);
        
        List<ExecutionError> errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());
        
        ExecutionError error = errors.get(0);
        assertNotNull(error);
        assertEquals("Job", error.getType());
        assertEquals(errorJob.getId(), error.getJobId());
        assertEquals("ScriptTask", error.getProcessId());
        assertEquals("", error.getActivityName());
        assertEquals(manager.getIdentifier(), error.getDeploymentId());
        assertNotNull(error.getError());
        assertNotNull(error.getErrorMessage());
        assertNotNull(error.getActivityId());
        assertNotNull(error.getProcessInstanceId());
        
        assertNull(error.getAcknowledgedAt());
        assertNull(error.getAcknowledgedBy());
        assertFalse(error.isAcknowledged());
        
    }
    
    @Test(timeout=10000)
    public void testRunProcessWithAsyncHandlerSkipExecutionError() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Handling error", 1);
        ((ExecutorServiceImpl) executorService).setRetries(0);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTaskErrorHandling.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.ThrowExceptionCommand"));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        Map<String, Object> params = new HashMap<>();
        params.put("command", "org.jbpm.executor.ThrowExceptionCommand");
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        countDownListener.waitTillCompleted();
        

        List<RequestInfo> errorJobs = executorService.getInErrorRequests(new QueryContext());
        assertEquals(1, errorJobs.size());
        RequestInfo errorJob = errorJobs.get(0);
        assertEquals(errorJob.getProcessInstanceId().longValue(), processInstance.getId());
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);               
        
        ExecutionErrorManager errorManager = ((AbstractRuntimeManager) manager).getExecutionErrorManager();
        assertNotNull("ErrorManager is null", errorManager);
        ExecutionErrorStorage errorStorage = errorManager.getStorage();
        assertNotNull("ErrorStorage is null", errorStorage);
        
        List<ExecutionError> errors = errorStorage.list(0, 10);
        assertEquals(0, errors.size());
        
    }
    
    @Test(timeout=20000)
    public void testRunProcessWithAsyncHandlerRecordExecutionErrorAutoAck() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        ((ExecutorServiceImpl) executorService).setRetries(0);
        
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, "org.jbpm.executor.ThrowExceptionCommand"));
                        return handlers;
                    }
              
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        countDownListener.waitTillCompleted();
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        List<RequestInfo> errorJobs = executorService.getInErrorRequests(new QueryContext());
        assertEquals(1, errorJobs.size());
        
        RequestInfo errorJob = errorJobs.get(0);
        assertEquals(errorJob.getProcessInstanceId().longValue(), processInstance.getId());
        
        ExecutionErrorManager errorManager = ((AbstractRuntimeManager) manager).getExecutionErrorManager();
        assertNotNull("ErrorManager is null", errorManager);
        ExecutionErrorStorage errorStorage = errorManager.getStorage();
        assertNotNull("ErrorStorage is null", errorStorage);
        
        List<ExecutionError> errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());
        
        ExecutionError error = errors.get(0);
        assertNotNull(error);
        assertEquals("Job", error.getType());
        assertEquals(errorJob.getId(), error.getJobId());
        assertEquals("ScriptTask", error.getProcessId());
        assertEquals("", error.getActivityName());
        assertEquals(manager.getIdentifier(), error.getDeploymentId());
        assertNotNull(error.getError());
        assertNotNull(error.getErrorMessage());
        assertNotNull(error.getActivityId());
        assertNotNull(error.getProcessInstanceId());
        
        assertNull(error.getAcknowledgedAt());
        assertNull(error.getAcknowledgedBy());
        assertFalse(error.isAcknowledged());
        
        countDownListener.reset(1);
        // first run should not ack the job as it's in error state
        CommandContext ctx = new CommandContext();
        ctx.setData("SingleRun", "true");
        ctx.setData("EmfName", "org.jbpm.persistence.complete");
        executorService.scheduleRequest(JobAutoAckErrorCommand.class.getName(), ctx);
                
        countDownListener.waitTillCompleted();
        
        errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());
        
        error = errors.get(0);
        assertNotNull(error);        
        assertFalse(error.isAcknowledged());
                
        executorService.cancelRequest(errorJob.getId());        
        countDownListener.reset(1);
        // since job was canceled auto ack should work
        executorService.scheduleRequest(JobAutoAckErrorCommand.class.getName(), ctx);
                
        countDownListener.waitTillCompleted();
        
        errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());
        
        error = errors.get(0);
        assertNotNull(error);        
        assertTrue(error.isAcknowledged());
        
    }
    
    @Test(timeout=20000)
    public void testRunProcessWithAsyncHandlerRecordExecutionErrorProcessAutoAck() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        ((ExecutorServiceImpl) executorService).setRetries(0);
        
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTaskWithRollback.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("UserTaskWithRollback");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        
        TaskService taskService = runtime.getTaskService();
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
        
        manager.disposeRuntimeEngine(runtime);
        
        
        ExecutionErrorManager errorManager = ((AbstractRuntimeManager) manager).getExecutionErrorManager();
        assertNotNull("ErrorManager is null", errorManager);
        ExecutionErrorStorage errorStorage = errorManager.getStorage();
        assertNotNull("ErrorStorage is null", errorStorage);
        
        List<ExecutionError> errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());
        
        ExecutionError error = errors.get(0);
        assertNotNull(error);
        assertEquals("Process", error.getType());        
        assertEquals("UserTaskWithRollback", error.getProcessId());
        assertEquals("Script Task 1", error.getActivityName());
        assertEquals(manager.getIdentifier(), error.getDeploymentId());
        assertNotNull(error.getError());
        assertNotNull(error.getErrorMessage());
        assertNotNull(error.getActivityId());
        assertNotNull(error.getProcessInstanceId());
        
        assertNull(error.getAcknowledgedAt());
        assertNull(error.getAcknowledgedBy());
        assertFalse(error.isAcknowledged());
        
        countDownListener.reset(1);
        // first run should not ack the job as it's in error state
        CommandContext ctx = new CommandContext();
        ctx.setData("SingleRun", "true");
        ctx.setData("EmfName", "org.jbpm.persistence.complete");
        executorService.scheduleRequest(ProcessAutoAckErrorCommand.class.getName(), ctx);
                
        countDownListener.waitTillCompleted();
        
        errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());
        
        error = errors.get(0);
        assertNotNull(error);        
        assertFalse(error.isAcknowledged());
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        taskId = tasks.get(0).getId();
        
        results = new HashMap<>();
        results.put("output1", "ok");
        
        taskService.complete(taskId, "john", results);
        manager.disposeRuntimeEngine(runtime);
        countDownListener.reset(1);
        // since task was completed auto ack should work
        executorService.scheduleRequest(ProcessAutoAckErrorCommand.class.getName(), ctx);
                
        countDownListener.waitTillCompleted();
        
        errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());
        
        error = errors.get(0);
        assertNotNull(error);        
        assertTrue(error.isAcknowledged());
        
    }
    
    @SuppressWarnings("unchecked")
    @Test(timeout=20000)
    public void testRunProcessWithAsyncHandlerRecordExecutionErrorTaskAutoAck() throws Exception {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);
        ((ExecutorServiceImpl) executorService).setRetries(0);
        
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTaskWithRollback.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment); 
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        ProcessInstance processInstance = ksession.startProcess("UserTaskWithRollback");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        
        TaskService taskService = runtime.getTaskService();
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
                
        TaskLifeCycleEventListener listener = new DefaultTaskEventListener(){

            @Override
            public void afterTaskStartedEvent(TaskEvent event) {
                throw new TaskExecutionException("On purpose");
            }                
        };
        try {
            ((EventService<TaskLifeCycleEventListener>)taskService).registerTaskEventListener(listener);            
            
            taskService.start(taskId, "john");
            fail("Start task should fail due to broken script");
        } catch (Throwable e) {
            // expected
        }
        manager.disposeRuntimeEngine(runtime);
        
        ExecutionErrorManager errorManager = ((AbstractRuntimeManager) manager).getExecutionErrorManager();
        assertNotNull("ErrorManager is null", errorManager);
        ExecutionErrorStorage errorStorage = errorManager.getStorage();
        assertNotNull("ErrorStorage is null", errorStorage);
        
        List<ExecutionError> errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());
        
        ExecutionError error = errors.get(0);
        assertNotNull(error);
        assertEquals("Task", error.getType());        
        assertEquals("UserTaskWithRollback", error.getProcessId());
        assertEquals("Hello", error.getActivityName());
        assertEquals(manager.getIdentifier(), error.getDeploymentId());
        assertNotNull(error.getError());
        assertNotNull(error.getErrorMessage());
        assertNotNull(error.getActivityId());
        assertNotNull(error.getProcessInstanceId());
        
        assertNull(error.getAcknowledgedAt());
        assertNull(error.getAcknowledgedBy());
        assertFalse(error.isAcknowledged());
        
        countDownListener.reset(1);
        // first run should not ack the job as it's in error state
        CommandContext ctx = new CommandContext();
        ctx.setData("SingleRun", "true");
        ctx.setData("EmfName", "org.jbpm.persistence.complete");
        executorService.scheduleRequest(TaskAutoAckErrorCommand.class.getName(), ctx);
                
        countDownListener.waitTillCompleted();
        
        errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());
        
        error = errors.get(0);
        assertNotNull(error);        
        assertFalse(error.isAcknowledged());
                
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        tasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        taskId = tasks.get(0).getId();
        
        ((EventService<TaskLifeCycleEventListener>)taskService).removeTaskEventListener(listener);
        
        taskService.start(taskId, "john");
        
        Map<String, Object> results = new HashMap<>();
        results.put("output1", "ok");
        
        taskService.complete(taskId, "john", results);
        manager.disposeRuntimeEngine(runtime);        
        
        countDownListener.reset(1);
        // since task was completed auto ack should work
        executorService.scheduleRequest(TaskAutoAckErrorCommand.class.getName(), ctx);
                
        countDownListener.waitTillCompleted();
        
        errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());
        
        error = errors.get(0);
        assertNotNull(error);        
        assertTrue(error.isAcknowledged());
        
    }
    
    private ExecutorService buildExecutorService() {        
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        
        executorService.init();
        
        return executorService;
    }
}
