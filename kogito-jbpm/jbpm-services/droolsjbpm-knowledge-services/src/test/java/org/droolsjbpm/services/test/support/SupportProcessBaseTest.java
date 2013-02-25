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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;

import org.droolsjbpm.services.api.Domain;
import org.droolsjbpm.services.api.KnowledgeAdminDataService;
import org.droolsjbpm.services.api.KnowledgeDataService;
import org.droolsjbpm.services.api.SessionManager;
import org.droolsjbpm.services.api.bpmn2.BPMN2DataService;
import org.droolsjbpm.services.impl.KnowledgeDomainServiceImpl;
import org.droolsjbpm.services.impl.SimpleDomainImpl;
import org.jbpm.shared.services.api.FileException;
import org.jbpm.shared.services.api.FileService;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.query.TaskSummary;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.commons.java.nio.file.Path;
import org.kie.runtime.process.ProcessInstance;

public abstract class SupportProcessBaseTest {

    @Inject
    protected TaskServiceEntryPoint taskService;
    @Inject
    private BPMN2DataService bpmn2Service;
    @Inject
    protected KnowledgeDataService dataService;
    @Inject
    protected KnowledgeAdminDataService adminDataService;
    @Inject
    private FileService fs;
    @Inject
    private SessionManager sessionManager;

 

    @Test
    public void testSupportProcess() throws FileException {
        Domain myDomain = new SimpleDomainImpl("myDomain");
        sessionManager.setDomain(myDomain);



        sessionManager.buildSession("myKsession", "processes/support/", false);



        sessionManager.registerHandlersForSession("myKsession", 1);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("customer", "polymita");


        ProcessInstance pI = sessionManager.getKsessionsByName("myKsession").get(1).startProcess("support.process", params);

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
