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

package org.jbpm.casemgmt.impl;

import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.services.task.impl.model.GroupImpl;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.Test;
import org.kie.api.runtime.process.CaseAssignment;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.TaskSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CaseAssignmentServiceImplTest extends AbstractCaseServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CaseAssignmentServiceImplTest.class);

    private static final String CASE_X_P_ID = "testCaseProject.caseDef_X";
    private static final String CASE_Y_P_ID = "testCaseProject.caseDef_Y";
    
    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<String>();
        processes.add("cases/caseDef_X.bpmn2");
        processes.add("cases/caseDef_Y.bpmn2");
        return processes;
    }

    
    @Test
    public void testProperRoleAssignedAutoStart() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("actorRole", new UserImpl("john"));
        roleAssignments.put("groupRole", new GroupImpl("managers"));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CASE_X_P_ID, data, roleAssignments);
        
        ((CaseAssignment)caseFile).assignGroup("generalRole", "managers");
        ((CaseAssignment)caseFile).assignUser("generalRole", "john");

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CASE_X_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    @Test
    public void testRoleAssignedMissingUserAutoStart() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("actorRole", new UserImpl("john"));
        roleAssignments.put("groupRole", new GroupImpl("managers"));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CASE_X_P_ID, data, roleAssignments);
                
        ((CaseAssignment)caseFile).assignUser("generalRole", "john");

        
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> { 
            caseService.startCase(deploymentUnit.getIdentifier(), CASE_X_P_ID, caseFile); })
        .withMessageContaining("Case role 'generalRole' has no matching assignments");
                          
    }
    
    @Test
    public void testRoleAssignedMissingGroupAutoStart() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("actorRole", new UserImpl("john"));
        roleAssignments.put("groupRole", new GroupImpl("managers"));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CASE_X_P_ID, data, roleAssignments);
                
        ((CaseAssignment)caseFile).assignGroup("generalRole", "managers");

        
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> { 
            caseService.startCase(deploymentUnit.getIdentifier(), CASE_X_P_ID, caseFile); })
        .withMessageContaining("Case role 'generalRole' has no matching assignments");
                          
    }
    
    @Test
    public void testProperRoleAssigned() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("actorRole", new UserImpl("mary"));
        roleAssignments.put("groupRole", new GroupImpl("managers"));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CASE_Y_P_ID, data, roleAssignments);
        
        ((CaseAssignment)caseFile).assignGroup("generalRole", "managers");
        ((CaseAssignment)caseFile).assignUser("generalRole", "john");

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CASE_Y_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            
            
            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(caseId, "mary", null, new QueryContext());
            assertEquals(5, tasks.size());
            
            TaskSummary foundTask = tasks.stream().filter(task -> task.getName().equals("Task E")).findFirst().get();
            
            userTaskService.completeAutoProgress(foundTask.getId(), "mary", new HashMap<>());
           
            tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(caseId, "john", null, new QueryContext());
            assertEquals(1, tasks.size());
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    
    @Test
    public void testRoleAssignedMissingUser() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("actorRole", new UserImpl("mary"));
        roleAssignments.put("groupRole", new GroupImpl("managers"));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CASE_Y_P_ID, data, roleAssignments);
                
        ((CaseAssignment)caseFile).assignUser("generalRole", "john");

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), CASE_Y_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            
            
            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(caseId, "mary", null, new QueryContext());
            assertEquals(5, tasks.size());
            
            TaskSummary foundTask = tasks.stream().filter(task -> task.getName().equals("Task E")).findFirst().get();
            
            assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> {
                userTaskService.completeAutoProgress(foundTask.getId(), "mary", new HashMap<>());
            })
            .withMessageContaining("Case role 'generalRole' has no matching assignments");
            
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
               
    }
    
    @Test
    public void testRoleAssignedMissingRole() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        
        roleAssignments.put("groupRole", new GroupImpl("managers"));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), CASE_Y_P_ID, data, roleAssignments);
        
        
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() -> { 
            caseService.startCase(deploymentUnit.getIdentifier(), CASE_Y_P_ID, caseFile); })
        .withMessageContaining("Case role 'actorRole' has no matching assignments");
                          
    }

}
