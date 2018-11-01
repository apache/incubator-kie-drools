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

package org.jbpm.runtime.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.NodeInstanceLog;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.KieInternalServices;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.kie.internal.runtime.manager.context.CorrelationKeyContext;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

public class PerProcessInstanceRuntimeManagerTest extends AbstractBaseTest {
    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;
    private RuntimeManager manager;
    private EntityManagerFactory emf;
    @Before
    public void setup() {
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        pds = TestUtil.setupPoolingDataSource();

        emf = EntityManagerFactoryManager.get().getOrCreate("org.jbpm.persistence.jpa");
    }

    @After
    public void teardown() {
        manager.close();
        EntityManagerFactoryManager.get().clear();
        pds.close();
    }

    @Test
    public void testCreationOfSession() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultInMemoryBuilder()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 1);

        // FIXME quick hack to overcome problems with same pi ids when not using persistence
        ksession.startProcess("ScriptTask");

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);
        long ksession2Id = ksession2.getIdentifier();
        assertTrue(ksession2Id == 2);

        ProcessInstance pi1 = ksession.startProcess("UserTask");

        ProcessInstance pi2 = ksession2.startProcess("UserTask");

        // both processes started
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getIdentifier());

        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getIdentifier());
        manager.close();
    }


    @Test
    public void testCreationOfSessionWithPersistence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 2);

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);
        long ksession2Id = ksession2.getIdentifier();
        assertTrue(ksession2Id == 3);

        ProcessInstance pi1 = ksession.startProcess("UserTask");

        ProcessInstance pi2 = ksession2.startProcess("UserTask");

        // both processes started
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());

        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getIdentifier());

        ksession.getWorkItemManager().completeWorkItem(1, null);
        manager.disposeRuntimeEngine(runtime);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId())).getKieSession();
            fail("Session for this (" + pi1.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {

        }

        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));;
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getIdentifier());

        ksession2.getWorkItemManager().completeWorkItem(2, null);
        manager.disposeRuntimeEngine(runtime2);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId())).getKieSession();
            fail("Session for this (" + pi2.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {

        }
        manager.close();
    }

    @Test
    public void testCreationOfSessionWithPersistenceByCorrelationKey() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();

        CorrelationKeyFactory keyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        CorrelationKey key = keyFactory.newCorrelationKey("first");
        RuntimeEngine runtime = manager.getRuntimeEngine(CorrelationKeyContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 2);

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        CorrelationKey key2 = keyFactory.newCorrelationKey("second");
        RuntimeEngine runtime2 = manager.getRuntimeEngine(CorrelationKeyContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);
        long ksession2Id = ksession2.getIdentifier();
        assertTrue(ksession2Id == 3);

        ProcessInstance pi1 = ((CorrelationAwareProcessRuntime)ksession).startProcess("UserTask", key, null);

        ProcessInstance pi2 = ((CorrelationAwareProcessRuntime)ksession2).startProcess("UserTask", key2, null);

        // both processes started
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());

        runtime = manager.getRuntimeEngine(CorrelationKeyContext.get(key));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getIdentifier());

        ksession.getWorkItemManager().completeWorkItem(1, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(CorrelationKeyContext.get(key)).getKieSession();
            fail("Session for this (" + pi1.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {

        }

        runtime2 = manager.getRuntimeEngine(CorrelationKeyContext.get(key2));
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getIdentifier());

        ksession2.getWorkItemManager().completeWorkItem(2, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(CorrelationKeyContext.get(key2)).getKieSession();
            fail("Session for this (" + pi2.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {

        }
        manager.close();
    }

    @Test
    public void testExecuteCompleteWorkItemOnInvalidSessionWithPersistence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 2);

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);
        long ksession2Id = ksession2.getIdentifier();
        assertTrue(ksession2Id == 3);

        ProcessInstance pi1 = ksession.startProcess("UserTask");

        ProcessInstance pi2 = ksession2.startProcess("UserTask");

        // both processes started
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());

        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getIdentifier());

        ksession.getWorkItemManager().completeWorkItem(1, null);
        manager.disposeRuntimeEngine(runtime);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId())).getKieSession();
            fail("Session for this (" + pi1.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {

        }
        try {
            ksession.getWorkItemManager().completeWorkItem(2, null);

            fail("Invalid session was used for (" + pi2.getId() + ") process instance");
        } catch (RuntimeException e) {

        }
        manager.close();
    }

    @Test
    public void testExecuteReusableSubprocess() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivity.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivitySubProcess.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 2);

        ProcessInstance pi1 = ksession.startProcess("ParentProcess");

        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());

        try {
            ksession.getWorkItemManager().completeWorkItem(1, null);

            fail("Invalid session was used for subprocess of (" + pi1.getId() + ") process instance");
        } catch (RuntimeException e) {

        }
        manager.disposeRuntimeEngine(runtime);
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(2l));
        ksession = runtime.getKieSession();
        ksession.getWorkItemManager().completeWorkItem(1, null);


        AuditService logService = runtime.getAuditService();

        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("ParentProcess");
        assertNotNull(logs);
        assertEquals(0, logs.size());

        logs = logService.findActiveProcessInstances("SubProcess");
        assertNotNull(logs);
        assertEquals(0, logs.size());

        logs = logService.findProcessInstances("ParentProcess");
        assertNotNull(logs);
        assertEquals(1, logs.size());

        String externalId = logs.get(0).getExternalId();
        assertEquals(manager.getIdentifier(), externalId);

        logs = logService.findProcessInstances("SubProcess");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        externalId = logs.get(0).getExternalId();
        assertEquals(manager.getIdentifier(), externalId);

        manager.close();
    }

    @Test
    public void testStartTwoProcessIntancesOnSameSession() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 2);

        ProcessInstance pi1 = ksession.startProcess("UserTask");


        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());

        try {
            ProcessInstance pi2 = ksession.startProcess("UserTask");
            fail("Invalid session was used for (" + pi2.getId() + ") process instance");
        } catch (RuntimeException e) {

        }
        manager.close();
    }

    @Test
    public void testCreationOfRuntimeManagerWithinTransaction() throws Exception {
        System.setProperty("jbpm.tm.jndi.lookup", "java:comp/UserTransaction");

        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);


        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        ksession.startProcess("ScriptTask");

        ut.commit();

        System.clearProperty("jbpm.tm.jndi.lookup");
    }

    @Test
    public void testCreationOfSessionWithEmptyContext() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultInMemoryBuilder()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 1);

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);
        long ksession2Id = ksession2.getIdentifier();
        assertTrue(ksession2Id == 2);

        ProcessInstance pi1 = ksession.startProcess("UserTask");

        ProcessInstance pi2 = ksession2.startProcess("UserTask");

        // both processes started
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());

        manager.disposeRuntimeEngine(runtime);
        manager.disposeRuntimeEngine(runtime2);

        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getIdentifier());

        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getIdentifier());
        manager.close();
    }

    @Test
    public void testCreationOfSessionTaskServiceNotConfigured() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newEmptyBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        try {
        	runtime.getTaskService();
        	fail("Should fail as task service is not configured");
        } catch (UnsupportedOperationException e) {
        	assertEquals("TaskService was not configured", e.getMessage());
        }
        manager.close();
    }

    @Test
    public void testCreationOfSessionWithCustomTaskListener() {
    	final List<Long> addedTasks = new ArrayList<Long>();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultInMemoryBuilder()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

					@Override
					public List<TaskLifeCycleEventListener> getTaskListeners() {
						List<TaskLifeCycleEventListener> listeners = super.getTaskListeners();
						listeners.add(new DefaultTaskEventListener(){

							@Override
							public void afterTaskAddedEvent(TaskEvent event) {
								addedTasks.add(event.getTask().getId());
							}

						});
						return listeners;
					}


                })
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 1);

        // FIXME quick hack to overcome problems with same pi ids when not using persistence
        ksession.startProcess("ScriptTask");

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);
        long ksession2Id = ksession2.getIdentifier();
        assertTrue(ksession2Id == 2);

        ProcessInstance pi1 = ksession.startProcess("UserTask");

        ProcessInstance pi2 = ksession2.startProcess("UserTask");

        // both processes started
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getIdentifier());

        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getIdentifier());

        assertEquals(2,  addedTasks.size());
        manager.close();
    }

    @Test
    public void testCreationOfSessionCustomTaskServiceFactory() {
    	final AtomicBoolean customTaskServiceUsed = new AtomicBoolean(false);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultInMemoryBuilder()
                .userGroupCallback(userGroupCallback)
                .entityManagerFactory(emf)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .addEnvironmentEntry("org.kie.internal.runtime.manager.TaskServiceFactory", new TaskServiceFactory() {
                	private EntityManagerFactory emf;
                	public EntityManagerFactory produceEntityManagerFactory() {
                        if (this.emf == null) {
                            this.emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
                        }

                        return this.emf;
                    }
					@Override
					public TaskService newTaskService() {
						customTaskServiceUsed.set(true);
						return HumanTaskServiceFactory.newTaskServiceConfigurator()
								.entityManagerFactory(produceEntityManagerFactory())
								.listener(new JPATaskLifeCycleEventListener(true))
								.getTaskService();
					}

					@Override
					public void close() {
					}

                })
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 1);

        // FIXME quick hack to overcome problems with same pi ids when not using persistence
        ksession.startProcess("ScriptTask");

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);
        long ksession2Id = ksession2.getIdentifier();
        assertTrue(ksession2Id == 2);

        ProcessInstance pi1 = ksession.startProcess("UserTask");

        ProcessInstance pi2 = ksession2.startProcess("UserTask");

        // both processes started
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getIdentifier());

        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getIdentifier());
        manager.close();
        // check if our custom task service factory was used
        assertTrue(customTaskServiceUsed.get());
    }

    @Test(timeout=30000)
    public void testRestoreTimersAfterManagerClose() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 2);
    	final List<Long> timerExpirations = new ArrayList<Long>();

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

					@Override
					public List<ProcessEventListener> getProcessEventListeners(
							RuntimeEngine runtime) {
						List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
						listeners.add(new DefaultProcessEventListener(){
				             @Override
				             public void afterNodeLeft(ProcessNodeLeftEvent event) {
				                 if (event.getNodeInstance().getNodeName().equals("timer")) {
				                     timerExpirations.add(event.getProcessInstance().getId());
				                 }
				             }

				         });
						listeners.add(countDownListener);
						return listeners;
					}
                })
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-IntermediateCatchEventTimerCycle3.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        ProcessInstance pi1 = ksession.startProcess("IntermediateCatchEvent");
        // both processes started
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        manager.disposeRuntimeEngine(runtime);

        // wait a bit for some timers to fire
        countDownListener.waitTillCompleted();

        ((AbstractRuntimeManager)manager).close(true);

        int currentNumberOfTriggers = timerExpirations.size();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        countDownListener.reset(2);
        countDownListener.waitTillCompleted();

        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));

        runtime.getKieSession().abortProcessInstance(pi1.getId());

        manager.disposeRuntimeEngine(runtime);
        manager.close();

        assertTrue(timerExpirations.size() > currentNumberOfTriggers);
    }

    @Test(expected=UnsupportedOperationException.class)
    public void testAuditServiceNotAvailable() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultInMemoryBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        runtime.getAuditService();

    }

    @Test
    public void testCreationOfSessionWithPersistenceAndAuditService() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 2);

        AuditService auditService = runtime.getAuditService();
        assertNotNull(auditService);

        List<? extends ProcessInstanceLog> logs = auditService.findProcessInstances();
        assertNotNull(logs);
        assertEquals(0, logs.size());

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);
        long ksession2Id = ksession2.getIdentifier();
        assertTrue(ksession2Id == 3);

        ProcessInstance pi1 = ksession.startProcess("UserTask");

        logs = auditService.findProcessInstances();
        assertNotNull(logs);
        assertEquals(1, logs.size());

        ProcessInstance pi2 = ksession2.startProcess("UserTask");

        logs = auditService.findProcessInstances();
        assertNotNull(logs);
        assertEquals(2, logs.size());

        // both processes started
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());

        manager.disposeRuntimeEngine(runtime);

        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getIdentifier());



        ksession.getWorkItemManager().completeWorkItem(1, null);
        manager.disposeRuntimeEngine(runtime);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId())).getKieSession();
            fail("Session for this (" + pi1.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {

        }

        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));;
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getIdentifier());

        ksession2.getWorkItemManager().completeWorkItem(2, null);
        auditService = runtime2.getAuditService();
        logs = auditService.findProcessInstances();
        assertNotNull(logs);
        assertEquals(2, logs.size());
        manager.disposeRuntimeEngine(runtime2);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId())).getKieSession();
            fail("Session for this (" + pi2.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {

        }

        manager.close();
    }

    @Test
    public void testIndependentSubprocessAbort() {
        // independent = true
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivity.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivitySubProcess.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 2);

        ProcessInstance pi1 = ksession.startProcess("ParentProcess");

        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());

        // Aborting the parent process
        ksession.abortProcessInstance(pi1.getId());

        AuditService logService = runtime.getAuditService();

        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("ParentProcess");
        assertNotNull(logs);
        assertEquals(0, logs.size());

        logs = logService.findActiveProcessInstances("SubProcess");
        assertNotNull(logs);
        assertEquals(1, logs.size());

        logs = logService.findProcessInstances("ParentProcess");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals(ProcessInstance.STATE_ABORTED, (int)logs.get(0).getStatus());

        logs = logService.findProcessInstances("SubProcess");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals(ProcessInstance.STATE_ACTIVE, (int)logs.get(0).getStatus());

        manager.close();
    }

    @Test
    public void testDependentSubprocessAbort() {
        // independent = false
        // JBPM-4422
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-DependentCallActivity.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-DependentCallActivitySubProcess.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 2);

        ProcessInstance pi1 = ksession.startProcess("DependentParentProcess");

        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());

        // Aborting the parent process
        ksession.abortProcessInstance(pi1.getId());

        AuditService logService = runtime.getAuditService();

        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("DependentParentProcess");
        assertNotNull(logs);
        assertEquals(0, logs.size());

        logs = logService.findActiveProcessInstances("DependentSubProcess");
        assertNotNull(logs);
        assertEquals(0, logs.size());

        logs = logService.findProcessInstances("DependentParentProcess");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals(ProcessInstance.STATE_ABORTED, (int)logs.get(0).getStatus());

        logs = logService.findProcessInstances("DependentSubProcess");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        assertEquals(ProcessInstance.STATE_ABORTED, (int)logs.get(0).getStatus());

        manager.close();
    }

    @Test
    public void testMultipleRuntimeEngineWithinSingleTransaction() throws Exception {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 2);

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);
        long ksession2Id = ksession2.getIdentifier();
        assertTrue(ksession2Id == 3);

        ProcessInstance pi1 = ksession.startProcess("UserTask");

        ProcessInstance pi2 = ksession2.startProcess("UserTask");

        // both processes started
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());

        ut.commit();

        Object cachedRE1 = ((PerProcessInstanceRuntimeManager) manager).findLocalRuntime(pi1.getId());
        assertNull(cachedRE1);
        Object cachedRE2 = ((PerProcessInstanceRuntimeManager) manager).findLocalRuntime(pi2.getId());
        assertNull(cachedRE2);

        ut.begin();
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getIdentifier());

        ksession.getWorkItemManager().completeWorkItem(1, null);


        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));;
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getIdentifier());

        ksession2.getWorkItemManager().completeWorkItem(2, null);

        ut.commit();

        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId())).getKieSession();
            fail("Session for this (" + pi1.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {

        }

        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId())).getKieSession();
            fail("Session for this (" + pi2.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {

        }
        cachedRE1 = ((PerProcessInstanceRuntimeManager) manager).findLocalRuntime(pi1.getId());
        assertNull(cachedRE1);
        cachedRE2 = ((PerProcessInstanceRuntimeManager) manager).findLocalRuntime(pi2.getId());
        assertNull(cachedRE2);
        manager.close();
    }

    @Test
    public void testEventSignalingBetweenProcessesWithPeristence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("events/throw-an-event.bpmn"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("events/start-on-event.bpmn"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        ksession.startProcess("com.sample.bpmn.hello");

        AuditService auditService = runtime.getAuditService();

        List<? extends ProcessInstanceLog> throwProcessLogs = auditService.findProcessInstances("com.sample.bpmn.hello");
        List<? extends ProcessInstanceLog> catchProcessLogs = auditService.findProcessInstances("com.sample.bpmn.Second");

        assertNotNull(throwProcessLogs);
        assertEquals(1, throwProcessLogs.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, throwProcessLogs.get(0).getStatus().intValue());

        assertNotNull(catchProcessLogs);
        assertEquals(1, catchProcessLogs.size());
        assertEquals(ProcessInstance.STATE_COMPLETED, catchProcessLogs.get(0).getStatus().intValue());

        manager.disposeRuntimeEngine(runtime);
        manager.close();
    }

    @Test
    public void testEventSignalingBetweenProcesses() {
    	final Map<String, Integer> processStates = new HashMap<String, Integer>();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultInMemoryBuilder()
    			.persistence(false)
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("events/throw-an-event.bpmn"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("events/start-on-event.bpmn"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory() {

					@Override
					public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {

						List<ProcessEventListener> listeners = new ArrayList<ProcessEventListener>();
						listeners.add(new DefaultProcessEventListener() {

							@Override
							public void afterProcessCompleted(ProcessCompletedEvent event) {
								processStates.put(event.getProcessInstance().getProcessId(), event.getProcessInstance().getState());
							}

							@Override
							public void beforeProcessStarted(ProcessStartedEvent event) {
								processStates.put(event.getProcessInstance().getProcessId(), event.getProcessInstance().getState());
							}

						});

						return listeners;
					}

                })
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        ksession.startProcess("com.sample.bpmn.hello");

        assertEquals(2, processStates.size());
        assertTrue(processStates.containsKey("com.sample.bpmn.hello"));
        assertTrue(processStates.containsKey("com.sample.bpmn.Second"));

        assertEquals(ProcessInstance.STATE_COMPLETED, processStates.get("com.sample.bpmn.hello").intValue());
        assertEquals(ProcessInstance.STATE_COMPLETED, processStates.get("com.sample.bpmn.Second").intValue());

        manager.disposeRuntimeEngine(runtime);
        manager.close();
    }

    @Test(timeout=10000)
    public void testReusableSubprocessWithWaitForCompletionFalse() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("SLATimer", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("reusable-subprocess/parentprocess.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("reusable-subprocess/subprocess.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {

                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }

                })
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        ksession.startProcess("Project01360830.parentprocess");

        countDownListener.waitTillCompleted();

        manager.disposeRuntimeEngine(runtime);
        manager.close();
    }

    @Test
    public void testSignalEventViaRuntimeManager() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2IntermediateThrowEventScope.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        RuntimeEngine runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();
        assertNotNull(ksession1);

        ProcessInstance processInstance = ksession1.startProcess("intermediate-event-scope");

        manager.disposeRuntimeEngine(runtime1);

        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();
        assertNotNull(ksession2);

        ProcessInstance processInstance2 = ksession2.startProcess("intermediate-event-scope");

        manager.disposeRuntimeEngine(runtime2);

        runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance.getId()));

        List<Long> tasks1 = runtime1.getTaskService().getTasksByProcessInstanceId(processInstance.getId());
        assertNotNull(tasks1);
        assertEquals(1, tasks1.size());


        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance2.getId()));
        List<Long> tasks2 = runtime1.getTaskService().getTasksByProcessInstanceId(processInstance2.getId());
        assertNotNull(tasks2);
        assertEquals(1, tasks2.size());

        Object data = "some data";

        runtime1.getTaskService().claim(tasks1.get(0), "john");
        runtime1.getTaskService().start(tasks1.get(0), "john");
        runtime1.getTaskService().complete(tasks1.get(0), "john", Collections.singletonMap("_output", data));

        manager.disposeRuntimeEngine(runtime1);
        manager.disposeRuntimeEngine(runtime2);

        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(processInstance2.getId()));

        AuditService auditService = runtime2.getAuditService();

        ProcessInstanceLog pi1Log = auditService.findProcessInstance(processInstance.getId());
        assertNotNull(pi1Log);
        assertEquals(ProcessInstance.STATE_COMPLETED, pi1Log.getStatus().intValue());
        ProcessInstanceLog pi2Log = auditService.findProcessInstance(processInstance2.getId());
        assertNotNull(pi2Log);
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2Log.getStatus().intValue());

        List<? extends NodeInstanceLog> nLogs = auditService.findNodeInstances(processInstance2.getId(), "_527AF0A7-D741-4062-9953-A05E51479C80");
        assertNotNull(nLogs);
        assertEquals(2, nLogs.size());

        auditService.dispose();

        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime1);
        manager.disposeRuntimeEngine(runtime2);

        // close manager which will close session maintained by the manager
        manager.close();
    }


    @Test
    public void testSignalStartMultipleProcesses() {
        // independent = true
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-SignalMultipleProcessesMain.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-SignalMultipleProcessesOne.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-SignalMultipleProcessesTwo.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 2);

        Map<String, Object> inputParams = new HashMap<String, Object>();
        inputParams.put("processInput", "MyCoolParam");

        ksession.startProcess("main-process", inputParams);

        AuditService auditService = runtime.getAuditService();

        List<? extends ProcessInstanceLog> processInstanceLogs = auditService.findProcessInstances();
        assertEquals(3, processInstanceLogs.size());

        manager.close();
    }

    @Test
    public void testErrorThrowOfChildProcessOnParent() {

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("reusable-subprocess/ParentError.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("reusable-subprocess/ChildError.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);

        ksession.startProcess("ParentError");

        List<? extends ProcessInstanceLog> processInstanceLogs = runtime.getAuditService().findProcessInstances();
        assertEquals(2, processInstanceLogs.size());

        for (ProcessInstanceLog log : processInstanceLogs) {
            if (log.getProcessId().equals("ParentError")) {
                assertEquals(ProcessInstance.STATE_COMPLETED, log.getStatus().intValue());
            } else if(log.getProcessId().equals("ChildError")) {
                assertEquals(ProcessInstance.STATE_ABORTED, log.getStatus().intValue());
            }
        }

        manager.disposeRuntimeEngine(runtime);
        manager.close();
    }
    @Test
    public void testSignalEventWithDeactivate() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("events/start-on-event.bpmn"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        RuntimeEngine runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession1 = runtime1.getKieSession();

        ksession1.signalEvent("SampleEvent", null);


        List<? extends ProcessInstanceLog> logs = runtime1.getAuditService().findProcessInstances();
        assertEquals(1, logs.size());
        manager.disposeRuntimeEngine(runtime1);

        ((InternalRuntimeManager) manager).deactivate();

        runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        ksession1 = runtime1.getKieSession();

        ksession1.signalEvent("SampleEvent", null);

        logs = runtime1.getAuditService().findProcessInstances();
        assertEquals(1, logs.size());
        manager.disposeRuntimeEngine(runtime1);

        ((InternalRuntimeManager) manager).activate();

        runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        ksession1 = runtime1.getKieSession();

        ksession1.signalEvent("SampleEvent", null);

        logs = runtime1.getAuditService().findProcessInstances();
        assertEquals(2, logs.size());
        manager.disposeRuntimeEngine(runtime1);

    }
    @Test(timeout=10000)
    public void testTimerStartWithDeactivate() {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Hello", 1);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-TimerStart.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(RuntimeEngine runtime) {

                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(countDownListener);
                        return listeners;
                    }

                })
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);

        countDownListener.waitTillCompleted();

        RuntimeEngine runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());

        List<? extends ProcessInstanceLog> logs = runtime1.getAuditService().findProcessInstances();
        assertEquals(1, logs.size());
        manager.disposeRuntimeEngine(runtime1);

        ((InternalRuntimeManager) manager).deactivate();

        countDownListener.reset(1);
        countDownListener.waitTillCompleted(2000);

        runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());

        logs = runtime1.getAuditService().findProcessInstances();
        assertEquals(1, logs.size());
        manager.disposeRuntimeEngine(runtime1);

        ((InternalRuntimeManager) manager).activate();

        countDownListener.reset(1);
        countDownListener.waitTillCompleted();

        runtime1 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());

        logs = runtime1.getAuditService().findProcessInstances();
        assertEquals(2, logs.size());
        manager.disposeRuntimeEngine(runtime1);

    }

    @Test
    public void testEndMessageEventProcess() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("events/EndMessageEvent.bpmn2"), ResourceType.BPMN2)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

                    @Override
                    public Map<String, WorkItemHandler> getWorkItemHandlers(RuntimeEngine runtime) {
                        Map<String, WorkItemHandler> handlers = new HashMap<String, WorkItemHandler>();
                        handlers.putAll(super.getWorkItemHandlers(runtime));
                        handlers.put("Send Task", new SendTaskHandler());
                        return handlers;
                    }



                })
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        ProcessInstance pi1 = ksession.startProcess("test-process");

        assertEquals(ProcessInstance.STATE_COMPLETED, pi1.getState());
        manager.close();
    }

    @Test(timeout=20000)
    public void testTimersOnMultiInstanceSubprocess() throws Exception {
        final NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("MIDelayTimer", 2);
        final List<Long> timerExpirations = new ArrayList<Long>();

        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
                .newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

                    @Override
                    public List<ProcessEventListener> getProcessEventListeners(
                            RuntimeEngine runtime) {
                        List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
                        listeners.add(new DefaultProcessEventListener(){
                             @Override
                             public void afterNodeLeft(ProcessNodeLeftEvent event) {
                                 if (event.getNodeInstance().getNodeName().equals("MIDebugScript")) {
                                     timerExpirations.add(event.getProcessInstance().getId());
                                 }
                             }

                         });
                        listeners.add(countDownListener);
                        return listeners;
                    }
                })
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-MultiInstanceProcess.bpmn2"), ResourceType.BPMN2)
                .get();

        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();
        ProcessInstance pi1 = ksession.startProcess("defaultPackage.MultiInstanceProcess");
        // both processes started
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        manager.disposeRuntimeEngine(runtime);

        // wait a bit for some timers to fire
        countDownListener.waitTillCompleted();

        // now make sure nothing else is triggered
        countDownListener.reset(4);
        countDownListener.waitTillCompleted(3000);

        assertEquals(2, timerExpirations.size());

        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();

        pi1 = ksession.getProcessInstance(pi1.getId());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());

        ksession.abortProcessInstance(pi1.getId());
        manager.disposeRuntimeEngine(runtime);

        manager.close();
    }
}
