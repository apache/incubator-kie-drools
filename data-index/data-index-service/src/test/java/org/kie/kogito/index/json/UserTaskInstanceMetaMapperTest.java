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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.event.KogitoUserTaskCloudEvent;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.kogito.index.Constants.USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE;
import static org.kie.kogito.index.TestUtils.getUserTaskCloudEvent;

public class UserTaskInstanceMetaMapperTest {

    @Test
    public void testUserTaskInstanceMapper() {
        String taskId = UUID.randomUUID().toString();
        String processId = "travels";
        String rootProcessId = "root_travels";
        String processInstanceId = UUID.randomUUID().toString();
        String rootProcessInstanceId = UUID.randomUUID().toString();
        KogitoUserTaskCloudEvent event = getUserTaskCloudEvent(taskId, processId, processInstanceId, rootProcessInstanceId, rootProcessId, "InProgress");
        ObjectNode json = new UserTaskInstanceMetaMapper().apply(event);

        assertThat(json).isNotNull();
        assertThatJson(json.toString()).and(
                a -> a.node("id").isEqualTo(rootProcessInstanceId),
                a -> a.node("processId").isEqualTo(rootProcessId),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE).isArray().hasSize(1),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].id").isEqualTo(taskId),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].processInstanceId").isEqualTo(processInstanceId),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].state").isEqualTo(event.getData().getState()),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].description").isEqualTo(event.getData().getDescription()),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].name").isEqualTo(event.getData().getName()),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].priority").isEqualTo(event.getData().getPriority()),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].actualOwner").isEqualTo(event.getData().getActualOwner()),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].adminUsers[0]").isEqualTo(event.getData().getAdminUsers().stream().findFirst().get()),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].adminGroups[0]").isEqualTo(event.getData().getAdminGroups().stream().findFirst().get()),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].excludedUsers[0]").isEqualTo(event.getData().getExcludedUsers().stream().findFirst().get()),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].potentialGroups[0]").isEqualTo(event.getData().getPotentialGroups().stream().findFirst().get()),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].potentialUsers[0]").isEqualTo(event.getData().getPotentialUsers().stream().findFirst().get()),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].started").isEqualTo(event.getData().getStarted().toInstant().toEpochMilli()),
                a -> a.node(USER_TASK_INSTANCES_DOMAIN_ATTRIBUTE + "[0].completed").isEqualTo(event.getData().getCompleted().toInstant().toEpochMilli())
        );
    }
}
