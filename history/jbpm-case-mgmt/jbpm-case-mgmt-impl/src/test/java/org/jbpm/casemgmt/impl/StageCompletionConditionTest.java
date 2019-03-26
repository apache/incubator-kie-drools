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
import java.util.List;
import java.util.Map;

import org.jbpm.casemgmt.api.AdHocFragmentNotFoundException;
import org.jbpm.casemgmt.api.StageNotFoundException;
import org.jbpm.casemgmt.api.dynamic.TaskSpecification;
import org.jbpm.casemgmt.api.model.CaseDefinition;
import org.jbpm.casemgmt.api.model.CaseStage;
import org.jbpm.casemgmt.api.model.instance.CaseFileInstance;
import org.jbpm.casemgmt.api.model.instance.CaseInstance;
import org.jbpm.casemgmt.impl.model.instance.CaseInstanceImpl;
import org.jbpm.casemgmt.impl.util.AbstractCaseServicesBaseTest;
import org.junit.Test;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;

import static org.assertj.core.api.Assertions.*;

public class StageCompletionConditionTest extends AbstractCaseServicesBaseTest {

    private static final String STAGE_WITH_TASK_AUTOCOMPLETE = "StageWithTaskAutocomplete";
    private static final String STAGE_WITH_TASK_COUNT = "StageWithTaskCount";
    private static final String STAGE_WITH_TASK_NO_AUTOSTART = "StageWithTaskNoAutoStart";
    private static final String STAGE_WITH_TASK_PROCESS_VARIABLE = "StageWithTaskProcessVariable";
    private static final String STAGE_WITH_TASK_CASE_FILE_VARIABLE = "StageWithTaskCaseFileVariable";
    private static final String STAGE_WITH_TASK_CASE_FILE_VARIABLE_NO_PREFIX = "StageWithTaskCaseFileVariableNoPrefix";
    private static final String STAGE_WITH_TASK_CASE_DATA_BOOLEAN = "StageWithTaskCaseDataBoolean";
    private static final String STAGE_WITH_TASK_CASE_DATA_INTEGER = "StageWithTaskCaseDataInteger";
    private static final String STAGE_WITH_TASK_CASE_DATA_STRING = "StageWithTaskCaseDataString";
    private static final String STAGE_WITH_TASK_CASE_FILE_AND_PROCESS_VARIABLE = "StageWithTaskCaseFileAndProcessVariable";
    private static final String USER_TASK_PROCESS = "UserTask";

    private static final String INSIDE_TASK = "InsideTask";
    private static final String DYNAMIC_TASK = "DynamicTask";
    private static final String HELLO_TASK = "Hello";

    @Override
    protected List<String> getProcessDefinitionFiles() {
        List<String> processes = new ArrayList<>();
        processes.add("cases/stage/completion/StageWithTaskAutocomplete.bpmn2");
        processes.add("cases/stage/completion/StageWithTaskCount.bpmn2");
        processes.add("cases/stage/completion/StageWithTaskNoAutoStart.bpmn2");
        processes.add("cases/stage/completion/StageWithTaskProcessVariable.bpmn2");
        processes.add("cases/stage/completion/StageWithTaskCaseFileVariable.bpmn2");
        processes.add("cases/stage/completion/StageWithTaskCaseFileVariableNoPrefix.bpmn2");
        processes.add("cases/stage/completion/StageWithTaskCaseDataBoolean.bpmn2");
        processes.add("cases/stage/completion/StageWithTaskCaseDataInteger.bpmn2");
        processes.add("cases/stage/completion/StageWithTaskCaseDataString.bpmn2");
        processes.add("cases/stage/completion/StageWithTaskCaseFileAndProcessVariable.bpmn2");
        processes.add("processes/UserTaskProcess.bpmn2");
        return processes;
    }

