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

package org.jbpm.kie.services.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.NamedObjectModel;
import org.kie.internal.runtime.conf.PersistenceMode;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;


public class KModuleDeploymentServiceTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();


    @Before
    public void prepare() {
    	configureServices();
    	logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
        close();
    }

    @Test
    public void testDeploymentOfProcesses() {

        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION, "KBase-test", "ksession-test");

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        assertNotNull(deploymentUnit.getDeploymentDescriptor());

        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertNull(deployed.getDeployedAssetLocation("customtask"));
        assertEquals(GROUP_ID+":"+ARTIFACT_ID+":"+VERSION+":"+"KBase-test"+":"+"ksession-test",
                deployed.getDeploymentUnit().getIdentifier());

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(3, processes.size());

        processes = runtimeDataService.getProcessesByFilter("custom", new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());

        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new QueryContext());
        assertNotNull(processes);
        assertEquals(3, processes.size());

        ProcessDefinition process = runtimeDataService.getProcessesByDeploymentIdProcessId(deploymentUnit.getIdentifier(), "customtask");
        assertNotNull(process);

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);

        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

        checkFormsDeployment( deploymentUnit.getIdentifier() );

    }

    @Test
    public void testDeploymentOfProcessesOnDefaultKbaseAndKsession() {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertNull(deployed.getDeployedAssetLocation("customtask"));
        assertEquals(GROUP_ID+":"+ARTIFACT_ID+":"+VERSION,
                deployed.getDeploymentUnit().getIdentifier());

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(3, processes.size());

        processes = runtimeDataService.getProcessesByFilter("custom", new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());

        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new QueryContext());
        assertNotNull(processes);
        assertEquals(3, processes.size());

        ProcessDefinition process = runtimeDataService.getProcessesByDeploymentIdProcessId(deploymentUnit.getIdentifier(), "customtask");
        assertNotNull(process);

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);

        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

        checkFormsDeployment( deploymentUnit.getIdentifier() );

    }

    @Test(expected=RuntimeException.class)
    public void testDuplicatedDeployment() {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployedGeneral);
        assertNotNull(deployedGeneral.getDeploymentUnit());
        assertNotNull(deployedGeneral.getRuntimeManager());
        // duplicated deployment of the same deployment unit should fail
        deploymentService.deploy(deploymentUnit);
    }

    @Test
    public void testUnDeploymentWithActiveProcesses() {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployedGeneral);
        assertNotNull(deployedGeneral.getDeploymentUnit());
        assertNotNull(deployedGeneral.getRuntimeManager());

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);

        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);

        Map<String, Object> params = new HashMap<String, Object>();

        ProcessInstance processInstance = engine.getKieSession().startProcess("org.jbpm.writedocument", params);

        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        try {
            // undeploy should fail due to active process instances
            deploymentService.undeploy(deploymentUnit);
            fail("Should fail due to active process instance");
        } catch (IllegalStateException e) {

        }

        engine.getKieSession().abortProcessInstance(processInstance.getId());

        checkFormsDeployment( deploymentUnit.getIdentifier() );
    }

    @Test
    public void testDeploymentAndExecutionOfProcessWithImports() {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployedGeneral);
        assertNotNull(deployedGeneral.getDeploymentUnit());
        assertNotNull(deployedGeneral.getRuntimeManager());

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);

        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);

        Map<String, Object> params = new HashMap<String, Object>();

        ProcessInstance processInstance = engine.getKieSession().startProcess("Import", params);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

        checkFormsDeployment( deploymentUnit.getIdentifier() );

    }

    @Test
    public void testDeploymentOfProcessWithDescriptor() {

        assertNotNull(deploymentService);

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, "kjar-with-dd", VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");

        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		customDescriptor.getBuilder()
		.runtimeStrategy(RuntimeStrategy.PER_REQUEST)
		.addWorkItemHandler(new NamedObjectModel("Log", "org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler"));

        Map<String, String> resources = new HashMap<String, String>();
		resources.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, resources);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, "kjar-with-dd", VERSION, "KBase-test", "ksession-test2");
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployedGeneral);
        assertNotNull(deployedGeneral.getDeploymentUnit());
        assertNotNull(deployedGeneral.getRuntimeManager());

        DeploymentDescriptor descriptor = ((InternalRuntimeManager)deployedGeneral.getRuntimeManager()).getDeploymentDescriptor();
        assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.PER_REQUEST, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(1, descriptor.getWorkItemHandlers().size());
		assertEquals(0, descriptor.getRequiredRoles().size());

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);

        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);

        Map<String, Object> params = new HashMap<String, Object>();

        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

        manager.disposeRuntimeEngine(engine);

        checkFormsDeployment( deploymentUnit.getIdentifier() );
    }

    @Test(expected=SecurityException.class)
    public void testDeploymentOfProcessWithDescriptorWitSecurityManager() {

        assertNotNull(deploymentService);

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, "kjar-with-dd", VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");

        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		customDescriptor.getBuilder()
		.runtimeStrategy(RuntimeStrategy.PER_PROCESS_INSTANCE)
		.addWorkItemHandler(new NamedObjectModel("Log", "org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler"))
		.addRequiredRole("experts");

        Map<String, String> resources = new HashMap<String, String>();
		resources.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, resources);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, "kjar-with-dd", VERSION, "KBase-test", "ksession-test2");
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployedGeneral);
        assertNotNull(deployedGeneral.getDeploymentUnit());
        assertNotNull(deployedGeneral.getRuntimeManager());

        DeploymentDescriptor descriptor = ((InternalRuntimeManager)deployedGeneral.getRuntimeManager()).getDeploymentDescriptor();
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
		assertEquals(1, descriptor.getWorkItemHandlers().size());
		assertEquals(1, descriptor.getRequiredRoles().size());

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);

        manager.getRuntimeEngine(EmptyContext.get());

        checkFormsDeployment( deploymentUnit.getIdentifier() );
    }


    @Test
    public void testDeploymentOfProcessWithDescriptorKieConteinerInjection() {

        assertNotNull(deploymentService);

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, "kjar-with-dd", VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");

        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		customDescriptor.getBuilder()
		.runtimeStrategy(RuntimeStrategy.PER_REQUEST)
		.addWorkItemHandler(new NamedObjectModel("mvel", "Log", "new org.jbpm.kie.services.test.objects.KieConteinerSystemOutWorkItemHandler(kieContainer)"));

        Map<String, String> resources = new HashMap<String, String>();
		resources.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, resources);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, "kjar-with-dd", VERSION, "KBase-test", "ksession-test2");
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployedGeneral);
        assertNotNull(deployedGeneral.getDeploymentUnit());
        assertNotNull(deployedGeneral.getRuntimeManager());

        DeploymentDescriptor descriptor = ((InternalRuntimeManager)deployedGeneral.getRuntimeManager()).getDeploymentDescriptor();
        assertNotNull(descriptor);
		assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
		assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
		assertEquals(AuditMode.JPA, descriptor.getAuditMode());
		assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
		assertEquals(RuntimeStrategy.PER_REQUEST, descriptor.getRuntimeStrategy());
		assertEquals(0, descriptor.getMarshallingStrategies().size());
		assertEquals(0, descriptor.getConfiguration().size());
		assertEquals(0, descriptor.getEnvironmentEntries().size());
		assertEquals(0, descriptor.getEventListeners().size());
		assertEquals(0, descriptor.getGlobals().size());
		assertEquals(0, descriptor.getTaskEventListeners().size());
		assertEquals(1, descriptor.getWorkItemHandlers().size());
		assertEquals(0, descriptor.getRequiredRoles().size());

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);

        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);

        Map<String, Object> params = new HashMap<String, Object>();

        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        manager.disposeRuntimeEngine(engine);

        checkFormsDeployment( deploymentUnit.getIdentifier() );
    }

    @Test
    public void testDeploymentOfProcessesKieConteinerInjection() {

        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION, "KBase-test", "ksession-test-2");

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        assertNotNull(deploymentUnit.getDeploymentDescriptor());

        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertNull(deployed.getDeployedAssetLocation("customtask"));
        assertEquals(GROUP_ID+":"+ARTIFACT_ID+":"+VERSION+":"+"KBase-test"+":"+"ksession-test-2",
                deployed.getDeploymentUnit().getIdentifier());


        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);

        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        manager.disposeRuntimeEngine(engine);

        checkFormsDeployment( deploymentUnit.getIdentifier() );
    }

    @Test
    public void testDeploymentAvoidEmptyDescriptorOverride() {

        assertNotNull(deploymentService);

        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, "kjar-with-dd", VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");

        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        customDescriptor.getBuilder()
        .runtimeStrategy(RuntimeStrategy.PER_REQUEST)
        .addWorkItemHandler(new NamedObjectModel("Log", "org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler"));

        Map<String, String> resources = new HashMap<String, String>();
        resources.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, resources);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, "kjar-with-dd", VERSION, "KBase-test", "ksession-test2");

        // let's simulate change of deployment descriptor on deploy time
        deploymentUnit.setDeploymentDescriptor(new DeploymentDescriptorImpl()); // set empty one...

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployedGeneral);
        assertNotNull(deployedGeneral.getDeploymentUnit());
        assertNotNull(deployedGeneral.getRuntimeManager());

        DeploymentDescriptor descriptor = ((InternalRuntimeManager)deployedGeneral.getRuntimeManager()).getDeploymentDescriptor();
        assertNotNull(descriptor);
        assertEquals("org.jbpm.domain", descriptor.getPersistenceUnit());
        assertEquals("org.jbpm.domain", descriptor.getAuditPersistenceUnit());
        assertEquals(AuditMode.JPA, descriptor.getAuditMode());
        assertEquals(PersistenceMode.JPA, descriptor.getPersistenceMode());
        assertEquals(RuntimeStrategy.PER_REQUEST, descriptor.getRuntimeStrategy());
        assertEquals(0, descriptor.getMarshallingStrategies().size());
        assertEquals(0, descriptor.getConfiguration().size());
        assertEquals(0, descriptor.getEnvironmentEntries().size());
        assertEquals(0, descriptor.getEventListeners().size());
        assertEquals(0, descriptor.getGlobals().size());
        assertEquals(0, descriptor.getTaskEventListeners().size());
        assertEquals(1, descriptor.getWorkItemHandlers().size());
        assertEquals(0, descriptor.getRequiredRoles().size());

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);

        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);

        Map<String, Object> params = new HashMap<String, Object>();

        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

        manager.disposeRuntimeEngine(engine);

        checkFormsDeployment( deploymentUnit.getIdentifier() );
    }

    protected void checkFormsDeployment( String deploymentId ) {
        Map<String, String> deployedForms = formManagerService.getAllFormsByDeployment( deploymentId );

        assertNotNull( deployedForms );
        assertEquals( 3, deployedForms.size() );

        assertNotNull( deployedForms.get( "DefaultProcess.frm" ) );
        assertNotNull( deployedForms.get( "DefaultProcess.form" ) );
        assertNotNull( deployedForms.get( "DefaultProcess.ftl" ) );
    }

}
