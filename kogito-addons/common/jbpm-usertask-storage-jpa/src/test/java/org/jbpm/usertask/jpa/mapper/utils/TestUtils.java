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

package org.jbpm.usertask.jpa.mapper.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.assertj.core.api.Assertions;
import org.jbpm.usertask.jpa.mapper.models.Person;
import org.jbpm.usertask.jpa.model.AttachmentEntity;
import org.jbpm.usertask.jpa.model.CommentEntity;
import org.jbpm.usertask.jpa.model.TaskNamedDataEntity;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.lifecycle.UserTaskState;
import org.kie.kogito.usertask.model.Attachment;
import org.kie.kogito.usertask.model.Comment;

public class TestUtils {

    private TestUtils() {
    }

    public static void assertUserTaskEntityData(UserTaskInstanceEntity userTaskInstanceEntity, UserTaskInstance userTaskInstance) {
        Assertions.assertThat(userTaskInstanceEntity)
                .hasFieldOrPropertyWithValue("id", userTaskInstance.getId())
                .hasFieldOrPropertyWithValue("userTaskId", userTaskInstance.getUserTaskId())
                .hasFieldOrPropertyWithValue("taskName", userTaskInstance.getTaskName())
                .hasFieldOrPropertyWithValue("taskDescription", userTaskInstance.getTaskDescription())
                .hasFieldOrPropertyWithValue("taskPriority", userTaskInstance.getTaskPriority())
                .hasFieldOrPropertyWithValue("status", userTaskInstance.getStatus().getName())
                .hasFieldOrPropertyWithValue("terminationType", userTaskInstance.getStatus().getTerminate().toString())
                .hasFieldOrPropertyWithValue("externalReferenceId", userTaskInstance.getExternalReferenceId())
                .hasFieldOrPropertyWithValue("actualOwner", userTaskInstance.getActualOwner());
    }

    public static void assertUserTaskInstanceData(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskInstanceEntity) {
        UserTaskState.TerminationType terminationType =
                Objects.isNull(userTaskInstanceEntity.getTerminationType()) ? null : UserTaskState.TerminationType.valueOf(userTaskInstanceEntity.getTerminationType());
        UserTaskState state = UserTaskState.of(userTaskInstanceEntity.getStatus(), terminationType);

        Assertions.assertThat(userTaskInstance)
                .hasFieldOrPropertyWithValue("id", userTaskInstanceEntity.getId())
                .hasFieldOrPropertyWithValue("userTaskId", userTaskInstanceEntity.getUserTaskId())
                .hasFieldOrPropertyWithValue("taskName", userTaskInstanceEntity.getTaskName())
                .hasFieldOrPropertyWithValue("taskDescription", userTaskInstanceEntity.getTaskDescription())
                .hasFieldOrPropertyWithValue("taskPriority", userTaskInstanceEntity.getTaskPriority())
                .hasFieldOrPropertyWithValue("status", state)
                .hasFieldOrPropertyWithValue("externalReferenceId", userTaskInstanceEntity.getExternalReferenceId())
                .hasFieldOrPropertyWithValue("actualOwner", userTaskInstanceEntity.getActualOwner());
    }

    public static void assertUserTaskEntityPotentialUserAndGroups(UserTaskInstanceEntity userTaskEntity, UserTaskInstance userTaskInstance) {
        assertUserOrGroupsAssignments(userTaskEntity.getPotentialUsers(), userTaskInstance.getPotentialUsers());
        assertUserOrGroupsAssignments(userTaskEntity.getPotentialGroups(), userTaskInstance.getPotentialGroups());
    }

    public static void assertUserTaskEntityAdminUserAndGroups(UserTaskInstanceEntity userTaskEntity, UserTaskInstance userTaskInstance) {
        assertUserOrGroupsAssignments(userTaskEntity.getAdminUsers(), userTaskInstance.getAdminUsers());
        assertUserOrGroupsAssignments(userTaskEntity.getAdminGroups(), userTaskInstance.getAdminGroups());
    }

