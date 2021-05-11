/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.taskassigning.core.model.solver.realtime;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssigningSolution;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.optaplanner.core.api.score.director.ScoreDirector;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskInfoChangeProblemFactChangeTest {

    private static final String TASK_ID = "TASK_ID";
    private static final String TASK_NAME = "TASK_NAME";
    private static final String REFERENCE_NAME = "REFERENCE_NAME";
    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    private static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";
    private static final ZonedDateTime STARTED = ZonedDateTime.parse("2021-05-04T10:00:00+01:00");
    private static final String ENDPOINT = "ENDPOINT";

    private static final String TASK_INFO_TASK_ID = "TASK_INFO_TASK_ID";
    private static final String TASK_INFO_TASK_NAME = "TASK_INFO_TASK_NAME";
    private static final String TASK_INFO_REFERENCE_NAME = "TASK_INFO_REFERENCE_NAME";
    private static final String TASK_INFO_PROCESS_INSTANCE_ID = "TASK_INFO_PROCESS_INSTANCE_ID";
    private static final String TASK_INFO_PROCESS_ID = "TASK_INFO_PROCESS_ID";
    private static final String TASK_INFO_ROOT_PROCESS_INSTANCE_ID = "TASK_INFO_ROOT_PROCESS_INSTANCE_ID";
    private static final String TASK_INFO_ROOT_PROCESS_ID = "TASK_INFO_ROOT_PROCESS_ID";
    private static final ZonedDateTime TASK_INFO_STARTED = ZonedDateTime.parse("2021-05-04T11:00:00+01:00");
    private static final String TASK_INFO_ENDPOINT = "TASK_INFO_ENDPOINT";
    private static final String TASK_INFO_STATE = "TASK_INFO_STATE";
    private static final String TASK_INFO_DESCRIPTION = "TASK_INFO_DESCRIPTION";
    private static final String TASK_INFO_PRIORITY = "TASK_INFO_PRIORITY";
    private static final Set<String> TASK_INFO_POTENTIAL_USERS = new HashSet<>();
    private static final Set<String> TASK_INFO_POTENTIAL_GROUPS = new HashSet<>();
    private static final Set<String> TASK_INFO_ADMIN_USERS = new HashSet<>();
    private static final Set<String> TASK_INFO_ADMIN_GROUPS = new HashSet<>();
    private static final Set<String> TASK_INFO_EXCLUDED_USERS = new HashSet<>();
    private static final ZonedDateTime TASK_INFO_COMPLETED = ZonedDateTime.parse("2021-05-04T12:00:00+01:00");
    private static final ZonedDateTime TASK_INFO_LAST_UPDATE = ZonedDateTime.parse("2021-05-04T13:00:00+01:00");
    private static final Map<String, Object> TASK_INFO_INPUTS = new HashMap<>();
    private static final Map<String, Object> TASK_INFO_ATTRIBUTES = new HashMap<>();

    @Mock
    protected ScoreDirector<TaskAssigningSolution> scoreDirector;

    protected TaskAssignment workingTaskAssignment;

    protected TaskAssignment taskAssignment;

    protected Task task;

    protected Task taskInfo;

    protected TaskInfoChangeProblemFactChange change;

    @BeforeEach
    public void setUp() {
        task = Task.newBuilder()
                .id(TASK_ID)
                .name(TASK_NAME)
                .referenceName(REFERENCE_NAME)
                .processInstanceId(PROCESS_INSTANCE_ID)
                .processId(PROCESS_ID)
                .rootProcessInstanceId(ROOT_PROCESS_INSTANCE_ID)
                .rootProcessId(ROOT_PROCESS_ID)
                .started(STARTED)
                .endpoint(ENDPOINT)
                .build();
        taskInfo = Task.newBuilder()
                .id(TASK_INFO_TASK_ID)
                .name(TASK_INFO_TASK_NAME)
                .referenceName(TASK_INFO_REFERENCE_NAME)
                .description(TASK_INFO_DESCRIPTION)
                .state(TASK_INFO_STATE)
                .priority(TASK_INFO_PRIORITY)
                .processInstanceId(TASK_INFO_PROCESS_INSTANCE_ID)
                .processId(TASK_INFO_PROCESS_ID)
                .rootProcessInstanceId(TASK_INFO_ROOT_PROCESS_INSTANCE_ID)
                .rootProcessId(TASK_INFO_ROOT_PROCESS_ID)
                .potentialGroups(TASK_INFO_POTENTIAL_GROUPS)
                .potentialUsers(TASK_INFO_POTENTIAL_USERS)
                .adminGroups(TASK_INFO_ADMIN_GROUPS)
                .adminUsers(TASK_INFO_ADMIN_USERS)
                .excludedUsers(TASK_INFO_EXCLUDED_USERS)
                .started(TASK_INFO_STARTED)
                .completed(TASK_INFO_COMPLETED)
                .lastUpdate(TASK_INFO_LAST_UPDATE)
                .endpoint(TASK_INFO_ENDPOINT)
                .inputData(TASK_INFO_INPUTS)
                .attributes(TASK_INFO_ATTRIBUTES)
                .build();

        taskAssignment = new TaskAssignment(Task.newBuilder().build());
        workingTaskAssignment = new TaskAssignment(task);
        lenient().when(scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment)).thenReturn(workingTaskAssignment);
        change = new TaskInfoChangeProblemFactChange(taskAssignment, taskInfo);
    }

    @Test
    void getTaskAssignment() {
        assertThat(change.getTaskAssignment()).isEqualTo(taskAssignment);
    }

    @Test
    void getTaskInfo() {
        assertThat(change.getTaskInfo()).isEqualTo(taskInfo);
    }

    @Test
    void doChange() {
        change.doChange(scoreDirector);
        assertThat(task).isNotSameAs(workingTaskAssignment.getTask());
        verify(scoreDirector, times(1)).beforeProblemPropertyChanged(workingTaskAssignment);
        verify(scoreDirector, times(1)).afterProblemPropertyChanged(workingTaskAssignment);
        verify(scoreDirector).triggerVariableListeners();

        Task clonedTask = workingTaskAssignment.getTask();
        assertThat(clonedTask.getId()).isEqualTo(TASK_ID);
        assertThat(clonedTask.getName()).isEqualTo(TASK_NAME);
        assertThat(clonedTask.getReferenceName()).isEqualTo(REFERENCE_NAME);
        assertThat(clonedTask.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(clonedTask.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(clonedTask.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);
        assertThat(clonedTask.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(clonedTask.getStarted()).isEqualTo(STARTED);
        assertThat(clonedTask.getEndpoint()).isEqualTo(ENDPOINT);

        assertThat(clonedTask.getState()).isEqualTo(TASK_INFO_STATE);
        assertThat(clonedTask.getDescription()).isEqualTo(TASK_INFO_DESCRIPTION);
        assertThat(clonedTask.getPriority()).isEqualTo(TASK_INFO_PRIORITY);
        assertThat(clonedTask.getPotentialUsers()).isSameAs(TASK_INFO_POTENTIAL_USERS);
        assertThat(clonedTask.getPotentialGroups()).isSameAs(TASK_INFO_POTENTIAL_GROUPS);
        assertThat(clonedTask.getAdminUsers()).isSameAs(TASK_INFO_ADMIN_USERS);
        assertThat(clonedTask.getAdminGroups()).isSameAs(TASK_INFO_ADMIN_GROUPS);
        assertThat(clonedTask.getExcludedUsers()).isSameAs(TASK_INFO_EXCLUDED_USERS);
        assertThat(clonedTask.getCompleted()).isEqualTo(TASK_INFO_COMPLETED);
        assertThat(clonedTask.getLastUpdate()).isEqualTo(TASK_INFO_LAST_UPDATE);
        assertThat(clonedTask.getInputData()).isSameAs(TASK_INFO_INPUTS);
        assertThat(clonedTask.getAttributes()).isSameAs(TASK_INFO_ATTRIBUTES);
    }

    @Test
    void doChangeFailure() {
        when(scoreDirector.lookUpWorkingObjectOrReturnNull(taskAssignment)).thenReturn(null);
        Assertions.assertThatThrownBy(() -> change.doChange(scoreDirector))
                .hasMessageContaining("was not found in current working solution");
    }
}
