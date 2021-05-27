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

package org.kie.kogito.taskassigning.service.event;

import java.time.ZonedDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.kie.kogito.taskassigning.service.TestUtil.parseZonedDateTime;

class SolutionUpdatedOnBackgroundDataEventTest {

    private static final long ID = 1;
    private static final ZonedDateTime EVENT_TIME = parseZonedDateTime("2021-05-19T10:00:00.001Z");

    private SolutionUpdatedOnBackgroundDataEvent event;

    @BeforeEach
    void setUp() {
        event = new SolutionUpdatedOnBackgroundDataEvent(ID, EVENT_TIME);
    }

    @Test
    void getData() {
        assertThat(event.getData()).isEqualTo(ID);
    }

    @Test
    void getEventTime() {
        assertThat(event.getEventTime()).isEqualTo(EVENT_TIME);
    }

    @Test
    void getDataEventType() {
        assertThat(event.getDataEventType()).isEqualTo(DataEvent.DataEventType.SOLUTION_UPDATED_ON_BACKGROUND_DATA_EVENT);
    }
}