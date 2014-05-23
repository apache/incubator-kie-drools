package org.jbpm.runtime.manager.impl.deploy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.IOUtils;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.runtime.manager.impl.jpa.EntityManagerFactoryManager;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.task.identity.JBossUserGroupCallbackImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.manager.RegisterableItemsFactory;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironment;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.internal.runtime.manager.InternalRegisterableItemsFactory;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.UserGroupCallback;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class RuntimeManagerWithDescriptorTest extends AbstractDeploymentDescriptorTest {
	
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
        EntityManagerFactoryManager.get().clear();
        pds.close();
    }

    @Test
    public void testDeployWithDefaultDeploymentDescriptor() throws Exception {

		KieServices ks = KieServices.Factory.get();
		ReleaseId releaseId = ks.newReleaseId("org.jbpm.test.dd", "kjar-with-dd", "1.0.0");

		String processString = IOUtils.toString(this.getClass().getResourceAsStream("/BPMN2-ScriptTask.bpmn2"), "UTF-8");
		
		Map<String, String> resources = new HashMap<String, String>();
		resources.put("src/main/resources/BPMN2-ScriptTask.bpmn2", processString);

		InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
		deployKjar(releaseId, kJar1);		
        
    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder(releaseId)
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);
        
        InternalRuntimeManager internalManager = (InternalRuntimeManager) manager;
        DeploymentDescriptor descriptor = internalManager.getDeploymentDescriptor();
        assertNotNull(descriptor);
        RegisterableItemsFactory factory = internalManager.getEnvironment().getRegisterableItemsFactory();
        assertNotNull(factory);
        assertTrue(factory instanceof InternalRegisterableItemsFactory);
        
        assertNotNull(((InternalRegisterableItemsFactory) factory).getRuntimeManager());
        
        String descriptorFromKjar = descriptor.toXml();
        DeploymentDescriptorManager ddManager = new DeploymentDescriptorManager();
        String defaultDescriptor = ddManager.getDefaultDescriptor().toXml();
        
        assertEquals(defaultDescriptor, descriptorFromKjar);
    }
    
    
    @Test
    public void testDeployWithCustomDeploymentDescriptor() throws Exception {

		KieServices ks = KieServices.Factory.get();
		ReleaseId releaseId = ks.newReleaseId("org.jbpm.test.dd", "-kjar-with-dd", "1.0.0");
		
		DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.persistence.jpa");
		customDescriptor.getBuilder()
		.runtimeStrategy(RuntimeStrategy.PER_REQUEST)
		.addGlobal(new NamedObjectModel("service", "java.util.ArrayList"));

		String processString = IOUtils.toString(this.getClass().getResourceAsStream("/BPMN2-ScriptTask.bpmn2"), "UTF-8");
		
		Map<String, String> resources = new HashMap<String, String>();
		resources.put("src/main/resources/BPMN2-ScriptTask.bpmn2", processString);
		resources.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());
		
		String drl = "package org.jbpm; global java.util.List service; "
				+ "	rule \"Start Hello1\"" + "	  when" + "	  then"
				+ "	    System.out.println(\"Hello\");" + "	end";
		resources.put("src/main/resources/simple.drl", drl);

		InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
		deployKjar(releaseId, kJar1);		
        
    	RuntimeEnvironment environment = RuntimeEnvironmentBuilder.Factory.get()
    			.newDefaultBuilder(releaseId)
                .userGroupCallback(userGroupCallback)
                .get();
        
        manager = RuntimeManagerFactory.Factory.get().newSingletonRuntimeManager(environment);
        assertNotNull(manager);
        
        InternalRuntimeManager internalManager = (InternalRuntimeManager) manager;
        
        RegisterableItemsFactory factory = internalManager.getEnvironment().getRegisterableItemsFactory();
        assertNotNull(factory);
        assertTrue(factory instanceof InternalRegisterableItemsFactory);
        
        assertNotNull(((InternalRegisterableItemsFactory) factory).getRuntimeManager());
        
        DeploymentDescriptor descriptor = internalManager.getDeploymentDescriptor();
        assertNotNull(descriptor);
		assertEquals("org.jbpm.persistence.jpa", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.persistence.jpa", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.PER_REQUEST, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(1, descriptor.getGlobals().size());		
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
		assertEquals(0, descriptor.getRequiredRoles().size());
		
		RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
		assertNotNull(engine);
		
		Object service = engine.getKieSession().getGlobal("service");
		assertNotNull(service);
		assertTrue(service instanceof ArrayList);
    }
}
