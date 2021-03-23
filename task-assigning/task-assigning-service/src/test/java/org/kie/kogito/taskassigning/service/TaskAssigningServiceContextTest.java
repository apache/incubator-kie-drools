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

package org.kie.kogito.taskassigning.service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.taskassigning.service.TestUtil.parseZonedDateTime;

class TaskAssigningServiceContextTest {

    private static final String TASK_ID = "TASK_ID";
    private static final ZonedDateTime TASK_LAST_UPDATE = parseZonedDateTime("2021-03-11T10:00:00.001Z");

    private TaskAssigningServiceContext context;

    @BeforeEach
    void setUp() {
        context = new TaskAssigningServiceContext();
    }

    @Test
    void currentChangeSetId() {
        context.setCurrentChangeSetId(2);
        assertThat(context.getCurrentChangeSetId()).isEqualTo(2);
    }

    @Test
    void nextChangeSetId() {
        for (int i = 1; i < 10; i++) {
            assertThat(context.nextChangeSetId()).isEqualTo(i);
        }
    }

    @Test
    void isProcessedChangeSet() {
        context.setProcessedChangeSet(5);
        for (int i = 0; i <= 5; i++) {
            assertThat(context.isProcessedChangeSet(i)).isTrue();
        }
    }

    @Test
    void isCurrentChangeSetProcessed() {
        context.setCurrentChangeSetId(5);
        assertThat(context.isCurrentChangeSetProcessed()).isFalse();
        context.setProcessedChangeSet(5);
        assertThat(context.isCurrentChangeSetProcessed()).isTrue();
    }

    @Test
    void setTaskPublished() {
        setTaskPublished(TASK_ID, true);
        setTaskPublished(TASK_ID, false);
    }

    private void setTaskPublished(String taskId, boolean published) {
        context.setTaskPublished(taskId, published);
        assertThat(context.isTaskPublished(taskId)).isEqualTo(published);
    }

    @Test
    void setLastTaskEventTime() {
        context.setTaskLastEventTime(TASK_ID, TASK_LAST_UPDATE);
        assertThat(context.getTaskLastEventTime(TASK_ID)).isEqualTo(TASK_LAST_UPDATE);
    }

    @Test
    void isNewTaskEventTimeWhenNotSet() {
        assertThat(context.isNewTaskEventTime(TASK_ID, TASK_LAST_UPDATE)).isTrue();
    }

    @Test
    void isNewTaskEventTimeWhenLessThanSet() {
        context.setTaskLastEventTime(TASK_ID, TASK_LAST_UPDATE);
        assertThat(context.isNewTaskEventTime(TASK_ID, TASK_LAST_UPDATE.minus(1, ChronoUnit.MILLIS))).isFalse();
    }

    @Test
    void isNewTaskEventTimeWhenEqualThanSet() {
        context.setTaskLastEventTime(TASK_ID, TASK_LAST_UPDATE);
        assertThat(context.isNewTaskEventTime(TASK_ID, TASK_LAST_UPDATE)).isFalse();
    }

    @Test
    void isNewTaskEventTimeWhenGreaterThanSet() {
        context.setTaskLastEventTime(TASK_ID, TASK_LAST_UPDATE);
        assertThat(context.isNewTaskEventTime(TASK_ID, TASK_LAST_UPDATE.plus(1, ChronoUnit.MILLIS))).isTrue();
    }

}
