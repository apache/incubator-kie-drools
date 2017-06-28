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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.executor.AsynchronousJobEvent;
import org.jbpm.executor.AsynchronousJobListener;
import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.process.core.async.AsyncSignalEventCommand;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
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

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class AsyncContinuationSupportTest extends AbstractExecutorBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(AsyncContinuationSupportTest.class);
    
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

    @Test(timeout=10000)
    public void testAsyncScriptTask() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNotNull(processInstance);

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
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(6, logs.size());
    }

    @Test(timeout=10000)
    public void testAsyncMIUserTask() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1, true);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.waitTillCompleted();

        List<TaskSummary> tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

        tasks = runtime.getTaskService().getTasksAssignedAsPotentialOwner("john", "en-UK");
        assertNotNull(tasks);
        assertEquals(2, tasks.size());

        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

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
        assertEquals(12, logs.size());
    }

    @Test(timeout=10000)
    public void testAsyncMISubProcess() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(26, logs.size());
    }

    @Test(timeout=10000)
    public void testAsyncSubProcess() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(18, logs.size());
    }

    @Test(timeout=10000)
    public void testSubProcessWithAsyncNodes() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello1", 1);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.reset("Hello2", 1);
        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.reset("Hello3", 1);
        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        Thread.sleep(delay);

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(18, logs.size());
    }

    @Test(timeout=10000)
    public void testSubProcessWithSomeAsyncNodes() throws Exception {

        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello2", 1);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.reset("Goodbye", 1);
        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(18, logs.size());
    }

    @Test(timeout=10000)
    public void testAsyncCallActivityTask() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("CallActivity", 1);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(6, logs.size());
    }


    @Test(timeout=10000)
    public void testAsyncAndSyncServiceTasks() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Async Service", 1);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);

        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(14, logs.size());
    }

    @Test(timeout=10000)
    public void testAsyncScriptTaskIgnoreNotExistingDeployments() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("Hello", 1);
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

        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "special-test-case");
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

        manager.close();

        List<RequestInfo> queued = executorService.getQueuedRequests(new QueryContext());
        assertNotNull(queued);
        assertEquals(1, queued.size());
        assertEquals(AsyncSignalEventCommand.class.getName(), queued.get(0).getCommandName());

        countDownListener.waitTillCompleted(2000);

        queued = executorService.getQueuedRequests(new QueryContext());
        assertNotNull(queued);
        assertEquals(1, queued.size());
        assertEquals(AsyncSignalEventCommand.class.getName(), queued.get(0).getCommandName());

        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "special-test-case");
        assertNotNull(manager);

        runtime = manager.getRuntimeEngine(EmptyContext.get());

        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNull(processInstance);

        List<? extends NodeInstanceLog> logs = runtime.getAuditService().findNodeInstances(processInstanceId);
        assertNotNull(logs);
        assertEquals(8, logs.size());
    }

    @Test(timeout=10000)
    public void testAsyncModeWithScriptTask() throws Exception {
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("EndProcess", 1);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNotNull(processInstance);

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
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("EndProcess", 1);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNotNull(processInstance);

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
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("EndProcess", 1);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNotNull(processInstance);

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
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("EndProcess", 1);
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

        // make sure that waiting for event process is not finished yet as it must be through executor/async
        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNotNull(processInstance);

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
        final CountDownProcessEventListener countDownListenerSignalAsync = new CountDownProcessEventListener("Signal", 1, true);
        final CountDownProcessEventListener countDownListener = new CountDownProcessEventListener("EndProcess", 1);
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

        // make sure that waiting for event process is not finished yet as it must be signalled first
        processInstance = runtime.getKieSession().getProcessInstance(processInstanceId);
        assertNotNull(processInstance);

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
        // there should be 3 completed commands (for intermediate catch event, script task and end node)
        assertEquals(3, completed.size());

        Set<String> commands = completed.stream().map(RequestInfo::getCommandName).collect(Collectors.toSet());
        assertEquals(1, commands.size());
        assertEquals(AsyncSignalEventCommand.class.getName(), commands.iterator().next());
    }
    
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
