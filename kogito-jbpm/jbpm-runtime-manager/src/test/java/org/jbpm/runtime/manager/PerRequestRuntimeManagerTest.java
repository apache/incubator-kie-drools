package org.jbpm.runtime.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Properties;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.task.identity.JBossUserGroupCallbackImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.manager.Runtime;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.manager.context.EmptyContext;
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
        SimpleRuntimeEnvironment environment = new SimpleRuntimeEnvironment();
        environment.setUserGroupCallback(userGroupCallback);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);        
        assertNotNull(manager);
        
        Runtime runtime = manager.getRuntime(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 0);
        manager.disposeRuntime(runtime);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();    
        // session id should be 1+ previous session id
        assertEquals(sessionId+1, ksession.getId());
        sessionId = ksession.getId();
        manager.disposeRuntime(runtime);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();         
        // session id should be 1+ previous session id
        assertEquals(sessionId+1, ksession.getId());
        manager.disposeRuntime(runtime);     
        
        // when trying to access session after dispose 
        try {
            ksession.getId();
            fail("Should fail as session manager was closed and with that it's session");
        } catch (IllegalStateException e) {
            
        }
    }
    
    @Test
    public void testCreationOfSessionWithPeristence() {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.setUserGroupCallback(userGroupCallback);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);        
        assertNotNull(manager);
        
        Runtime runtime = manager.getRuntime(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 1);
        manager.disposeRuntime(runtime);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();    
        // session id should be 1+ previous session id
        assertEquals(sessionId+1, ksession.getId());
        sessionId = ksession.getId();
        manager.disposeRuntime(runtime);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();         
        // session id should be 1+ previous session id
        assertEquals(sessionId+1, ksession.getId());
        manager.disposeRuntime(runtime);       
        
        // when trying to access session after dispose 
        try {
            ksession.getId();
            fail("Should fail as session manager was closed and with that it's session");
        } catch (IllegalStateException e) {
            
        }
    }
    
    @Test
    public void testCreationOfSessionWithinTransaction() throws Exception {
        
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.setUserGroupCallback(userGroupCallback);
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newPerRequestRuntimeManager(environment);        
        assertNotNull(manager);
        
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        
        Runtime runtime = manager.getRuntime(EmptyContext.get());
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
    }
}