    public static void assertUserTaskEntityExcludedUsers(UserTaskInstanceEntity userTaskEntity, UserTaskInstance userTaskInstance) {
        assertUserOrGroupsAssignments(userTaskEntity.getExcludedUsers(), userTaskInstance.getExcludedUsers());
    }

    public static void assertUserTaskInstancePotentialUserAndGroups(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskEntity) {
        assertUserOrGroupsAssignments(userTaskInstance.getPotentialUsers(), userTaskEntity.getPotentialUsers());
        assertUserOrGroupsAssignments(userTaskInstance.getPotentialGroups(), userTaskEntity.getPotentialGroups());
    }

    public static void assertUserTaskInstanceAdminUserAndGroups(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskEntity) {
        assertUserOrGroupsAssignments(userTaskInstance.getAdminUsers(), userTaskEntity.getAdminUsers());
        assertUserOrGroupsAssignments(userTaskInstance.getAdminGroups(), userTaskEntity.getAdminGroups());
    }

    public static void assertUserTaskInstanceExcludedUsers(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskEntity) {
        assertUserOrGroupsAssignments(userTaskInstance.getExcludedUsers(), userTaskEntity.getExcludedUsers());
    }

    private static void assertUserOrGroupsAssignments(Collection<String> entityAssignments, Collection<String> instanceAssignments) {
        Assertions.assertThat(entityAssignments)
                .hasSize(instanceAssignments.size())
                .containsExactlyInAnyOrder(instanceAssignments.toArray(new String[0]));
    }

    public static void assertUserTaskEntityComments(Collection<CommentEntity> entityComments, Collection<Comment> instanceComments) {
        Assertions.assertThat(entityComments)
                .hasSize(instanceComments.size());

        entityComments.forEach(entityComment -> {
            Optional<Comment> optional = instanceComments.stream()
                    .filter(instanceComment -> instanceComment.getId().equals(entityComment.getId()))
                    .findFirst();

            Assertions.assertThat(optional)
                    .isPresent();

            Comment instanceComment = optional.get();

            Assertions.assertThat(entityComment)
                    .hasFieldOrPropertyWithValue("id", instanceComment.getId())
                    .hasFieldOrPropertyWithValue("comment", instanceComment.getContent())
                    .hasFieldOrPropertyWithValue("updatedBy", instanceComment.getUpdatedBy())
                    .matches(entity -> entity.getUpdatedAt().getTime() == instanceComment.getUpdatedAt().getTime());
        });
    }

    public static void assertUserTaskInstanceComments(Collection<Comment> instanceComments, Collection<CommentEntity> entityComments) {
        Assertions.assertThat(instanceComments)
                .hasSize(instanceComments.size());

        instanceComments.forEach(instanceComment -> {
            Optional<CommentEntity> optional = entityComments.stream()
                    .filter(entityComment -> entityComment.getId().equals(instanceComment.getId()))
                    .findFirst();

            Assertions.assertThat(optional)
                    .isPresent();

            CommentEntity entityComment = optional.get();

            Assertions.assertThat(instanceComment)
                    .hasFieldOrPropertyWithValue("id", entityComment.getId())
                    .hasFieldOrPropertyWithValue("content", entityComment.getComment())
                    .hasFieldOrPropertyWithValue("updatedBy", entityComment.getUpdatedBy())
                    .matches(entity -> entity.getUpdatedAt().getTime() == entityComment.getUpdatedAt().getTime());
        });
    }

    public static void assertUserTaskEntityAttachments(Collection<AttachmentEntity> entityAttachments, Collection<Attachment> instanceAttachments) {
        Assertions.assertThat(entityAttachments)
                .hasSize(instanceAttachments.size());

        entityAttachments.forEach(entityAttachment -> {
            Optional<Attachment> optional = instanceAttachments.stream()
                    .filter(instanceAttachment -> instanceAttachment.getId().equals(entityAttachment.getId()))
                    .findFirst();

            Assertions.assertThat(optional)
                    .isPresent();

            Attachment instanceAttachment = optional.get();

            Assertions.assertThat(entityAttachment)
                    .hasFieldOrPropertyWithValue("id", instanceAttachment.getId())
                    .hasFieldOrPropertyWithValue("name", instanceAttachment.getName())
                    .hasFieldOrPropertyWithValue("updatedBy", instanceAttachment.getUpdatedBy())
                    .matches(entity -> entity.getUpdatedAt().getTime() == instanceAttachment.getUpdatedAt().getTime())
                    .hasFieldOrPropertyWithValue("url", instanceAttachment.getContent().toString());
        });
    }

