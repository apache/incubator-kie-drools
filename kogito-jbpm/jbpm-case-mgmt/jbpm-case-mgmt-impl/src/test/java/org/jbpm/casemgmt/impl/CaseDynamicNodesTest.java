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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jbpm.casemgmt.api.CaseNotFoundException;
import org.jbpm.casemgmt.api.StageNotFoundException;
import org.jbpm.casemgmt.api.dynamic.TaskSpecification;
import org.jbpm.casemgmt.api.model.CaseDefinition;
import org.jbpm.casemgmt.api.model.CaseStage;
import org.jbpm.casemgmt.api.model.CaseStatus;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.jbpm.services.api.ProcessDefinitionNotFoundException;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.Test;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;

import static org.assertj.core.api.Assertions.*;

public class CaseDynamicNodesTest extends AbstractCaseServicesBaseTest {

    private static final String NOT_EXISTING_STAGE = "NotExistingStage";
    private static final String EMPTY_CASE_STAGE = "EmptyCaseStage";

    private static final String DYNAMIC_TASK = "DynamicUserTask";
    private static final String SUBPROCESS_TASK = "Hello";

    private static final String SUBPROCESS = "UserTask";
    private static final String NOT_EXISTING_SUBPROCESS = "NotExistingSubprocess";

    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<>();
        processes.add("cases/EmptyCase.bpmn2");
        processes.add("cases/EmptyCaseStage.bpmn2");
        processes.add("cases/CaseWithTwoStages.bpmn2");
        processes.add("processes/UserTaskProcess.bpmn2");
        return processes;
    }

    @Test
    public void testAddDynamicTaskToNotExistingCase() {
        assertThatThrownBy(() -> caseService.addDynamicTask(FIRST_CASE_ID, createUserTask()))
                .isInstanceOf(CaseNotFoundException.class);
    }

    @Test
    public void testAddDynamicTaskToNotExistingCaseByProcessInstanceId() {
        assertThatThrownBy(() -> caseService.addDynamicTask(Long.MAX_VALUE, createUserTask()))
                .isInstanceOf(ProcessInstanceNotFoundException.class);
    }

    @Test
    public void testAddDynamicTaskToStageNotExistingCase() {
        assertThatThrownBy(() -> caseService.addDynamicTaskToStage(FIRST_CASE_ID, NOT_EXISTING_STAGE, createUserTask()))
                .isInstanceOf(CaseNotFoundException.class);
    }

    @Test
    public void testAddDynamicTaskToNotExistingStage() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        assertThatThrownBy(() -> caseService.addDynamicTaskToStage(caseId, NOT_EXISTING_STAGE, createUserTask()))
                .isInstanceOf(StageNotFoundException.class);
    }

    @Test
    public void testAddDynamicTaskToStageNotExistingCaseByProcessInstanceId() {
        assertThatThrownBy(() -> caseService.addDynamicTaskToStage(Long.MAX_VALUE, NOT_EXISTING_STAGE,
                createUserTask())).isInstanceOf(ProcessInstanceNotFoundException.class);
    }

    @Test
    public void testAddDynamicTaskToNotExistingStageByProcessInstanceId() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        Collection<ProcessInstanceDesc> caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId,
                new QueryContext());
        assertThat(caseProcessInstances).isNotNull().hasSize(1);

        ProcessInstanceDesc processInstance = caseProcessInstances.iterator().next();
        assertThat(processInstance).isNotNull();
        assertThat(processInstance.getCorrelationKey()).isEqualTo(FIRST_CASE_ID);

        assertThatThrownBy(() -> caseService.addDynamicTaskToStage(processInstance.getId(), NOT_EXISTING_STAGE,
                createUserTask())).isInstanceOf(StageNotFoundException.class);
    }

    @Test
    public void testAddDynamicUserTaskToEmptyStage() {
        Map<String, Object> data = new HashMap<>();
        data.put("continue", false);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_STAGE,
                data, Collections.emptyMap());

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_STAGE, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        assertThat(caseInstance.getCaseStages()).hasSize(1);

        String stageId = caseInstance.getCaseStages().iterator().next().getId();
        caseService.addDynamicTaskToStage(caseId, stageId, createUserTask());

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary insideTask = tasks.get(0);
        assertThat(insideTask.getName()).isEqualTo(DYNAMIC_TASK);
        userTaskService.completeAutoProgress(insideTask.getId(), USER, null);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).isEmpty();
    }

    @Test
    public void testAddMultipleDynamicTasksToStage() {
        Map<String, Object> data = new HashMap<>();
        data.put("continue", false);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(), EMPTY_CASE_STAGE,
                data, Collections.emptyMap());

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_STAGE, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        assertThat(caseInstance.getCaseStages()).hasSize(1);

        String stageId = caseInstance.getCaseStages().iterator().next().getId();
        caseService.addDynamicTaskToStage(caseId, stageId, createUserTask());
        caseService.addDynamicTaskToStage(caseId, stageId, createUserTask());

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(2);

        TaskSummary userTask1 = tasks.get(0);
        assertThat(userTask1.getName()).isEqualTo(DYNAMIC_TASK);
        userTaskService.completeAutoProgress(userTask1.getId(), USER, null);

        TaskSummary userTask2 = tasks.get(1);
        assertThat(userTask2.getName()).isEqualTo(DYNAMIC_TASK);
        userTaskService.completeAutoProgress(userTask2.getId(), USER, null);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).isEmpty();
    }

    @Test
    public void testAddDynamicTaskToNotActiveStage() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), TWO_STAGES_CASE_P_ID);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        CaseDefinition caseDefinition = caseRuntimeDataService.getCase(deploymentUnit.getIdentifier(),
                TWO_STAGES_CASE_P_ID);
        assertThat(caseDefinition).isNotNull();
        assertThat(caseDefinition.getCaseStages()).hasSize(2);

        Iterator<CaseStage> caseStageIterator = caseDefinition.getCaseStages().iterator();
        caseStageIterator.next();
        String stageId = caseStageIterator.next().getId();

        assertThatThrownBy(() -> caseService.addDynamicTaskToStage(caseId, stageId, createUserTask()))
                .isInstanceOf(StageNotFoundException.class);
    }

    @Test
    public void testAddDynamicSubprocessToNotExistingCase() {
        assertThatThrownBy(() -> caseService.addDynamicSubprocess(FIRST_CASE_ID, SUBPROCESS, Collections.emptyMap()))
                .isInstanceOf(CaseNotFoundException.class);
    }

    @Test
    public void testAddDynamicSubprocessToNotExistingCaseByProcessInstanceId() {
        assertThatThrownBy(() -> caseService.addDynamicSubprocess(Long.MAX_VALUE, SUBPROCESS, Collections.emptyMap()))
                .isInstanceOf(ProcessInstanceNotFoundException.class);
    }

    @Test
    public void testAddDynamicSubprocessToStageNotExistingCase() {
        assertThatThrownBy(() -> caseService.addDynamicSubprocessToStage(FIRST_CASE_ID, NOT_EXISTING_STAGE, SUBPROCESS,
                Collections.emptyMap())).isInstanceOf(CaseNotFoundException.class);
    }

    @Test
    public void testAddDynamicSubprocessToNotExistingStage() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        assertThatThrownBy(() -> caseService.addDynamicSubprocessToStage(caseId, NOT_EXISTING_STAGE, SUBPROCESS,
                Collections.emptyMap())).isInstanceOf(StageNotFoundException.class);
    }

    @Test
    public void testAddDynamicSubprocessToStageNotExistingCaseByProcessInstanceId() {
        assertThatThrownBy(() -> caseService.addDynamicSubprocessToStage(Long.MAX_VALUE, NOT_EXISTING_STAGE, SUBPROCESS,
                Collections.emptyMap())).isInstanceOf(ProcessInstanceNotFoundException.class);
    }

    @Test
    public void testAddDynamicSubprocessToNotExistingStageByProcessInstanceId() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        Collection<ProcessInstanceDesc> caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId,
                new QueryContext());
        assertThat(caseProcessInstances).isNotNull().hasSize(1);

        ProcessInstanceDesc processInstance = caseProcessInstances.iterator().next();
        assertThat(processInstance).isNotNull();
        assertThat(processInstance.getCorrelationKey()).isEqualTo(FIRST_CASE_ID);

        assertThatThrownBy(() -> caseService.addDynamicSubprocessToStage(processInstance.getId(), NOT_EXISTING_STAGE,
                SUBPROCESS, Collections.emptyMap())).isInstanceOf(StageNotFoundException.class);
    }

    @Test
    public void testAddDynamicSubprocessWithNotExistingProcessId() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        CaseStatus initialStatus = CaseStatus.fromId(caseInstance.getStatus());

        assertThatThrownBy(() -> caseService.addDynamicSubprocess(caseId, NOT_EXISTING_SUBPROCESS,
                Collections.emptyMap())).isInstanceOf(ProcessDefinitionNotFoundException.class);

        // case instance status should not have changed
        caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        CaseStatus afterStatus = CaseStatus.fromId(caseInstance.getStatus());
        assertThat(initialStatus).isEqualTo(afterStatus);
    }

    @Test
    public void testAddDynamicSubprocessToStageWithNotExistingProcessId() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_STAGE);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        assertThat(caseInstance.getCaseStages()).hasSize(1);
        CaseStatus initialStatus = CaseStatus.fromId(caseInstance.getStatus());

        String stageId = caseInstance.getCaseStages().iterator().next().getId();
        assertThat(stageId).isNotNull();

        assertThatThrownBy(() -> caseService.addDynamicSubprocessToStage(caseId, stageId, NOT_EXISTING_SUBPROCESS,
                Collections.emptyMap())).isInstanceOf(ProcessDefinitionNotFoundException.class);

        // case instance status should not have changed
        caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        CaseStatus afterStatus = CaseStatus.fromId(caseInstance.getStatus());
        assertThat(initialStatus).isEqualTo(afterStatus);
    }

    @Test
    public void testAddDynamicSubprocessToCaseByProcessInstanceId() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        Collection<ProcessInstanceDesc> caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId,
                new QueryContext());
        assertThat(caseProcessInstances).isNotNull().hasSize(1);

        ProcessInstanceDesc processInstance = caseProcessInstances.iterator().next();
        assertThat(processInstance).isNotNull();
        assertThat(processInstance.getCorrelationKey()).isEqualTo(FIRST_CASE_ID);

        Long subprocessId = caseService.addDynamicSubprocess(processInstance.getId(), SUBPROCESS, Collections.emptyMap());
        ProcessInstanceDesc subprocessInstance = runtimeDataService.getProcessInstanceById(subprocessId);
        assertThat(subprocessInstance).isNotNull();
        assertThat(subprocessInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);

        TaskSummary userTask = tasks.get(0);
        assertThat(userTask.getName()).isEqualTo(SUBPROCESS_TASK);
        userTaskService.completeAutoProgress(userTask.getId(), USER, null);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).isEmpty();

        subprocessInstance = runtimeDataService.getProcessInstanceById(subprocessId);
        assertThat(subprocessInstance).isNotNull();
        assertThat(subprocessInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testAddDynamicSubprocessToStageByProcessInstanceId() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_STAGE);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        Collection<ProcessInstanceDesc> caseProcessInstances = caseRuntimeDataService.getProcessInstancesForCase(caseId,
                new QueryContext());
        assertThat(caseProcessInstances).isNotNull().hasSize(1);

        ProcessInstanceDesc processInstance = caseProcessInstances.iterator().next();
        assertThat(processInstance).isNotNull();
        assertThat(processInstance.getCorrelationKey()).isEqualTo(FIRST_CASE_ID);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        assertThat(caseInstance.getCaseStages()).hasSize(1);

        String stageId = caseInstance.getCaseStages().iterator().next().getId();
        assertThat(stageId).isNotNull();

        Long subprocessId = caseService.addDynamicSubprocessToStage(processInstance.getId(), stageId, SUBPROCESS,
                Collections.emptyMap());
        ProcessInstanceDesc subprocessInstance = runtimeDataService.getProcessInstanceById(subprocessId);
        assertThat(subprocessInstance).isNotNull();
        assertThat(subprocessInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);

        TaskSummary userTask = tasks.get(0);
        assertThat(userTask.getName()).isEqualTo(SUBPROCESS_TASK);
        userTaskService.completeAutoProgress(userTask.getId(), USER, null);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).isEmpty();

        subprocessInstance = runtimeDataService.getProcessInstanceById(subprocessId);
        assertThat(subprocessInstance).isNotNull();
        assertThat(subprocessInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testAddMultipleDynamicSubprocessesToCase() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_P_ID);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        Long subprocessId1 = caseService.addDynamicSubprocess(caseId, SUBPROCESS, Collections.emptyMap());
        ProcessInstanceDesc subprocessInstance1 = runtimeDataService.getProcessInstanceById(subprocessId1);
        assertThat(subprocessInstance1).isNotNull();
        assertThat(subprocessInstance1.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        Long subprocessId2 = caseService.addDynamicSubprocess(caseId, SUBPROCESS, Collections.emptyMap());
        ProcessInstanceDesc subprocessInstance2 = runtimeDataService.getProcessInstanceById(subprocessId2);
        assertThat(subprocessInstance2).isNotNull();
        assertThat(subprocessInstance2.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(2);

        TaskSummary userTask1 = tasks.get(0);
        assertThat(userTask1.getName()).isEqualTo(SUBPROCESS_TASK);
        userTaskService.completeAutoProgress(userTask1.getId(), USER, null);

        TaskSummary userTask2 = tasks.get(1);
        assertThat(userTask2.getName()).isEqualTo(SUBPROCESS_TASK);
        userTaskService.completeAutoProgress(userTask2.getId(), USER, null);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).isEmpty();

        subprocessInstance1 = runtimeDataService.getProcessInstanceById(subprocessId1);
        assertThat(subprocessInstance1).isNotNull();
        assertThat(subprocessInstance1.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        subprocessInstance2 = runtimeDataService.getProcessInstanceById(subprocessId2);
        assertThat(subprocessInstance2).isNotNull();
        assertThat(subprocessInstance2.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testAddMultipleDynamicSubprocessesToStage() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), EMPTY_CASE_STAGE);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        assertThat(caseInstance.getCaseStages()).hasSize(1);

        String stageId = caseInstance.getCaseStages().iterator().next().getId();
        assertThat(stageId).isNotNull();

        Long subprocessId1 = caseService.addDynamicSubprocessToStage(caseId, stageId, SUBPROCESS, Collections.emptyMap());
        ProcessInstanceDesc subprocessInstance1 = runtimeDataService.getProcessInstanceById(subprocessId1);
        assertThat(subprocessInstance1).isNotNull();
        assertThat(subprocessInstance1.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        Long subprocessId2 = caseService.addDynamicSubprocessToStage(caseId, stageId, SUBPROCESS, Collections.emptyMap());
        ProcessInstanceDesc subprocessInstance2 = runtimeDataService.getProcessInstanceById(subprocessId2);
        assertThat(subprocessInstance2).isNotNull();
        assertThat(subprocessInstance2.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(2);

        TaskSummary userTask1 = tasks.get(0);
        assertThat(userTask1.getName()).isEqualTo(SUBPROCESS_TASK);
        userTaskService.completeAutoProgress(userTask1.getId(), USER, null);

        TaskSummary userTask2 = tasks.get(1);
        assertThat(userTask2.getName()).isEqualTo(SUBPROCESS_TASK);
        userTaskService.completeAutoProgress(userTask2.getId(), USER, null);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).isEmpty();

        subprocessInstance1 = runtimeDataService.getProcessInstanceById(subprocessId1);
        assertThat(subprocessInstance1).isNotNull();
        assertThat(subprocessInstance1.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);

        subprocessInstance2 = runtimeDataService.getProcessInstanceById(subprocessId2);
        assertThat(subprocessInstance2).isNotNull();
        assertThat(subprocessInstance2.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testAddDynamicSubprocessToNotActiveStage() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), TWO_STAGES_CASE_P_ID);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);

        CaseDefinition caseDefinition = caseRuntimeDataService.getCase(deploymentUnit.getIdentifier(),
                TWO_STAGES_CASE_P_ID);
        assertThat(caseDefinition).isNotNull();
        assertThat(caseDefinition.getCaseStages()).hasSize(2);

        Iterator<CaseStage> caseStageIterator = caseDefinition.getCaseStages().iterator();
        caseStageIterator.next();
        String stageId = caseStageIterator.next().getId();

        assertThatThrownBy(() -> caseService.addDynamicSubprocessToStage(caseId, stageId, SUBPROCESS,
                Collections.emptyMap())).isInstanceOf(StageNotFoundException.class);
    }

    private TaskSpecification createUserTask() {
        return caseService.newHumanTaskSpec(DYNAMIC_TASK, "", USER, null, Collections.emptyMap());
    }
}
