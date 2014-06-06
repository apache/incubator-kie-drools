package org.jbpm.runtime.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.naming.InitialContext;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.transaction.UserTransaction;

import org.jbpm.runtime.manager.impl.AbstractRuntimeManager;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.events.DefaultTaskEventListener;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessNodeLeftEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.ProcessInstanceLog;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskEvent;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskService;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.KieInternalServices;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.runtime.manager.TaskServiceFactory;
import org.kie.internal.runtime.manager.context.CorrelationKeyContext;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class PerProcessInstanceRuntimeManagerTest extends AbstractBaseTest {
    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;
    private RuntimeManager manager; 
    @Before
    public void setup() {
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);

        pds = TestUtil.setupPoolingDataSource();
    }
    
    @After
    public void teardown() {
        manager.close();
        pds.close();
    }
    
    @Test
    public void testCreationOfSession() {
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
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);       
        int ksession1Id = ksession.getId();
        assertTrue(ksession1Id == 1);
        
        // FIXME quick hack to overcome problems with same pi ids when not using persistence
        ksession.startProcess("ScriptTask");
        
        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);       
        int ksession2Id = ksession2.getId();
        assertTrue(ksession2Id == 2);
        
        ProcessInstance pi1 = ksession.startProcess("UserTask");
        
        ProcessInstance pi2 = ksession2.startProcess("UserTask");
        
        // both processes started 
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getId());
        
        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getId());
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
        int ksession1Id = ksession.getId();
        assertTrue(ksession1Id == 2);

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);       
        int ksession2Id = ksession2.getId();
        assertTrue(ksession2Id == 3);
        
        ProcessInstance pi1 = ksession.startProcess("UserTask");
        
        ProcessInstance pi2 = ksession2.startProcess("UserTask");
        
        // both processes started 
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getId());
        
        ksession.getWorkItemManager().completeWorkItem(1, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
            fail("Session for this (" + pi1.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {
            
        }
        
        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));;
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getId());
        
        ksession2.getWorkItemManager().completeWorkItem(2, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));
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
        int ksession1Id = ksession.getId();
        assertTrue(ksession1Id == 2);

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        CorrelationKey key2 = keyFactory.newCorrelationKey("second");
        RuntimeEngine runtime2 = manager.getRuntimeEngine(CorrelationKeyContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);       
        int ksession2Id = ksession2.getId();
        assertTrue(ksession2Id == 3);
        
        ProcessInstance pi1 = ((CorrelationAwareProcessRuntime)ksession).startProcess("UserTask", key, null);
        
        ProcessInstance pi2 = ((CorrelationAwareProcessRuntime)ksession2).startProcess("UserTask", key2, null);
        
        // both processes started 
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        
        runtime = manager.getRuntimeEngine(CorrelationKeyContext.get(key));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getId());
        
        ksession.getWorkItemManager().completeWorkItem(1, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(CorrelationKeyContext.get(key));
            fail("Session for this (" + pi1.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {
            
        }
        
        runtime2 = manager.getRuntimeEngine(CorrelationKeyContext.get(key2));
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getId());
        
        ksession2.getWorkItemManager().completeWorkItem(2, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(CorrelationKeyContext.get(key2));
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
        int ksession1Id = ksession.getId();
        assertTrue(ksession1Id == 2);

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);       
        int ksession2Id = ksession2.getId();
        assertTrue(ksession2Id == 3);
        
        ProcessInstance pi1 = ksession.startProcess("UserTask");
        
        ProcessInstance pi2 = ksession2.startProcess("UserTask");
        
        // both processes started 
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getId());
        
        ksession.getWorkItemManager().completeWorkItem(1, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
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
        int ksession1Id = ksession.getId();
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
        
        
        AuditService logService = runtime.getAuditLogService();
        
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
        int ksession1Id = ksession.getId();
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
        int ksession1Id = ksession.getId();
        assertTrue(ksession1Id == 1);
        
        // FIXME quick hack to overcome problems with same pi ids when not using persistence
        ksession.startProcess("ScriptTask");
        
        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);       
        int ksession2Id = ksession2.getId();
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
        assertEquals(ksession1Id, ksession.getId());
        
        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getId());
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
        int ksession1Id = ksession.getId();
        assertTrue(ksession1Id == 1);
        
        // FIXME quick hack to overcome problems with same pi ids when not using persistence
        ksession.startProcess("ScriptTask");
        
        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);       
        int ksession2Id = ksession2.getId();
        assertTrue(ksession2Id == 2);
        
        ProcessInstance pi1 = ksession.startProcess("UserTask");
        
        ProcessInstance pi2 = ksession2.startProcess("UserTask");
        
        // both processes started 
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getId());
        
        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getId());
        
        assertEquals(2,  addedTasks.size());
        manager.close();
    }
    
    @Test
    public void testCreationOfSessionCustomTaskServiceFactory() {
    	final AtomicBoolean customTaskServiceUsed = new AtomicBoolean(false);
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultInMemoryBuilder()
                .userGroupCallback(userGroupCallback)
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
        int ksession1Id = ksession.getId();
        assertTrue(ksession1Id == 1);
        
        // FIXME quick hack to overcome problems with same pi ids when not using persistence
        ksession.startProcess("ScriptTask");
        
        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);       
        int ksession2Id = ksession2.getId();
        assertTrue(ksession2Id == 2);
        
        ProcessInstance pi1 = ksession.startProcess("UserTask");
        
        ProcessInstance pi2 = ksession2.startProcess("UserTask");
        
        // both processes started 
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getId());
        
        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getId());
        manager.close();
        // check if our custom task service factory was used
        assertTrue(customTaskServiceUsed.get());
    }
    
    @Test
    public void testRestoreTimersAfterManagerClose() throws Exception {
    	 final List<Long> timerExpirations = new ArrayList<Long>();
         
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

					@Override
					public List<ProcessEventListener> getProcessEventListeners(
							RuntimeEngine runtime) {
						// TODO Auto-generated method stub
						List<ProcessEventListener> listeners = super.getProcessEventListeners(runtime);
						listeners.add(new DefaultProcessEventListener(){
				             @Override
				             public void afterNodeLeft(ProcessNodeLeftEvent event) {
				                 if (event.getNodeInstance().getNodeName().equals("timer")) {
				                     timerExpirations.add(event.getProcessInstance().getId());
				                 }
				             }
				             
				         });
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

        // wait a bit for some timers to fire
        Thread.sleep(2000);
        manager.disposeRuntimeEngine(runtime);
        ((AbstractRuntimeManager)manager).close(true);
        
        manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);        
        assertNotNull(manager);
        
        Thread.sleep(2000);
        
        runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        
        ksession.abortProcessInstance(pi1.getId());
        Thread.sleep(2000);
        manager.disposeRuntimeEngine(runtime);
        manager.close();
        
        assertEquals(4,  timerExpirations.size());
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
        runtime.getAuditLogService();

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
        int ksession1Id = ksession.getId();
        assertTrue(ksession1Id == 2);
        
        AuditService auditService = runtime.getAuditLogService();
        assertNotNull(auditService);
        
        List<? extends ProcessInstanceLog> logs = auditService.findProcessInstances();
        assertNotNull(logs);
        assertEquals(0, logs.size());

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);       
        int ksession2Id = ksession2.getId();
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
        assertEquals(ksession1Id, ksession.getId());
        
        auditService = runtime.getAuditLogService();
        
        ksession.getWorkItemManager().completeWorkItem(1, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi1.getId()));
            fail("Session for this (" + pi1.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {
            
        }
        
        runtime2 = manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));;
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getId());
        
        ksession2.getWorkItemManager().completeWorkItem(2, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntimeEngine(ProcessInstanceIdContext.get(pi2.getId()));
            fail("Session for this (" + pi2.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {
            
        }
        logs = auditService.findProcessInstances();
        assertNotNull(logs);
        assertEquals(2, logs.size());
        manager.disposeRuntimeEngine(runtime);
        manager.disposeRuntimeEngine(runtime2);
        manager.close();
    }
}