    public static void assertUserTaskInstanceAttachments(Collection<Attachment> instanceAttachments, Collection<AttachmentEntity> entityAttachments) {
        Assertions.assertThat(instanceAttachments)
                .hasSize(instanceAttachments.size());

        instanceAttachments.forEach(instanceAttachment -> {
            Optional<AttachmentEntity> optional = entityAttachments.stream()
                    .filter(entityAttachment -> entityAttachment.getId().equals(instanceAttachment.getId()))
                    .findFirst();

            Assertions.assertThat(optional)
                    .isPresent();

            AttachmentEntity entityAttachment = optional.get();

            Assertions.assertThat(instanceAttachment)
                    .hasFieldOrPropertyWithValue("id", entityAttachment.getId())
                    .hasFieldOrPropertyWithValue("name", entityAttachment.getName())
                    .hasFieldOrPropertyWithValue("updatedBy", entityAttachment.getUpdatedBy())
                    .matches(entity -> entity.getUpdatedAt().getTime() == entityAttachment.getUpdatedAt().getTime())
                    .matches(entity -> entity.getContent().toString().equals(entityAttachment.getUrl()));
        });
    }

    public static void assertUserTaskEntityInputs(UserTaskInstanceEntity userTaskInstanceEntity, UserTaskInstance userTaskInstance) {
        assertUserTaskEntityMapData(userTaskInstanceEntity.getInputs(), userTaskInstance.getInputs());
    }

    public static void assertUserTaskEntityOutputs(UserTaskInstanceEntity userTaskInstanceEntity, UserTaskInstance userTaskInstance) {
        assertUserTaskEntityMapData(userTaskInstanceEntity.getOutputs(), userTaskInstance.getOutputs());
    }

    public static void assertUserTaskEntityMetadata(UserTaskInstanceEntity userTaskInstanceEntity, UserTaskInstance userTaskInstance) {
        assertUserTaskEntityMapData(userTaskInstanceEntity.getMetadata(), userTaskInstance.getMetadata());
    }

    private static void assertUserTaskEntityMapData(Collection<? extends TaskNamedDataEntity> entityData, Map<String, Object> instanceData) {
        Assertions.assertThat(entityData.size())
                .isEqualTo(instanceData.size());

        entityData.stream().forEach(entity -> {
            Object value = instanceData.get(entity.getName());
            Assertions.assertThat(entity)
                    .isNotNull()
                    .matches(e -> instanceData.containsKey(e.getName()))
                    .matches(e -> Objects.isNull(e.getValue()) ? Objects.isNull(value) : e.getJavaType().equals(value.getClass().getName()));
        });
    }

    public static void assertUserTaskInstanceInputs(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskInstanceEntity) {
        assertUserTaskInstanceMapData(userTaskInstance.getInputs(), userTaskInstanceEntity.getInputs());
    }

    public static void assertUserTaskInstanceOutputs(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskInstanceEntity) {
        assertUserTaskInstanceMapData(userTaskInstance.getOutputs(), userTaskInstanceEntity.getOutputs());
    }

    public static void assertUserTaskInstanceMetadata(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskInstanceEntity) {
        assertUserTaskInstanceMapData(userTaskInstance.getMetadata(), userTaskInstanceEntity.getMetadata());
    }

