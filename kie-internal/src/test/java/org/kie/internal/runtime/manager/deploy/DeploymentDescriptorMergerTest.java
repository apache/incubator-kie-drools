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

package org.kie.internal.runtime.manager.deploy;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.MergeMode;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DeploymentDescriptorMergerTest {

    @Test
    public void testDeploymentDesciptorMergeOverrideAll() {
        DeploymentDescriptor primary = new DeploymentDescriptorImpl("org.jbpm.domain");

        primary.getBuilder()
                .addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}))
                .setLimitSerializationClasses(true);

        assertThat(primary).isNotNull();
        assertEquals("org.jbpm.domain", primary.getPersistenceUnit());
        assertEquals("org.jbpm.domain", primary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JPA, primary.getAuditMode());
        assertEquals(PersistenceMode.JPA, primary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, primary.getRuntimeStrategy());
        assertEquals(1, primary.getMarshallingStrategies().size());
        assertEquals(0, primary.getConfiguration().size());
        assertEquals(0, primary.getEnvironmentEntries().size());
        assertEquals(0, primary.getEventListeners().size());
        assertEquals(0, primary.getGlobals().size());
        assertEquals(0, primary.getTaskEventListeners().size());
        assertEquals(0, primary.getWorkItemHandlers().size());
        assertTrue(primary.getLimitSerializationClasses());

        DeploymentDescriptor secondary = new DeploymentDescriptorImpl("org.jbpm.domain");

        secondary.getBuilder()
                .auditMode(AuditMode.JMS)
                .persistenceMode(PersistenceMode.JPA)
                .persistenceUnit("my.custom.unit")
                .auditPersistenceUnit("my.custom.unit2")
                .setLimitSerializationClasses(false);

        assertThat(secondary).isNotNull();
        assertEquals("my.custom.unit", secondary.getPersistenceUnit());
        assertEquals("my.custom.unit2", secondary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JMS, secondary.getAuditMode());
        assertEquals(PersistenceMode.JPA, secondary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, secondary.getRuntimeStrategy());
        assertEquals(0, secondary.getMarshallingStrategies().size());
        assertEquals(0, secondary.getConfiguration().size());
        assertEquals(0, secondary.getEnvironmentEntries().size());
        assertEquals(0, secondary.getEventListeners().size());
        assertEquals(0, secondary.getGlobals().size());
        assertEquals(0, secondary.getTaskEventListeners().size());
        assertEquals(0, secondary.getWorkItemHandlers().size());
        assertFalse(secondary.getLimitSerializationClasses());

        // and now let's merge them

        DeploymentDescriptor outcome = DeploymentDescriptorMerger.merge(primary, secondary, MergeMode.OVERRIDE_ALL);

        assertThat(outcome).isNotNull();
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
        DeploymentDescriptor primary = new DeploymentDescriptorImpl("org.jbpm.domain");

        primary.getBuilder()
                .addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}))
                .setLimitSerializationClasses(true);

        assertThat(primary).isNotNull();
        assertEquals("org.jbpm.domain", primary.getPersistenceUnit());
        assertEquals("org.jbpm.domain", primary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JPA, primary.getAuditMode());
        assertEquals(PersistenceMode.JPA, primary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, primary.getRuntimeStrategy());
        assertEquals(1, primary.getMarshallingStrategies().size());
        assertEquals(0, primary.getConfiguration().size());
        assertEquals(0, primary.getEnvironmentEntries().size());
        assertEquals(0, primary.getEventListeners().size());
        assertEquals(0, primary.getGlobals().size());
        assertEquals(0, primary.getTaskEventListeners().size());
        assertEquals(0, primary.getWorkItemHandlers().size());
        assertTrue(primary.getLimitSerializationClasses());

        DeploymentDescriptor secondary = new DeploymentDescriptorImpl("org.jbpm.domain");

        secondary.getBuilder()
                .auditMode(AuditMode.JMS)
                .persistenceMode(PersistenceMode.JPA)
                .persistenceUnit("my.custom.unit")
                .auditPersistenceUnit("my.custom.unit2")
                .setLimitSerializationClasses(false);

        assertThat(secondary).isNotNull();
        assertEquals("my.custom.unit", secondary.getPersistenceUnit());
        assertEquals("my.custom.unit2", secondary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JMS, secondary.getAuditMode());
        assertEquals(PersistenceMode.JPA, secondary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, secondary.getRuntimeStrategy());
        assertEquals(0, secondary.getMarshallingStrategies().size());
        assertEquals(0, secondary.getConfiguration().size());
        assertEquals(0, secondary.getEnvironmentEntries().size());
        assertEquals(0, secondary.getEventListeners().size());
        assertEquals(0, secondary.getGlobals().size());
        assertEquals(0, secondary.getTaskEventListeners().size());
        assertEquals(0, secondary.getWorkItemHandlers().size());
        assertFalse(secondary.getLimitSerializationClasses());

        // and now let's merge them
        DeploymentDescriptor outcome = DeploymentDescriptorMerger.merge(primary, secondary, MergeMode.KEEP_ALL);

        assertThat(outcome).isNotNull();
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
        DeploymentDescriptor primary = new DeploymentDescriptorImpl("org.jbpm.domain");

        primary.getBuilder()
                .addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}))
                .setLimitSerializationClasses(true);

        assertThat(primary).isNotNull();
        assertEquals("org.jbpm.domain", primary.getPersistenceUnit());
        assertEquals("org.jbpm.domain", primary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JPA, primary.getAuditMode());
        assertEquals(PersistenceMode.JPA, primary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, primary.getRuntimeStrategy());
        assertEquals(1, primary.getMarshallingStrategies().size());
        assertEquals(0, primary.getConfiguration().size());
        assertEquals(0, primary.getEnvironmentEntries().size());
        assertEquals(0, primary.getEventListeners().size());
        assertEquals(0, primary.getGlobals().size());
        assertEquals(0, primary.getTaskEventListeners().size());
        assertEquals(0, primary.getWorkItemHandlers().size());
        assertTrue(primary.getLimitSerializationClasses());

        DeploymentDescriptor secondary = new DeploymentDescriptorImpl("org.jbpm.domain");

        secondary.getBuilder()
                .auditMode(AuditMode.JMS)
                .persistenceMode(PersistenceMode.JPA)
                .persistenceUnit(null)
                .auditPersistenceUnit("");

        assertThat(secondary).isNotNull();
        assertEquals(null, secondary.getPersistenceUnit());
        assertEquals("", secondary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JMS, secondary.getAuditMode());
        assertEquals(PersistenceMode.JPA, secondary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, secondary.getRuntimeStrategy());
        assertEquals(0, secondary.getMarshallingStrategies().size());
        assertEquals(0, secondary.getConfiguration().size());
        assertEquals(0, secondary.getEnvironmentEntries().size());
        assertEquals(0, secondary.getEventListeners().size());
        assertEquals(0, secondary.getGlobals().size());
        assertEquals(0, secondary.getTaskEventListeners().size());
        assertEquals(0, secondary.getWorkItemHandlers().size());
        ((DeploymentDescriptorImpl) secondary).setLimitSerializationClasses(null);
        assertNull(secondary.getLimitSerializationClasses());

        // and now let's merge them
        DeploymentDescriptor outcome = DeploymentDescriptorMerger.merge(primary, secondary, MergeMode.OVERRIDE_EMPTY);

        assertThat(outcome).isNotNull();
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
        DeploymentDescriptor primary = new DeploymentDescriptorImpl("org.jbpm.domain");

        primary.getBuilder()
                .addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}))
                .setLimitSerializationClasses(true);

        assertThat(primary).isNotNull();
        assertEquals("org.jbpm.domain", primary.getPersistenceUnit());
        assertEquals("org.jbpm.domain", primary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JPA, primary.getAuditMode());
        assertEquals(PersistenceMode.JPA, primary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, primary.getRuntimeStrategy());
        assertEquals(1, primary.getMarshallingStrategies().size());
        assertEquals(0, primary.getConfiguration().size());
        assertEquals(0, primary.getEnvironmentEntries().size());
        assertEquals(0, primary.getEventListeners().size());
        assertEquals(0, primary.getGlobals().size());
        assertEquals(0, primary.getTaskEventListeners().size());
        assertEquals(0, primary.getWorkItemHandlers().size());
        assertTrue(primary.getLimitSerializationClasses());

        DeploymentDescriptor secondary = new DeploymentDescriptorImpl("org.jbpm.domain");

        secondary.getBuilder()
                .auditMode(AuditMode.JMS)
                .persistenceMode(PersistenceMode.JPA)
                .persistenceUnit(null)
                .auditPersistenceUnit("")
                .addMarshalingStrategy(new ObjectModel("org.jbpm.test.AnotherCustomStrategy", new Object[]{"param2"}))
                .setLimitSerializationClasses(false);

        assertThat(secondary).isNotNull();
        assertEquals(null, secondary.getPersistenceUnit());
        assertEquals("", secondary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JMS, secondary.getAuditMode());
        assertEquals(PersistenceMode.JPA, secondary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, secondary.getRuntimeStrategy());
        assertEquals(1, secondary.getMarshallingStrategies().size());
        assertEquals(0, secondary.getConfiguration().size());
        assertEquals(0, secondary.getEnvironmentEntries().size());
        assertEquals(0, secondary.getEventListeners().size());
        assertEquals(0, secondary.getGlobals().size());
        assertEquals(0, secondary.getTaskEventListeners().size());
        assertEquals(0, secondary.getWorkItemHandlers().size());
        assertFalse(secondary.getLimitSerializationClasses());

        // and now let's merge them
        DeploymentDescriptor outcome = DeploymentDescriptorMerger.merge(primary, secondary,
                MergeMode.MERGE_COLLECTIONS);

        assertThat(outcome).isNotNull();
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
        DeploymentDescriptor primary = new DeploymentDescriptorImpl("org.jbpm.domain");

        primary.getBuilder()
                .addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}));

        assertThat(primary).isNotNull();
        assertEquals("org.jbpm.domain", primary.getPersistenceUnit());
        assertEquals("org.jbpm.domain", primary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JPA, primary.getAuditMode());
        assertEquals(PersistenceMode.JPA, primary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, primary.getRuntimeStrategy());
        assertEquals(1, primary.getMarshallingStrategies().size());
        assertEquals(0, primary.getConfiguration().size());
        assertEquals(0, primary.getEnvironmentEntries().size());
        assertEquals(0, primary.getEventListeners().size());
        assertEquals(0, primary.getGlobals().size());
        assertEquals(0, primary.getTaskEventListeners().size());
        assertEquals(0, primary.getWorkItemHandlers().size());

        DeploymentDescriptor secondary = new DeploymentDescriptorImpl("org.jbpm.domain");

        secondary.getBuilder()
                .auditMode(AuditMode.NONE)
                .persistenceMode(PersistenceMode.JPA)
                .persistenceUnit("my.custom.unit")
                .auditPersistenceUnit("my.custom.unit2");

        assertThat(secondary).isNotNull();
        assertEquals("my.custom.unit", secondary.getPersistenceUnit());
        assertEquals("my.custom.unit2", secondary.getAuditPersistenceUnit());
        assertEquals(AuditMode.NONE, secondary.getAuditMode());
        assertEquals(PersistenceMode.JPA, secondary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, secondary.getRuntimeStrategy());
        assertEquals(0, secondary.getMarshallingStrategies().size());
        assertEquals(0, secondary.getConfiguration().size());
        assertEquals(0, secondary.getEnvironmentEntries().size());
        assertEquals(0, secondary.getEventListeners().size());
        assertEquals(0, secondary.getGlobals().size());
        assertEquals(0, secondary.getTaskEventListeners().size());
        assertEquals(0, secondary.getWorkItemHandlers().size());

        DeploymentDescriptor third = new DeploymentDescriptorImpl("org.jbpm.domain");

        third.getBuilder()
                .auditMode(AuditMode.JMS)
                .persistenceMode(PersistenceMode.JPA)
                .persistenceUnit("my.custom.unit2")
                .auditPersistenceUnit("my.custom.altered")
                .runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE)
                .addEnvironmentEntry(new NamedObjectModel("IS_JTA", "java.lang.Boolean", new Object[]{"false"}));

        assertThat(third).isNotNull();
        assertEquals("my.custom.unit2", third.getPersistenceUnit());
        assertEquals("my.custom.altered", third.getAuditPersistenceUnit());
        assertEquals(AuditMode.JMS, third.getAuditMode());
        assertEquals(PersistenceMode.JPA, third.getPersistenceMode());
        assertEquals(RuntimeStrategy.PER_PROCESS_INSTANCE, third.getRuntimeStrategy());
        assertEquals(0, third.getMarshallingStrategies().size());
        assertEquals(0, third.getConfiguration().size());
        assertEquals(1, third.getEnvironmentEntries().size());
        assertEquals(0, third.getEventListeners().size());
        assertEquals(0, third.getGlobals().size());
        assertEquals(0, third.getTaskEventListeners().size());
        assertEquals(0, third.getWorkItemHandlers().size());

        // assemble hierarchy
        List<DeploymentDescriptor> hierarchy = new ArrayList<DeploymentDescriptor>();
        hierarchy.add(third);
        hierarchy.add(secondary);
        hierarchy.add(primary);

        // and now let's merge them
        DeploymentDescriptor outcome = DeploymentDescriptorMerger.merge(hierarchy, MergeMode.MERGE_COLLECTIONS);

        assertThat(outcome).isNotNull();
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
        DeploymentDescriptor primary = new DeploymentDescriptorImpl("org.jbpm.domain");

        primary.getBuilder()
                .addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}));

        assertThat(primary).isNotNull();
        assertEquals("org.jbpm.domain", primary.getPersistenceUnit());
        assertEquals("org.jbpm.domain", primary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JPA, primary.getAuditMode());
        assertEquals(PersistenceMode.JPA, primary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, primary.getRuntimeStrategy());
        assertEquals(1, primary.getMarshallingStrategies().size());
        assertEquals(0, primary.getConfiguration().size());
        assertEquals(0, primary.getEnvironmentEntries().size());
        assertEquals(0, primary.getEventListeners().size());
        assertEquals(0, primary.getGlobals().size());
        assertEquals(0, primary.getTaskEventListeners().size());
        assertEquals(0, primary.getWorkItemHandlers().size());

        DeploymentDescriptor secondary = new DeploymentDescriptorImpl("org.jbpm.domain");

        secondary.getBuilder()
                .auditMode(AuditMode.JMS)
                .persistenceMode(PersistenceMode.JPA)
                .persistenceUnit(null)
                .auditPersistenceUnit("")
                .addMarshalingStrategy(new ObjectModel("org.jbpm.test.CustomStrategy", new Object[]{"param2"}));

        assertThat(secondary).isNotNull();
        assertEquals(null, secondary.getPersistenceUnit());
        assertEquals("", secondary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JMS, secondary.getAuditMode());
        assertEquals(PersistenceMode.JPA, secondary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, secondary.getRuntimeStrategy());
        assertEquals(1, secondary.getMarshallingStrategies().size());
        assertEquals(0, secondary.getConfiguration().size());
        assertEquals(0, secondary.getEnvironmentEntries().size());
        assertEquals(0, secondary.getEventListeners().size());
        assertEquals(0, secondary.getGlobals().size());
        assertEquals(0, secondary.getTaskEventListeners().size());
        assertEquals(0, secondary.getWorkItemHandlers().size());

        // and now let's merge them
        DeploymentDescriptor outcome = DeploymentDescriptorMerger.merge(primary, secondary,
                MergeMode.MERGE_COLLECTIONS);

        assertThat(outcome).isNotNull();
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
        DeploymentDescriptor primary = new DeploymentDescriptorImpl("org.jbpm.domain");

        primary.getBuilder()
                .addWorkItemHandler(new NamedObjectModel("mvel", "Log",
                        "new org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler()"));

        assertThat(primary).isNotNull();
        assertEquals("org.jbpm.domain", primary.getPersistenceUnit());
        assertEquals("org.jbpm.domain", primary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JPA, primary.getAuditMode());
        assertEquals(PersistenceMode.JPA, primary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, primary.getRuntimeStrategy());
        assertEquals(0, primary.getMarshallingStrategies().size());
        assertEquals(0, primary.getConfiguration().size());
        assertEquals(0, primary.getEnvironmentEntries().size());
        assertEquals(0, primary.getEventListeners().size());
        assertEquals(0, primary.getGlobals().size());
        assertEquals(0, primary.getTaskEventListeners().size());
        assertEquals(1, primary.getWorkItemHandlers().size());

        DeploymentDescriptor secondary = new DeploymentDescriptorImpl("org.jbpm.domain");

        secondary.getBuilder()
                .auditMode(AuditMode.JMS)
                .persistenceMode(PersistenceMode.JPA)
                .persistenceUnit(null)
                .auditPersistenceUnit("")
                .addWorkItemHandler(new NamedObjectModel("mvel", "Log",
                        "new org.jbpm.process.instance.impl.demo.CustomSystemOutWorkItemHandler()"));

        assertThat(secondary).isNotNull();
        assertEquals(null, secondary.getPersistenceUnit());
        assertEquals("", secondary.getAuditPersistenceUnit());
        assertEquals(AuditMode.JMS, secondary.getAuditMode());
        assertEquals(PersistenceMode.JPA, secondary.getPersistenceMode());
        assertEquals(RuntimeStrategy.SINGLETON, secondary.getRuntimeStrategy());
        assertEquals(0, secondary.getMarshallingStrategies().size());
        assertEquals(0, secondary.getConfiguration().size());
        assertEquals(0, secondary.getEnvironmentEntries().size());
        assertEquals(0, secondary.getEventListeners().size());
        assertEquals(0, secondary.getGlobals().size());
        assertEquals(0, secondary.getTaskEventListeners().size());
        assertEquals(1, secondary.getWorkItemHandlers().size());

        // and now let's merge them
        DeploymentDescriptor outcome = DeploymentDescriptorMerger.merge(primary, secondary,
                MergeMode.MERGE_COLLECTIONS);

        assertThat(outcome).isNotNull();
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

        // let's check if the secondary version is preserved
        NamedObjectModel model = outcome.getWorkItemHandlers().get(0);
        assertEquals("Log", model.getName());
        assertEquals("new org.jbpm.process.instance.impl.demo.CustomSystemOutWorkItemHandler()", model.getIdentifier());
    }
}
