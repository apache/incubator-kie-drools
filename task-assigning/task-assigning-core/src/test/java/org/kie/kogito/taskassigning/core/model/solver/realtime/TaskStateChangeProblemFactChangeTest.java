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

import org.kie.kogito.taskassigning.core.model.Task;
import org.kie.kogito.taskassigning.core.model.TaskAssignment;

import static org.assertj.core.api.Assertions.assertThat;

class TaskStateChangeProblemFactChangeTest extends AbstractTaskPropertyChangeProblemFactChangeTest<TaskStateChangeProblemFactChange> {

    private static final String CURRENT_STATE = "CURRENT_STATE";
    private static final String NEW_STATE = "NEW_STATE";

    @Override
    protected TaskStateChangeProblemFactChange createChange(TaskAssignment taskAssignment) {
        return new TaskStateChangeProblemFactChange(taskAssignment, NEW_STATE);
    }

    @Override
    protected Task createTask() {
        Task task = super.createTask();
        task.setState(CURRENT_STATE);
        return task;
    }

    @Override
    protected void verifyValuesWhereApplied() {
        assertThat(workingTaskAssignment.getTask().getState()).isEqualTo(NEW_STATE);
        assertThat(task.getState()).isEqualTo(CURRENT_STATE);
    }
}