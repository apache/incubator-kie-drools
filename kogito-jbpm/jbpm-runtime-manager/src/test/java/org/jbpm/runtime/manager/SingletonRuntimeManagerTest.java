package org.jbpm.runtime.manager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.jbpm.runtime.manager.impl.DefaultRuntimeEnvironment;
import org.jbpm.runtime.manager.impl.SimpleRuntimeEnvironment;
import org.jbpm.runtime.manager.util.TestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.KieSession;
import org.kie.runtime.manager.Runtime;
import org.kie.runtime.manager.RuntimeManager;
import org.kie.runtime.manager.RuntimeManagerFactory;
import org.kie.runtime.manager.context.EmptyContext;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class SingletonRuntimeManagerTest {
    
    private PoolingDataSource pds;
    
    @Before
    public void setup() {
        TestUtil.cleanupSingletonSessionId();
        pds = TestUtil.setupPoolingDataSource();
    }
    
    @After
    public void teardown() {
        pds.close();
    }

    @Test
    public void testCreationOfSession() {
        SimpleRuntimeEnvironment environment = new SimpleRuntimeEnvironment();
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        Runtime runtime = manager.getRuntime(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 0);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();       
        assertEquals(sessionId, ksession.getId());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntime(runtime);
        
        ksession = manager.getRuntime(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getId());
        
        // close manager which will close session maintained by the manager
        manager.close();
        
        runtime = manager.getRuntime(EmptyContext.get());
        assertNull(runtime);
    }
    
    @Test
    public void testCreationOfSessionWithPersistence() {
        SimpleRuntimeEnvironment environment = new DefaultRuntimeEnvironment();
        environment.addAsset(ResourceFactory.newClassPathResource("BPMN2-ScriptTask.bpmn2"), ResourceType.BPMN2);
        
        RuntimeManager manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);        
        assertNotNull(manager);
        
        Runtime runtime = manager.getRuntime(EmptyContext.get());
        KieSession ksession = runtime.getKieSession();
        assertNotNull(ksession);       
        
        int sessionId = ksession.getId();
        assertTrue(sessionId == 1);
        
        runtime = manager.getRuntime(EmptyContext.get());
        ksession = runtime.getKieSession();        
        assertEquals(sessionId, ksession.getId());
        // dispose session that should not have affect on the session at all
        manager.disposeRuntime(runtime);
        
        ksession = manager.getRuntime(EmptyContext.get()).getKieSession();        
        assertEquals(sessionId, ksession.getId());
        
        // close manager which will close session maintained by the manager
        manager.close();
        
        runtime = manager.getRuntime(EmptyContext.get());
        assertNull(runtime);
    }
}