    private static void assertUserTaskInstanceMapData(Map<String, Object> instanceData, Collection<? extends TaskNamedDataEntity> entityData) {
        Assertions.assertThat(instanceData.size())
                .isEqualTo(entityData.size());

        instanceData.forEach((key, value) -> {
            Optional<? extends TaskNamedDataEntity> optional = entityData.stream().filter(data -> data.getName().equals(key)).findFirst();

            Assertions.assertThat(optional)
                    .isPresent();

            if (Objects.nonNull(value)) {
                TaskNamedDataEntity data = optional.get();
                Assertions.assertThat(value.getClass().getName())
                        .isEqualTo(data.getJavaType());
            }
        });
    }

    public static DefaultUserTaskInstance createUserTaskInstance() {
        DefaultUserTaskInstance instance = new DefaultUserTaskInstance();
        instance.setId(UUID.randomUUID().toString());
        instance.setUserTaskId("user-task-id");
        instance.setTaskName("test-task");
        instance.setTaskDescription("this is a test task description");
        instance.setTaskPriority("1");
        instance.setStatus(UserTaskState.of("Complete", UserTaskState.TerminationType.COMPLETED));

        instance.setActualOwner("Homer");
        instance.setPotentialUsers(Set.of("Bart", "Liza"));
        instance.setPotentialGroups(Set.of("Simpson", "Family"));
        instance.setAdminUsers(Set.of("Seymour"));
        instance.setAdminGroups(Set.of("Administrators", "Managers"));
        instance.setExcludedUsers(Set.of("Ned", "Bart"));

        instance.setNotStartedDeadlines(new ArrayList<>());
        instance.setNotStartedDeadlinesTimers(new HashMap<>());
        instance.setNotCompletedDeadlines(new ArrayList<>());
        instance.setNotCompletedDeadlinesTimers(new HashMap<>());

        instance.setNotStartedReassignments(new ArrayList<>());
        instance.setNotStartedReassignmentsTimers(new HashMap<>());
        instance.setNotCompletedReassignments(new ArrayList<>());
        instance.setNotCompletedReassignmentsTimers(new HashMap<>());

        instance.setExternalReferenceId("external-reference-id");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("ProcessId", "process-id");
        metadata.put("ProcessType", "BPMN");
        metadata.put("ProcessVersion", "1.0.0");
        metadata.put("boolean", true);
        metadata.put("integer", 0);
        metadata.put("null", 0);

        instance.setMetadata(metadata);

        instance.setInput("in_string", "hello this is a string");
        instance.setInput("in_integer", 1);
        instance.setInput("in_long", 1000L);
        instance.setInput("in_float", 1.02f);
        instance.setInput("in_boolean", true);
        instance.setInput("in_date", new Date());
        instance.setInput("in_person", new Person("Ned", "Stark", 50));
        instance.setInput("in_null", null);

        instance.setOutput("out_string", "hello this is an output string");
        instance.setOutput("out_integer", 12);
        instance.setOutput("out_long", 2000L);
        instance.setOutput("out_float", 3.5f);
        instance.setOutput("out_boolean", false);
        instance.setOutput("out_date", new Date());
        instance.setOutput("out_person", new Person("Jon", "Snow", 17));
        instance.setOutput("out_null", null);

        return instance;
    }

    public static UserTaskInstanceEntity createUserTaskInstanceEntity() {
        UserTaskInstanceEntity instance = new UserTaskInstanceEntity();

        instance.setId(UUID.randomUUID().toString());
        instance.setUserTaskId("user-task-id");
        instance.setTaskName("test-task");
        instance.setTaskDescription("this is a test task description");
        instance.setTaskPriority("1");
        instance.setStatus("Complete");
        instance.setTerminationType(UserTaskState.TerminationType.COMPLETED.name());

        instance.setActualOwner("Homer");
        instance.setPotentialUsers(Set.of("Bart"));
        instance.setPotentialGroups(Set.of("Simpson", "Family"));
        instance.setAdminUsers(Set.of("Seymour"));
        instance.setAdminGroups(Set.of("Administrators", "Managers"));
        instance.setExcludedUsers(Set.of("Ned"));

        instance.setExternalReferenceId("external-reference-id");

        return instance;
    }
}
