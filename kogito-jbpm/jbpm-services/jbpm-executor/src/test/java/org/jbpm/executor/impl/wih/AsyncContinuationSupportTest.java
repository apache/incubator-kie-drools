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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.RequeueAware;
import org.jbpm.executor.commands.PrintOutCommand;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.process.core.async.AsyncSignalEventCommand;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.listener.process.NodeTriggeredCountDownProcessEventListener;
import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.jbpm.test.util.ExecutorTestUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeTriggeredEvent;
import org.kie.api.executor.ExecutorService;
import org.kie.api.executor.RequestInfo;
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
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncContinuationSupportTest extends AbstractExecutorBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AsyncContinuationSupportTest.class);
    
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
    public void testAsyncScriptTask() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Hello", 1);
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

        ProcessInstance processInstance = ksession.startProcess("AsyncScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(8, logs.size());
    }

    @Test
    public void testNoAsyncServiceAvailableScriptTask() throws Exception {

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

    @Test(timeout=10000)
    public void testAsyncServiceTask() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Hello", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncServiceProcess.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        handlers.put("Service Task", new ServiceTaskHandler());
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
        params.put("s", "john");

        ProcessInstance processInstance = ksession.startProcess("AsyncServiceProcess", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(6, logs.size());
    }

    @Test(timeout=10000)
    public void testAsyncMIUserTask() throws Exception {
        final NodeTriggeredCountDownProcessEventListener countDownListener = new NodeTriggeredCountDownProcessEventListener("Hello", 3);
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

        ArrayList<String> items = new ArrayList<String>();
        items.add("one");
        items.add("two");
        items.add("three");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", items);

        ProcessInstance processInstance = ksession.startProcess("MultiInstanceLoopCharacteristicsTask", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
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
        assertEquals(12, logs.size());
    }

    @Test(timeout=10000)
    public void testAsyncMISubProcess() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Hello", 3);
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

        ArrayList<String> items = new ArrayList<String>();
        items.add("one");
        items.add("two");
        items.add("three");

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", items);

        ProcessInstance processInstance = ksession.startProcess("MultiInstanceLoopCharacteristicsProcess", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(26, logs.size());
    }

    @Test(timeout=10000)
    public void testAsyncSubProcess() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Hello", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncSubProcess.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
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

        ProcessInstance processInstance = ksession.startProcess("AsyncSubProcess", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(18, logs.size());
    }

    @Test(timeout=10000)
    public void testSubProcessWithAsyncNodes() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("EndProcess", 1);
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

        ProcessInstance processInstance = ksession.startProcess("SubProcess", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        
        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(18, logs.size());
    }

    @Test(timeout=10000)
    public void testSubProcessWithSomeAsyncNodes() throws Exception {

        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Goodbye", 1);
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

        ProcessInstance processInstance = ksession.startProcess("SubProcess", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(18, logs.size());
    }

    @Test(timeout=10000)
    public void testAsyncCallActivityTask() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("CallActivity", 1);
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

        ProcessInstance processInstance = ksession.startProcess("ParentProcess");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
    
        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(6, logs.size());
    }


    @Test(timeout=10000)
    public void testAsyncAndSyncServiceTasks() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Async Service", 3);
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
        params.put("name", "john");

        ProcessInstance processInstance = ksession.startProcess("async-cont.async-service-task", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(14, logs.size());
    }

    @Test(timeout=1000000)
    public void testAsyncScriptTaskIgnoreNotExistingDeployments() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Hello", 1);
        final NodeLeftCountDownProcessEventListener countDownListener2 = new NodeLeftCountDownProcessEventListener("Task 1", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncScriptTask.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new AsyncWorkItemHandler(executorService, PrintOutCommand.class.getName()));
                        return handlers;
                    }
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners( RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        listeners.add(countDownListener2);
                        return listeners;
                    }
                })
                .get();

        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "special-test-case");
        assertNotNull(manager);

        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);
        
        Map<String, Object> params = new HashMap<>();
        params.put("delayAsync", "2s");

        ProcessInstance processInstance = ksession.startProcess("AsyncScriptTask", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        
        countDownListener.waitTillCompleted(1000);

        manager.close();

        List<RequestInfo> queued = executorService.getQueuedRequests(new QueryContext());
        assertNotNull(queued);
        assertEquals(1, queued.size());
        assertEquals(PrintOutCommand.class.getName(), queued.get(0).getCommandName());

        countDownListener2.waitTillCompleted(2000);

        queued = executorService.getQueuedRequests(new QueryContext());
        assertNotNull(queued);
        assertEquals(1, queued.size());
        assertEquals(PrintOutCommand.class.getName(), queued.get(0).getCommandName());

        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "special-test-case");
        assertNotNull(manager);

        runtime = manager.getRuntimeEngine(EmptyContext.get());
        countDownListener2.reset(1);
        
        ((RequeueAware)executorService).requeueById(queued.get(0).getId());
        
        countDownListener2.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(8, logs.size());
    }

    @Test(timeout=10000)
    public void testAsyncModeWithScriptTask() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("EndProcess", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("AsyncMode", "true")
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
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
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(8, logs.size());

        waitForAllJobsToComplete();
        List<RequestInfo> completed = executorService.getCompletedRequests(new QueryContext());
        // there should 3 completed commands (for script, for task and end node)
        assertEquals(3, completed.size());

        Set<String> commands = completed.stream().map(RequestInfo::getCommandName).collect(Collectors.toSet());
        assertEquals(1, commands.size());
        assertEquals(AsyncSignalEventCommand.class.getName(), commands.iterator().next());
    }

    @Test(timeout=10000)
    public void testAsyncModeWithAsyncScriptTask() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("EndProcess", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncScriptTask.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("AsyncMode", "true")
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {
                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("async", new SystemOutWorkItemHandler());
                        return handlers;
                    }

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
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

        ProcessInstance processInstance = ksession.startProcess("AsyncScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(8, logs.size());

        waitForAllJobsToComplete();
        List<RequestInfo> completed = executorService.getCompletedRequests(new QueryContext());
        // there should 3 completed commands (for script, for task and end node)
        assertEquals(3, completed.size());

        Set<String> commands = completed.stream().map(RequestInfo::getCommandName).collect(Collectors.toSet());
        assertEquals(1, commands.size());
        assertEquals(AsyncSignalEventCommand.class.getName(), commands.iterator().next());
    }

    @Test(timeout=10000)
    public void testAsyncModeWithServiceTask() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("EndProcess", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ServiceProcess.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("AsyncMode", "true")
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {
                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("Service Task", new ServiceTaskHandler());
                        return handlers;
                    }

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
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

        ProcessInstance processInstance = ksession.startProcess("ServiceProcess");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(6, logs.size());

        waitForAllJobsToComplete();
        List<RequestInfo> completed = executorService.getCompletedRequests(new QueryContext());
        // there should be 2 completed commands (for service task and end node)
        assertEquals(2, completed.size());

        Set<String> commands = completed.stream().map(RequestInfo::getCommandName).collect(Collectors.toSet());
        assertEquals(1, commands.size());
        assertEquals(AsyncSignalEventCommand.class.getName(), commands.iterator().next());
    }

    @Test(timeout=10000)
    public void testAsyncModeWithSubProcess() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("EndProcess", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-SubProcess.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("AsyncMode", "true")
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
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

        ProcessInstance processInstance = ksession.startProcess("SubProcess");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(18, logs.size());

        waitForAllJobsToComplete();
        List<RequestInfo> completed = executorService.getCompletedRequests(new QueryContext());
        // there should be 7 completed commands (subprocess node itself, 3 inner script tasks, subprocess end node, outer script task, process end node)
        assertEquals(7, completed.size());

        Set<String> commands = completed.stream().map(RequestInfo::getCommandName).collect(Collectors.toSet());
        assertEquals(1, commands.size());
        assertEquals(AsyncSignalEventCommand.class.getName(), commands.iterator().next());
    }

    @Test(timeout=10000)
    public void testAsyncModeWithSignalProcess() throws Exception {
        final NodeTriggeredCountDownProcessEventListener countDownListenerSignalAsync = new NodeTriggeredCountDownProcessEventListener("Signal", 1);
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("EndProcess", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-WaitForEvent.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("AsyncMode", "true")
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {
                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        listeners.add(countDownListenerSignalAsync);
                        return listeners;
                    }
                })
                .get();

        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);

        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        ProcessInstance processInstance = ksession.startProcess("WaitForEvent");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        // wait for the signal not to be triggered in async way before sending signal
        countDownListenerSignalAsync.waitTillCompleted();
        // Send async signal to the process instance
        System.out.println("<<<< Sending signal >>>>>");
        runtime.getKieSession().signalEvent("MySignal", null);
        
        countDownListener.waitTillCompleted();        

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(8, logs.size());

        waitForAllJobsToComplete();
        List<RequestInfo> completed = executorService.getCompletedRequests(new QueryContext());
        List<RequestInfo> all = executorService.getAllRequests(new QueryContext());
        logger.info("all jobs from db {}", all);
        // there should be 2 completed commands (for script task and end node)
        assertEquals(2, completed.size());

        Set<String> commands = completed.stream().map(RequestInfo::getCommandName).collect(Collectors.toSet());
        assertEquals(1, commands.size());
        assertEquals(AsyncSignalEventCommand.class.getName(), commands.iterator().next());
    }
    
    @Test(timeout=10000)
    public void testAsyncParallelGateway() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("REST", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncParallelGateway.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {

                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("Rest", new SystemOutWorkItemHandler());
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

        ProcessInstance processInstance = ksession.startProcess("TestProject.DemoProcess");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

//        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
//        assertNotNull(logs);
//        assertEquals(8, logs.size());
    }
    
    @Test(timeout=10000)
    public void testAsyncModeWithParallelGateway() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("EndProcess", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncParallelGateway.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("AsyncMode", "true")
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {
                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("Rest", new SystemOutWorkItemHandler());
                        return handlers;
                    }

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
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

        ProcessInstance processInstance = ksession.startProcess("TestProject.DemoProcess");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);
    }
    
    @Test(timeout=10000)
    public void testAsyncModeWithTimer() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("BoundaryEnd", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("ProbAsync.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("AsyncMode", "true")
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {
                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        return handlers;
                    }

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
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
        params.put("timer1", "2s");
        params.put("timer2", "10s");

        ProcessInstance processInstance = ksession.startProcess("ProbAsync", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

    }
    
    @Test(timeout=10000)
    public void testAsyncModeWithSignal() throws Exception {
        CountDownAsyncJobListener initialJob = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(initialJob);
               
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("SignalEnd", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("ProbAsync.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("AsyncMode", "true")
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {
                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        return handlers;
                    }

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
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
        params.put("timer1", "20s");
        params.put("timer2", "10s");

        ProcessInstance processInstance = ksession.startProcess("ProbAsync", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        
        initialJob.waitTillCompleted();
        
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        runtime.getTaskService().start(taskId, "john");
        runtime.getTaskService().complete(taskId, "john", null);
        
        runtime.getKieSession().signalEvent("signal1", null, processInstanceId);

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

    }
    
    @Test(timeout=10000)
    public void testAsyncModeWithSignalEventSubProcess() throws Exception {
        CountDownAsyncJobListener initialJob = new CountDownAsyncJobListener(1);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(initialJob);
               
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("SubprocessEnd", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("ProbAsync.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("AsyncMode", "true")
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {
                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        return handlers;
                    }

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
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
        params.put("timer1", "20s");
        params.put("timer2", "10s");

        ProcessInstance processInstance = ksession.startProcess("ProbAsync", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        initialJob.waitTillCompleted();
        
        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertEquals(1, tasks.size());
        
        runtime.getKieSession().signalEvent("startSignal", null, processInstanceId);

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

    }

    @Test(timeout=10000)
    public void testAsyncModeWithInclusiveGateway() throws Exception {
        // JBPM-7414
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("EndProcess", 1);
        final NodeTriggerCountListener triggerListener = new NodeTriggerCountListener("ScriptTask-4");

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-AsyncInclusiveGateway.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("ExecutorService", executorService)
                .addEnvironmentEntry("AsyncMode", "true")
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {
                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                        Map<String, WorkItemHandler> handlers = super.getWorkItemHandlers(runtime);
                        handlers.put("Rest", new SystemOutWorkItemHandler());
                        return handlers;
                    }

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        listeners.add(triggerListener);
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
        params.put("Var1", "AAA");
        ProcessInstance processInstance = ksession.startProcess("TestProject.DemoProcess", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

        assertEquals(1, triggerListener.getCount().intValue());
    }

    private static class NodeTriggerCountListener extends DefaultProcessEventListener {
        private AtomicInteger count = new AtomicInteger(0);
        private String nodeName;

        private NodeTriggerCountListener(String nodeName) {
            this.nodeName = nodeName;
        }

        @Override
        public void afterNodeTriggered(ProcessNodeTriggeredEvent event) {
            if (event.getNodeInstance().getNodeName().equals(nodeName)) {
                count.getAndIncrement();
            }
        }

        public AtomicInteger getCount() {
            return count;
        }
    };

    private boolean waitForAllJobsToComplete() throws Exception {
        int attempts = 10;
        do {
            List<RequestInfo> running = executorService.getRunningRequests(new QueryContext());
            attempts--;
            
            if (running.isEmpty()) {
                return true;
            }
            
            Thread.sleep(500);
            
        } while (attempts > 0);
        
        return false;
    }

    private ExecutorService buildExecutorService() {
        emf = Persistence.createEntityManagerFactory("org.jbpm.executor");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.init();

        return executorService;
    }
}
