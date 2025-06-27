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
package org.kie.kogito.index.jpa.storage;

import java.net.URI;
import java.util.*;

import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.usertask.UserTaskInstanceAttachmentEventBody;
import org.kie.kogito.index.jpa.model.UserTaskInstanceEntity;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.index.storage.UserTaskInstanceStorage;
import org.kie.kogito.index.test.TestUtils;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

public abstract class AbstractUserTaskInstanceStorageIT {

    private static final String PROCESS_INSTANCE_ID = "87daz3446-2386-4056-91dc-dbc3804d157c";
    private static final String PROCESS_ID = "travels";
    private static final String TASK_NAME = "HR Interview";

    @Inject
    UserTaskInstanceStorage storage;

    @Inject
    EntityManager em;

    @Test
    @Transactional
    public void testUserTaskStateEvent() {
        String taskId = createUserTaskInstance();

        storage.indexState(TestUtils.createUserTaskStateEvent(taskId, TASK_NAME, PROCESS_INSTANCE_ID, PROCESS_ID, "Completed"));

        UserTaskInstance task = storage.get(taskId);

        Assertions.assertThat(task)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", taskId)
                .hasFieldOrPropertyWithValue("name", TASK_NAME)
                .hasFieldOrPropertyWithValue("processId", PROCESS_ID)
                .hasFieldOrPropertyWithValue("processInstanceId", PROCESS_INSTANCE_ID)
                .hasFieldOrPropertyWithValue("state", "Completed");
    }

