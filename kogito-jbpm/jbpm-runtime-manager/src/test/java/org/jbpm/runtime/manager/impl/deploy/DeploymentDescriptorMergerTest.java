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

package org.jbpm.runtime.manager.impl.deploy;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assume;
import org.junit.Test;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.MergeMode;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;

public class DeploymentDescriptorMergerTest {

	@Test
	public void testDeploymentDesciptorMergeOverrideAll() {
		DeploymentDescriptor master = new DeploymentDescriptorImpl("org.jbpm.domain");

		master.getBuilder()
		.addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}))
		.setLimitSerializationClasses(true);

		assertNotNull(master);
		assertEquals("org.jbpm.domain", master.getPersistenceUnit());
		assertEquals("org.jbpm.domain", master.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, master.getAuditMode());
		assertEquals(PersistenceMode.JPA, master.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, master.getRuntimeStrategy());
		assertEquals(1, master.getMarshallingStrategies().size());
		assertEquals(0, master.getConfiguration().size());
		assertEquals(0, master.getEnvironmentEntries().size());
		assertEquals(0, master.getEventListeners().size());
		assertEquals(0, master.getGlobals().size());
		assertEquals(0, master.getTaskEventListeners().size());
		assertEquals(0, master.getWorkItemHandlers().size());
		assertTrue(master.getLimitSerializationClasses());

		DeploymentDescriptor slave = new DeploymentDescriptorImpl("org.jbpm.domain");

		slave.getBuilder()
		.auditMode(AuditMode.JMS)
		.persistenceMode(PersistenceMode.JPA)
		.persistenceUnit("my.custom.unit")
		.auditPersistenceUnit("my.custom.unit2")
		.setLimitSerializationClasses(false);

		assertNotNull(slave);
		assertEquals("my.custom.unit", slave.getPersistenceUnit());
		assertEquals("my.custom.unit2", slave.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, slave.getAuditMode());
		assertEquals(PersistenceMode.JPA, slave.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, slave.getRuntimeStrategy());
		assertEquals(0, slave.getMarshallingStrategies().size());
		assertEquals(0, slave.getConfiguration().size());
		assertEquals(0, slave.getEnvironmentEntries().size());
		assertEquals(0, slave.getEventListeners().size());
		assertEquals(0, slave.getGlobals().size());
		assertEquals(0, slave.getTaskEventListeners().size());
		assertEquals(0, slave.getWorkItemHandlers().size());
		assertFalse(slave.getLimitSerializationClasses());

		// and now let's merge them
		DeploymentDescriptorMerger merger = new DeploymentDescriptorMerger();
		DeploymentDescriptor outcome = merger.merge(master, slave, MergeMode.OVERRIDE_ALL);

		assertNotNull(outcome);
		assertEquals("my.custom.unit", outcome.getPersistenceUnit());
		assertEquals("my.custom.unit2", outcome.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, outcome.getAuditMode());
		assertEquals(PersistenceMode.JPA, outcome.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, outcome.getRuntimeStrategy());
		assertEquals(0, outcome.getMarshallingStrategies().size());
		assertEquals(0, outcome.getConfiguration().size());
		assertEquals(0, outcome.getEnvironmentEntries().size());
		assertEquals(0, outcome.getEventListeners().size());
		assertEquals(0, outcome.getGlobals().size());
		assertEquals(0, outcome.getTaskEventListeners().size());
		assertEquals(0, outcome.getWorkItemHandlers().size());
		assertFalse(outcome.getLimitSerializationClasses());
	}

	@Test
	public void testDeploymentDesciptorMergeKeepAll() {
		DeploymentDescriptor master = new DeploymentDescriptorImpl("org.jbpm.domain");

		master.getBuilder()
		.addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}))
		.setLimitSerializationClasses(true);

		assertNotNull(master);
		assertEquals("org.jbpm.domain", master.getPersistenceUnit());
		assertEquals("org.jbpm.domain", master.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, master.getAuditMode());
		assertEquals(PersistenceMode.JPA, master.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, master.getRuntimeStrategy());
		assertEquals(1, master.getMarshallingStrategies().size());
		assertEquals(0, master.getConfiguration().size());
		assertEquals(0, master.getEnvironmentEntries().size());
		assertEquals(0, master.getEventListeners().size());
		assertEquals(0, master.getGlobals().size());
		assertEquals(0, master.getTaskEventListeners().size());
		assertEquals(0, master.getWorkItemHandlers().size());
		assertTrue(master.getLimitSerializationClasses());

		DeploymentDescriptor slave = new DeploymentDescriptorImpl("org.jbpm.domain");

		slave.getBuilder()
		.auditMode(AuditMode.JMS)
		.persistenceMode(PersistenceMode.JPA)
		.persistenceUnit("my.custom.unit")
		.auditPersistenceUnit("my.custom.unit2")
		.setLimitSerializationClasses(false);

		assertNotNull(slave);
		assertEquals("my.custom.unit", slave.getPersistenceUnit());
		assertEquals("my.custom.unit2", slave.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, slave.getAuditMode());
		assertEquals(PersistenceMode.JPA, slave.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, slave.getRuntimeStrategy());
		assertEquals(0, slave.getMarshallingStrategies().size());
		assertEquals(0, slave.getConfiguration().size());
		assertEquals(0, slave.getEnvironmentEntries().size());
		assertEquals(0, slave.getEventListeners().size());
		assertEquals(0, slave.getGlobals().size());
		assertEquals(0, slave.getTaskEventListeners().size());
		assertEquals(0, slave.getWorkItemHandlers().size());
		assertFalse(slave.getLimitSerializationClasses());

		// and now let's merge them
		DeploymentDescriptorMerger merger = new DeploymentDescriptorMerger();
		DeploymentDescriptor outcome = merger.merge(master, slave, MergeMode.KEEP_ALL);

		assertNotNull(outcome);
		assertEquals("org.jbpm.domain", outcome.getPersistenceUnit());
		assertEquals("org.jbpm.domain", outcome.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, outcome.getAuditMode());
		assertEquals(PersistenceMode.JPA, outcome.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, outcome.getRuntimeStrategy());
		assertEquals(1, outcome.getMarshallingStrategies().size());
		assertEquals(0, outcome.getConfiguration().size());
		assertEquals(0, outcome.getEnvironmentEntries().size());
		assertEquals(0, outcome.getEventListeners().size());
		assertEquals(0, outcome.getGlobals().size());
		assertEquals(0, outcome.getTaskEventListeners().size());
		assertEquals(0, outcome.getWorkItemHandlers().size());
		assertTrue(outcome.getLimitSerializationClasses());
	}

	@Test
	public void testDeploymentDesciptorMergeOverrideEmpty() {
		DeploymentDescriptor master = new DeploymentDescriptorImpl("org.jbpm.domain");

		master.getBuilder()
		.addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}))
		.setLimitSerializationClasses(true);

		assertNotNull(master);
		assertEquals("org.jbpm.domain", master.getPersistenceUnit());
		assertEquals("org.jbpm.domain", master.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, master.getAuditMode());
		assertEquals(PersistenceMode.JPA, master.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, master.getRuntimeStrategy());
		assertEquals(1, master.getMarshallingStrategies().size());
		assertEquals(0, master.getConfiguration().size());
		assertEquals(0, master.getEnvironmentEntries().size());
		assertEquals(0, master.getEventListeners().size());
		assertEquals(0, master.getGlobals().size());
		assertEquals(0, master.getTaskEventListeners().size());
		assertEquals(0, master.getWorkItemHandlers().size());
		assertTrue(master.getLimitSerializationClasses());

		DeploymentDescriptor slave = new DeploymentDescriptorImpl("org.jbpm.domain");

		slave.getBuilder()
		.auditMode(AuditMode.JMS)
		.persistenceMode(PersistenceMode.JPA)
		.persistenceUnit(null)
		.auditPersistenceUnit("");

		assertNotNull(slave);
		assertEquals(null, slave.getPersistenceUnit());
		assertEquals("", slave.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, slave.getAuditMode());
		assertEquals(PersistenceMode.JPA, slave.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, slave.getRuntimeStrategy());
		assertEquals(0, slave.getMarshallingStrategies().size());
		assertEquals(0, slave.getConfiguration().size());
		assertEquals(0, slave.getEnvironmentEntries().size());
		assertEquals(0, slave.getEventListeners().size());
		assertEquals(0, slave.getGlobals().size());
		assertEquals(0, slave.getTaskEventListeners().size());
		assertEquals(0, slave.getWorkItemHandlers().size());
		((DeploymentDescriptorImpl) slave).setLimitSerializationClasses(null);
		assertNull(slave.getLimitSerializationClasses());

		// and now let's merge them
		DeploymentDescriptorMerger merger = new DeploymentDescriptorMerger();
		DeploymentDescriptor outcome = merger.merge(master, slave, MergeMode.OVERRIDE_EMPTY);

		assertNotNull(outcome);
		assertEquals("org.jbpm.domain", outcome.getPersistenceUnit());
		assertEquals("org.jbpm.domain", outcome.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, outcome.getAuditMode());
		assertEquals(PersistenceMode.JPA, outcome.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, outcome.getRuntimeStrategy());
		assertEquals(1, outcome.getMarshallingStrategies().size());
		assertEquals(0, outcome.getConfiguration().size());
		assertEquals(0, outcome.getEnvironmentEntries().size());
		assertEquals(0, outcome.getEventListeners().size());
		assertEquals(0, outcome.getGlobals().size());
		assertEquals(0, outcome.getTaskEventListeners().size());
		assertEquals(0, outcome.getWorkItemHandlers().size());
		assertTrue(outcome.getLimitSerializationClasses());
	}

	@Test
	public void testDeploymentDesciptorMergeMergeCollections() {
		DeploymentDescriptor master = new DeploymentDescriptorImpl("org.jbpm.domain");

		master.getBuilder()
		.addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}))
		.setLimitSerializationClasses(true);

		assertNotNull(master);
		assertEquals("org.jbpm.domain", master.getPersistenceUnit());
		assertEquals("org.jbpm.domain", master.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, master.getAuditMode());
		assertEquals(PersistenceMode.JPA, master.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, master.getRuntimeStrategy());
		assertEquals(1, master.getMarshallingStrategies().size());
		assertEquals(0, master.getConfiguration().size());
		assertEquals(0, master.getEnvironmentEntries().size());
		assertEquals(0, master.getEventListeners().size());
		assertEquals(0, master.getGlobals().size());
		assertEquals(0, master.getTaskEventListeners().size());
		assertEquals(0, master.getWorkItemHandlers().size());
		assertTrue(master.getLimitSerializationClasses());

		DeploymentDescriptor slave = new DeploymentDescriptorImpl("org.jbpm.domain");

		slave.getBuilder()
		.auditMode(AuditMode.JMS)
		.persistenceMode(PersistenceMode.JPA)
		.persistenceUnit(null)
		.auditPersistenceUnit("")
		.addMarshalingStrategy(new ObjectModel("org.jbpm.test.AnotherCustomStrategy", new Object[]{"param2"}))
		.setLimitSerializationClasses(false);

		assertNotNull(slave);
		assertEquals(null, slave.getPersistenceUnit());
		assertEquals("", slave.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, slave.getAuditMode());
		assertEquals(PersistenceMode.JPA, slave.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, slave.getRuntimeStrategy());
		assertEquals(1, slave.getMarshallingStrategies().size());
		assertEquals(0, slave.getConfiguration().size());
		assertEquals(0, slave.getEnvironmentEntries().size());
		assertEquals(0, slave.getEventListeners().size());
		assertEquals(0, slave.getGlobals().size());
		assertEquals(0, slave.getTaskEventListeners().size());
		assertEquals(0, slave.getWorkItemHandlers().size());
		assertFalse(slave.getLimitSerializationClasses());

		// and now let's merge them
		DeploymentDescriptorMerger merger = new DeploymentDescriptorMerger();
		DeploymentDescriptor outcome = merger.merge(master, slave, MergeMode.MERGE_COLLECTIONS);

		assertNotNull(outcome);
		assertEquals("org.jbpm.domain", outcome.getPersistenceUnit());
		assertEquals("org.jbpm.domain", outcome.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, outcome.getAuditMode());
		assertEquals(PersistenceMode.JPA, outcome.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, outcome.getRuntimeStrategy());
		assertEquals(2, outcome.getMarshallingStrategies().size());
		assertEquals(0, outcome.getConfiguration().size());
		assertEquals(0, outcome.getEnvironmentEntries().size());
		assertEquals(0, outcome.getEventListeners().size());
		assertEquals(0, outcome.getGlobals().size());
		assertEquals(0, outcome.getTaskEventListeners().size());
		assertEquals(0, outcome.getWorkItemHandlers().size());
		assertFalse(outcome.getLimitSerializationClasses());
	}

	@Test
	public void testDeploymentDesciptorMergeHierarchy() {
		DeploymentDescriptor master = new DeploymentDescriptorImpl("org.jbpm.domain");

		master.getBuilder()
		.addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}));

		assertNotNull(master);
		assertEquals("org.jbpm.domain", master.getPersistenceUnit());
		assertEquals("org.jbpm.domain", master.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, master.getAuditMode());
		assertEquals(PersistenceMode.JPA, master.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, master.getRuntimeStrategy());
		assertEquals(1, master.getMarshallingStrategies().size());
		assertEquals(0, master.getConfiguration().size());
		assertEquals(0, master.getEnvironmentEntries().size());
		assertEquals(0, master.getEventListeners().size());
		assertEquals(0, master.getGlobals().size());
		assertEquals(0, master.getTaskEventListeners().size());
		assertEquals(0, master.getWorkItemHandlers().size());

		DeploymentDescriptor slave = new DeploymentDescriptorImpl("org.jbpm.domain");

		slave.getBuilder()
		.auditMode(AuditMode.NONE)
		.persistenceMode(PersistenceMode.JPA)
		.persistenceUnit("my.custom.unit")
		.auditPersistenceUnit("my.custom.unit2");

		assertNotNull(slave);
		assertEquals("my.custom.unit", slave.getPersistenceUnit());
		assertEquals("my.custom.unit2", slave.getAuditPersistenceUnit());
		assertEquals(AuditMode.NONE, slave.getAuditMode());
		assertEquals(PersistenceMode.JPA, slave.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, slave.getRuntimeStrategy());
		assertEquals(0, slave.getMarshallingStrategies().size());
		assertEquals(0, slave.getConfiguration().size());
		assertEquals(0, slave.getEnvironmentEntries().size());
		assertEquals(0, slave.getEventListeners().size());
		assertEquals(0, slave.getGlobals().size());
		assertEquals(0, slave.getTaskEventListeners().size());
		assertEquals(0, slave.getWorkItemHandlers().size());

		DeploymentDescriptor slave2 = new DeploymentDescriptorImpl("org.jbpm.domain");

		slave2.getBuilder()
		.auditMode(AuditMode.JMS)
		.persistenceMode(PersistenceMode.JPA)
		.persistenceUnit("my.custom.unit2")
		.auditPersistenceUnit("my.custom.altered")
		.runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE)
		.addEnvironmentEntry(new NamedObjectModel("IS_JTA", "java.lang.Boolean", new Object[]{"false"}));

		assertNotNull(slave2);
		assertEquals("my.custom.unit2", slave2.getPersistenceUnit());
		assertEquals("my.custom.altered", slave2.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, slave2.getAuditMode());
		assertEquals(PersistenceMode.JPA, slave2.getPersistenceMode());
		assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, slave2.getRuntimeStrategy());
		assertEquals(0, slave2.getMarshallingStrategies().size());
		assertEquals(0, slave2.getConfiguration().size());
		assertEquals(1, slave2.getEnvironmentEntries().size());
		assertEquals(0, slave2.getEventListeners().size());
		assertEquals(0, slave2.getGlobals().size());
		assertEquals(0, slave2.getTaskEventListeners().size());
		assertEquals(0, slave2.getWorkItemHandlers().size());

		// assemble hierarchy
		List<DeploymentDescriptor> hierarchy = new ArrayList<DeploymentDescriptor>();
		hierarchy.add(slave2);
		hierarchy.add(slave);
		hierarchy.add(master);

		// and now let's merge them
		DeploymentDescriptorMerger merger = new DeploymentDescriptorMerger();
		DeploymentDescriptor outcome = merger.merge(hierarchy, MergeMode.MERGE_COLLECTIONS);

		assertNotNull(outcome);
		assertEquals("my.custom.unit2", outcome.getPersistenceUnit());
		assertEquals("my.custom.altered", outcome.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, outcome.getAuditMode());
		assertEquals(PersistenceMode.JPA, outcome.getPersistenceMode());
		assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, outcome.getRuntimeStrategy());
		assertEquals(1, outcome.getMarshallingStrategies().size());
		assertEquals(0, outcome.getConfiguration().size());
		assertEquals(1, outcome.getEnvironmentEntries().size());
		assertEquals(0, outcome.getEventListeners().size());
		assertEquals(0, outcome.getGlobals().size());
		assertEquals(0, outcome.getTaskEventListeners().size());
		assertEquals(0, outcome.getWorkItemHandlers().size());
	}

	@Test
	public void testDeploymentDesciptorMergeMergeCollectionsAvoidDuplicates() {
		DeploymentDescriptor master = new DeploymentDescriptorImpl("org.jbpm.domain");

		master.getBuilder()
		.addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}));

		assertNotNull(master);
		assertEquals("org.jbpm.domain", master.getPersistenceUnit());
		assertEquals("org.jbpm.domain", master.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, master.getAuditMode());
		assertEquals(PersistenceMode.JPA, master.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, master.getRuntimeStrategy());
		assertEquals(1, master.getMarshallingStrategies().size());
		assertEquals(0, master.getConfiguration().size());
		assertEquals(0, master.getEnvironmentEntries().size());
		assertEquals(0, master.getEventListeners().size());
		assertEquals(0, master.getGlobals().size());
		assertEquals(0, master.getTaskEventListeners().size());
		assertEquals(0, master.getWorkItemHandlers().size());

		DeploymentDescriptor slave = new DeploymentDescriptorImpl("org.jbpm.domain");

		slave.getBuilder()
		.auditMode(AuditMode.JMS)
		.persistenceMode(PersistenceMode.JPA)
		.persistenceUnit(null)
		.auditPersistenceUnit("")
		.addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}));

		assertNotNull(slave);
		assertEquals(null, slave.getPersistenceUnit());
		assertEquals("", slave.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, slave.getAuditMode());
		assertEquals(PersistenceMode.JPA, slave.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, slave.getRuntimeStrategy());
		assertEquals(1, slave.getMarshallingStrategies().size());
		assertEquals(0, slave.getConfiguration().size());
		assertEquals(0, slave.getEnvironmentEntries().size());
		assertEquals(0, slave.getEventListeners().size());
		assertEquals(0, slave.getGlobals().size());
		assertEquals(0, slave.getTaskEventListeners().size());
		assertEquals(0, slave.getWorkItemHandlers().size());

		// and now let's merge them
		DeploymentDescriptorMerger merger = new DeploymentDescriptorMerger();
		DeploymentDescriptor outcome = merger.merge(master, slave, MergeMode.MERGE_COLLECTIONS);

		assertNotNull(outcome);
		assertEquals("org.jbpm.domain", outcome.getPersistenceUnit());
		assertEquals("org.jbpm.domain", outcome.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, outcome.getAuditMode());
		assertEquals(PersistenceMode.JPA, outcome.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, outcome.getRuntimeStrategy());
		assertEquals(1, outcome.getMarshallingStrategies().size());
		assertEquals(0, outcome.getConfiguration().size());
		assertEquals(0, outcome.getEnvironmentEntries().size());
		assertEquals(0, outcome.getEventListeners().size());
		assertEquals(0, outcome.getGlobals().size());
		assertEquals(0, outcome.getTaskEventListeners().size());
		assertEquals(0, outcome.getWorkItemHandlers().size());
	}

	@Test
	public void testDeploymentDesciptorMergeMergeCollectionsAvoidDuplicatesNamedObject() {
		DeploymentDescriptor master = new DeploymentDescriptorImpl("org.jbpm.domain");

		master.getBuilder()
		.addWorkItemHandler(new NamedObjectModel("mvel", "Log", "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()"));

		assertNotNull(master);
		assertEquals("org.jbpm.domain", master.getPersistenceUnit());
		assertEquals("org.jbpm.domain", master.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, master.getAuditMode());
		assertEquals(PersistenceMode.JPA, master.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, master.getRuntimeStrategy());
		assertEquals(0, master.getMarshallingStrategies().size());
		assertEquals(0, master.getConfiguration().size());
		assertEquals(0, master.getEnvironmentEntries().size());
		assertEquals(0, master.getEventListeners().size());
		assertEquals(0, master.getGlobals().size());
		assertEquals(0, master.getTaskEventListeners().size());
		assertEquals(1, master.getWorkItemHandlers().size());

		DeploymentDescriptor slave = new DeploymentDescriptorImpl("org.jbpm.domain");

		slave.getBuilder()
		.auditMode(AuditMode.JMS)
		.persistenceMode(PersistenceMode.JPA)
		.persistenceUnit(null)
		.auditPersistenceUnit("")
		.addWorkItemHandler(new NamedObjectModel("mvel", "Log", "new org.jbpm.process.instance.impl.demo.CustomSystemOutWorkItemHandler()"));

		assertNotNull(slave);
		assertEquals(null, slave.getPersistenceUnit());
		assertEquals("", slave.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, slave.getAuditMode());
		assertEquals(PersistenceMode.JPA, slave.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, slave.getRuntimeStrategy());
		assertEquals(0, slave.getMarshallingStrategies().size());
		assertEquals(0, slave.getConfiguration().size());
		assertEquals(0, slave.getEnvironmentEntries().size());
		assertEquals(0, slave.getEventListeners().size());
		assertEquals(0, slave.getGlobals().size());
		assertEquals(0, slave.getTaskEventListeners().size());
		assertEquals(1, slave.getWorkItemHandlers().size());

		// and now let's merge them
		DeploymentDescriptorMerger merger = new DeploymentDescriptorMerger();
		DeploymentDescriptor outcome = merger.merge(master, slave, MergeMode.MERGE_COLLECTIONS);

		assertNotNull(outcome);
		assertEquals("org.jbpm.domain", outcome.getPersistenceUnit());
		assertEquals("org.jbpm.domain", outcome.getAuditPersistenceUnit());
		assertEquals(AuditMode.JMS, outcome.getAuditMode());
		assertEquals(PersistenceMode.JPA, outcome.getPersistenceMode());
		assertEquals(RuntimeStrategy.SINGLETON, outcome.getRuntimeStrategy());
		assertEquals(0, outcome.getMarshallingStrategies().size());
		assertEquals(0, outcome.getConfiguration().size());
		assertEquals(0, outcome.getEnvironmentEntries().size());
		assertEquals(0, outcome.getEventListeners().size());
		assertEquals(0, outcome.getGlobals().size());
		assertEquals(0, outcome.getTaskEventListeners().size());
		assertEquals(1, outcome.getWorkItemHandlers().size());

		// let's check if the slave version is preserved
		NamedObjectModel model = outcome.getWorkItemHandlers().get(0);
		assertEquals("Log", model.getName());
		assertEquals("new org.jbpm.process.instance.impl.demo.CustomSystemOutWorkItemHandler()", model.getIdentifier());
	}

	private static final String jarLocRegexStr = "([\\d\\.]{3})\\S*";
	private static final Pattern jarLocRegex = Pattern.compile(jarLocRegexStr);

	/**
	 * This test will fail in the IDE because of the IDE will mess with the classpath.
	 */
	@Test
	public void changeDefaultLimitSerializationClassesValueToTrueIn7x() throws Exception {
	    Properties props = new Properties();
	    String testPropsFileName = "test.properties";
	    InputStream testPropsStream = this.getClass().getResourceAsStream("/" + testPropsFileName);
	    assertNotNull("Unable to find or open " + testPropsFileName, testPropsFileName);
	    props.load(testPropsStream);
	    String projectVersionStr = (String) props.get("project.version");

        Matcher matcher = jarLocRegex.matcher(projectVersionStr);
        assertTrue( "Fix regular expression: " + jarLocRegexStr, matcher.matches() );
        double jarVersion = Double.parseDouble(matcher.group(1));

	    DeploymentDescriptorImpl depDesc = new DeploymentDescriptorImpl();

	    assertTrue( "The default value of 'limitSerializationClasses is FALSE in 6.x and TRUE in 7.x",
	            jarVersion < 7.0d && ! depDesc.getLimitSerializationClasses()
	            || jarVersion >= 7.0d && depDesc.getLimitSerializationClasses() );
	}
}
