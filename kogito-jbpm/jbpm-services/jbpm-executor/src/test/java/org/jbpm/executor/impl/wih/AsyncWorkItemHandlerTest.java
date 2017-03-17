/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.objects.IncrementService;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.process.core.async.AsyncExecutionMarker;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.jbpm.test.util.CountDownProcessEventListener;
import org.jbpm.test.util.ExecutorTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.event.process.ProcessEventListener;
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
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class AsyncWorkItemHandlerTest extends AbstractExecutorBaseTest {

    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;  
    private RuntimeManager manager;
    private ExecutorService executorService;
    private EntityManagerFactory emf = null;
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

    @Test(timeout=10000)
    public void testRunProcessWithAsyncHandler() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1);
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
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Task 1", 1);
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
        
        
        runtime.getKieSession().abortProcessInstance(processInstance.getId());
        
        countDownListener.waitTillCompleted();
        
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
    
    @Test(timeout=10000)
    public void testRunProcessWithAsyncHandlerDuplicatedRegister() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Task 1", 1);
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
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Task 1", 1);
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
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Task 1", 1);
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
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Task 1", 1);
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
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        countDownListener.waitTillCompleted(4000);
        
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
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Task 1", 1);
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
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
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
        
        ProcessInstance processInstance = ksession.startProcess("ScriptTask", params);
        Long processInstanceId = processInstance.getId();
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        countDownListener.waitTillCompleted(4000);
        
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
    
    private ExecutorService buildExecutorService() {        
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        
        executorService.init();
        
        return executorService;
    }
}
