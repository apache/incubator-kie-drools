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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.jbpm.executor.ExecutorServiceFactory;
import org.jbpm.executor.impl.ExecutorServiceImpl;
import org.jbpm.executor.test.CountDownAsyncJobListener;
import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractExecutorBaseTest;
import org.jbpm.test.util.ExecutorTestUtil;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.executor.CommandContext;
import org.kie.api.executor.ExecutorService;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.error.ExecutionError;
import org.kie.internal.runtime.error.ExecutionErrorStorage;
import org.kie.internal.runtime.manager.RuntimeManagerRegistry;
import org.kie.internal.runtime.manager.context.EmptyContext;

public class CleanupExecutionErrorCommandWithProcessTest extends AbstractExecutorBaseTest {

    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;
    private RuntimeManager manager;
    private ExecutorService executorService;
    private EntityManagerFactory emf = null;

    @Before
    public void setup() {
        ExecutorTestUtil.cleanupSingletonSessionId();
        pds = ExecutorTestUtil.setupPoolingDataSource();
        Properties properties = new Properties();
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

    protected CountDownAsyncJobListener configureListener(int threads) {
        CountDownAsyncJobListener countDownListener = new CountDownAsyncJobListener(threads);
        ((ExecutorServiceImpl) executorService).addAsyncJobListener(countDownListener);

        return countDownListener;
    }

    @Test(timeout = 30000)
    public void testRunProcessWithAsyncHandlerDeleteUsingOlderThan() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        RuntimeEnvironment environment = configureEnvironment();

        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);

        ExecutionErrorStorage errorStorage = ((AbstractRuntimeManager) manager).getExecutionErrorManager().getStorage();

        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        Date startDate = new Date();
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        countDownListener.waitTillCompleted();

        List<ExecutionError> errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());

        scheduleLogCleanup(startDate, null, true, null, "ScriptTask", String.valueOf(processInstanceId), "yyyy-MM-dd", manager.getIdentifier());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        System.out.println("Aborting process instance " + processInstance.getId());
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        // should not delete any errors as the process instance is still active
        errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());

        runtime.getKieSession().abortProcessInstance(processInstance.getId());

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        Thread.sleep(1000);

        scheduleLogCleanup(new Date(), null, true, null, "ScriptTask", String.valueOf(processInstanceId), "yyyy-MM-dd HH:mm:ss", manager.getIdentifier());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

        errors = errorStorage.list(0, 10);
        assertEquals(0, errors.size());

    }

    @Test(timeout = 30000)
    public void testRunProcessWithAsyncHandlerDeleteUsingOlderThanPeriod() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        RuntimeEnvironment environment = configureEnvironment();

        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);

        ExecutionErrorStorage errorStorage = ((AbstractRuntimeManager) manager).getExecutionErrorManager().getStorage();

        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        long processInstanceId = processInstance.getId();
        countDownListener.waitTillCompleted();

        List<ExecutionError> errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());

        // advance time 1 second
        Thread.sleep(1000);

        // delete errors which happened 1s or more ago
        scheduleLogCleanup(null, "1s", true, null, "ScriptTask", String.valueOf(processInstanceId), "yyyy-MM-dd HH:mm:ss", manager.getIdentifier());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        System.out.println("Aborting process instance " + processInstance.getId());
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        // should not delete any errors as the process instance is still active
        errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());

        runtime.getKieSession().abortProcessInstance(processInstance.getId());

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        // delete errors which happened 5s or more ago
        scheduleLogCleanup(null, "5s", true, null, "ScriptTask", String.valueOf(processInstanceId), "yyyy-MM-dd HH:mm:ss", manager.getIdentifier());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

        // should not delete any errors as we wanted to delete errors from 5s ago
        errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());

        // delete errors which happened 1s or more ago
        scheduleLogCleanup(null, "0s", true, null, "ScriptTask", String.valueOf(processInstanceId), "yyyy-MM-dd HH:mm:ss", manager.getIdentifier());
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

        errors = errorStorage.list(0, 10);
        assertEquals(0, errors.size());

    }

    @Test(timeout = 30000)
    public void testRunProcessWithAsyncHandlerDeleteUsingReoccurring() throws Exception {
        CountDownAsyncJobListener countDownListener = configureListener(1);
        RuntimeEnvironment environment = configureEnvironment();

        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);

        ExecutionErrorStorage errorStorage = ((AbstractRuntimeManager) manager).getExecutionErrorManager().getStorage();

        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        // schedule log cleanup every 3 seconds for processes with processId ScriptTask
        scheduleLogCleanup(null, null, false, "3s", "ScriptTask", null, "yyyy-MM-dd HH:mm:ss", manager.getIdentifier());
        // wait for the first cleanup
        countDownListener.waitTillCompleted();

        List<ExecutionError> errors = errorStorage.list(0, 10);
        assertEquals(0, errors.size());

        System.out.println("Process starting...");
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        System.out.println("Process started...");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        // wait for the process instance to generate an error
        countDownListener.reset(1);
        System.out.println("Waiting to generate an error...");
        countDownListener.waitTillCompleted();

        errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());

        // wait for another log cleanup
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();
        System.out.println("Aborting process instance " + processInstance.getId());
        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNotNull(processInstance);
        // should not delete any errors as the process instance is still active
        errors = errorStorage.list(0, 10);
        assertEquals(1, errors.size());

        runtime.getKieSession().abortProcessInstance(processInstance.getId());

        processInstance = runtime.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);

        // wait for another log cleanup
        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

        errors = errorStorage.list(0, 10);
        assertEquals(0, errors.size());

    }

    private ExecutorService buildExecutorService() {
        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.complete");

        executorService = ExecutorServiceFactory.newExecutorService(emf);
        executorService.setInterval(1);
        executorService.setRetries(0);
        executorService.init();

        return executorService;
    }

    private void scheduleLogCleanup(Date olderThan, String olderThanPeriod, boolean singleRun, String nextRun,
                                    String forProcess, String forProcessInstance, String dateFormat, String identifier) {
        CommandContext commandContext = new CommandContext();
        commandContext.setData("EmfName", "org.jbpm.persistence.complete");
        commandContext.setData("SingleRun", Boolean.toString(singleRun));

        if (nextRun != null) {
            commandContext.setData("NextRun", nextRun);
        }

        if (olderThan != null) {
            commandContext.setData("OlderThan", new SimpleDateFormat(dateFormat).format(olderThan));
        }

        if (olderThanPeriod != null) {
            commandContext.setData("OlderThanPeriod", olderThanPeriod);
        }
        commandContext.setData("DateFormat", dateFormat);
        commandContext.setData("ForDeployment", identifier);
        commandContext.setData("ForProcess", forProcess);
        if (forProcessInstance != null) {
            commandContext.setData("ForProcessInstance", forProcessInstance);
        }
        executorService.scheduleRequest("org.jbpm.executor.commands.ExecutionErrorCleanupCommand", commandContext);
    }

    private RuntimeEnvironment configureEnvironment() {
        return RuntimeEnvironmentBuilder.Factory.get().newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(emf)
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
    }

}
