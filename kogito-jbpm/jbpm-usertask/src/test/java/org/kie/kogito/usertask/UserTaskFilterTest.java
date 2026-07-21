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

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UserTaskFilterTest {

    @Test
    public void testEmptyFilter() {
        UserTaskFilter filter = UserTaskFilter.builder().build();

        assertThat(filter.processId()).isNull();
        assertThat(filter.processInstanceId()).isNull();
        assertThat(filter.statuses()).isNull();
        assertThat(filter.taskName()).isNull();
    }

    @Test
    public void testFilterWithProcessId() {
        UserTaskFilter filter = UserTaskFilter.builder()
                .processId("hiring")
                .build();

        assertThat(filter.processId()).isEqualTo("hiring");
        assertThat(filter.processInstanceId()).isNull();
        assertThat(filter.statuses()).isNull();
        assertThat(filter.taskName()).isNull();
    }

    @Test
    public void testFilterWithProcessInstanceId() {
        UserTaskFilter filter = UserTaskFilter.builder()
                .processInstanceId("12345")
                .build();

        assertThat(filter.processId()).isNull();
        assertThat(filter.processInstanceId()).isEqualTo("12345");
        assertThat(filter.statuses()).isNull();
        assertThat(filter.taskName()).isNull();
    }

    @Test
    public void testFilterWithStatuses() {
        UserTaskFilter filter = UserTaskFilter.builder()
                .statuses(List.of("Reserved", "InProgress"))
                .build();

        assertThat(filter.processId()).isNull();
        assertThat(filter.processInstanceId()).isNull();
        assertThat(filter.statuses()).containsExactly("Reserved", "InProgress");
        assertThat(filter.taskName()).isNull();
    }

    @Test
    public void testFilterWithTaskName() {
        UserTaskFilter filter = UserTaskFilter.builder()
                .taskName("hr_interview")
                .build();

        assertThat(filter.processId()).isNull();
        assertThat(filter.processInstanceId()).isNull();
        assertThat(filter.statuses()).isNull();
        assertThat(filter.taskName()).isEqualTo("hr_interview");
    }

    @Test
    public void testFilterWithAllFields() {
        UserTaskFilter filter = UserTaskFilter.builder()
                .processId("hiring")
                .processInstanceId("12345")
                .statuses(List.of("Reserved"))
                .taskName("hr_interview")
                .build();

        assertThat(filter.processId()).isEqualTo("hiring");
        assertThat(filter.processInstanceId()).isEqualTo("12345");
        assertThat(filter.statuses()).hasSize(1);
        assertThat(filter.statuses().get(0)).isEqualTo("Reserved");
        assertThat(filter.taskName()).isEqualTo("hr_interview");
    }

    @Test
    public void testBuilderChaining() {
        UserTaskFilter filter = UserTaskFilter.builder()
                .processId("hiring")
                .taskName("hr_interview")
                .statuses(List.of("Reserved"))
                .build();

        assertThat(filter.processId()).isEqualTo("hiring");
        assertThat(filter.taskName()).isEqualTo("hr_interview");
        assertThat(filter.statuses()).hasSize(1);
        assertThat(filter.statuses().get(0)).isEqualTo("Reserved");
    }

    @Test
    public void testBuilderReuse() {
        UserTaskFilter.Builder builder = UserTaskFilter.builder()
                .processId("hiring");

        UserTaskFilter filter1 = builder.taskName("hr_interview").build();
        UserTaskFilter filter2 = builder.taskName("it_interview").build();

        assertThat(filter1.processId()).isEqualTo("hiring");
        assertThat(filter1.taskName()).isEqualTo("hr_interview");

        assertThat(filter2.processId()).isEqualTo("hiring");
        assertThat(filter2.taskName()).isEqualTo("it_interview");
    }

    // Edge Case Tests

    @Test
    public void testFilterWithNullValues() {
        UserTaskFilter filter = UserTaskFilter.builder()
                .processId(null)
                .processInstanceId(null)
                .statuses(null)
                .taskName(null)
                .build();

        assertThat(filter.processId()).isNull();
        assertThat(filter.processInstanceId()).isNull();
        assertThat(filter.statuses()).isNull();
        assertThat(filter.taskName()).isNull();
    }

    @Test
    public void testFilterWithEmptyStrings() {
        UserTaskFilter filter = UserTaskFilter.builder()
                .processId("")
                .processInstanceId("")
                .taskName("")
                .build();

        assertThat(filter.processId()).isEmpty();
        assertThat(filter.processInstanceId()).isEmpty();
        assertThat(filter.taskName()).isEmpty();
    }

    @Test
    public void testFilterWithSpecialCharacters() {
        UserTaskFilter filter = UserTaskFilter.builder()
                .processId("process-with-dashes_and_underscores")
                .taskName("Task with spaces & special chars!@#")
                .build();

        assertThat(filter.processId()).isEqualTo("process-with-dashes_and_underscores");
        assertThat(filter.taskName()).isEqualTo("Task with spaces & special chars!@#");
    }

    @Test
    public void testFilterWithVeryLongValues() {
        String longValue = "a".repeat(1000);
        UserTaskFilter filter = UserTaskFilter.builder()
                .processId(longValue)
                .taskName(longValue)
                .build();

        assertThat(filter.processId()).hasSize(1000);
        assertThat(filter.taskName()).hasSize(1000);
    }

}
