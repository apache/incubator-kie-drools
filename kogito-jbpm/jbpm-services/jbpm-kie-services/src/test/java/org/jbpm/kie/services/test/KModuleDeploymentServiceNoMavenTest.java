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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.kie.services.impl.CustomIdKModuleDeploymentUnit;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.services.api.model.DeployedUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessDefinition;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.runtime.manager.context.EmptyContext;


public class KModuleDeploymentServiceNoMavenTest extends AbstractKieServicesBaseTest {

    private String artifactId = "not-existing";
    private String groupId = "org.jbpm.test";
    private String version = "9.9.9";
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();


    @Before
    public void prepare() {
    	configureServices(); 
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

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(groupId, artifactId, version, "KBase-test", "ksession-test");

        KieContainer kieContainer = KieServices.Factory.get().newKieClasspathContainer();
        deploymentUnit.setKieContainer(kieContainer);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        assertNotNull(deploymentUnit.getDeploymentDescriptor());

        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());        
        assertEquals(groupId+":"+artifactId+":"+version+":"+"KBase-test"+":"+"ksession-test",
                deployed.getDeploymentUnit().getIdentifier());

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());

        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);

        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        ProcessInstance processInstance = engine.getKieSession().startProcess("Import", params);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }

    @Test
    public void testDeploymentOfProcessesCustomId() {

        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new CustomIdKModuleDeploymentUnit("test", groupId, artifactId, version, "KBase-test", "ksession-test");

        KieContainer kieContainer = KieServices.Factory.get().newKieClasspathContainer();
        deploymentUnit.setKieContainer(kieContainer);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        assertNotNull(deploymentUnit.getDeploymentDescriptor());

        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());        
        assertEquals("test",
                deployed.getDeploymentUnit().getIdentifier());

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());

        processes = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());

        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);

        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");
        ProcessInstance processInstance = engine.getKieSession().startProcess("Import", params);

        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
}
