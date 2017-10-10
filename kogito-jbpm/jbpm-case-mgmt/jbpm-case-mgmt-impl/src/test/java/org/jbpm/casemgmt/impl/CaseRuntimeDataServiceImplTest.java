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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.assertj.core.api.Assertions;
import org.jbpm.casemgmt.api.model.AdHocFragment;
import org.jbpm.casemgmt.api.model.CaseStatus;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.api.model.instance.CaseStageInstance;
import org.jbpm.casemgmt.api.model.instance.StageStatus;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.task.impl.model.UserImpl;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CaseRuntimeDataServiceImplTest extends AbstractCaseServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CaseRuntimeDataServiceImplTest.class);

    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<String>();
        processes.add("cases/EmptyCase.bpmn2");
        processes.add("cases/UserTaskCase.bpmn2");
        processes.add("cases/UserTaskCaseBoundary.bpmn2");
        processes.add("cases/UserTaskWithStageCase.bpmn2");
        processes.add("cases/CaseWithTwoStages.bpmn2");
        processes.add("cases/CaseWithTwoStagesConditions.bpmn2");
        // add processes that can be used by cases but are not cases themselves
        processes.add("processes/DataVerificationProcess.bpmn2");
        processes.add("processes/UserTaskProcess.bpmn2");
        return processes;
    }

    /*
     * Case instance queries
     */
    @Test
    public void testStartEmptyCaseWithCaseFile() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "my first case");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId, true, false, false, false);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertNotNull(cInstance.getCaseFile());
            assertEquals("my first case", cInstance.getCaseFile().getData("name"));

            Collection<CaseInstance> instances = caseRuntimeDataService.getCaseInstances(new QueryContext());
            assertNotNull(instances);

            assertEquals(1, instances.size());
            CaseInstance instance = instances.iterator().next();
            assertNotNull(instance);

            assertEquals(FIRST_CASE_ID, instance.getCaseId());
            assertEquals(EMPTY_CASE_P_ID, instance.getCaseDefinitionId());
            assertEquals("my first case", instance.getCaseDescription());
            assertEquals(USER, instance.getOwner());
            assertEquals(ProcessInstance.STATE_ACTIVE, instance.getStatus().intValue());
            assertEquals(deploymentUnit.getIdentifier(), instance.getDeploymentId());
            assertNotNull(instance.getStartedAt());

            // add dynamic user task to empty case instance - first by case id
            Map<String, Object> parameters = new HashMap<>();
            caseService.addDynamicTask(FIRST_CASE_ID, caseService.newHumanTaskSpec("First task", "test", "john", null, parameters));

            Collection<NodeInstanceDesc> activeNodes = caseRuntimeDataService.getActiveNodesForCase(FIRST_CASE_ID, new QueryContext());
            assertNotNull(activeNodes);
            assertEquals(1, activeNodes.size());

            NodeInstanceDesc activeNode = activeNodes.iterator().next();
            assertNotNull(activeNodes);
            assertEquals("[Dynamic] First task", activeNode.getName());

            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(caseId, "john", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            TaskSummary task = tasks.get(0);
            assertEquals("First task", task.getName());
            assertEquals("test", task.getDescription());
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
    public void testUserTasksInCase() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl(USER));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, data, roleAssignments);
        String caseId2 = null;
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertEquals(deploymentUnit.getIdentifier(),
                    cInstance.getDeploymentId());

            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(caseId, "john", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(0, tasks.size());

            Map<String, Object> taskInput = new HashMap<>();
            taskInput.put("ActorId", "john");
            taskInput.put("Comment",
                    "Need to provide data");
            caseService.triggerAdHocFragment(caseId,
                    "Missing data",
                    taskInput);

            tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(caseId, "john", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            TaskSummary task = tasks.get(0);
            assertEquals("Missing data", task.getName());
            assertEquals("Need to provide data", task.getSubject());

            caseId2 = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID, caseFile);
            assertNotNull(caseId2);
            assertEquals("CASE-0000000002", caseId2);

            caseService.triggerAdHocFragment(caseId2,
                    "Missing data",
                    taskInput);

            tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(caseId2, "john", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1,
                    tasks.size());
            task = tasks.get(0);
            assertEquals("Missing data",
                    task.getName());

            tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(caseId, "john", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            task = tasks.get(0);
            assertEquals("Missing data", task.getName());
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
            if (caseId2 != null) {
                caseService.cancelCase(caseId2);
            }
        }
    }

    @Test
    public void testUserTasksInCaseWithSubprocess() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl(USER));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, data, roleAssignments);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID,
                    cInstance.getCaseId());
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(caseId,
                    "john",
                    null,
                    new QueryContext());
            assertNotNull(tasks);
            assertEquals(0, tasks.size());

            Map<String, Object> taskInput = new HashMap<>();
            taskInput.put("ActorId", "john");
            caseService.triggerAdHocFragment(caseId,
                    "Missing data",
                    taskInput);

            tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(caseId, "john", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            TaskSummary task = tasks.get(0);
            assertEquals("Missing data",
                    task.getName());

            caseService.addDynamicSubprocess(caseId,
                    "UserTask",
                    null);

            tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(caseId,
                    "john",
                    null,
                    new QueryContext());
            assertNotNull(tasks);
            assertEquals(2, tasks.size());
            task = tasks.get(0);
            assertEquals("Hello", task.getName());
            task = tasks.get(1);
            assertEquals("Missing data", task.getName());
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
    public void testUserTasksInCaseAdBusinessAdmin() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl(USER));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, data, roleAssignments);
        String caseId2 = null;
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsBusinessAdmin(caseId, "Administrator", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(0, tasks.size());

            Map<String, Object> taskInput = new HashMap<>();
            taskInput.put("ActorId",
                    "john");
            caseService.triggerAdHocFragment(caseId,
                    "Missing data",
                    taskInput);

            tasks = caseRuntimeDataService.getCaseTasksAssignedAsBusinessAdmin(caseId, "Administrator", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            TaskSummary task = tasks.get(0);
            assertEquals("Missing data", task.getName());

            caseId2 = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID, caseFile);
            assertNotNull(caseId2);
            assertEquals("CASE-0000000002", caseId2);

            caseService.triggerAdHocFragment(caseId2,
                    "Missing data",
                    taskInput);

            tasks = caseRuntimeDataService.getCaseTasksAssignedAsBusinessAdmin(caseId2, "Administrator", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1,
                    tasks.size());
            task = tasks.get(0);
            assertEquals("Missing data",
                    task.getName());

            tasks = caseRuntimeDataService.getCaseTasksAssignedAsBusinessAdmin(caseId, "Administrator", null, new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            task = tasks.get(0);
            assertEquals("Missing data", task.getName());
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
            if (caseId2 != null) {
                caseService.cancelCase(caseId2);
            }
        }
    }

    @Test
    public void testUserTasksInCaseAdStakeholder() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl(USER));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), USER_TASK_CASE_P_ID, data, roleAssignments);
        String caseId2 = null;
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID, caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertEquals(deploymentUnit.getIdentifier(), cInstance.getDeploymentId());

            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsStakeholder(caseId,
                    "john",
                    null,
                    new QueryContext());
            assertNotNull(tasks);
            assertEquals(0, tasks.size());

            Map<String, Object> taskInput = new HashMap<>();
            taskInput.put("ActorId", "mary");
            taskInput.put("TaskStakeholderId", "john");
            caseService.triggerAdHocFragment(caseId, "Missing data", taskInput);

            tasks = caseRuntimeDataService.getCaseTasksAssignedAsStakeholder(caseId,
                    "john",
                    null,
                    new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            TaskSummary task = tasks.get(0);
            assertEquals("Missing data", task.getName());

            caseId2 = caseService.startCase(deploymentUnit.getIdentifier(), USER_TASK_STAGE_CASE_P_ID, caseFile);
            assertNotNull(caseId2);
            assertEquals("CASE-0000000002", caseId2);

            caseService.triggerAdHocFragment(caseId2, "Missing data", taskInput);

            tasks = caseRuntimeDataService.getCaseTasksAssignedAsStakeholder(caseId2,
                    "john",
                    null,
                    new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            task = tasks.get(0);
            assertEquals("Missing data", task.getName());

            tasks = caseRuntimeDataService.getCaseTasksAssignedAsStakeholder(caseId,
                    "john",
                    null,
                    new QueryContext());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            task = tasks.get(0);
            assertEquals("Missing data", task.getName());
        } catch (Exception e) {
            logger.error("Unexpected error {}", e.getMessage(), e);
            fail("Unexpected exception " + e.getMessage());
        } finally {
            if (caseId != null) {
                caseService.cancelCase(caseId);
            }
            if (caseId2 != null) {
                caseService.cancelCase(caseId2);
            }
        }
    }

    @Test
    public void testGetProcessDefinitions() {
        Collection<ProcessDefinition> processes = caseRuntimeDataService.getProcessDefinitions(new QueryContext());
        assertNotNull(processes);
        assertEquals(2, processes.size());

        Map<String, ProcessDefinition> mappedProcesses = mapProcesses(processes);
        assertTrue(mappedProcesses.containsKey("UserTask"));
        assertTrue(mappedProcesses.containsKey("DataVerification"));

        processes = caseRuntimeDataService.getProcessDefinitions("User", new QueryContext());
        assertNotNull(processes);
        assertEquals(1,
                processes.size());

        mappedProcesses = mapProcesses(processes);
        assertTrue(mappedProcesses.containsKey("UserTask"));

        processes = caseRuntimeDataService.getProcessDefinitionsByDeployment(deploymentUnit.getIdentifier(), new QueryContext());
        assertNotNull(processes);
        assertEquals(2, processes.size());

        mappedProcesses = mapProcesses(processes);
        assertTrue(mappedProcesses.containsKey("UserTask"));
        assertTrue(mappedProcesses.containsKey("DataVerification"));
    }

    @Test
    public void testTransitionBetweenStagesInCase() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl(USER));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), TWO_STAGES_CASE_P_ID, data, roleAssignments);
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), TWO_STAGES_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        try {

            Collection<CaseStageInstance> stage = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext(0, 1));
            assertNotNull(stage);
            assertEquals(1, stage.size());
            assertEquals("Stage One", stage.iterator().next().getName());
            assertEquals(StageStatus.Active, stage.iterator().next().getStatus());

            Collection<AdHocFragment> adhocTasks = caseRuntimeDataService.getAdHocFragmentsForCase(caseId);
            assertNotNull(adhocTasks);
            assertEquals(1, adhocTasks.size());
            assertEquals("Task 1", adhocTasks.iterator().next().getName());

            Collection<NodeInstanceDesc> activeNodes = caseRuntimeDataService.getActiveNodesForCase(caseId, new QueryContext(0, 10));
            assertNotNull(activeNodes);
            assertEquals(1, activeNodes.size());
            assertEquals("Stage One", activeNodes.iterator().next().getName());

            Collection<NodeInstanceDesc> completedNodes = caseRuntimeDataService.getCompletedNodesForCase(caseId, new QueryContext(0, 10));
            assertNotNull(completedNodes);
            assertEquals(0, completedNodes.size());

            caseService.addDataToCaseFile(caseId, "customData", "nextStagePlease");

            stage = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext(0, 1));
            assertNotNull(stage);
            assertEquals(1, stage.size());
            assertEquals("Stage Two", stage.iterator().next().getName());
            assertEquals(StageStatus.Active, stage.iterator().next().getStatus());

            adhocTasks = caseRuntimeDataService.getAdHocFragmentsForCase(caseId);
            assertNotNull(adhocTasks);
            assertEquals(1, adhocTasks.size());
            assertEquals("Task 2", adhocTasks.iterator().next().getName());

            activeNodes = caseRuntimeDataService.getActiveNodesForCase(caseId, new QueryContext(0, 10));
            assertNotNull(activeNodes);
            assertEquals(1, activeNodes.size());
            assertEquals("Stage Two", activeNodes.iterator().next().getName());

            completedNodes = caseRuntimeDataService.getCompletedNodesForCase(caseId, new QueryContext(0, 10));
            assertNotNull(completedNodes);
            assertEquals(1, completedNodes.size());
            assertEquals("Stage One", completedNodes.iterator().next().getName());
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
    public void testAddSubprocessToEmptyCaseCheckCaseNodes() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "my first case");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, data);

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        assertEquals(FIRST_CASE_ID,
                caseId);
        try {
            CaseInstance cInstance = caseService.getCaseInstance(caseId);
            assertNotNull(cInstance);
            assertEquals(FIRST_CASE_ID, cInstance.getCaseId());
            assertEquals(deploymentUnit.getIdentifier(),
                    cInstance.getDeploymentId());

            Collection<NodeInstanceDesc> activeNodes = caseRuntimeDataService.getActiveNodesForCase(caseId, new QueryContext(0, 10));
            assertNotNull(activeNodes);
            assertEquals(0, activeNodes.size());

            Collection<NodeInstanceDesc> completedNodes = caseRuntimeDataService.getCompletedNodesForCase(caseId,
                    new QueryContext(0,
                            10));
            assertNotNull(completedNodes);
            assertEquals(0, completedNodes.size());

            Map<String, Object> parameters = new HashMap<>();
            caseService.addDynamicSubprocess(caseId,
                    "UserTask",
                    parameters);

            Collection<ProcessInstanceDesc> caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId,
                    new QueryContext());
            assertNotNull(caseProcessInstances);
            assertEquals(2, caseProcessInstances.size());

            activeNodes = caseRuntimeDataService.getActiveNodesForCase(caseId, new QueryContext(0, 10));
            assertNotNull(activeNodes);
            assertEquals(2,
                    activeNodes.size());
            Map<String, NodeInstanceDesc> mappedNodes = mapNodeInstances(activeNodes);
            assertEquals("HumanTaskNode", mappedNodes.get("Hello").getNodeType());
            assertEquals("SubProcessNode", mappedNodes.get("[Dynamic] Sub Process").getNodeType());

            completedNodes = caseRuntimeDataService.getCompletedNodesForCase(caseId, new QueryContext(0, 10));
            assertNotNull(completedNodes);
            assertEquals(0,
                    completedNodes.size());

            List<TaskSummary> tasks = caseRuntimeDataService.getCaseTasksAssignedAsPotentialOwner(caseId, "john", null, new QueryContext());
            assertEquals(1, tasks.size());

            userTaskService.completeAutoProgress(tasks.get(0).getId(),
                    "john",
                    null);

            activeNodes = caseRuntimeDataService.getActiveNodesForCase(caseId, new QueryContext(0, 10));
            assertNotNull(activeNodes);
            assertEquals(0, activeNodes.size());

            completedNodes = caseRuntimeDataService.getCompletedNodesForCase(caseId, new QueryContext(0, 10));
            assertNotNull(completedNodes);
            assertEquals(2,
                    completedNodes.size());
            assertEquals("HumanTaskNode", mappedNodes.get("Hello").getNodeType());
            assertEquals("SubProcessNode", mappedNodes.get("[Dynamic] Sub Process").getNodeType());
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
    public void testTransitionBetweenStagesWithConditionsInCase() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl(USER));

        Map<String, Object> data = new HashMap<>();
        data.put("customData", "none");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), TWO_STAGES_CONDITIONS_CASE_P_ID, data, roleAssignments);
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), TWO_STAGES_CONDITIONS_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        try {

            Collection<CaseStageInstance> stage = caseRuntimeDataService.getCaseInstanceStages(caseId,
                    true,
                    new QueryContext(0,
                            1));
            assertNotNull(stage);
            assertEquals(1,
                    stage.size());
            assertEquals("Stage One", stage.iterator().next().getName());
            assertEquals(StageStatus.Active, stage.iterator().next().getStatus());

            Collection<AdHocFragment> adhocTasks = caseRuntimeDataService.getAdHocFragmentsForCase(caseId);
            assertNotNull(adhocTasks);
            assertEquals(1, adhocTasks.size());
            assertEquals("Task 1",
                    adhocTasks.iterator().next().getName());

            caseService.triggerAdHocFragment(caseId,
                    "Task 1",
                    null);

            List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1,
                    tasks.size());
            assertTask(tasks.get(0),
                    "john",
                    "Task 1",
                    Status.Reserved);

            Map<String, Object> params = new HashMap<>();
            params.put("myData",
                    "nextStage");
            userTaskService.completeAutoProgress(tasks.get(0).getId(), "john", params);

            stage = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext(0, 1));
            assertNotNull(stage);
            assertEquals(1, stage.size());
            assertEquals("Stage Two", stage.iterator().next().getName());
            assertEquals(StageStatus.Active, stage.iterator().next().getStatus());

            adhocTasks = caseRuntimeDataService.getAdHocFragmentsForCase(caseId);
            assertNotNull(adhocTasks);
            assertEquals(1, adhocTasks.size());
            assertEquals("Task 2",
                    adhocTasks.iterator().next().getName());

            caseService.triggerAdHocFragment(caseId, "Task 2", null);

            tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("john", new QueryFilter());
            assertNotNull(tasks);
            assertEquals(1, tasks.size());
            assertTask(tasks.get(0),
                    "john",
                    "Task 2",
                    Status.Reserved);

            params = new HashMap<>();
            params.put("myData",
                    "none");
            userTaskService.completeAutoProgress(tasks.get(0).getId(),
                    "john",
                    params);
        
            CaseInstance instance = caseService.getCaseInstance(caseId);
            Assertions.assertThat(instance.getStatus()).isEqualTo(CaseStatus.CLOSED.getId());
            caseId = null;
            
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
    public void testTransitionBetweenStagesInCaseWithActiveElements() {
        Map<String, OrganizationalEntity> roleAssignments = new HashMap<>();
        roleAssignments.put("owner", new UserImpl(USER));

        Map<String, Object> data = new HashMap<>();
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), TWO_STAGES_CASE_P_ID, data, roleAssignments);
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), TWO_STAGES_CASE_P_ID, caseFile);
        assertNotNull(caseId);
        try {

            Collection<CaseStageInstance> stage = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext(0, 1));
            assertNotNull(stage);
            assertEquals(1, stage.size());

            CaseStageInstance stageInstance = stage.iterator().next();
            assertEquals("Stage One", stageInstance.getName());
            assertEquals(StageStatus.Active, stageInstance.getStatus());

            Collection<NodeInstanceDesc> activeNodes = stageInstance.getActiveNodes();
            assertNotNull(activeNodes);
            assertEquals(0, activeNodes.size());

            caseService.triggerAdHocFragment(caseId, "Task 1", data);
            stage = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext(0, 1));
            assertNotNull(stage);
            assertEquals(1, stage.size());

            stageInstance = stage.iterator().next();
            assertEquals("Stage One", stageInstance.getName());
            assertEquals(StageStatus.Active, stageInstance.getStatus());

            activeNodes = stageInstance.getActiveNodes();
            assertNotNull(activeNodes);
            assertEquals(1, activeNodes.size());
            assertEquals("Task 1", activeNodes.iterator().next().getName());

            caseService.addDataToCaseFile(caseId, "customData", "nextStagePlease");

            stage = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext(0, 1));
            assertNotNull(stage);
            assertEquals(1, stage.size());
            assertEquals("Stage Two", stage.iterator().next().getName());
            assertEquals(StageStatus.Active, stage.iterator().next().getStatus());

            stage = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext(0, 1));
            assertNotNull(stage);
            assertEquals(1, stage.size());

            stageInstance = stage.iterator().next();
            assertEquals("Stage Two", stageInstance.getName());
            assertEquals(StageStatus.Active, stageInstance.getStatus());

            activeNodes = stageInstance.getActiveNodes();
            assertNotNull(activeNodes);
            assertEquals(0, activeNodes.size());

            caseService.triggerAdHocFragment(caseId, "Task 2", data);
            stage = caseRuntimeDataService.getCaseInstanceStages(caseId, true, new QueryContext(0, 1));
            assertNotNull(stage);
            assertEquals(1, stage.size());

            stageInstance = stage.iterator().next();
            assertEquals("Stage Two", stageInstance.getName());
            assertEquals(StageStatus.Active, stageInstance.getStatus());

            activeNodes = stageInstance.getActiveNodes();
            assertNotNull(activeNodes);
            assertEquals(1, activeNodes.size());
            assertEquals("Task 2", activeNodes.iterator().next().getName());
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
    public void testResolveCaseStatuses() {
        List<CaseStatus> testStatuses = Arrays.asList(CaseStatus.CANCELLED,
                CaseStatus.CLOSED,
                CaseStatus.OPEN);
        List<Integer> resolvedTestStatuses = ((CaseRuntimeDataServiceImpl) caseRuntimeDataService).resolveCaseStatuses(testStatuses);

        List<Integer> resultStatuses = Arrays.asList(3,
                2,
                1);
        List<Integer> invalidResultStatuses = Arrays.asList(1,
                2,
                3);

        assertTrue(resolvedTestStatuses.equals(resultStatuses));
        assertFalse(resolvedTestStatuses.equals(invalidResultStatuses));
    }

    @Test
    public void testCaseStatusCreation() {
        List<Integer> testStatuses = Arrays.asList(1);
        List<String> testStatusesString = Arrays.asList("open");
        List<CaseStatus> testCaseStatusesFromIds = CaseStatus.fromIdList(testStatuses);
        List<CaseStatus> testCaseStatusesFromNames = CaseStatus.fromNameList(testStatusesString);

        assertNotNull(testCaseStatusesFromIds);
        assertNotNull(testCaseStatusesFromNames);
        assertEquals(1,
                testCaseStatusesFromIds.size());
        assertEquals(1,
                testCaseStatusesFromNames.size());
        assertTrue(testCaseStatusesFromIds.contains(CaseStatus.OPEN));
        assertTrue(testCaseStatusesFromNames.contains(CaseStatus.OPEN));

        testStatuses = Arrays.asList(1, 2);
        testStatusesString = Arrays.asList("open", "closed");
        testCaseStatusesFromIds = CaseStatus.fromIdList(testStatuses);
        testCaseStatusesFromNames = CaseStatus.fromNameList(testStatusesString);

        assertNotNull(testCaseStatusesFromIds);
        assertNotNull(testCaseStatusesFromNames);
        assertEquals(2,
                testCaseStatusesFromIds.size());
        assertEquals(2,
                testCaseStatusesFromNames.size());
        assertTrue(testCaseStatusesFromIds.contains(CaseStatus.OPEN));
        assertTrue(testCaseStatusesFromIds.contains(CaseStatus.CLOSED));
        assertTrue(testCaseStatusesFromNames.contains(CaseStatus.OPEN));
        assertTrue(testCaseStatusesFromNames.contains(CaseStatus.CLOSED));

        testStatuses = Arrays.asList(1, 2, 3);
        testStatusesString = Arrays.asList("open", "closed", "cancelled");
        testCaseStatusesFromIds = CaseStatus.fromIdList(testStatuses);
        testCaseStatusesFromNames = CaseStatus.fromNameList(testStatusesString);

        assertNotNull(testCaseStatusesFromIds);
        assertNotNull(testCaseStatusesFromNames);
        assertEquals(3,
                testCaseStatusesFromIds.size());
        assertEquals(3,
                testCaseStatusesFromNames.size());
        assertTrue(testCaseStatusesFromIds.contains(CaseStatus.OPEN));
        assertTrue(testCaseStatusesFromIds.contains(CaseStatus.CLOSED));
        assertTrue(testCaseStatusesFromIds.contains(CaseStatus.CANCELLED));
        assertTrue(testCaseStatusesFromNames.contains(CaseStatus.OPEN));
        assertTrue(testCaseStatusesFromNames.contains(CaseStatus.CLOSED));
        assertTrue(testCaseStatusesFromNames.contains(CaseStatus.CANCELLED));
    }
}