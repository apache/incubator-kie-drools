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
import org.jbpm.kie.services.impl.DeployedUnitImpl;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
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
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.InternalRuntimeManager;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class DeploymentServiceTest extends AbstractKieServicesBaseTest {

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    @Before
    public void prepare() {
    	configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/signal.bpmn");
        processes.add("repo/processes/general/import.bpmn");
        processes.add("repo/processes/general/callactivity.bpmn");

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

        ReleaseId releaseIdSupport = ks.newReleaseId(GROUP_ID, "support", VERSION);
        List<String> processesSupport = new ArrayList<String>();
        processesSupport.add("repo/processes/support/support.bpmn");

        InternalKieModule kJar2 = createKieJar(ks, releaseIdSupport, processesSupport);
        File pom2 = new File("target/kmodule2", "pom.xml");
        pom2.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom2);
            fs.write(getPom(releaseIdSupport).getBytes());
            fs.close();
        } catch (Exception e) {

        }

        repository.deployArtifact(releaseIdSupport, kJar2, pom2);

        ReleaseId releaseId3 = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, "1.1.0");
        processes.add("repo/rules/RuleWIthDeclaredType.drl");

        InternalKieModule kJar3 = createKieJar(ks, releaseId3, processes);
        File pom3 = new File("target/kmodule3", "pom.xml");
        pom3.getParentFile().mkdirs();
        try {
            FileOutputStream fs = new FileOutputStream(pom3);
            fs.write(getPom(releaseId3).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        repository = getKieMavenRepository();
        repository.deployArtifact(releaseId3, kJar3, pom3);
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

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());

        assertEquals(0, ((DeployedUnitImpl) deployed).getDeployedClasses().size());

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(5, processes.size());

        processes = runtimeDataService.getProcessesByFilter("custom", new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());

        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new QueryContext());
        assertNotNull(processes);
        assertEquals(5, processes.size());

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

    }


    @Test
    public void testDeploymentOfAllProcesses() {

        assertNotNull(deploymentService);
        // deploy first unit
        DeploymentUnit deploymentUnitGeneral = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnitGeneral);
        units.add(deploymentUnitGeneral);

        RuntimeManager managerGeneral = deploymentService.getRuntimeManager(deploymentUnitGeneral.getIdentifier());
        assertNotNull(managerGeneral);

        // deploy second unit
        DeploymentUnit deploymentUnitSupport = new KModuleDeploymentUnit(GROUP_ID, "support", VERSION);
        deploymentService.deploy(deploymentUnitSupport);
        units.add(deploymentUnitSupport);

        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnitGeneral.getIdentifier());
        assertNotNull(deployedGeneral);
        assertNotNull(deployedGeneral.getDeploymentUnit());
        assertNotNull(deployedGeneral.getRuntimeManager());

        RuntimeManager managerSupport = deploymentService.getRuntimeManager(deploymentUnitSupport.getIdentifier());
        assertNotNull(managerSupport);

        DeployedUnit deployedSupport = deploymentService.getDeployedUnit(deploymentUnitSupport.getIdentifier());
        assertNotNull(deployedSupport);
        assertNotNull(deployedSupport.getDeploymentUnit());
        assertNotNull(deployedSupport.getRuntimeManager());

        // execute process that is bundled in first deployment unit
        RuntimeEngine engine = managerGeneral.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        ProcessInstance processInstance = engine.getKieSession().startProcess("customtask", params);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());

        // execute process that is in second deployment unit
        RuntimeEngine engineSupport = managerSupport.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engineSupport);

        ProcessInstance supportPI = engineSupport.getKieSession().startProcess("support.process");
        assertEquals(ProcessInstance.STATE_ACTIVE, supportPI.getState());

        List<TaskSummary> tasks = engineSupport.getTaskService().getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        engineSupport.getKieSession().abortProcessInstance(supportPI.getId());
        assertNull(engineSupport.getKieSession().getProcessInstance(supportPI.getState()));
    }

    @Test(expected=RuntimeException.class)
    public void testDuplicatedDeployment() {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        assertFalse(deploymentService.isDeployed(deploymentUnit.getIdentifier()));
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        assertTrue(deploymentService.isDeployed(deploymentUnit.getIdentifier()));
        DeployedUnit deployedGeneral = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployedGeneral);
        assertNotNull(deployedGeneral.getDeploymentUnit());
        assertNotNull(deployedGeneral.getRuntimeManager());
        // duplicated deployment of the same deployment unit should fail
        deploymentService.deploy(deploymentUnit);
    }

    @Test
    public void testDeploymentOfMultipleVersions() {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        DeploymentUnit deploymentUnit3 = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, "1.1.0");

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        deploymentService.deploy(deploymentUnit3);
        units.add(deploymentUnit3);

        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());

        assertEquals(0, ((DeployedUnitImpl) deployed).getDeployedClasses().size());

        DeployedUnit deployed3 = deploymentService.getDeployedUnit(deploymentUnit3.getIdentifier());
        assertNotNull(deployed3);
        assertNotNull(deployed3.getDeploymentUnit());
        assertNotNull(deployed3.getRuntimeManager());

        assertEquals(1, ((DeployedUnitImpl) deployed3).getDeployedClasses().size());

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(10, processes.size());

        DeployedUnit deployedLatest = deploymentService.getDeployedUnit(GROUP_ID+":"+ARTIFACT_ID+":LATEST");
        assertNotNull(deployedLatest);
        assertNotNull(deployedLatest.getDeploymentUnit());
        assertNotNull(deployedLatest.getRuntimeManager());

        assertEquals(deploymentUnit3.getIdentifier(), deployedLatest.getDeploymentUnit().getIdentifier());
    }

    @Test
    public void testDeploymentOfProcessesWithActivation() {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertTrue(deployed.isActive());

        assertEquals(0, ((DeployedUnitImpl) deployed).getDeployedClasses().size());

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(5, processes.size());

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);

        // then deactivate it
        deploymentService.deactivate(deploymentUnit.getIdentifier());

        deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertFalse(deployed.isActive());

        processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(0, processes.size());

        // and not activate it again
        deploymentService.activate(deploymentUnit.getIdentifier());

        deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertTrue(deployed.isActive());

        processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(5, processes.size());
    }

    @Test
    public void testDeploymentWithDeclaredTypeInDRL() {

        assertNotNull(deploymentService);

        DeploymentUnit deploymentUnit3 = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, "1.1.0");

        deploymentService.deploy(deploymentUnit3);
        units.add(deploymentUnit3);

        DeployedUnit deployed3 = deploymentService.getDeployedUnit(deploymentUnit3.getIdentifier());
        assertNotNull(deployed3);
        assertNotNull(deployed3.getDeploymentUnit());
        assertNotNull(deployed3.getRuntimeManager());

        assertEquals(1, ((DeployedUnitImpl) deployed3).getDeployedClasses().size());
        try {
			assertNotNull(Class.forName("org.pkg1.Message", true, ((InternalRuntimeManager)deployed3.getRuntimeManager()).getEnvironment().getClassLoader()));
		} catch (ClassNotFoundException e) {
			fail("Class org.pkg1.Message should be found in deployment");
		}

    }
}
