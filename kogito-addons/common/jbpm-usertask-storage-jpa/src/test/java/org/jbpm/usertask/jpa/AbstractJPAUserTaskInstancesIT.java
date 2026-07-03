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
package org.jbpm.usertask.jpa;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.AttachmentRepository;
import org.jbpm.usertask.jpa.repository.CommentRepository;
import org.jbpm.usertask.jpa.repository.UserTaskInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.model.Attachment;
import org.kie.kogito.usertask.model.Comment;
import org.mockito.Mockito;

import jakarta.persistence.EntityExistsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.assertUserTaskEntityAdminUserAndGroups;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.assertUserTaskEntityAttachments;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.assertUserTaskEntityComments;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.assertUserTaskEntityData;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.assertUserTaskEntityExcludedUsers;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.assertUserTaskEntityInputs;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.assertUserTaskEntityMetadata;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.assertUserTaskEntityOutputs;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.assertUserTaskEntityPotentialUserAndGroups;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.createActiveUserTaskInstance;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.createCompletedUserTaskInstance;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.createReservedUserTaskInstance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public abstract class AbstractJPAUserTaskInstancesIT {

    protected final JPAUserTaskInstances userTaskInstances;
    protected final UserTaskInstanceRepository userTaskInstanceRepository;
    protected final AttachmentRepository attachmentRepository;
    protected final CommentRepository commentRepository;

    private Function<UserTaskInstance, UserTaskInstance> connect;
    private Function<UserTaskInstance, UserTaskInstance> disconnect;

    protected AbstractJPAUserTaskInstancesIT(JPAUserTaskInstances userTaskInstances,
            UserTaskInstanceRepository userTaskInstanceRepository,
            AttachmentRepository attachmentRepository,
            CommentRepository commentRepository) {
        this.userTaskInstances = userTaskInstances;
        this.userTaskInstanceRepository = userTaskInstanceRepository;
        this.attachmentRepository = attachmentRepository;
        this.commentRepository = commentRepository;
    }

    @BeforeEach
    public void init() {
        connect = Mockito.mock(Function.class);
        disconnect = Mockito.mock(Function.class);

        when(connect.apply(any(UserTaskInstance.class))).thenAnswer(i -> i.getArgument(0));
        when(disconnect.apply(any(UserTaskInstance.class))).thenAnswer(i -> i.getArgument(0));

        userTaskInstances.setReconnectUserTaskInstance(connect);
        userTaskInstances.setDisconnectUserTaskInstance(disconnect);
    }

    @Test
    public void testCreateUserTask() {
        UserTaskInstance instance = createCompletedUserTaskInstance();

        assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();

        assertThat(userTaskInstances.findById(instance.getId()))
                .isNotNull()
                .isEmpty();

        userTaskInstances.create(instance);

        verify(connect, times(1)).apply(any(UserTaskInstance.class));

        Optional<UserTaskInstanceEntity> entityOptional = userTaskInstanceRepository.findById(instance.getId());

        assertThat(entityOptional)
                .isNotNull()
                .isPresent();

        UserTaskInstanceEntity entity = entityOptional.get();

        assertEntityAndInstance(entity, instance);

        Optional<UserTaskInstance> persistedInstanceOptional = userTaskInstances.findById(instance.getId());

        assertThat(persistedInstanceOptional)
                .isNotNull()
                .isPresent();

        assertEntityAndInstance(entity, persistedInstanceOptional.get());
    }

    @Test
    public void testCreateExistingTask() {
        UserTaskInstance instance = getReservedUserTaskInstance();

        Assertions.assertThatThrownBy(() -> userTaskInstances.create(instance))
                .satisfiesAnyOf(
                        // Quarkus throws EntityExistsException directly
                        ex -> Assertions.assertThat(ex).isInstanceOf(EntityExistsException.class),
                        // Spring Boot wraps it in DataIntegrityViolationException with ConstraintViolationException cause
                        ex -> Assertions.assertThat(ex.getClass().getName()).contains("DataIntegrityViolationException"));
    }

    @Test
    public void testEditTaskInputOutputs() {

        UserTaskInstance instance = getReservedUserTaskInstance();

        Optional<UserTaskInstanceEntity> entityOptional = userTaskInstanceRepository.findById(instance.getId());

        assertThat(entityOptional)
                .isNotNull()
                .isPresent();

        UserTaskInstanceEntity entity = entityOptional.get();

        assertThat(entity.getInputs())
                .hasSize(instance.getInputs().size());

        assertThat(entity.getOutputs())
                .hasSize(instance.getOutputs().size());
        instance.getInputs().clear();
        instance.setInput("new_input", "this is a new input");

        instance.getOutputs().clear();
        instance.setOutput("new_output", "this is a new output");

        userTaskInstances.update(instance);

        entity = userTaskInstanceRepository.findById(instance.getId()).get();

        assertThat(entity.getInputs())
                .hasSize(1);

        assertThat(entity.getOutputs())
                .hasSize(1);

        assertUserTaskEntityInputs(entity, instance);
        assertUserTaskEntityOutputs(entity, instance);

        userTaskInstances.remove(instance);

        assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByActualOwner() {

        UserTaskInstance instance = getReservedUserTaskInstance();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer", "Group"));

        assertThat(result)
                .hasSize(1);

        verify(connect, times(2)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(instance);

        assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByPotentialUsers() {
        UserTaskInstance reservedTaskInstance = getReservedUserTaskInstance();
        UserTaskInstance activeUserTaskInstance = getActiveUserTaskInstance();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Liza"));

        assertThat(result)
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", activeUserTaskInstance.getId());

        verify(connect, times(3)).apply(any(UserTaskInstance.class));

        result = userTaskInstances.findByIdentity(IdentityProviders.of("Maggie", "Simpson"));

        assertThat(result)
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", activeUserTaskInstance.getId());

        verify(connect, times(4)).apply(any(UserTaskInstance.class));

        result = userTaskInstances.findByIdentity(IdentityProviders.of("Liza", "Group"));

        assertThat(result)
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", activeUserTaskInstance.getId());

        verify(connect, times(5)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(activeUserTaskInstance);
        userTaskInstances.remove(reservedTaskInstance);

        assertThat(userTaskInstances.exists(activeUserTaskInstance.getId()))
                .isFalse();
        assertThat(userTaskInstances.exists(reservedTaskInstance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByPotentialGroups() {
        UserTaskInstance reservedTaskInstance = getReservedUserTaskInstance();
        UserTaskInstance activeUserTaskInstance = getActiveUserTaskInstance();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Abraham", "Admin", "Simpson"));

        assertThat(result)
                .hasSize(1)
                .element(0)
                .hasFieldOrPropertyWithValue("id", activeUserTaskInstance.getId());

        verify(connect, times(3)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(activeUserTaskInstance);
        userTaskInstances.remove(reservedTaskInstance);

        assertThat(userTaskInstances.exists(activeUserTaskInstance.getId()))
                .isFalse();
        assertThat(userTaskInstances.exists(reservedTaskInstance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByAdminUsers() {
        UserTaskInstance instance = getReservedUserTaskInstance();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Seymour", "Group"));

        assertThat(result)
                .hasSize(1);

        verify(connect, times(2)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(instance);

        assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByAdminGroups() {
        UserTaskInstance instance = getReservedUserTaskInstance();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Abraham"));

        assertThat(result)
                .hasSize(0);

        verify(connect, times(1)).apply(any(UserTaskInstance.class));

        result = userTaskInstances.findByIdentity(IdentityProviders.of("Abraham", "Administrators", "IT"));

        assertThat(result)
                .hasSize(1);

        verify(connect, times(2)).apply(any(UserTaskInstance.class));

        result = userTaskInstances.findByIdentity(IdentityProviders.of("Abraham", "Managers", "IT"));

        assertThat(result)
                .hasSize(1);

        verify(connect, times(3)).apply(any(UserTaskInstance.class));

        assertThat(result)
                .hasSize(1);

        result = userTaskInstances.findByIdentity(IdentityProviders.of("Abraham", "Administrators", "Managers", "IT"));

        assertThat(result)
                .hasSize(1);

        verify(connect, times(4)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(instance);

        assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByExcludedUser() {
        UserTaskInstance instance = getActiveUserTaskInstance();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Ned"));

        assertThat(result)
                .hasSize(0);

        verify(connect, times(1)).apply(any(UserTaskInstance.class));

        result = userTaskInstances.findByIdentity(IdentityProviders.of("Bart"));

        assertThat(result)
                .hasSize(0);

        verify(connect, times(1)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(instance);

        assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByUnknownUser() {
        UserTaskInstance instance = getActiveUserTaskInstance();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Someone", "Group"));

        assertThat(result)
                .hasSize(0);

        verify(connect, times(1)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(instance);

        assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testAttachments() throws URISyntaxException {
        UserTaskInstance instance = getReservedUserTaskInstance();

        Optional<UserTaskInstanceEntity> entityOptional = userTaskInstanceRepository.findById(instance.getId());

        assertThat(entityOptional)
                .isNotNull()
                .isPresent();

        assertEntityAndInstance(entityOptional.get(), instance);

        Attachment attachment = new Attachment("1", "Admin");
        attachment.setName("attachment 1");
        attachment.setContent(new URI("http://url.com/to/my/attachment"));
        attachment.setUpdatedAt(new Date());

        instance.addAttachment(attachment);

        entityOptional = userTaskInstanceRepository.findById(instance.getId());

        assertUserTaskEntityAttachments(entityOptional.get().getAttachments(), instance.getAttachments());

        Attachment attachment2 = new Attachment("2", "Admin");
        attachment2.setName("attachment 2");
        attachment2.setContent(new URI("http://url.com/to/my/attachment2"));
        attachment2.setUpdatedAt(new Date());

        instance.addAttachment(attachment2);

        entityOptional = userTaskInstanceRepository.findById(instance.getId());
        assertUserTaskEntityAttachments(entityOptional.get().getAttachments(), instance.getAttachments());

        instance.removeAttachment(attachment);
        instance.removeAttachment(attachment2);

        userTaskInstances.update(instance);

        entityOptional = userTaskInstanceRepository.findById(instance.getId());

        assertThat(entityOptional.get().getAttachments())
                .isEmpty();

        assertThat(attachmentRepository.findAll())
                .isEmpty();

        userTaskInstances.remove(instance);

        assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testComments() {
        UserTaskInstance instance = getReservedUserTaskInstance();

        Optional<UserTaskInstanceEntity> entityOptional = userTaskInstanceRepository.findById(instance.getId());

        assertThat(entityOptional)
                .isNotNull()
                .isPresent();

        assertEntityAndInstance(entityOptional.get(), instance);

        Comment comment = new Comment("1", "Admin");
        comment.setContent("This the comment 1");
        comment.setUpdatedAt(new Date());

        instance.addComment(comment);

        entityOptional = userTaskInstanceRepository.findById(instance.getId());

        UserTaskInstanceEntity userTaskInstanceEntity = entityOptional.get();

        assertThat(userTaskInstanceEntity.getComments())
                .hasSize(1);

        assertUserTaskEntityComments(entityOptional.get().getComments(), instance.getComments());

        Comment comment2 = new Comment("2", "Admin");
        comment2.setContent("This the comment 2");
        comment2.setUpdatedAt(new Date());

        instance.addComment(comment2);

        entityOptional = userTaskInstanceRepository.findById(instance.getId());

        userTaskInstanceEntity = entityOptional.get();

        assertThat(userTaskInstanceEntity.getComments())
                .hasSize(2);

        assertUserTaskEntityComments(userTaskInstanceEntity.getComments(), instance.getComments());

        instance.removeComment(comment);
        instance.removeComment(comment2);

        entityOptional = userTaskInstanceRepository.findById(instance.getId());

        assertThat(entityOptional.get().getComments())
                .isEmpty();

        assertThat(commentRepository.findAll())
                .isEmpty();

        userTaskInstances.remove(instance);

        assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    // ========== Filter Tests ==========

    @Test
    public void testFindByIdentityWithTaskNameFilter() {
        // Create tasks with different names
        UserTaskInstance task1 = createTaskWithName("task1", "Approve Request", "Reserved", "Homer");
        UserTaskInstance task2 = createTaskWithName("task2", "Approve Document", "InProgress", "Homer");
        UserTaskInstance task3 = createTaskWithName("task3", "Reject Application", "Reserved", "Homer");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .taskName("Approve Request")
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(1)
                .extracting(UserTaskInstance::getTaskName)
                .containsExactly("Approve Request");

        // Cleanup
        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
        userTaskInstances.remove(task3);
    }

    @Test
    public void testFindByIdentityWithTaskNameFilterUpperCase() {
        UserTaskInstance task1 = createTaskWithName("task1", "approve request", "Reserved", "Homer");
        UserTaskInstance task2 = createTaskWithName("task2", "APPROVE REQUEST", "Reserved", "Homer");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .taskName("APPROVE REQUEST")
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result).hasSize(1);

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
    }

    @Test
    public void testFindByIdentityWithStatusFilter() {
        UserTaskInstance task1 = createTaskWithName("task1", "Task 1", "Reserved", "Homer");
        UserTaskInstance task2 = createTaskWithName("task2", "Task 2", "InProgress", "Homer");
        UserTaskInstance task3 = createTaskWithName("task3", "Task 3", "Completed", "Homer");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .statuses(Collections.singletonList("Reserved"))
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(1)
                .extracting(task -> task.getStatus().getName())
                .containsExactly("Reserved");

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
        userTaskInstances.remove(task3);
    }

    @Test
    public void testFindByIdentityWithMultipleStatusFilter() {
        UserTaskInstance task1 = createTaskWithName("task1", "Task 1", "Reserved", "Homer");
        UserTaskInstance task2 = createTaskWithName("task2", "Task 2", "InProgress", "Homer");
        UserTaskInstance task3 = createTaskWithName("task3", "Task 3", "Completed", "Homer");
        UserTaskInstance task4 = createTaskWithName("task4", "Task 4", "Suspended", "Homer");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .statuses(Arrays.<String> asList(
                        "Reserved",
                        "InProgress"))
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(2)
                .extracting(task -> task.getStatus().getName())
                .containsExactlyInAnyOrder("Reserved", "InProgress");

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
        userTaskInstances.remove(task3);
        userTaskInstances.remove(task4);
    }

    @Test
    public void testFindByIdentityWithProcessIdFilter() {
        UserTaskInstance task1 = createTaskWithProcessInfo("task1", "Task 1", "Reserved", "Homer", "hiring", "inst1");
        UserTaskInstance task2 = createTaskWithProcessInfo("task2", "Task 2", "Reserved", "Homer", "onboarding", "inst2");
        UserTaskInstance task3 = createTaskWithProcessInfo("task3", "Task 3", "Reserved", "Homer", "hiring", "inst3");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .processId("hiring")
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(2)
                .extracting(UserTaskInstance::getId)
                .containsExactlyInAnyOrder("task1", "task3");

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
        userTaskInstances.remove(task3);
    }

    @Test
    public void testFindByIdentityWithProcessInstanceIdFilter() {
        UserTaskInstance task1 = createTaskWithProcessInfo("task1", "Task 1", "Reserved", "Homer", "hiring", "inst1");
        UserTaskInstance task2 = createTaskWithProcessInfo("task2", "Task 2", "Reserved", "Homer", "hiring", "inst1");
        UserTaskInstance task3 = createTaskWithProcessInfo("task3", "Task 3", "Reserved", "Homer", "hiring", "inst2");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .processInstanceId("inst1")
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(2)
                .extracting(UserTaskInstance::getId)
                .containsExactlyInAnyOrder("task1", "task2");

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
        userTaskInstances.remove(task3);
    }

    @Test
    public void testFindByIdentityWithCombinedFilters() {
        UserTaskInstance task1 = createTaskWithProcessInfo("task1", "Approve Request", "Reserved", "Homer", "hiring", "inst1");
        UserTaskInstance task2 = createTaskWithProcessInfo("task2", "Approve Document", "InProgress", "Homer", "hiring", "inst1");
        UserTaskInstance task3 = createTaskWithProcessInfo("task3", "Approve Request", "Reserved", "Homer", "onboarding", "inst2");
        UserTaskInstance task4 = createTaskWithProcessInfo("task4", "Review Request", "Reserved", "Homer", "hiring", "inst1");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .taskName("Approve Request")
                .statuses(Collections.singletonList("Reserved"))
                .processId("hiring")
                .processInstanceId("inst1")
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result)
                .hasSize(1)
                .extracting(UserTaskInstance::getId)
                .containsExactly("task1");

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
        userTaskInstances.remove(task3);
        userTaskInstances.remove(task4);
    }

    @Test
    public void testFindByIdentityWithNullFilter() {
        UserTaskInstance task1 = createTaskWithName("task1", "Task 1", "Reserved", "Homer");
        UserTaskInstance task2 = createTaskWithName("task2", "Task 2", "InProgress", "Homer");

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), null);

        assertThat(result).hasSize(2);

        userTaskInstances.remove(task1);
        userTaskInstances.remove(task2);
    }

    @Test
    public void testFindByIdentityWithEmptyResultFilter() {
        UserTaskInstance task1 = createTaskWithName("task1", "Approve Request", "Reserved", "Homer");

        org.kie.kogito.usertask.UserTaskFilter filter = org.kie.kogito.usertask.UserTaskFilter.builder()
                .taskName("reject")
                .build();

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer"), filter);

        assertThat(result).isEmpty();

        userTaskInstances.remove(task1);
    }

    // Helper methods for filter tests

    private UserTaskInstance createTaskWithName(String id, String taskName, String status, String actualOwner) {
        DefaultUserTaskInstance instance = new DefaultUserTaskInstance();
        instance.setId(id);
        instance.setTaskName(taskName);
        instance.setStatus(org.kie.kogito.usertask.lifecycle.UserTaskState.of(status));
        instance.setActualOwner(actualOwner);
        instance.setAdminUsers(Collections.singleton(actualOwner));

        // Initialize deadline and reassignment collections to avoid NPE
        instance.setNotStartedDeadlines(Collections.emptyList());
        instance.setNotCompletedDeadlines(Collections.emptyList());
        instance.setNotStartedReassignments(Collections.emptyList());
        instance.setNotCompletedReassignments(Collections.emptyList());

        instance.setInstances(userTaskInstances);
        userTaskInstances.create(instance);
        return instance;
    }

    private UserTaskInstance createTaskWithProcessInfo(String id, String taskName, String status, String actualOwner,
            String processId, String processInstanceId) {
        DefaultUserTaskInstance instance = new DefaultUserTaskInstance();
        instance.setId(id);
        instance.setTaskName(taskName);
        instance.setStatus(org.kie.kogito.usertask.lifecycle.UserTaskState.of(status));
        instance.setActualOwner(actualOwner);
        instance.setAdminUsers(Collections.singleton(actualOwner));

        org.kie.kogito.usertask.model.ProcessInfo processInfo = org.kie.kogito.usertask.model.ProcessInfo.builder()
                .withProcessId(processId)
                .withProcessInstanceId(processInstanceId)
                .build();
        instance.setProcessInfo(processInfo);

        // Initialize deadline and reassignment collections to avoid NPE
        instance.setNotStartedDeadlines(Collections.emptyList());
        instance.setNotCompletedDeadlines(Collections.emptyList());
        instance.setNotStartedReassignments(Collections.emptyList());
        instance.setNotCompletedReassignments(Collections.emptyList());

        instance.setInstances(userTaskInstances);
        userTaskInstances.create(instance);
        return instance;
    }

    private void assertEntityAndInstance(UserTaskInstanceEntity entity, UserTaskInstance instance) {
        assertUserTaskEntityData(entity, instance);

        assertUserTaskEntityPotentialUserAndGroups(entity, instance);
        assertUserTaskEntityAdminUserAndGroups(entity, instance);
        assertUserTaskEntityExcludedUsers(entity, instance);

        assertUserTaskEntityInputs(entity, instance);
        assertUserTaskEntityOutputs(entity, instance);

        assertUserTaskEntityAttachments(entity.getAttachments(), instance.getAttachments());
        assertUserTaskEntityComments(entity.getComments(), instance.getComments());
        assertUserTaskEntityMetadata(entity, instance);
    }

    private UserTaskInstance getReservedUserTaskInstance() {
        DefaultUserTaskInstance instance = createReservedUserTaskInstance();

        instance.setInstances(userTaskInstances);
        userTaskInstances.create(instance);

        return instance;
    }

    private UserTaskInstance getActiveUserTaskInstance() {
        DefaultUserTaskInstance instance = createActiveUserTaskInstance();

        instance.setInstances(userTaskInstances);
        userTaskInstances.create(instance);

        return instance;
    }
}
