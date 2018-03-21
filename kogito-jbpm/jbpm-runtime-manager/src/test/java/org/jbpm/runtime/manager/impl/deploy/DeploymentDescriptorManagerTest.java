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

import java.io.ByteArrayInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;

import org.assertj.core.api.Assertions;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.MergeMode;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
        installKjar(releaseId, kJar1);

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
        installKjar(releaseId, kJar1);

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
        installKjar(releaseIdDep, kJarDep);

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
        installKjar(releaseId, kJar1);

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
        installKjar(releaseIdDep, kJarDep);

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
        installKjar(releaseId, kJar1);

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

    @Test
    public void roundTripDescriptorMarshallingTest() throws Exception {
        DeploymentDescriptorImpl depDescImpl = new DeploymentDescriptorImpl();

        List<Field> fieldsToFill = new LinkedList<Field>();
        for (Field field : DeploymentDescriptorImpl.class.getDeclaredFields()) {
            if (field.getAnnotation(XmlElement.class) != null) {
                fieldsToFill.add(field);
            }
        }
        for (Field field : fieldsToFill) {
            field.setAccessible(true);
            Class fieldType = field.getType();
            if (fieldType.equals(String.class)) {
                field.set(depDescImpl, getStringVal());
            } else if (fieldType.equals(Boolean.class)) {
                field.set(depDescImpl, true);
            } else if (fieldType.equals(PersistenceMode.class)) {
                field.set(depDescImpl, PersistenceMode.NONE);
            } else if (fieldType.equals(AuditMode.class)) {
                field.set(depDescImpl, AuditMode.JMS);
            } else if (fieldType.equals(RuntimeStrategy.class)) {
                field.set(depDescImpl, RuntimeStrategy.PER_PROCESS_INSTANCE);
            } else if (Set.class.isAssignableFrom(fieldType)) {
                Type genType = field.getGenericType();
                Type genParamType = ((ParameterizedType) genType).getActualTypeArguments()[0];
                Set val = new HashSet();
                if (genParamType.equals(String.class)) {
                    val.add(getStringVal());
                } else if (genParamType.equals(ObjectModel.class)) {
                    val.add(getObjectModelParameter(getStringVal(), false));
                } else if (genParamType.equals(NamedObjectModel.class)) {
                    val.add(getObjectModelParameter(getStringVal(), true));
                }
                field.set(depDescImpl, val);
            }
        }

        String depDescXml = DeploymentDescriptorIO.toXml(depDescImpl);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(depDescXml.getBytes());

        DeploymentDescriptor copyDepDescImpl = DeploymentDescriptorIO.fromXml(inputStream);

        Assertions.assertThat(depDescImpl).isEqualToComparingFieldByFieldRecursively(copyDepDescImpl);
    }

    private static String getStringVal() {
        String val = UUID.randomUUID().toString();
        return val.substring(0, val.indexOf("-"));
    }

    private static Random random = new Random();

    private static ObjectModel getObjectModelParameter(String resolver, boolean named) {
        if (named) {
            return new NamedObjectModel(resolver, UUID.randomUUID().toString(), Integer.toString(random.nextInt(100000)));
        } else {
            return new ObjectModel(resolver, UUID.randomUUID().toString(), Integer.toString(random.nextInt(100000)));
        }
    }
}
