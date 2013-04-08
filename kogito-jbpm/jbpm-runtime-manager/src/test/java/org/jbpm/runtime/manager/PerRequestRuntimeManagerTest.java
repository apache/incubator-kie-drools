package org.jbpm.runtime.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.jbpm.process.audit.JPAProcessInstanceDbLog;
import org.jbpm.process.audit.ProcessInstanceLog;
import org.jbpm.runtime.manager.impl.RuntimeEnvironmentBuilder;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.RuntimeEngine;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.runtime.manager.context.ProcessInstanceIdContext;
import org.kie.internal.task.api.UserGroupCallback;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class PerRequestRuntimeManagerTest {

    private PoolingDataSource pds;
    private UserGroupCallback userGroupCallback;  
    
    @Before
    public void setup() {
        pds = TestUtil.setupPoolingDataSource();
        Properties properties= new Properties();
        properties.setProperty("mary", "HR");
        properties.setProperty("john", "HR");
        userGroupCallback = new JBossUserGroupCallbackImpl(properties);
    }
    
    @After
    public void teardown() {
        pds.close();
    }
    
    @Test
    public void testCreationOfSession() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getEmpty()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 0);
        manager.disposeRuntimeEngine(runtime);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();    
        // session id should be 1+ previous session id
        assertEquals(sessionId+1, ksession.getId());
        sessionId = ksession.getId();
        manager.disposeRuntimeEngine(runtime);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();         
        // session id should be 1+ previous session id
        assertEquals(sessionId+1, ksession.getId());
        manager.disposeRuntimeEngine(runtime);     
        
        // when trying to access session after dispose 
        try {
            ksession.getId();
            fail("Should fail as session manager was closed and with that it's session");
        } catch (IllegalStateException e) {
            
        }
        manager.close();
    }
    
    @Test
    public void testCreationOfSessionWithPeristence() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);        
        assertNotNull(manager);
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 1);
        manager.disposeRuntimeEngine(runtime);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();    
        // session id should be 1+ previous session id
        assertEquals(sessionId+1, ksession.getId());
        sessionId = ksession.getId();
        manager.disposeRuntimeEngine(runtime);
        
        runtime = manager.getRuntimeEngine(EmptyContext.get());
        ksession = runtime.getKieSession();         
        // session id should be 1+ previous session id
        assertEquals(sessionId+1, ksession.getId());
        manager.disposeRuntimeEngine(runtime);       
        
        // when trying to access session after dispose 
        try {
            ksession.getId();
            fail("Should fail as session manager was closed and with that it's session");
        } catch (IllegalStateException e) {
            
        }
        manager.close();
    }
    
    @Test
    public void testCreationOfSessionWithinTransaction() throws Exception {
        
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2)
                .get();
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);        
        assertNotNull(manager);
        
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        
        RuntimeEngine runtime = manager.getRuntimeEngine(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 1);
        
        ut.commit();
        
        // since session was created with transaction tx sync is registered to dispose session
        // so now session should already be disposed
        try {
            ksession.getId();
            fail("Should fail as session manager was closed and with that it's session");
        } catch (IllegalStateException e) {
            
        }
        manager.close();
    }
    
    @Test
    public void testExecuteReusableSubprocess() {
        RuntimeEnvironment environment = RuntimeEnvironmentBuilder.getDefault()
                .userGroupCallback(userGroupCallback)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivity.bpmn2"), ResourceType.BPMN2)
                .addAsset(ResourceFactory.newClassPathResource("BPMN2-CallActivitySubProcess.bpmn2"), ResourceType.BPMN2)
                .get();
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);        
        assertNotNull(manager);
        // since there is no process instance yet we need to get new session
        RuntimeEngine runtime = manager.getRuntimeEngine(ProcessInstanceIdContext.get());
        KieSession ksession = runtime.getKieSession();

        assertNotNull(ksession);       
        int ksession1Id = ksession.getId();
        assertTrue(ksession1Id == 1);

        ProcessInstance pi1 = ksession.startProcess("ParentProcess");
        
        assertEquals(ProcessInstance.STATE_ACTIVE, pi1.getState());
               
        ksession.getWorkItemManager().completeWorkItem(1, null);
        manager.disposeRuntimeEngine(runtime);
        manager.close();
        
        JPAProcessInstanceDbLog.setEnvironment(environment.getEnvironment());
        
        List<ProcessInstanceLog> logs = JPAProcessInstanceDbLog.findActiveProcessInstances("ParentProcess");
        assertNotNull(logs);
        assertEquals(0, logs.size());
        
        logs = JPAProcessInstanceDbLog.findActiveProcessInstances("SubProcess");
        assertNotNull(logs);
        assertEquals(0, logs.size());
        
        logs = JPAProcessInstanceDbLog.findProcessInstances("ParentProcess");
        assertNotNull(logs);
        assertEquals(1, logs.size());
        
        logs = JPAProcessInstanceDbLog.findProcessInstances("SubProcess");
        assertNotNull(logs);
        assertEquals(1, logs.size());
    }
}
