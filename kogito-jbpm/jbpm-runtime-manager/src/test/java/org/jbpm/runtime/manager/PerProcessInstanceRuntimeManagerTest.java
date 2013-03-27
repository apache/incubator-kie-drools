package org.jbpm.runtime.manager;

import static org.junit.Assert.*;

import java.util.Properties;

import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.SimpleRegisterableItemsFactory;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.KieInternalServices;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.process.CorrelationKeyFactory;
import org.kie.internal.runtime.manager.Runtime;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.context.CorrelationKeyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.UserGroupCallback;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class PerProcessInstanceRuntimeManagerTest {
    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;    
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

        pds.close();
    }
    
    @Test
    public void testCreationOfSession() {
        SimpleRuntimeEnvironment environment = new SimpleRuntimeEnvironment();
        environment.setUserGroupCallback(userGroupCallback);
        ((SimpleRegisterableItemsFactory)environment.getRegisterableItemsFactory()).addWorkItemHandler("Human Task", DoNothingWorkItemHandler.class);
        
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);
        assertNotNull(manager);
       
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        Runtime runtime = manager.getRuntime(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);       
        int ksession1Id = ksession.getId();
        assertTrue(ksession1Id == 0);
        
        // FIXME quick hack to overcome problems with same pi ids when not using persistence
        ksession.startProcess("ScriptTask");
        
        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        Runtime runtime2 = manager.getRuntime(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);       
        int ksession2Id = ksession2.getId();
        assertTrue(ksession2Id == 1);
        
        ProcessInstance pi1 = ksession.startProcess("UserTask");
        
        ProcessInstance pi2 = ksession2.startProcess("UserTask");
        
        // both processes started 
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        runtime = manager.getRuntime(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getId());
        
        runtime2 = manager.getRuntime(ProcessInstanceIdContext.get(pi2.getId()));
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getId());
        manager.close();
    }

    
    @Test
    public void testCreationOfSessionWithPersistence() {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.setUserGroupCallback(userGroupCallback);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);        
        assertNotNull(manager);
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        Runtime runtime = manager.getRuntime(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);       
        int ksession1Id = ksession.getId();
        assertTrue(ksession1Id == 1);

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        Runtime runtime2 = manager.getRuntime(ProcessInstanceIdContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);       
        int ksession2Id = ksession2.getId();
        assertTrue(ksession2Id == 2);
        
        ProcessInstance pi1 = ksession.startProcess("UserTask");
        
        ProcessInstance pi2 = ksession2.startProcess("UserTask");
        
        // both processes started 
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        
        runtime = manager.getRuntime(ProcessInstanceIdContext.get(pi1.getId()));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getId());
        
        ksession.getWorkItemManager().completeWorkItem(1, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntime(ProcessInstanceIdContext.get(pi1.getId()));
            fail("Session for this (" + pi1.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {
            
        }
        
        runtime2 = manager.getRuntime(ProcessInstanceIdContext.get(pi2.getId()));;
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getId());
        
        ksession2.getWorkItemManager().completeWorkItem(2, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntime(ProcessInstanceIdContext.get(pi2.getId()));
            fail("Session for this (" + pi2.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {
            
        }
        manager.close();
    }
    
    @Test
    public void testCreationOfSessionWithPersistenceByCorrelationKey() {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.setUserGroupCallback(userGroupCallback);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-UserTask.bpmn2"), ResourceType.BPMN2);
        
        CorrelationKeyFactory keyFactory = KieInternalServices.Factory.get().newCorrelationKeyFactory();
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerProcessInstanceRuntimeManager(environment);        
        assertNotNull(manager);
        // ksession for process instance #1
        // since there is no process instance yet we need to get new session
        CorrelationKey key = keyFactory.newCorrelationKey("first");
        Runtime runtime = manager.getRuntime(CorrelationKeyContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);       
        int ksession1Id = ksession.getId();
        assertTrue(ksession1Id == 1);

        // ksession for process instance #2
        // since there is no process instance yet we need to get new session
        CorrelationKey key2 = keyFactory.newCorrelationKey("second");
        Runtime runtime2 = manager.getRuntime(CorrelationKeyContext.get());
        KieSession ksession2 = runtime2.getKieSession();

        assertNotNull(ksession2);       
        int ksession2Id = ksession2.getId();
        assertTrue(ksession2Id == 2);
        
        ProcessInstance pi1 = ((CorrelationAwareProcessRuntime)ksession).startProcess("UserTask", key, null);
        
        ProcessInstance pi2 = ((CorrelationAwareProcessRuntime)ksession2).startProcess("UserTask", key2, null);
        
        // both processes started 
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
        assertEquals(ProcessInstance.STATE_ACTIVE, pi2.getState());
        
        runtime = manager.getRuntime(CorrelationKeyContext.get(key));
        ksession = runtime.getKieSession();
        assertEquals(ksession1Id, ksession.getId());
        
        ksession.getWorkItemManager().completeWorkItem(1, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntime(CorrelationKeyContext.get(key));
            fail("Session for this (" + pi1.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {
            
        }
        
        runtime2 = manager.getRuntime(CorrelationKeyContext.get(key2));
        ksession2 = runtime2.getKieSession();
        assertEquals(ksession2Id, ksession2.getId());
        
        ksession2.getWorkItemManager().completeWorkItem(2, null);
        // since process is completed now session should not be there any more
        try {
            manager.getRuntime(CorrelationKeyContext.get(key2));
            fail("Session for this (" + pi2.getId() + ") process instance is no more accessible");
        } catch (RuntimeException e) {
            
        }
        manager.close();
    }
}