    @Test
    @Transactional
    public void testUserTaskAssignmentEvents() {

        String taskId = createUserTaskInstance();

        UserTaskInstance task = storage.get(taskId);

        Assertions.assertThat(task)
                .hasFieldOrPropertyWithValue("potentialUsers", null)
                .hasFieldOrPropertyWithValue("potentialGroups", null)
                .hasFieldOrPropertyWithValue("adminGroups", null)
                .hasFieldOrPropertyWithValue("adminUsers", null)
                .hasFieldOrPropertyWithValue("excludedUsers", null);

        storage.indexAssignment(TestUtils.createUserTaskAssignmentEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, "USER_OWNERS", "John"));
        storage.indexAssignment(TestUtils.createUserTaskAssignmentEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, "USER_GROUPS", "user-group"));
        storage.indexAssignment(TestUtils.createUserTaskAssignmentEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, "ADMIN_GROUPS", "administrators"));
        storage.indexAssignment(TestUtils.createUserTaskAssignmentEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, "ADMIN_USERS", "super-user"));
        storage.indexAssignment(TestUtils.createUserTaskAssignmentEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, "USERS_EXCLUDED", "excluded-user"));

        task = storage.get(taskId);

        Assertions.assertThat(task.getPotentialUsers())
                .containsExactly("John");
        Assertions.assertThat(task.getPotentialGroups())
                .containsExactly("user-group");
        Assertions.assertThat(task.getAdminGroups())
                .containsExactly("administrators");
        Assertions.assertThat(task.getAdminUsers())
                .containsExactly("super-user");
        Assertions.assertThat(task.getExcludedUsers())
                .containsExactly("excluded-user");
    }

    @Transactional
    @Test
    public void testUserTaskVariableEvents() {
        String taskId = createUserTaskInstance();

        UserTaskInstance task = storage.get(taskId);

        Assertions.assertThat(task.getInputs())
                .isNull();
        Assertions.assertThat(task.getOutputs())
                .isNull();

        Map<String, Object> person = new HashMap<>();
        person.put("firstName", "John");
        person.put("lastName", "Doe");

        storage.indexVariable(TestUtils.createUserTaskVariableEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, "person", person, "INPUT"));

        task = storage.get(taskId);

        Assertions.assertThatObject(task.getInputs())
                .isNotNull()
                .extracting(jsonNodes -> jsonNodes.at("/person/firstName").asText(), jsonNodes -> jsonNodes.at("/person/lastName").asText())
                .contains("John", "Doe");

        Assertions.assertThat(task.getOutputs())
                .isNull();

        person = new HashMap<>();
        person.put("age", 50);
        person.put("married", true);

        storage.indexVariable(TestUtils.createUserTaskVariableEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, "person", person, "OUTPUT"));

        task = storage.get(taskId);

        Assertions.assertThatObject(task.getOutputs())
                .isNotNull()
                .extracting(jsonNodes -> jsonNodes.at("/person/age").asInt(), jsonNodes -> jsonNodes.at("/person/married").asBoolean())
                .contains(50, true);
    }

    @Transactional
    @Test
    public void testUserTaskCommentEvents() {
        String taskId = createUserTaskInstance();

        UserTaskInstance task = storage.get(taskId);

        Assertions.assertThat(task.getComments())
                .isEmpty();

        String commentId = UUID.randomUUID().toString();
        storage.indexComment(
                TestUtils.createUserTaskCommentEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, commentId, "this is a comment", "John", UserTaskInstanceAttachmentEventBody.EVENT_TYPE_ADDED));

        task = storage.get(taskId);

        Assertions.assertThat(task.getComments())
                .hasSize(1);

        Assertions.assertThat(task.getComments().get(0))
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", commentId)
                .hasFieldOrPropertyWithValue("content", "this is a comment")
                .hasFieldOrPropertyWithValue("updatedBy", "John");

        storage.indexComment(
                TestUtils.createUserTaskCommentEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, commentId, "this is an updated comment", "John", UserTaskInstanceAttachmentEventBody.EVENT_TYPE_CHANGE));

        task = storage.get(taskId);

        Assertions.assertThat(task.getComments())
                .hasSize(1);

        Assertions.assertThat(task.getComments().get(0))
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", commentId)
                .hasFieldOrPropertyWithValue("content", "this is an updated comment")
                .hasFieldOrPropertyWithValue("updatedBy", "John");

        storage.indexComment(
                TestUtils.createUserTaskCommentEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, commentId, "this is an updated comment", "John", UserTaskInstanceAttachmentEventBody.EVENT_TYPE_DELETED));

        task = storage.get(taskId);

        Assertions.assertThat(task.getComments())
                .hasSize(0);
    }

    @Transactional
    @Test
    public void testUserTaskAttachmentEvents() {
        String taskId = createUserTaskInstance();

        UserTaskInstance task = storage.get(taskId);

        Assertions.assertThat(task.getAttachments())
                .isEmpty();

        String attachmentId = UUID.randomUUID().toString();
        storage.indexAttachment(TestUtils.createUserTaskAttachmentEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, attachmentId, "Attachment-1", URI.create("http://localhost:8080/my-doc.txt"), "John",
                UserTaskInstanceAttachmentEventBody.EVENT_TYPE_ADDED));

        task = storage.get(taskId);

        Assertions.assertThat(task.getAttachments())
                .hasSize(1);

        Assertions.assertThat(task.getAttachments().get(0))
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", attachmentId)
                .hasFieldOrPropertyWithValue("name", "Attachment-1")
                .hasFieldOrPropertyWithValue("content", "http://localhost:8080/my-doc.txt")
                .hasFieldOrPropertyWithValue("updatedBy", "John");

        storage.indexAttachment(TestUtils.createUserTaskAttachmentEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, attachmentId, "Attachment-1.2", URI.create("http://localhost:8080/my-doc2.txt"),
                "John", UserTaskInstanceAttachmentEventBody.EVENT_TYPE_CHANGE));

        task = storage.get(taskId);

        Assertions.assertThat(task.getAttachments())
                .hasSize(1);

        Assertions.assertThat(task.getAttachments().get(0))
                .hasNoNullFieldsOrProperties()
                .hasFieldOrPropertyWithValue("id", attachmentId)
                .hasFieldOrPropertyWithValue("name", "Attachment-1.2")
                .hasFieldOrPropertyWithValue("content", "http://localhost:8080/my-doc2.txt")
                .hasFieldOrPropertyWithValue("updatedBy", "John");

        storage.indexAttachment(TestUtils.createUserTaskAttachmentEvent(taskId, PROCESS_INSTANCE_ID, PROCESS_ID, attachmentId, "Attachment-1", URI.create("http://localhost:8080/my-doc2.txt"), "John",
                UserTaskInstanceAttachmentEventBody.EVENT_TYPE_DELETED));

        task = storage.get(taskId);

        Assertions.assertThat(task.getAttachments())
                .hasSize(0);
    }

    @Test
    public void testUserTaskInstanceEntity() {
        String taskId = UUID.randomUUID().toString();
        String processInstanceId = UUID.randomUUID().toString();
        TestUtils
                .createUserTaskInstance(taskId, processInstanceId, RandomStringUtils.randomAlphabetic(5),
                        UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "InProgress", 0L);
        TestUtils
                .createUserTaskInstance(taskId, processInstanceId, RandomStringUtils.randomAlphabetic(5),
                        UUID.randomUUID().toString(),
                        RandomStringUtils.randomAlphabetic(10), "Completed", 1000L);
    }

    private String createUserTaskInstance() {
        String taskId = UUID.randomUUID().toString();

        storage.indexState(TestUtils.createUserTaskStateEvent(taskId, TASK_NAME, PROCESS_INSTANCE_ID, PROCESS_ID, "InProgress"));

        UserTaskInstance task = storage.get(taskId);

        Assertions.assertThat(task)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", taskId)
                .hasFieldOrPropertyWithValue("name", TASK_NAME)
                .hasFieldOrPropertyWithValue("processId", PROCESS_ID)
                .hasFieldOrPropertyWithValue("processInstanceId", PROCESS_INSTANCE_ID)
                .hasFieldOrPropertyWithValue("state", "InProgress");

        // Initializing comments and attachments just for the test
        UserTaskInstanceEntity taskInstanceEntity = em.find(UserTaskInstanceEntity.class, taskId);
        taskInstanceEntity.setComments(new ArrayList<>());
        taskInstanceEntity.setAttachments(new ArrayList<>());

        return taskId;
    }
}
