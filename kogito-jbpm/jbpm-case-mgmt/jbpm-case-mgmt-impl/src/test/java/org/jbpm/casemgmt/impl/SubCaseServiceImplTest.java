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

import static java.util.stream.Collectors.toMap;
import static org.assertj.core.api.Assertions.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.jbpm.casemgmt.api.dynamic.TaskSpecification;
import org.jbpm.casemgmt.api.model.CaseStatus;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.process.instance.ProcessInstance;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.Test;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubCaseServiceImplTest extends AbstractCaseServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(SubCaseServiceImplTest.class);

    protected static final String BASIC_SUB_CASE_P_ID = "CaseWithSubCase";
    protected static final String SUB_CASE_ID = "SUB-0000000001";
    protected static final String PROCESS_TO_CASE_P_ID = "process2case";
    
    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<String>();
        processes.add("cases/CaseWithSubCase.bpmn2");
        processes.add("cases/UserTaskCase.bpmn2");
        processes.add("cases/EmptyCase.bpmn2");
        // add processes that can be used by cases but are not cases themselves
        processes.add("processes/DataVerificationProcess.bpmn2");
        processes.add("processes/Process2Case.bpmn2");
        return processes;
    }

    @Test
    public void testStartCaseWithSubCaseDestroySubCase() {
        
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("manager", new UserImpl("mary"));
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), BASIC_SUB_CASE_P_ID, data, roleAssignments);

        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), BASIC_SUB_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(SUB_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            caseService.triggerAdHocFragment(caseId, "Sub Case", null);
            
            Collection<CaseInstance> caseInstances = caseRuntimeDataService.getCaseInstances(new QueryContext());
            assertNotNull(caseInstances);
            assertEquals(2, caseInstances.size());
            
            Map<String, CaseInstance> byCaseId = caseInstances.stream().collect(toMap(CaseInstance::getCaseId, c -> c));
            assertTrue(byCaseId.containsKey(SUB_CASE_ID));
            assertTrue(byCaseId.containsKey(HR_CASE_ID));
            
            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(HR_CASE_ID, "john", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            assertEquals("Hello1", tasks.get(0).getName());
            
            CaseFileInstance mainCaseFile = caseService.getCaseFileInstance(caseId);
            assertNotNull(mainCaseFile);
            assertNull(mainCaseFile.getData("subCaseId"));
            
            caseService.destroyCase(HR_CASE_ID);
            
            mainCaseFile = caseService.getCaseFileInstance(caseId);
            assertNotNull(mainCaseFile);
            assertEquals(HR_CASE_ID, mainCaseFile.getData("subCaseId"));
            assertEquals("John Doe", mainCaseFile.getData("outcome"));
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
    public void testStartProcessWithSubCaseDestroySubCase() {
        
        Long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), PROCESS_TO_CASE_P_ID);
        assertNotNull(processInstanceId);
        try {
            // check if case was created from within the process
            CaseInstance cInstance = caseService.getCaseInstance(HR_CASE_ID);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());
            
            Map<String, Object> processVariables = processService.getProcessInstanceVariables(processInstanceId);
            assertNull(processVariables.get("CaseId"));
            assertNull(processVariables.get("outcome"));
            
            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(HR_CASE_ID, "john", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            assertEquals("Hello1", tasks.get(0).getName());
            
            caseService.destroyCase(HR_CASE_ID);
            
            processVariables = processService.getProcessInstanceVariables(processInstanceId);
            assertEquals(HR_CASE_ID, processVariables.get("CaseId"));
            assertEquals("process2case", processVariables.get("outcome"));
            
            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            assertEquals("Verify case results", tasks.get(0).getName());
            
            Map<String, Object> taskInputs = userTaskService.getTaskInputContentByTaskId(tasks.get(0).getId());
            assertEquals(HR_CASE_ID, taskInputs.get("CaseId"));
            assertEquals("process2case", taskInputs.get("outcome"));
            
            userTaskService.completeAutoProgress(tasks.get(0).getId(), "john", null);
            
            ProcessInstanceDesc piLog = runtimeDataService.getProcessInstanceById(processInstanceId);
            assertNotNull(piLog);
            assertEquals(ProcessInstance.STATE_COMPLETED, piLog.getState().intValue());
            
            processInstanceId = null;
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (processInstanceId != null) {
                processService.abortProcessInstance(processInstanceId);
            }
        }
    }
    
    @Test
    public void testStartCaseWithDynamicSubCaseAbortMainCase() {
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);

        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CaseDefinitionId", "UserTaskCase");
            parameters.put("DeploymentId", deploymentUnit.getIdentifier());
            parameters.put("UserRole_owner", "john");
            parameters.put("Data_s", "#{name}");
            parameters.put("Independent", "false");
            TaskSpecification taskSpecification = caseService.newTaskSpec("StartCaseInstance", "Sub Case", parameters);
            
            caseService.addDynamicTask(caseId, taskSpecification);
            
            Collection<CaseInstance> caseInstances = caseRuntimeDataService.getCaseInstances(new QueryContext());
            assertNotNull(caseInstances);
            assertEquals(2, caseInstances.size());
            
            Map<String, CaseInstance> byCaseId = caseInstances.stream().collect(toMap(CaseInstance::getCaseId, c -> c));
            assertTrue(byCaseId.containsKey(FIRST_CASE_ID));
            assertTrue(byCaseId.containsKey(HR_CASE_ID));
            
            caseService.cancelCase(caseId);
            caseId = null;
            
            caseInstances = caseRuntimeDataService.getCaseInstances(new QueryContext());
            assertNotNull(caseInstances);
            assertEquals(0, caseInstances.size());
            
            caseInstances = caseRuntimeDataService.getCaseInstances(Arrays.asList(CaseStatus.CANCELLED), new QueryContext());
            assertNotNull(caseInstances);
            assertEquals(2, caseInstances.size());
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
    public void testStartCaseWithIndependentDynamicSubCaseAbortMainCase() {
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);

        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CaseDefinitionId", "UserTaskCase");
            parameters.put("DeploymentId", deploymentUnit.getIdentifier());
            parameters.put("UserRole_owner", "john");
            parameters.put("Data_s", "#{name}");
            parameters.put("Independent", "true");
            TaskSpecification taskSpecification = caseService.newTaskSpec("StartCaseInstance", "Sub Case", parameters);
            
            caseService.addDynamicTask(caseId, taskSpecification);
            
            Collection<CaseInstance> caseInstances = caseRuntimeDataService.getCaseInstances(new QueryContext());
            assertNotNull(caseInstances);
            assertEquals(2, caseInstances.size());
            
            Map<String, CaseInstance> byCaseId = caseInstances.stream().collect(toMap(CaseInstance::getCaseId, c -> c));
            assertTrue(byCaseId.containsKey(FIRST_CASE_ID));
            assertTrue(byCaseId.containsKey(HR_CASE_ID));
            
            caseService.cancelCase(caseId);
            caseId = HR_CASE_ID;
            
            caseInstances = caseRuntimeDataService.getCaseInstances(new QueryContext());
            assertNotNull(caseInstances);
            assertEquals(1, caseInstances.size());
            
            caseInstances = caseRuntimeDataService.getCaseInstances(Arrays.asList(CaseStatus.CANCELLED), new QueryContext());
            assertNotNull(caseInstances);
            assertEquals(1, caseInstances.size());
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
    public void testStartCaseWithDynamicSubCaseDestroySubCase() {
        testStartCaseWithDynamicSubCase("false", s -> caseService.destroyCase(HR_CASE_ID));
    }
    
    @Test
    public void testStartCaseWithDynamicSubCaseCancelSubCase() {
        testStartCaseWithDynamicSubCase("false", s -> caseService.cancelCase(HR_CASE_ID));
    }
    
    @Test
    public void testStartCaseWithDynamicSubCaseCloseSubCase() {
        testStartCaseWithDynamicSubCase("false", s -> caseService.closeCase(HR_CASE_ID, "I am done"));
    }
    
    @Test
    public void testStartCaseWithIndependentDynamicSubCaseDestroySubCase() {
        testStartCaseWithIndependentDynamicSubCase("true", s -> caseService.destroyCase(HR_CASE_ID));
    }
    
    @Test
    public void testStartCaseWithIndependentDynamicSubCaseCancelSubCase() {
        testStartCaseWithIndependentDynamicSubCase("true", s -> caseService.cancelCase(HR_CASE_ID));
    }
    
    @Test
    public void testStartCaseWithIndependentDynamicSubCaseCloseSubCase() {
        testStartCaseWithIndependentDynamicSubCase("true", s -> caseService.closeCase(HR_CASE_ID, "I am done"));
    }
    
    private void testStartCaseWithIndependentDynamicSubCase(String idependent, Consumer<String> finishCase) {
                
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);

        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CaseDefinitionId", "UserTaskCase");
            parameters.put("DeploymentId", deploymentUnit.getIdentifier());
            parameters.put("UserRole_owner", "john");
            parameters.put("Data_s", "#{name}");
            parameters.put("Independent", idependent);
            TaskSpecification taskSpecification = caseService.newTaskSpec("StartCaseInstance", "Sub Case", parameters);
            
            caseService.addDynamicTask(caseId, taskSpecification);
            
            Collection<CaseInstance> caseInstances = caseRuntimeDataService.getCaseInstances(new QueryContext());
            assertNotNull(caseInstances);
            assertEquals(2, caseInstances.size());
            
            Map<String, CaseInstance> byCaseId = caseInstances.stream().collect(toMap(CaseInstance::getCaseId, c -> c));
            assertTrue(byCaseId.containsKey(FIRST_CASE_ID));
            assertTrue(byCaseId.containsKey(HR_CASE_ID));
            
            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(HR_CASE_ID, "john", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            assertEquals("Hello1", tasks.get(0).getName());
            
            CaseFileInstance mainCaseFile = caseService.getCaseFileInstance(caseId);
            assertNotNull(mainCaseFile);
            assertEquals(HR_CASE_ID, mainCaseFile.getData("CaseId"));
            assertEquals("John Doe", mainCaseFile.getData("s"));
            
            finishCase.accept(HR_CASE_ID);
            
            mainCaseFile = caseService.getCaseFileInstance(caseId);
            assertNotNull(mainCaseFile);
            assertEquals(HR_CASE_ID, mainCaseFile.getData("CaseId"));
            assertEquals("John Doe", mainCaseFile.getData("s"));
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }
    
    private void testStartCaseWithDynamicSubCase(String idependent, Consumer<String> finishCase) {
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);

        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("CaseDefinitionId", "UserTaskCase");
            parameters.put("DeploymentId", deploymentUnit.getIdentifier());
            parameters.put("UserRole_owner", "john");
            parameters.put("Data_s", "#{name}");
            parameters.put("Independent", idependent);
            TaskSpecification taskSpecification = caseService.newTaskSpec("StartCaseInstance", "Sub Case", parameters);
            
            caseService.addDynamicTask(caseId, taskSpecification);
            
            Collection<CaseInstance> caseInstances = caseRuntimeDataService.getCaseInstances(new QueryContext());
            assertNotNull(caseInstances);
            assertEquals(2, caseInstances.size());
            
            Map<String, CaseInstance> byCaseId = caseInstances.stream().collect(toMap(CaseInstance::getCaseId, c -> c));
            assertTrue(byCaseId.containsKey(FIRST_CASE_ID));
            assertTrue(byCaseId.containsKey(HR_CASE_ID));
            
            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(HR_CASE_ID, "john", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            assertEquals("Hello1", tasks.get(0).getName());
            
            CaseFileInstance mainCaseFile = caseService.getCaseFileInstance(caseId);
            assertNotNull(mainCaseFile);
            assertEquals(FIRST_CASE_ID, mainCaseFile.getData("CaseId"));
            assertNull(mainCaseFile.getData("s"));
            
            finishCase.accept(HR_CASE_ID);
            
            mainCaseFile = caseService.getCaseFileInstance(caseId);
            assertNotNull(mainCaseFile);
            assertEquals(HR_CASE_ID, mainCaseFile.getData("CaseId"));
            assertEquals("John Doe", mainCaseFile.getData("s"));
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
    public void testStartCaseWithSubCaseAbortProcessInstanceOfSubCase() {
        
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl("john"));
        roleAssignments.put("manager", new UserImpl("mary"));
        
        Map<String, Object> data = new HashMap<>();
        data.put("name", "John Doe");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), BASIC_SUB_CASE_P_ID, data, roleAssignments);

        
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), BASIC_SUB_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(SUB_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            caseService.triggerAdHocFragment(caseId, "Sub Case", null);
            
            Collection<CaseInstance> caseInstances = caseRuntimeDataService.getCaseInstances(new QueryContext());
            assertNotNull(caseInstances);
            assertEquals(2, caseInstances.size());
            
            Map<String, CaseInstance> byCaseId = caseInstances.stream().collect(toMap(CaseInstance::getCaseId, c -> c));
            assertTrue(byCaseId.containsKey(SUB_CASE_ID));
            assertTrue(byCaseId.containsKey(HR_CASE_ID));
            
            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(HR_CASE_ID, "john", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            assertEquals("Hello1", tasks.get(0).getName());
            
            CaseFileInstance mainCaseFile = caseService.getCaseFileInstance(caseId);
            assertNotNull(mainCaseFile);
            assertNull(mainCaseFile.getData("subCaseId"));
            
            processService.abortProcessInstance(tasks.get(0).getProcessInstanceId());
            
            mainCaseFile = caseService.getCaseFileInstance(caseId);
            assertNotNull(mainCaseFile);
            assertEquals(HR_CASE_ID, mainCaseFile.getData("subCaseId"));
            assertEquals("John Doe", mainCaseFile.getData("outcome"));
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
        }
    }

}
