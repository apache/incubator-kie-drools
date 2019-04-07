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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.kie.internal.task.api.InternalTaskService;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class KModuleWithDependenciesDeploymentServiceTest extends AbstractKieServicesBaseTest {
   
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    private static final String ARTIFACT_ID = "jbpm-module";
    private static final String GROUP_ID = "org.jbpm.test";
    private static final String VERSION = "1.0.0";
    
    private static final String DATA_ARTIFACT_ID = "custom-data";
    private static final String DATA_GROUP_ID = "org.jbpm.test.data";
    private static final String DATA_VERSION = "1.0.0";
    
    @Before
    public void prepare() {
    	configureServices();
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        File kjar = new File("src/test/resources/kjar-with-deps/jbpm-module.jar");
        File pom = new File("src/test/resources/kjar-with-deps/pom.xml");
        KieMavenRepository repository = getKieMavenRepository();
        repository.installArtifact(releaseId, kjar, pom);
        
        ReleaseId dataReleaseId = ks.newReleaseId(DATA_GROUP_ID, DATA_ARTIFACT_ID, DATA_VERSION);
        File datajar = new File("src/test/resources/kjar-with-deps/custom-data.jar");
        File datapom = new File("src/test/resources/kjar-with-deps/data-pom.xml");
        
        repository.installArtifact(dataReleaseId, datajar, datapom);
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
    public void testDeploymentOfProcesses() throws Exception {
        
        assertNotNull(deploymentService);
        
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION, "defaultKieBase", "defaultKieSession");

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        DeployedUnit deployed = deploymentService.getDeployedUnit(deploymentUnit.getIdentifier());
        assertNotNull(deployed);
        assertNotNull(deployed.getDeploymentUnit());
        assertNotNull(deployed.getRuntimeManager());
        assertEquals("org.jbpm.test:jbpm-module:1.0.0:defaultKieBase:defaultKieSession", 
                deployed.getDeploymentUnit().getIdentifier());
        assertTrue(deployed instanceof DeployedUnitImpl);
        assertEquals(2, ((DeployedUnitImpl) deployed).getDeployedClasses().size());
        List<String> classnames = new ArrayList<String>();
        for (Class<?> clazz : ((DeployedUnitImpl) deployed).getDeployedClasses()) {
        	classnames.add(clazz.getName());
        }
        assertTrue(classnames.contains("org.jbpm.data.rest.CustomDataObject"));
        assertTrue(classnames.contains("org.jbpm.test.Person"));

        assertNotNull(runtimeDataService);
        Collection<ProcessDefinition> processes = runtimeDataService.getProcesses(new QueryContext());
        assertNotNull(processes);
        assertEquals(1, processes.size());
        
        RuntimeManager manager = deploymentService.getRuntimeManager(deploymentUnit.getIdentifier());
        assertNotNull(manager);
        
        RuntimeEngine engine = manager.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        
        Class<?> clazz = Class.forName("org.jbpm.test.Person", true, ((InternalRuntimeManager)manager).getEnvironment().getClassLoader());
        Object instance = clazz.newInstance();
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("person", instance);
        ProcessInstance processInstance = engine.getKieSession().startProcess("testkjar.src.main.resources.process", params);
        
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        List<TaskSummary> tasks = engine.getTaskService().getTasksOwned("salaboy", "en-UK");
        assertEquals(1, tasks.size());
        
        long taskId = tasks.get(0).getId();
        
        Map<String, Object> content = ((InternalTaskService)engine.getTaskService()).getTaskContent(taskId);
        assertTrue(content.containsKey("personIn"));
        Object person = content.get("personIn");
        assertEquals(clazz.getName(), person.getClass().getName());
        
        engine.getTaskService().start(taskId, "salaboy");
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("personOut", instance);
        engine.getTaskService().complete(taskId, "salaboy", data);
        
        processInstance = engine.getKieSession().getProcessInstance(processInstance.getId());
        assertNull(processInstance);
    }
}
