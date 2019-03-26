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
package org.jbpm.test.functional.workitem;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.drools.core.process.instance.WorkItemManager;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.runtime.manager.audit.AuditService;
import org.kie.api.runtime.manager.audit.VariableInstanceLog;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.runtime.manager.context.EmptyContext;

import static org.junit.Assert.*;

public class WorkitemAssignmentTest extends JbpmTestCase {
    public WorkitemAssignmentTest() {
        super( true, true );
    }

    @Test
    public void testWorkitemAssignment() {
        manager = createRuntimeManager( "org/jbpm/test/functional/workitem/workitemAssignmentTest.bpmn2" );
        RuntimeEngine runtimeEngine = getRuntimeEngine( EmptyContext.get() );
        KieSession kieSession = runtimeEngine.getKieSession();
        WorkItemManager workItemManager = (org.drools.core.process.instance.WorkItemManager) kieSession.getWorkItemManager();

        workItemManager.registerWorkItemHandler( "SampleUserWorkitem", new UserAssignmentWorkitemHandler() );

        Map<String, Object> initEmptyVars = new HashMap<String, Object>();
        initEmptyVars.put("firstName", "initValue");
        initEmptyVars.put("lastName", "initValue");

        ProcessInstance pi = kieSession.startProcess( "workitemassignmenttest", initEmptyVars );

        // values should be from initial state when process instance started -- "initValue", "initValue"
        String varFirstNameOrig = (String) ((WorkflowProcessInstance) pi).getVariable("firstName");
        String varLasttNameOrig = (String) ((WorkflowProcessInstance) pi).getVariable("lastName");
        assertEquals("initValue", varFirstNameOrig);
        assertEquals("initValue", varLasttNameOrig);

        //advance process execution past first user task
        // it will then go through the custom workitem and
        // get into the second user task
        TaskService taskService = runtimeEngine.getTaskService();
        assertProcessInstanceActive( pi.getId() );
        assertNodeTriggered( pi.getId(), "myFirstUserTask" );
        assertNodeActive( pi.getId(), kieSession, "myFirstUserTask" );

        List<TaskSummary> firstTasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK" );
        assertEquals( 1, firstTasks.size() );
        TaskSummary firstTaskSummary = firstTasks.get(0);

        taskService.start( firstTaskSummary.getId(), "john" );
        taskService.complete( firstTaskSummary.getId(), "john", null );

        // get process instance again to get updated process var values which were set
        // by the assignments in custom workitem handler
        ProcessInstance processInstance = kieSession.getProcessInstance( pi.getId() );
        String varFirstName = (String) ((WorkflowProcessInstance) processInstance).getVariable("firstName");
        String varLasttName = (String) ((WorkflowProcessInstance) processInstance).getVariable("lastName");

        assertNotNull(varFirstName);
        assertNotNull(varLasttName);

        assertEquals(UserAssignmentWorkitemHandler.fnameStr, varFirstName);
        assertEquals(UserAssignmentWorkitemHandler.lnameStr, varLasttName);

        // make sure we passed the custom workitem
        assertNodeTriggered( pi.getId(), "SampleUserWorkitem" );
        assertNodeActive( pi.getId(), kieSession, "mySecondUserTask" );

        List<TaskSummary> secondTasks = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK" );
        assertEquals( 1, secondTasks.size() );

        // advance paste the second user task to complete the process instance
        TaskSummary secondTaskSummary = secondTasks.get(0);
        taskService.start( secondTaskSummary.getId(), "john" );
        taskService.complete( secondTaskSummary.getId(), "john", null );

        assertProcessInstanceCompleted( processInstance.getId() );


        // now we can check audit service to see process vars
        // and how they chaged throughout process execution
        AuditService auditService = runtimeEngine.getAuditService();

        List<? extends VariableInstanceLog> firstNameVars =
                auditService.findVariableInstances(processInstance.getId(), "firstName");

        List<? extends VariableInstanceLog> lastNameVars =
                auditService.findVariableInstances(processInstance.getId(), "lastName");

        assertNotNull(firstNameVars);
        assertNotNull(lastNameVars);

        assertEquals(2, firstNameVars.size());
        assertEquals("initValue", firstNameVars.get(0).getValue());
        assertEquals(UserAssignmentWorkitemHandler.fnameStr, firstNameVars.get(1).getValue());

        assertEquals(2, lastNameVars.size());
        assertEquals("initValue", lastNameVars.get(0).getValue());
        assertEquals(UserAssignmentWorkitemHandler.lnameStr, lastNameVars.get(1).getValue());

    }
}
