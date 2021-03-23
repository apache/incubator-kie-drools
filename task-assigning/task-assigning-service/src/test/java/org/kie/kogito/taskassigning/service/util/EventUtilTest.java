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
import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.taskassigning.service.TaskAssigningServiceContext;
import org.kie.kogito.taskassigning.service.TestUtil;
import org.kie.kogito.taskassigning.service.messaging.UserTaskEvent;

import static org.assertj.core.api.Assertions.assertThat;

class EventUtilTest {

    private static final String TASK_1 = "TASK_1";
    private static final String TASK_2 = "TASK_2";
    private static final String TASK_3 = "TASK_3";
    private static final String TASK_4 = "TASK_4";

    private static final UserTaskEvent TASK_1_EVENT_1 = mockEvent(TASK_1, TestUtil.parseZonedDateTime("2021-03-10T08:00:00.001Z"));
    private static final UserTaskEvent TASK_1_EVENT_2 = mockEvent(TASK_1, TestUtil.parseZonedDateTime("2021-03-10T08:00:00.002Z"));
    private static final UserTaskEvent TASK_1_EVENT_3 = mockEvent(TASK_1, TestUtil.parseZonedDateTime("2021-03-10T08:00:00.003Z"));

    private static final UserTaskEvent TASK_2_EVENT_1 = mockEvent(TASK_2, TestUtil.parseZonedDateTime("2021-03-11T08:00:00.001Z"));
    private static final UserTaskEvent TASK_2_EVENT_2 = mockEvent(TASK_2, TestUtil.parseZonedDateTime("2021-03-11T08:00:00.002Z"));
    private static final UserTaskEvent TASK_2_EVENT_3 = mockEvent(TASK_2, TestUtil.parseZonedDateTime("2021-03-11T08:00:00.003Z"));

    private static final UserTaskEvent TASK_3_EVENT_1 = mockEvent(TASK_3, TestUtil.parseZonedDateTime("2021-03-12T08:00:00.001Z"));
    private static final UserTaskEvent TASK_3_EVENT_2 = mockEvent(TASK_3, TestUtil.parseZonedDateTime("2021-03-12T08:00:00.002Z"));
    private static final UserTaskEvent TASK_3_EVENT_3 = mockEvent(TASK_3, TestUtil.parseZonedDateTime("2021-03-12T08:00:00.003Z"));

    private static final UserTaskEvent TASK_4_EVENT_1 = mockEvent(TASK_4, TestUtil.parseZonedDateTime("2021-03-12T08:00:00.001Z"));
    private static final UserTaskEvent TASK_4_EVENT_2 = mockEvent(TASK_4, TestUtil.parseZonedDateTime("2021-03-12T08:00:00.002Z"));
    private static final UserTaskEvent TASK_4_EVENT_3 = mockEvent(TASK_4, TestUtil.parseZonedDateTime("2021-03-12T08:00:00.003Z"));

    @Test
    void filterNewestTaskEventsInContext() {
        TaskAssigningServiceContext context = new TaskAssigningServiceContext();
        context.setTaskLastEventTime(TASK_2, TASK_2_EVENT_1.getLastUpdate());
        context.setTaskLastEventTime(TASK_3, TASK_3_EVENT_2.getLastUpdate());
        context.setTaskLastEventTime(TASK_4, TASK_4_EVENT_3.getLastUpdate());
        List<UserTaskEvent> eventList = buildUserTaskEvents();
        List<UserTaskEvent> result = EventUtil.filterNewestTaskEventsInContext(context, eventList);
        assertThat(result)
                .hasSize(3)
                .contains(TASK_1_EVENT_3, TASK_2_EVENT_3, TASK_3_EVENT_3);
        assertThat(context.getTaskLastEventTime(TASK_1)).isEqualTo(TASK_1_EVENT_3.getLastUpdate());
        assertThat(context.getTaskLastEventTime(TASK_2)).isEqualTo(TASK_2_EVENT_3.getLastUpdate());
        assertThat(context.getTaskLastEventTime(TASK_3)).isEqualTo(TASK_3_EVENT_3.getLastUpdate());
        assertThat(context.getTaskLastEventTime(TASK_4)).isEqualTo(TASK_4_EVENT_3.getLastUpdate());
    }

    @Test
    void filterNewestTaskEvents() {
        List<UserTaskEvent> eventList = buildUserTaskEvents();
        List<UserTaskEvent> result = EventUtil.filterNewestTaskEvents(eventList);
        assertThat(result)
                .hasSize(4)
                .contains(TASK_1_EVENT_3, TASK_2_EVENT_3, TASK_3_EVENT_3, TASK_4_EVENT_3);
    }

    private List<UserTaskEvent> buildUserTaskEvents() {
        return Arrays.asList(
                TASK_4_EVENT_1,
                TASK_1_EVENT_1,
                TASK_2_EVENT_3,
                TASK_4_EVENT_2,
                TASK_1_EVENT_3,
                TASK_2_EVENT_1,
                TASK_3_EVENT_2,
                TASK_4_EVENT_3,
                TASK_2_EVENT_2,
                TASK_3_EVENT_1,
                TASK_1_EVENT_2,
                TASK_3_EVENT_3);
    }

    private static UserTaskEvent mockEvent(String taskId, ZonedDateTime lastUpdate) {
        UserTaskEvent result = new UserTaskEvent();
        result.setTaskId(taskId);
        result.setLastUpdate(lastUpdate);
        return result;
    }
}
