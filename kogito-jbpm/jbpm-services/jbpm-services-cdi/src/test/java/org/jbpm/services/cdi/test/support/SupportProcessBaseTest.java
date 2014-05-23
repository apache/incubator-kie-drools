/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.services.cdi.test.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.scanner.MavenRepository.getMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractBaseTest;
import org.jbpm.runtime.manager.util.TestUtil;
import org.jbpm.services.api.DefinitionService;
import org.jbpm.services.api.DeploymentService;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.cdi.Kjar;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.InternalTaskService;
import org.kie.scanner.MavenRepository;


public abstract class SupportProcessBaseTest extends AbstractBaseTest {

    @Inject
    @Kjar
    protected DeploymentService deploymentService;
    @Inject
    protected DefinitionService bpmn2Service;
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    @Before
    public void prepare() {
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/support/support.bpmn");
        
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
            
        }
        MavenRepository repository = getMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
    }
    
    @After
    public void cleanup() {
        TestUtil.cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
    }

 

    @Test
    public void testSupportProcess()  {
    	DeploymentUnit deploymentUnitSupport = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);       
        deploymentService.deploy(deploymentUnitSupport);
        units.add(deploymentUnitSupport);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("customer", "polymita");

        RuntimeManager managerSupport = deploymentService.getRuntimeManager(deploymentUnitSupport.getIdentifier());
        assertNotNull(managerSupport);
        
        RuntimeEngine engine = managerSupport.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        ProcessInstance pI = engine.getKieSession().startProcess("support.process", params);
        assertNotNull(pI);
        TaskService taskService = engine.getTaskService();
        
        // Configure Release
        List<TaskSummary> tasksAssignedToSalaboy = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");

        assertEquals(1, tasksAssignedToSalaboy.size());
        assertEquals("Create Support", tasksAssignedToSalaboy.get(0).getName());


        TaskSummary createSupportTask = tasksAssignedToSalaboy.get(0);

        taskService.start(createSupportTask.getId(), "salaboy");



        Map<String, Object> taskContent = ((InternalTaskService) taskService).getTaskContent(createSupportTask.getId());

        assertEquals("polymita", taskContent.get("input_customer"));



        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings(deploymentUnitSupport.getIdentifier(), 
        		"support.process", createSupportTask.getName());

        assertEquals(1, taskOutputMappings.size());
        assertEquals("output_customer", taskOutputMappings.values().iterator().next());

        Map<String, Object> output = new HashMap<String, Object>();

        output.put("output_customer", "polymita/redhat");
        taskService.complete(createSupportTask.getId(), "salaboy", output);

        tasksAssignedToSalaboy = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertEquals(1, tasksAssignedToSalaboy.size());

        assertEquals("Resolve Support", tasksAssignedToSalaboy.get(0).getName());

        TaskSummary resolveSupportTask = tasksAssignedToSalaboy.get(0);

        taskService.start(resolveSupportTask.getId(), "salaboy");

        taskService.complete(resolveSupportTask.getId(), "salaboy", null);


        tasksAssignedToSalaboy = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");
        assertEquals(1, tasksAssignedToSalaboy.size());

        assertEquals("Notify Customer", tasksAssignedToSalaboy.get(0).getName());

        TaskSummary notifySupportTask = tasksAssignedToSalaboy.get(0);

        taskService.start(notifySupportTask.getId(), "salaboy");
        output = new HashMap<String, Object>();
        output.put("output_solution", "solved today");
        taskService.complete(notifySupportTask.getId(), "salaboy", output);



    }
}
