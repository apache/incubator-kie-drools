/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.usertask;

import org.junit.jupiter.api.Test;
import org.kie.kogito.usertask.lifecycle.UserTaskState;
import org.kie.kogito.usertask.model.ProcessInfo;
import org.kie.kogito.usertask.view.UserTaskView;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTaskViewTest {

    @Test
    public void testUserTaskViewCreation() {
        UserTaskView info = new UserTaskView();
        info.setId("task-123");
        info.setUserTaskId("ut-456");
        info.setTaskName("hr_interview");
        info.setTaskDescription("Interview with HR");
        info.setTaskPriority("High");
        info.setStatus(UserTaskState.of("Reserved"));
        info.setActualOwner("recruiter");
        info.setProcessInfo(ProcessInfo.builder()
                .withProcessId("hiring")
                .withProcessInstanceId("pi-789")
                .withProcessVersion("1.0")
                .build());

        assertThat(info.getId()).isEqualTo("task-123");
        assertThat(info.getUserTaskId()).isEqualTo("ut-456");
        assertThat(info.getTaskName()).isEqualTo("hr_interview");
        assertThat(info.getTaskDescription()).isEqualTo("Interview with HR");
        assertThat(info.getTaskPriority()).isEqualTo("High");
        assertThat(info.getStatus().getName()).isEqualTo("Reserved");
        assertThat(info.getActualOwner()).isEqualTo("recruiter");
        assertThat(info.getProcessInfo().getProcessId()).isEqualTo("hiring");
        assertThat(info.getProcessInfo().getProcessInstanceId()).isEqualTo("pi-789");
        assertThat(info.getProcessInfo().getProcessVersion()).isEqualTo("1.0");
    }

    @Test
    public void testUserTaskViewWithNullValues() {
        UserTaskView info = new UserTaskView();
        info.setId("task-123");
        info.setUserTaskId("ut-456");
        info.setTaskName("hr_interview");
        info.setProcessInfo(ProcessInfo.builder()
                .withProcessId("hiring")
                .withProcessInstanceId("pi-789")
                .build());

        assertThat(info.getId()).isEqualTo("task-123");
        assertThat(info.getUserTaskId()).isEqualTo("ut-456");
        assertThat(info.getTaskName()).isEqualTo("hr_interview");
        assertThat(info.getTaskDescription()).isNull();
        assertThat(info.getTaskPriority()).isNull();
        assertThat(info.getStatus()).isNull();
        assertThat(info.getActualOwner()).isNull();
        assertThat(info.getProcessInfo().getProcessId()).isEqualTo("hiring");
        assertThat(info.getProcessInfo().getProcessInstanceId()).isEqualTo("pi-789");
        assertThat(info.getProcessInfo().getProcessVersion()).isNull();
    }

    @Test
    public void testUserTaskViewMinimalData() {
        UserTaskView info = new UserTaskView();
        info.setId("task-123");
        info.setTaskName("task");
        info.setProcessInfo(ProcessInfo.builder()
                .withProcessId("process")
                .withProcessInstanceId("instance")
                .build());

        assertThat(info.getId()).isEqualTo("task-123");
        assertThat(info.getTaskName()).isEqualTo("task");
        assertThat(info.getProcessInfo().getProcessId()).isEqualTo("process");
        assertThat(info.getProcessInfo().getProcessInstanceId()).isEqualTo("instance");
    }

    @Test
    public void testUserTaskViewEquality() {
        UserTaskView info1 = new UserTaskView();
        info1.setId("task-123");
        info1.setTaskName("hr_interview");
        info1.setProcessInfo(ProcessInfo.builder()
                .withProcessId("hiring")
                .build());

        UserTaskView info2 = new UserTaskView();
        info2.setId("task-123");
        info2.setTaskName("hr_interview");
        info2.setProcessInfo(ProcessInfo.builder()
                .withProcessId("hiring")
                .build());

        assertThat(info1.getId()).isEqualTo(info2.getId());
        assertThat(info1.getTaskName()).isEqualTo(info2.getTaskName());
        assertThat(info1.getProcessInfo().getProcessId()).isEqualTo(info2.getProcessInfo().getProcessId());
    }

    @Test
    public void testUserTaskViewDifferentIds() {
        UserTaskView info1 = new UserTaskView();
        info1.setId("task-123");
        info1.setTaskName("hr_interview");

        UserTaskView info2 = new UserTaskView();
        info2.setId("task-456");
        info2.setTaskName("hr_interview");

        assertThat(info1.getId()).isNotEqualTo(info2.getId());
        assertThat(info1.getTaskName()).isEqualTo(info2.getTaskName());
    }

    @Test
    public void testUserTaskViewStatusUpdate() {
        UserTaskView info = new UserTaskView();
        info.setId("task-123");
        info.setStatus(UserTaskState.of("Ready"));

        assertThat(info.getStatus().getName()).isEqualTo("Ready");

        info.setStatus(UserTaskState.of("Reserved"));
        assertThat(info.getStatus().getName()).isEqualTo("Reserved");

        info.setStatus(UserTaskState.of("InProgress"));
        assertThat(info.getStatus().getName()).isEqualTo("InProgress");
    }

    @Test
    public void testUserTaskViewOwnerUpdate() {
        UserTaskView info = new UserTaskView();
        info.setId("task-123");
        info.setActualOwner(null);

        assertThat(info.getActualOwner()).isNull();

        info.setActualOwner("user1");
        assertThat(info.getActualOwner()).isEqualTo("user1");

        info.setActualOwner("user2");
        assertThat(info.getActualOwner()).isEqualTo("user2");
    }
}
