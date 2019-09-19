/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.json;

import java.util.UUID;

import javax.json.JsonObject;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;

import static javax.json.Json.createArrayBuilder;
import static javax.json.Json.createValue;
import static org.kie.kogito.index.TestUtils.getUserTaskCloudEvent;

public class UserTaskInstanceMetaMapperTest {

    @Test
    public void testUserTaskInstanceMapper() {
        String taskId = UUID.randomUUID().toString();
        String processId = "travels";
        String rootProcessId = "root_travels";
        String processInstanceId = UUID.randomUUID().toString();
        String rootProcessInstanceId = UUID.randomUUID().toString();
        KogitoUserTaskCloudEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, rootProcessInstanceId, rootProcessId);
        JsonObject json = new UserTaskInstanceMetaMapper().apply(event);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(json)
                .isNotNull()
                .containsEntry("id", createValue(taskId))
                .containsEntry("processInstanceId", createValue(processInstanceId))
                .containsEntry("state", createValue(event.getData().getState()))
                .containsEntry("description", createValue(event.getData().getDescription()))
                .containsEntry("name", createValue(event.getData().getName()))
                .containsEntry("priority", createValue(event.getData().getPriority()))
                .containsEntry("actualOwner", createValue(event.getData().getActualOwner()))
                .containsEntry("adminUsers", createArrayBuilder(event.getData().getAdminUsers()).build())
                .containsEntry("adminGroups", createArrayBuilder(event.getData().getAdminGroups()).build())
                .containsEntry("excludedUsers", createArrayBuilder(event.getData().getExcludedUsers()).build())
                .containsEntry("potentialGroups", createArrayBuilder(event.getData().getPotentialGroups()).build())
                .containsEntry("potentialUsers", createArrayBuilder(event.getData().getPotentialUsers()).build())
                .containsEntry("started", createValue(event.getData().getStarted().toInstant().toEpochMilli()))
                .containsEntry("completed", createValue(event.getData().getCompleted().toInstant().toEpochMilli()));

        softly.assertAll();
    }
}
