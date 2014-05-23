package org.jbpm.runtime.manager.impl.deploy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.MergeMode;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;

public class DeploymentDescriptorManagerTest extends AbstractDeploymentDescriptorTest {

	private static final String SIMPLE_DRL = "package org.jbpm; "
			+ "	rule \"Start Hello1\"" + "	  when" + "	  then"
			+ "	    System.out.println(\"Hello\");" + "	end";

	protected static final String ARTIFACT_ID = "test-module";
	protected static final String GROUP_ID = "org.jbpm.test";
	protected static final String VERSION = "1.0.0-SNAPSHOT";

	

	@Test
	public void testDefaultDeploymentDescriptor() {
		DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");

		DeploymentDescriptor descriptor = manager.getDefaultDescriptor();

		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
	}

	@Test
	public void testDefaultDeploymentDescriptorFromClasspath() {
		System.setProperty("org.kie.deployment.desc.location",
				"classpath:/deployment/deployment-descriptor-defaults-and-ms.xml");
		DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");

		DeploymentDescriptor descriptor = manager.getDefaultDescriptor();

		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
		assertEquals(1, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
	}

	@Test
	public void testDefaultDeploymentDescriptorFromFile() {
		System.setProperty("org.kie.deployment.desc.location",
				"file:src/test/resources/deployment/deployment-descriptor-defaults-and-ms.xml");
		DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");

		DeploymentDescriptor descriptor = manager.getDefaultDescriptor();

		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
		assertEquals(1, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
	}

	@Test
	public void testDeploymentDescriptorFromKieContainerNoDescInKjar() {
		DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");

		KieServices ks = KieServices.Factory.get();
		ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);

		Map<String, String> resources = new HashMap<String, String>();
		resources.put("src/main/resources/simple.drl", SIMPLE_DRL);

		InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
		deployKjar(releaseId, kJar1);

		KieContainer kieContainer = ks.newKieContainer(releaseId);
		assertNotNull(kieContainer);

		List<DeploymentDescriptor> descriptorHierarchy = manager.getDeploymentDescriptorHierarchy(kieContainer);
		assertNotNull(descriptorHierarchy);
		assertEquals(1, descriptorHierarchy.size());

		DeploymentDescriptor descriptor = descriptorHierarchy.get(0);

		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());

	}

	@Test
	public void testDeploymentDescriptorFromKieContainer() {
		DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");

		KieServices ks = KieServices.Factory.get();
		ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);

		DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		descriptor.getBuilder().runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);

		Map<String, String> resources = new HashMap<String, String>();
		resources.put("src/main/resources/simple.drl", SIMPLE_DRL);
		resources.put("src/main/resources/META-INF/kie-deployment-descriptor.xml",
				descriptor.toXml());

		InternalKieModule kJar1 = createKieJar(ks, releaseId, resources);
		deployKjar(releaseId, kJar1);

		KieContainer kieContainer = ks.newKieContainer(releaseId);
		assertNotNull(kieContainer);

		List<DeploymentDescriptor> descriptorHierarchy = manager
				.getDeploymentDescriptorHierarchy(kieContainer);
		assertNotNull(descriptorHierarchy);
		assertEquals(2, descriptorHierarchy.size());
		
		descriptor = descriptorHierarchy.get(0);

		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
		
		descriptor = descriptorHierarchy.get(1);

		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());

	}
	
	@Test
	public void testDeploymentDescriptorFromKieContainerWithDependency() {
		DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");

		KieServices ks = KieServices.Factory.get();
		// create dependency kjar
		ReleaseId releaseIdDep = ks.newReleaseId(GROUP_ID, "dependency-data", VERSION);

		DeploymentDescriptor descriptorDep = new DeploymentDescriptorImpl("org.jbpm.domain");
		descriptorDep.getBuilder()
		.runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE)
		.auditPersistenceUnit("org.jbpm.audit");

		Map<String, String> resourcesDep = new HashMap<String, String>();
		resourcesDep.put("src/main/resources/simple.drl", SIMPLE_DRL);
		resourcesDep.put("src/main/resources/META-INF/kie-deployment-descriptor.xml",
				descriptorDep.toXml());

		InternalKieModule kJarDep = createKieJar(ks, releaseIdDep, resourcesDep);
		deployKjar(releaseIdDep, kJarDep);
		
		// create first kjar that will have dependency to another
		ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);

		DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		descriptor.getBuilder()
		.runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);

		Map<String, String> resources = new HashMap<String, String>();
		resources.put("src/main/resources/simple.drl", SIMPLE_DRL);
		resources.put("src/main/resources/META-INF/kie-deployment-descriptor.xml",
				descriptor.toXml());

		InternalKieModule kJar1 = createKieJar(ks, releaseId, resources, releaseIdDep);
		deployKjar(releaseId, kJar1);

		KieContainer kieContainer = ks.newKieContainer(releaseId);
		assertNotNull(kieContainer);

		List<DeploymentDescriptor> descriptorHierarchy = manager.getDeploymentDescriptorHierarchy(kieContainer);
		assertNotNull(descriptorHierarchy);
		assertEquals(3, descriptorHierarchy.size());
		
		descriptor = descriptorHierarchy.get(0);
		
		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
		
		descriptor = descriptorHierarchy.get(1);

		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.audit", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());		

		descriptor = descriptorHierarchy.get(2);

		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());

	}

	@Test
	public void testDeploymentDescriptorFromKieContainerWithDependencyMerged() {
		DeploymentDescriptorManager manager = new DeploymentDescriptorManager("org.jbpm.domain");

		KieServices ks = KieServices.Factory.get();
		// create dependency kjar
		ReleaseId releaseIdDep = ks.newReleaseId(GROUP_ID, "dependency-data", VERSION);

		DeploymentDescriptor descriptorDep = new DeploymentDescriptorImpl("org.jbpm.domain");
		descriptorDep.getBuilder()
		.runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE)
		.auditPersistenceUnit("org.jbpm.audit")
		.addGlobal(new NamedObjectModel("service", "org.jbpm.global.Service"));

		Map<String, String> resourcesDep = new HashMap<String, String>();
		resourcesDep.put("src/main/resources/simple.drl", SIMPLE_DRL);
		resourcesDep.put("src/main/resources/META-INF/kie-deployment-descriptor.xml",
				descriptorDep.toXml());

		InternalKieModule kJarDep = createKieJar(ks, releaseIdDep, resourcesDep);
		deployKjar(releaseIdDep, kJarDep);
		
		// create first kjar that will have dependency to another
		ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);

		DeploymentDescriptor descriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		descriptor.getBuilder()
		.runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE);

		Map<String, String> resources = new HashMap<String, String>();
		resources.put("src/main/resources/simple.drl", SIMPLE_DRL);
		resources.put("src/main/resources/META-INF/kie-deployment-descriptor.xml",
				descriptor.toXml());

		InternalKieModule kJar1 = createKieJar(ks, releaseId, resources, releaseIdDep);
		deployKjar(releaseId, kJar1);

		KieContainer kieContainer = ks.newKieContainer(releaseId);
		assertNotNull(kieContainer);

		List<DeploymentDescriptor> descriptorHierarchy = manager.getDeploymentDescriptorHierarchy(kieContainer);
		assertNotNull(descriptorHierarchy);
		assertEquals(3, descriptorHierarchy.size());
		
		descriptor = descriptorHierarchy.get(0);
		
		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
		
		descriptor = descriptorHierarchy.get(1);

		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.audit", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(1, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());		

		descriptor = descriptorHierarchy.get(2);

		assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(0, descriptor.getWorkItemHandlers().size());
		
		DeploymentDescriptorMerger merger = new DeploymentDescriptorMerger();
		DeploymentDescriptor outcome = merger.merge(descriptorHierarchy, MergeMode.MERGE_COLLECTIONS);
		
		assertNotNull(outcome);
		assertEquals("org.jbpm.domain", outcome.getPersistenceUnit());
		assertEquals("org.jbpm.domain", outcome.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, outcome.getAuditMode());
		assertEquals(PersistenceMode.JPA, outcome.getPersistenceMode());
		assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, outcome.getRuntimeStrategy());
		assertEquals(0, outcome.getMarshallingStrategies().size());
		assertEquals(0, outcome.getConfiguration().size());
		assertEquals(0, outcome.getEnvironmentEntries().size());
		assertEquals(0, outcome.getEventListeners().size());
		assertEquals(1, outcome.getGlobals().size());
		assertEquals(0, outcome.getTaskEventListeners().size());
		assertEquals(0, outcome.getWorkItemHandlers().size());

	}


}
