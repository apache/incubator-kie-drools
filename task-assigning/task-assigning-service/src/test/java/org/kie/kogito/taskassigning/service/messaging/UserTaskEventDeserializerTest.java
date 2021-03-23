/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.taskassigning.service.messaging;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.kie.kogito.taskassigning.service.TestUtil.parseZonedDateTime;

class UserTaskEventDeserializerTest {

    private static final String TOPIC = "TOPIC";
    private static final String TASK_INSTANCE_ID = "TASK_INSTANCE_ID";
    private static final String TASK_NAME = "TASK_NAME";
    private static final String TASK_DESCRIPTION = "TASK_DESCRIPTION";
    private static final String TASK_PRIORITY = "0";
    private static final String REFERENCE_NAME = "REFERENCE_NAME";

    private static final String START_DATE = "2020-12-10T15:50:15.917+01:00";
    private static final String COMPLETED_DATE = "2020-12-11T15:50:15.917+01:00";
    private static final String LAST_UPDATE = "2020-12-11T15:50:15.917+01:00";
    private static final String EVENT_TIME = "2020-12-11T15:50:15.917635+01:00";
    private static final String STATE = "STATE";
    private static final String ACTUAL_OWNER = "ACTUAL_OWNER";
    private static final List<String> POTENTIAL_USERS = Arrays.asList("POTENTIAL_USER_1", "POTENTIAL_USER_2");
    private static final List<String> POTENTIAL_GROUPS = Arrays.asList("POTENTIAL_GROUP_1", "POTENTIAL_GROUP_2");
    private static final List<String> EXCLUDED_USERS = Arrays.asList("EXCLUDED_USER_1", "EXCLUDED_USER_2");
    private static final List<String> ADMIN_USERS = Arrays.asList("ADMIN_USER_1", "ADMIN_USER_2");
    private static final List<String> ADMIN_GROUPS = Arrays.asList("ADMIN_GROUP_1", "ADMIN_GROUP_2");

    private static final String INPUT_1 = "INPUT_1";
    private static final String INPUT_2 = "INPUT_2";
    private static final String INPUT_VALUE_1 = "INPUT_VALUE_1";
    private static final String INPUT_VALUE_2 = "INPUT_VALUE_2";

    private static final String PROCESS_INSTANCE_ID = "PROCESS_INSTANCE_ID";
    private static final String ROOT_PROCESS_INSTANCE_ID = "ROOT_PROCESS_INSTANCE_ID";
    private static final String PROCESS_ID = "PROCESS_ID";
    private static final String ROOT_PROCESS_ID = "ROOT_PROCESS_ID";

    private static final String ENDPOINT = "http://myapplication.cloud.com:8280/" + PROCESS_ID + "/" + PROCESS_INSTANCE_ID + "/" + TASK_NAME + "/" + TASK_INSTANCE_ID;

    private UserTaskEventDeserializer deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new UserTaskEventDeserializer();
    }

    @Test
    void deserialize() throws Exception {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource("org/kie/kogito/taskassigning/messaging/UserTaskCloudEvent.json").toURI());
        byte[] eventBytes = Files.readAllBytes(path);
        UserTaskEvent event = deserializer.deserialize(TOPIC, eventBytes);

        assertThat(event).isNotNull();
        assertThat(event.getTaskId()).isEqualTo(TASK_INSTANCE_ID);
        assertThat(event.getName()).isEqualTo(TASK_NAME);
        assertThat(event.getDescription()).isEqualTo(TASK_DESCRIPTION);
        assertThat(event.getPriority()).isEqualTo(TASK_PRIORITY);
        assertThat(event.getReferenceName()).isEqualTo(REFERENCE_NAME);

        assertThat(event.getEventTime()).isEqualTo(parseZonedDateTime(EVENT_TIME));
        assertThat(event.getStarted()).isEqualTo(parseZonedDateTime(START_DATE));
        assertThat(event.getCompleted()).isEqualTo(parseZonedDateTime(COMPLETED_DATE));
        assertThat(event.getLastUpdate()).isEqualTo(LAST_UPDATE);
        assertThat(event.getState()).isEqualTo(STATE);
        assertThat(event.getActualOwner()).isEqualTo(ACTUAL_OWNER);
        assertThat(event.getPotentialUsers()).isEqualTo(POTENTIAL_USERS);
        assertThat(event.getPotentialGroups()).isEqualTo(POTENTIAL_GROUPS);
        assertThat(event.getExcludedUsers()).isEqualTo(EXCLUDED_USERS);
        assertThat(event.getAdminUsers()).isEqualTo(ADMIN_USERS);
        assertThat(event.getAdminGroups()).isEqualTo(ADMIN_GROUPS);

        assertThat(event.getInputs().size()).isEqualTo(2);
        assertHasValue(event.getInputs(), INPUT_1, INPUT_VALUE_1);
        assertHasValue(event.getInputs(), INPUT_2, INPUT_VALUE_2);

        assertThat(event.getProcessId()).isEqualTo(PROCESS_ID);
        assertThat(event.getProcessInstanceId()).isEqualTo(PROCESS_INSTANCE_ID);
        assertThat(event.getRootProcessId()).isEqualTo(ROOT_PROCESS_ID);
        assertThat(event.getRootProcessInstanceId()).isEqualTo(ROOT_PROCESS_INSTANCE_ID);

        assertThat(event.getEndpoint()).isEqualTo(ENDPOINT);
    }

    @Test
    void deserializeNull() {
        assertThat(deserializer.deserialize(TOPIC, null)).isNull();
    }

    @Test
    void deserializeError() {
        assertThatThrownBy(() -> deserializer.deserialize(TOPIC, "wrong json".getBytes()))
                .hasMessageStartingWith("An error was produced during UserTaskEventMessage deserialization");
    }

    private static void assertHasValue(JsonNode node, String name, String value) {
        JsonNode valueNode = node.get(name);
        assertThat(valueNode).isNotNull();
        assertThat(valueNode.asText()).isEqualTo(value);
    }
}
