package org.jbpm.runtime.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.jbpm.process.audit.AuditLogService;
import org.jbpm.process.audit.JPAAuditLogService;
import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventLister;
import org.jbpm.runtime.manager.impl.DefaultRegisterableItemsFactory;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.jbpm.test.util.AbstractBaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessCompletedEvent;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
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
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.UserGroupCallback;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class SingletonRuntimeManagerTest extends AbstractBaseTest {
    
    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;  
    private RuntimeManager manager;
    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
    }
    
    @After
    public void teardown() {
        if (manager != null) {
            manager.close();
        }
        pds.close();
    }

    @Test
    public void testCreationOfSession() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newEmptyBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        long sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 0);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();       
        assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        // close manager which will close session maintained by the manager
        manager.close();
    }
    
    @Test
    public void testCreationOfSessionWithPersistence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        long sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        // close manager which will close session maintained by the manager
        manager.close();
    }
    
    @Test
    public void testReCreationOfSessionWithPersistence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        long sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        // close manager which will close session maintained by the manager
        manager.close();
        
        // recreate it once again to ensure it has right id
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        // close manager which will close session maintained by the manager
        manager.close();
    }
    
    @Test
    public void testCreationOfMultipleSingletonManagerWithPersistence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .get();
        // create first manager
        //-----------------------------------------
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "manager1");        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        long sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        // close manager which will close session maintained by the manager
        manager.close();
        
        // create another manager
        //-----------------------------------------
        RuntimeManager manager2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "manager2");        
        assertNotNull(manager2);
        
        runtime = manager2.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 2);
        
        runtime = manager2.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager2.disposeRuntimeEngine(runtime);
        
        ksession = manager2.getRuntimeEngine(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        // close manager which will close session maintained by the manager
        manager2.close();
        
        // recreate first manager
        //-----------------------------------------
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "manager1");        
        assertNotNull(manager);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        // close manager which will close session maintained by the manager
        manager.close();
        
        // create another manager
        //-----------------------------------------
        manager2 = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment, "manager2");        
        assertNotNull(manager2);
        
        runtime = manager2.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 2);
        
        runtime = manager2.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager2.disposeRuntimeEngine(runtime);
        
        ksession = manager2.getRuntimeEngine(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        // close manager which will close session maintained by the manager
        manager2.close();        
    }
    
    @Test
    public void testCreationOfDuplicatedManagers() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        try {
            RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
            fail("Should fail as it's not allowed to have singleton manager with same identifier");
        } catch (IllegalStateException e) {
            
        }
        manager.close();
        
        // now it is possible to load the manager again
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
    }
    
    @Test
    public void testExecuteReusableSubprocess() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivity.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivitySubProcess.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);       
        long ksession1Id = ksession.getIdentifier();
        assertTrue(ksession1Id == 1);

        ProcessInstance pi1 = ksession.startProcess("ParentProcess");
        
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
               
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
        
        logs = logService.findProcessInstances("SubProcess");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        
        manager.disposeRuntimeEngine(runtime);
        manager.close();
    }
    
    @Test
    public void testBusinessRuleTask() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-BusinessRuleTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-BusinessRuleTask.drl"), ResourceType.DRL)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        long sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        // start process
        ProcessInstance pi = ksession.createProcessInstance("BPMN2-BusinessRuleTask", null);
        ksession.insert(pi);
        
        ksession.startProcessInstance(pi.getId());
        
        assertNull(ksession.getProcessInstance(pi.getId()));
        
        AuditService logService = runtime.getAuditService();
        
        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("BPMN2-BusinessRuleTask");
        assertNotNull(logs);
        assertEquals(0, logs.size());
        
        logs = logService.findProcessInstances("BPMN2-BusinessRuleTask");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        
        // close manager which will close session maintained by the manager
        manager.close();
    }
    
    @Test
    public void testBusinessRuleTaskWithRuleAwareListener() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-BusinessRuleTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-BusinessRuleTask.drl"), ResourceType.DRL)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        long sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        // start process
        ksession.addEventListener(new RuleAwareProcessEventLister());
        ProcessInstance pi = ksession.startProcess("BPMN2-BusinessRuleTask");
        
        assertNull(ksession.getProcessInstance(pi.getId()));
                
        AuditService logService = runtime.getAuditService();
        
        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("BPMN2-BusinessRuleTask");
        assertNotNull(logs);
        assertEquals(0, logs.size());
        
        logs = logService.findProcessInstances("BPMN2-BusinessRuleTask");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        
        // close manager which will close session maintained by the manager
        manager.close();
    }
    
    @Test
    @Ignore //until KieSession is protected so it won't be possible to dispose it manually
    public void testCreationOfSessionProtectedDispose() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newEmptyBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        long sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 0);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();       
        assertEquals(sessionId, ksession.getIdentifier());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        
        ksession = manager.getRuntimeEngine(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        ksession.dispose();
        assertEquals(sessionId, ksession.getIdentifier());
        
        // close manager which will close session maintained by the manager
        manager.close();
 
    }
    
    @Test
    public void testCreationOfSessionTaskServiceNotConfigured() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newEmptyBuilder()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        try {
        	runtime.getTaskService();
        	fail("Should fail as task service is not configured");
        } catch (UnsupportedOperationException e) {
        	assertEquals("TaskService was not configured", e.getMessage());
        }
        
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        // close manager which will close session maintained by the manager
        manager.close();
    }
    
    @Test
    public void testBusinessRuleTaskWithGlobal() {
    	final List<String> list = new ArrayList<String>();
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder()
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-BusinessRuleTask.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-BusinessRuleTaskWithGlobal.drl"), ResourceType.DRL)
                .registerableItemsFactory(new DefaultRegisterableItemsFactory(){

					@Override
					public Map<String, Object> getGlobals(RuntimeEngine runtime) {
						Map<String, Object> globals = super.getGlobals(runtime);
						globals.put("list", list);
						return globals;
					}
					
                })
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        long sessionId = ksession.getIdentifier();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getIdentifier());
        
        // start process
        ProcessInstance pi = ksession.createProcessInstance("BPMN2-BusinessRuleTask", null);
        ksession.insert(pi);
        
        ksession.startProcessInstance(pi.getId());
        
        assertNull(ksession.getProcessInstance(pi.getId()));
        assertEquals(1, list.size());
                
        AuditService logService = runtime.getAuditService();
        
        List<? extends ProcessInstanceLog> logs = logService.findActiveProcessInstances("BPMN2-BusinessRuleTask");
        assertNotNull(logs);
        assertEquals(0, logs.size());
        
        logs = logService.findProcessInstances("BPMN2-BusinessRuleTask");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        
        // dispose session that should not have affect on the session at all
        manager.disposeRuntimeEngine(runtime);
        
        // close manager which will close session maintained by the manager
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
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
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
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
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
}
