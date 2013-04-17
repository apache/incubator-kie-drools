/**
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
package org.droolsjbpm.services.test.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.droolsjbpm.services.api.DeploymentService;
import org.droolsjbpm.services.api.DeploymentUnit;
import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.VFSDeploymentUnit;
import org.jbpm.shared.services.api.FileException;
import org.junit.After;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.manager.RuntimeEngine;
import org.kie.internal.runtime.manager.RuntimeManager;
import org.kie.internal.runtime.manager.context.EmptyContext;
import org.kie.internal.task.api.TaskService;
import org.kie.internal.task.api.model.TaskSummary;


public abstract class SupportProcessBaseTest {

    @Inject
    protected DeploymentService deploymentService;
    @Inject
    protected BPMN2DataService bpmn2Service;
    @Inject
    protected KnowledgeAdminDataService adminDataService;
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    
    @After
    public void cleanup() {
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
    }

 

    @Test
    public void testSupportProcess() throws FileException {
        DeploymentUnit deploymentUnitSupport = new VFSDeploymentUnit("support", "", "processes/support");        
        deploymentService.deploy(deploymentUnitSupport);
        units.add(deploymentUnitSupport);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("customer", "polymita");

        RuntimeManager managerSupport = deploymentService.getRuntimeManager(deploymentUnitSupport.getIdentifier());
        assertNotNull(managerSupport);
        
        RuntimeEngine engine = managerSupport.getRuntimeEngine(EmptyContext.get());
        assertNotNull(engine);
        ProcessInstance pI = engine.getKieSession().startProcess("support.process", params);

        TaskService taskService = engine.getTaskService();
        
        // Configure Release
        List<TaskSummary> tasksAssignedToSalaboy = taskService.getTasksAssignedAsPotentialOwner("salaboy", "en-UK");

        assertEquals(1, tasksAssignedToSalaboy.size());
        assertEquals("Create Support", tasksAssignedToSalaboy.get(0).getName());


        TaskSummary createSupportTask = tasksAssignedToSalaboy.get(0);

        taskService.start(createSupportTask.getId(), "salaboy");



        Map<String, Object> taskContent = taskService.getTaskContent(createSupportTask.getId());

        assertEquals("polymita", taskContent.get("input_customer"));



        Map<String, String> taskOutputMappings = bpmn2Service.getTaskOutputMappings("support.process", createSupportTask.getName());

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