    @Test
    public void testAutoComplete() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_AUTOCOMPLETE);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);

        userTaskService.completeAutoProgress(tasks.get(0).getId(), USER, null);
        assertCaseInstanceNotActive(caseId);
    }

    @Test
    public void testAutoCompleteNoAutoStartTask() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_NO_AUTOSTART);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).isEmpty();

        caseService.triggerAdHocFragment(caseId, INSIDE_TASK, null);
        assertCaseInstanceActive(caseId);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary insideTask = tasks.get(0);
        assertThat(insideTask.getName()).isEqualTo(INSIDE_TASK);

        userTaskService.completeAutoProgress(insideTask.getId(), USER, null);
        assertCaseInstanceNotActive(caseId);
    }

    @Test
    public void testAutoCompleteDynamicTask() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_AUTOCOMPLETE);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary insideTask = tasks.get(0);
        assertThat(insideTask.getName()).isEqualTo(INSIDE_TASK);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        assertThat(caseInstance.getCaseStages()).hasSize(1);
        String stageId = caseInstance.getCaseStages().iterator().next().getId();
        TaskSpecification dynamicTaskSpecification = caseService.newHumanTaskSpec(DYNAMIC_TASK, "", USER,
                null, Collections.emptyMap());
        caseService.addDynamicTaskToStage(caseId, stageId, dynamicTaskSpecification);

        userTaskService.completeAutoProgress(insideTask.getId(), USER, null);
        assertCaseInstanceActive(caseId);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary dynamicTask = tasks.get(0);
        assertThat(dynamicTask.getName()).isEqualTo(DYNAMIC_TASK);

        userTaskService.completeAutoProgress(dynamicTask.getId(), USER, null);
        assertCaseInstanceNotActive(caseId);
    }

    @Test
    public void testAutoCompleteDynamicSubProcess() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_AUTOCOMPLETE);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary insideTask = tasks.get(0);
        assertThat(insideTask.getName()).isEqualTo(INSIDE_TASK);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        assertThat(caseInstance.getCaseStages()).hasSize(1);
        String stageId = caseInstance.getCaseStages().iterator().next().getId();
        caseService.addDynamicSubprocessToStage(caseId, stageId, USER_TASK_PROCESS, Collections.emptyMap());

        userTaskService.completeAutoProgress(insideTask.getId(), USER, null);
        assertCaseInstanceActive(caseId);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary dynamicTask = tasks.get(0);
        assertThat(dynamicTask.getName()).isEqualTo(HELLO_TASK);

        userTaskService.completeAutoProgress(dynamicTask.getId(), USER, null);
        assertCaseInstanceNotActive(caseId);
    }

    @Test
    public void testNoActiveTasksCondition() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_COUNT);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId);
        assertThat(caseInstance).isNotNull();
        Long processInstanceId = ((CaseInstanceImpl) caseInstance).getProcessInstanceId();
        processService.setProcessVariable(processInstanceId, "taskCount", 0);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);

        userTaskService.completeAutoProgress(tasks.get(0).getId(), USER, null);
        assertCaseInstanceNotActive(caseId);
    }

    @Test
    public void testProcessVariable() {
        Map<String, Object> data = new HashMap<>();
        data.put("continue", false);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(),
                STAGE_WITH_TASK_PROCESS_VARIABLE, data, Collections.emptyMap());

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_PROCESS_VARIABLE, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId);
        assertThat(caseInstance).isNotNull();
        Long processInstanceId = ((CaseInstanceImpl) caseInstance).getProcessInstanceId();
        processService.setProcessVariable(processInstanceId, "continue", false);

        caseService.addDataToCaseFile(caseId, "continue", true);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary insideTask = tasks.get(0);
        assertThat(insideTask.getName()).isEqualTo(INSIDE_TASK);

        userTaskService.completeAutoProgress(insideTask.getId(), USER, null);
        assertCaseInstanceActive(caseId);

        processService.setProcessVariable(processInstanceId, "continue", true);

        caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        assertThat(caseInstance.getCaseStages()).hasSize(1);
        String stageId = caseInstance.getCaseStages().iterator().next().getId();
        TaskSpecification dynamicTaskSpecification = caseService.newHumanTaskSpec(DYNAMIC_TASK, "", USER,
                null, Collections.emptyMap());
        caseService.addDynamicTaskToStage(caseId, stageId, dynamicTaskSpecification);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary dynamicTask = tasks.get(0);
        assertThat(dynamicTask.getName()).isEqualTo(DYNAMIC_TASK);
        userTaskService.completeAutoProgress(dynamicTask.getId(), USER, null);
        assertCaseInstanceNotActive(caseId);
    }

    @Test
    public void testCaseFileVariable() {
        Map<String, Object> data = new HashMap<>();
        data.put("continue", false);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(),
                STAGE_WITH_TASK_CASE_FILE_VARIABLE, data, Collections.emptyMap());

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_CASE_FILE_VARIABLE, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary insideTask = tasks.get(0);
        assertThat(insideTask.getName()).isEqualTo(INSIDE_TASK);
        userTaskService.completeAutoProgress(insideTask.getId(), USER, null);
        assertCaseInstanceActive(caseId);

        caseService.addDataToCaseFile(caseId, "continue", true);
        assertCaseInstanceActive(caseId);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        assertThat(caseInstance.getCaseStages()).hasSize(1);
        String stageId = caseInstance.getCaseStages().iterator().next().getId();
        TaskSpecification dynamicTaskSpecification = caseService.newHumanTaskSpec(DYNAMIC_TASK, "", USER,
                null, Collections.emptyMap());
        caseService.addDynamicTaskToStage(caseId, stageId, dynamicTaskSpecification);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary dynamicTask = tasks.get(0);
        assertThat(dynamicTask.getName()).isEqualTo(DYNAMIC_TASK);
        userTaskService.completeAutoProgress(dynamicTask.getId(), USER, null);
        assertCaseInstanceNotActive(caseId);
    }

    @Test
    public void testCaseFileVariableWithoutPrefix() {
        Map<String, Object> data = new HashMap<>();
        data.put("continue", false);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(),
                STAGE_WITH_TASK_CASE_FILE_VARIABLE_NO_PREFIX, data, Collections.emptyMap());

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(),
                STAGE_WITH_TASK_CASE_FILE_VARIABLE_NO_PREFIX, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        caseService.addDataToCaseFile(caseId, "continue", true);
        assertCaseInstanceActive(caseId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary insideTask = tasks.get(0);
        assertThat(insideTask.getName()).isEqualTo(INSIDE_TASK);
        userTaskService.completeAutoProgress(insideTask.getId(), USER, null);
        assertCaseInstanceNotActive(caseId);
    }

    @Test
    public void testCaseFileVariableWithProcessVariable() {
        Map<String, Object> data = new HashMap<>();
        data.put("continue", false);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(),
                STAGE_WITH_TASK_CASE_FILE_AND_PROCESS_VARIABLE, data, Collections.emptyMap());

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_CASE_FILE_AND_PROCESS_VARIABLE, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        CaseInstance caseInstance = caseService.getCaseInstance(caseId);
        assertThat(caseInstance).isNotNull();
        Long processInstanceId = ((CaseInstanceImpl) caseInstance).getProcessInstanceId();
        processService.setProcessVariable(processInstanceId, "continue", true);
        assertCaseInstanceActive(caseId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary insideTask = tasks.get(0);
        assertThat(insideTask.getName()).isEqualTo(INSIDE_TASK);
        userTaskService.completeAutoProgress(insideTask.getId(), USER, null);
        assertCaseInstanceActive(caseId);

        caseService.addDataToCaseFile(caseId, "continue", true);
        assertCaseInstanceActive(caseId);

        caseInstance = caseService.getCaseInstance(caseId, false, false, false, true);
        assertThat(caseInstance).isNotNull();
        assertThat(caseInstance.getCaseStages()).hasSize(1);
        String stageId = caseInstance.getCaseStages().iterator().next().getId();
        TaskSpecification dynamicTaskSpecification = caseService.newHumanTaskSpec(DYNAMIC_TASK, "", USER,
                null, Collections.emptyMap());
        caseService.addDynamicTaskToStage(caseId, stageId, dynamicTaskSpecification);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary dynamicTask = tasks.get(0);
        assertThat(dynamicTask.getName()).isEqualTo(DYNAMIC_TASK);
        userTaskService.completeAutoProgress(dynamicTask.getId(), USER, null);
        assertCaseInstanceNotActive(caseId);
    }

    @Test
    public void testCaseDataVariableBoolean() {
        Map<String, Object> data = new HashMap<>();
        data.put("continue", false);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(),
                STAGE_WITH_TASK_CASE_DATA_BOOLEAN, data, Collections.emptyMap());

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_CASE_DATA_BOOLEAN, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        caseService.addDataToCaseFile(caseId, "continue", true);
        assertCaseInstanceNotActive(caseId);
    }

    @Test
    public void testCaseDataVariableInteger() {
        Map<String, Object> data = new HashMap<>();
        data.put("continue", 0);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(),
                STAGE_WITH_TASK_CASE_DATA_INTEGER, data, Collections.emptyMap());

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_CASE_DATA_INTEGER, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        caseService.addDataToCaseFile(caseId, "continue", 1);
        assertCaseInstanceNotActive(caseId);
    }

    @Test
    public void testCaseDataVariableString() {
        Map<String, Object> data = new HashMap<>();
        data.put("continue", "wait");
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(),
                STAGE_WITH_TASK_CASE_DATA_STRING, data, Collections.emptyMap());

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_CASE_DATA_STRING, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        caseService.addDataToCaseFile(caseId, "continue", "continue");
        assertCaseInstanceNotActive(caseId);
    }

    @Test
    public void testCaseDataVariableCompleteTask() {
        Map<String, Object> data = new HashMap<>();
        data.put("continue", false);
        CaseFileInstance caseFile = caseService.newCaseFileInstance(deploymentUnit.getIdentifier(),
                STAGE_WITH_TASK_CASE_DATA_BOOLEAN, data, Collections.emptyMap());

        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_CASE_DATA_BOOLEAN, caseFile);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary insideTask = tasks.get(0);
        assertThat(insideTask.getName()).isEqualTo(INSIDE_TASK);
        userTaskService.completeAutoProgress(insideTask.getId(), USER, null);
        assertCaseInstanceActive(caseId);

        caseService.addDataToCaseFile(caseId, "continue", true);
        assertCaseInstanceNotActive(caseId);
    }
    
    @Test
    public void testAutoCompleteNoAutoStartTaskAdHocInStage() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_NO_AUTOSTART);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).isEmpty();

        CaseDefinition caseDef = caseRuntimeDataService.getCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_NO_AUTOSTART);
        Collection<CaseStage> stages = caseDef.getCaseStages();
        assertThat(stages).hasSize(1);
        
        
        String stageId = stages.iterator().next().getId();
        caseService.triggerAdHocFragment(caseId, stageId, INSIDE_TASK, null);
        assertCaseInstanceActive(caseId);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(1);
        TaskSummary insideTask = tasks.get(0);
        assertThat(insideTask.getName()).isEqualTo(INSIDE_TASK);

        userTaskService.completeAutoProgress(insideTask.getId(), USER, null);
        assertCaseInstanceNotActive(caseId);
    }
    
    @Test
    public void testAutoCompleteNoAutoStartTaskAdHocInNotExistingStage() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_NO_AUTOSTART);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).isEmpty();

        String stageId = "not existing";
        
        assertThatExceptionOfType(StageNotFoundException.class).isThrownBy(() -> { 
            caseService.triggerAdHocFragment(caseId, stageId, INSIDE_TASK, null);})
        .withMessageContaining("No stage found with id " + stageId);
        
        
        assertCaseInstanceActive(caseId);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(0);
        caseService.cancelCase(caseId);
        assertCaseInstanceNotActive(caseId);
    }
    
    @Test
    public void testAutoCompleteNoAutoStartTaskNotExistingAdHocInStage() {
        String caseId = caseService.startCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_NO_AUTOSTART);
        assertThat(caseId).isNotNull().isEqualTo(FIRST_CASE_ID);
        assertCaseInstanceActive(caseId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).isEmpty();
        
        CaseDefinition caseDef = caseRuntimeDataService.getCase(deploymentUnit.getIdentifier(), STAGE_WITH_TASK_NO_AUTOSTART);
        Collection<CaseStage> stages = caseDef.getCaseStages();
        assertThat(stages).hasSize(1);        
        
        String stageId = stages.iterator().next().getId();
        
        assertThatExceptionOfType(AdHocFragmentNotFoundException.class).isThrownBy(() -> { 
            caseService.triggerAdHocFragment(caseId, stageId, "not existing", null);})
        .withMessageContaining("AdHoc fragment 'not existing' not found in case " + caseId + " and stage " + stageId);
        
        
        assertCaseInstanceActive(caseId);

        tasks = runtimeDataService.getTasksAssignedAsPotentialOwner(USER, new QueryFilter());
        assertThat(tasks).hasSize(0);
        caseService.cancelCase(caseId);
        assertCaseInstanceNotActive(caseId);
    }
}
