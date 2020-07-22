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

package org.kie.kogito.index.mongodb.model;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.model.UserTaskInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MAPPER;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.jsonNodeToDocument;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.zonedDateTimeToInstant;

class UserTaskInstanceEntityMapperTest {

    UserTaskInstanceEntityMapper userTaskInstanceEntityMapper = new UserTaskInstanceEntityMapper();

    static UserTaskInstance userTaskInstance;

    static UserTaskInstanceEntity userTaskInstanceEntity;

    @BeforeAll
    static void setup() {
        String testId = "testId";
        String description = "testDescription";
        String name = "testName";
        String priority = "10";
        String processInstanceId = "testProcessInstanceId";
        String state = "testState";
        String actualOwner = "testActualOwner";
        Set<String> adminGroups = Set.of("testAdminGroups");
        Set<String> adminUsers = Set.of("testAdminUsers");
        ZonedDateTime time = ZonedDateTime.now();
        Set<String> excludedUsers = Set.of("testExcludedUsers");
        Set<String> potentialGroups = Set.of("testPotentialGroups");
        Set<String> potentialUsers = Set.of("testPotentialUsers");
        String referenceName = "testReferenceName";
        String processId = "testProcessId";
        String rootProcessId = "testRootProcessId";
        String rootProcessInstanceId = "testRootProcessInstanceId";
        Map<String, String> object = new HashMap<>();
        object.put("test", "testValue");
        JsonNode inputs = MAPPER.valueToTree(object);
        JsonNode outputs = MAPPER.valueToTree(object);

        userTaskInstance = new UserTaskInstance();
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

        userTaskInstanceEntity = new UserTaskInstanceEntity();
        userTaskInstanceEntity.setId(testId);
        userTaskInstanceEntity.setDescription(description);
        userTaskInstanceEntity.setName(name);
        userTaskInstanceEntity.setPriority(priority);
        userTaskInstanceEntity.setProcessInstanceId(processInstanceId);
        userTaskInstanceEntity.setState(state);
        userTaskInstanceEntity.setActualOwner(actualOwner);
        userTaskInstanceEntity.setAdminGroups(adminGroups);
        userTaskInstanceEntity.setAdminUsers(adminUsers);
        userTaskInstanceEntity.setCompleted(zonedDateTimeToInstant(time));
        userTaskInstanceEntity.setStarted(zonedDateTimeToInstant(time));
        userTaskInstanceEntity.setExcludedUsers(excludedUsers);
        userTaskInstanceEntity.setPotentialGroups(potentialGroups);
        userTaskInstanceEntity.setPotentialUsers(potentialUsers);
        userTaskInstanceEntity.setReferenceName(referenceName);
        userTaskInstanceEntity.setLastUpdate(zonedDateTimeToInstant(time));
        userTaskInstanceEntity.setProcessId(processId);
        userTaskInstanceEntity.setRootProcessId(rootProcessId);
        userTaskInstanceEntity.setRootProcessInstanceId(rootProcessInstanceId);
        userTaskInstanceEntity.setInputs(jsonNodeToDocument(inputs));
        userTaskInstanceEntity.setOutputs(jsonNodeToDocument(outputs));
    }

    @Test
    void testGetEntityClass() {
        assertEquals(UserTaskInstanceEntity.class, userTaskInstanceEntityMapper.getEntityClass());
    }

    @Test
    void testMapToEntity() {
        UserTaskInstanceEntity result = userTaskInstanceEntityMapper.mapToEntity(userTaskInstance.getId(), userTaskInstance);
        assertEquals(userTaskInstanceEntity, result);
    }

    @Test
    void testMapToModel() {
        UserTaskInstance result = userTaskInstanceEntityMapper.mapToModel(userTaskInstanceEntity);
        assertEquals(userTaskInstance, result);
    }
}