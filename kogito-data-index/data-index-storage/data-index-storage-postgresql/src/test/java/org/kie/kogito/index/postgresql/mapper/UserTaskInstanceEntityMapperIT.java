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

package org.kie.kogito.index.postgresql.mapper;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.postgresql.model.UserTaskInstanceEntity;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.test.junit.QuarkusTest;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class UserTaskInstanceEntityMapperIT {

    UserTaskInstance userTaskInstance = new UserTaskInstance();
    UserTaskInstanceEntity userTaskInstanceEntity = new UserTaskInstanceEntity();

    @Inject
    ObjectMapper jsonMapper;

    @Inject
    UserTaskInstanceEntityMapper mapper;

    @BeforeEach
    void setup() {
        String testId = "testId";
        String description = "testDescription";
        String name = "testName";
        String priority = "10";
        String processInstanceId = "testProcessInstanceId";
        String state = "testState";
        String actualOwner = "testActualOwner";
        Set<String> adminGroups = singleton("testAdminGroups");
        Set<String> adminUsers = singleton("testAdminUsers");
        ZonedDateTime time = ZonedDateTime.now();
        Set<String> excludedUsers = singleton("testExcludedUsers");
        Set<String> potentialGroups = singleton("testPotentialGroups");
        Set<String> potentialUsers = singleton("testPotentialUsers");
        String referenceName = "testReferenceName";
        String processId = "testProcessId";
        String rootProcessId = "testRootProcessId";
        String rootProcessInstanceId = "testRootProcessInstanceId";
        Map<String, String> object = new HashMap<>();
        object.put("test", "testValue");
        ObjectNode inputs = jsonMapper.createObjectNode().put("testInput", "testValue");
        ObjectNode outputs = jsonMapper.createObjectNode().put("testOutput", "testValue");

        userTaskInstance.setId(testId);
        userTaskInstance.setDescription(description);
        userTaskInstance.setName(name);
        userTaskInstance.setPriority(priority);
        userTaskInstance.setProcessInstanceId(processInstanceId);
        userTaskInstance.setState(state);
        userTaskInstance.setActualOwner(actualOwner);
        userTaskInstance.setAdminGroups(adminGroups);
        userTaskInstance.setAdminUsers(adminUsers);
        userTaskInstance.setCompleted(time);
        userTaskInstance.setStarted(time);
        userTaskInstance.setExcludedUsers(excludedUsers);
        userTaskInstance.setPotentialGroups(potentialGroups);
        userTaskInstance.setPotentialUsers(potentialUsers);
        userTaskInstance.setReferenceName(referenceName);
        userTaskInstance.setLastUpdate(time);
        userTaskInstance.setProcessId(processId);
        userTaskInstance.setRootProcessId(rootProcessId);
        userTaskInstance.setRootProcessInstanceId(rootProcessInstanceId);
        userTaskInstance.setInputs(inputs);
        userTaskInstance.setOutputs(outputs);

        userTaskInstanceEntity.setId(testId);
        userTaskInstanceEntity.setDescription(description);
        userTaskInstanceEntity.setName(name);
        userTaskInstanceEntity.setPriority(priority);
        userTaskInstanceEntity.setProcessInstanceId(processInstanceId);
        userTaskInstanceEntity.setState(state);
        userTaskInstanceEntity.setActualOwner(actualOwner);
        userTaskInstanceEntity.setAdminGroups(adminGroups);
        userTaskInstanceEntity.setAdminUsers(adminUsers);
        userTaskInstanceEntity.setCompleted(time);
        userTaskInstanceEntity.setStarted(time);
        userTaskInstanceEntity.setExcludedUsers(excludedUsers);
        userTaskInstanceEntity.setPotentialGroups(potentialGroups);
        userTaskInstanceEntity.setPotentialUsers(potentialUsers);
        userTaskInstanceEntity.setReferenceName(referenceName);
        userTaskInstanceEntity.setLastUpdate(time);
        userTaskInstanceEntity.setProcessId(processId);
        userTaskInstanceEntity.setRootProcessId(rootProcessId);
        userTaskInstanceEntity.setRootProcessInstanceId(rootProcessInstanceId);
        userTaskInstanceEntity.setInputs(inputs);
        userTaskInstanceEntity.setOutputs(outputs);
    }

    @Test
    void testMapToEntity() {
        UserTaskInstanceEntity result = mapper.mapToEntity(userTaskInstance);
        assertThat(result).isEqualToIgnoringGivenFields(userTaskInstanceEntity, "$$_hibernate_tracker");
    }

    @Test
    void testMapToModel() {
        UserTaskInstance result = mapper.mapToModel(userTaskInstanceEntity);
        assertThat(result).isEqualToComparingFieldByField(userTaskInstance);
    }
}
