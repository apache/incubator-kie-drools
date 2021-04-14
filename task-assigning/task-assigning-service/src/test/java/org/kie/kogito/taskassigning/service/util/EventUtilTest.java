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

package org.kie.kogito.taskassigning.service.util;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.service.TaskAssigningServiceContext;
import org.kie.kogito.taskassigning.service.TaskData;
import org.kie.kogito.taskassigning.service.TestUtil;
import org.kie.kogito.taskassigning.service.event.DataEvent;
import org.kie.kogito.taskassigning.service.event.TaskDataEvent;
import org.kie.kogito.taskassigning.service.event.UserDataEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

class EventUtilTest {

    private static final String TASK_1 = "TASK_1";
    private static final String TASK_2 = "TASK_2";
    private static final String TASK_3 = "TASK_3";
    private static final String TASK_4 = "TASK_4";

    private static final TaskDataEvent TASK_1_EVENT_1 = mockTaskDataEvent(TASK_1, TestUtil.parseZonedDateTime("2021-03-10T08:00:00.001Z"));
    private static final TaskDataEvent TASK_1_EVENT_2 = mockTaskDataEvent(TASK_1, TestUtil.parseZonedDateTime("2021-03-10T08:00:00.002Z"));
    private static final TaskDataEvent TASK_1_EVENT_3 = mockTaskDataEvent(TASK_1, TestUtil.parseZonedDateTime("2021-03-10T08:00:00.003Z"));

    private static final TaskDataEvent TASK_2_EVENT_1 = mockTaskDataEvent(TASK_2, TestUtil.parseZonedDateTime("2021-03-11T08:00:00.001Z"));
    private static final TaskDataEvent TASK_2_EVENT_2 = mockTaskDataEvent(TASK_2, TestUtil.parseZonedDateTime("2021-03-11T08:00:00.002Z"));
    private static final TaskDataEvent TASK_2_EVENT_3 = mockTaskDataEvent(TASK_2, TestUtil.parseZonedDateTime("2021-03-11T08:00:00.003Z"));

    private static final TaskDataEvent TASK_3_EVENT_1 = mockTaskDataEvent(TASK_3, TestUtil.parseZonedDateTime("2021-03-12T08:00:00.001Z"));
    private static final TaskDataEvent TASK_3_EVENT_2 = mockTaskDataEvent(TASK_3, TestUtil.parseZonedDateTime("2021-03-12T08:00:00.002Z"));
    private static final TaskDataEvent TASK_3_EVENT_3 = mockTaskDataEvent(TASK_3, TestUtil.parseZonedDateTime("2021-03-12T08:00:00.003Z"));

    private static final TaskDataEvent TASK_4_EVENT_1 = mockTaskDataEvent(TASK_4, TestUtil.parseZonedDateTime("2021-03-12T08:00:00.001Z"));
    private static final TaskDataEvent TASK_4_EVENT_2 = mockTaskDataEvent(TASK_4, TestUtil.parseZonedDateTime("2021-03-12T08:00:00.002Z"));
    private static final TaskDataEvent TASK_4_EVENT_3 = mockTaskDataEvent(TASK_4, TestUtil.parseZonedDateTime("2021-03-12T08:00:00.003Z"));

    private static final UserDataEvent USER_DATA_EVENT_1 = mockUserDataEvent(TestUtil.parseZonedDateTime("2021-03-31T08:00:00.001Z"));
    private static final UserDataEvent USER_DATA_EVENT_2 = mockUserDataEvent(TestUtil.parseZonedDateTime("2021-03-31T08:00:00.002Z"));
    private static final UserDataEvent USER_DATA_EVENT_3 = mockUserDataEvent(TestUtil.parseZonedDateTime("2021-03-31T08:00:00.003Z"));

    @Test
    void filterNewestTaskEventsInContext() {
        TaskAssigningServiceContext context = new TaskAssigningServiceContext();
        context.setTaskLastEventTime(TASK_2, TASK_2_EVENT_1.getEventTime());
        context.setTaskLastEventTime(TASK_3, TASK_3_EVENT_2.getEventTime());
        context.setTaskLastEventTime(TASK_4, TASK_4_EVENT_3.getEventTime());
        List<DataEvent<?>> eventList = buildDataEvents();
        List<TaskDataEvent> result = EventUtil.filterNewestTaskEventsInContext(context, eventList);
        assertThat(result)
                .hasSize(3)
                .contains(TASK_1_EVENT_3, TASK_2_EVENT_3, TASK_3_EVENT_3);
        assertThat(context.getTaskLastEventTime(TASK_1)).isEqualTo(TASK_1_EVENT_3.getEventTime());
        assertThat(context.getTaskLastEventTime(TASK_2)).isEqualTo(TASK_2_EVENT_3.getEventTime());
        assertThat(context.getTaskLastEventTime(TASK_3)).isEqualTo(TASK_3_EVENT_3.getEventTime());
        assertThat(context.getTaskLastEventTime(TASK_4)).isEqualTo(TASK_4_EVENT_3.getEventTime());
    }

    @Test
    void filterNewestTaskEvents() {
        List<DataEvent<?>> eventList = buildDataEvents();
        List<TaskDataEvent> result = EventUtil.filterNewestTaskEvents(eventList);
        assertThat(result)
                .hasSize(4)
                .contains(TASK_1_EVENT_3, TASK_2_EVENT_3, TASK_3_EVENT_3, TASK_4_EVENT_3);
    }

    @Test
    void filterNewestUserEvent() {
        List<DataEvent<?>> eventList = buildDataEvents();
        UserDataEvent result = EventUtil.filterNewestUserEvent(eventList);
        assertThat(result)
                .isSameAs(USER_DATA_EVENT_3);
    }

    private static List<DataEvent<?>> buildDataEvents() {
        return Arrays.asList(
                TASK_4_EVENT_1,
                TASK_1_EVENT_1,
                TASK_2_EVENT_3,
                TASK_4_EVENT_2,
                TASK_1_EVENT_3,
                TASK_2_EVENT_1,
                USER_DATA_EVENT_1,
                TASK_3_EVENT_2,
                TASK_4_EVENT_3,
                TASK_2_EVENT_2,
                USER_DATA_EVENT_3,
                TASK_3_EVENT_1,
                TASK_1_EVENT_2,
                USER_DATA_EVENT_2,
                TASK_3_EVENT_3);
    }

    private static TaskDataEvent mockTaskDataEvent(String taskId, ZonedDateTime lastUpdate) {
        TaskData taskData = mock(TaskData.class);
        doReturn(taskId).when(taskData).getId();
        doReturn(lastUpdate).when(taskData).getLastUpdate();
        return new TaskDataEvent(taskData);
    }

    private static UserDataEvent mockUserDataEvent(ZonedDateTime eventTime) {
        return new UserDataEvent(Collections.emptyList(), eventTime);
    }
}
