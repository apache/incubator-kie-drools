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

package org.jbpm.usertask.jpa.quarkus;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.jbpm.usertask.jpa.JPAUserTaskInstances;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.quarkus.repository.QuarkusUserTaskJPAContext;
import org.jbpm.usertask.jpa.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.model.Attachment;
import org.kie.kogito.usertask.model.Comment;
import org.mockito.Mockito;

import jakarta.inject.Inject;
import jakarta.persistence.EntityExistsException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jbpm.usertask.jpa.mapper.utils.TestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public abstract class BaseQuarkusJPAUserTaskInstancesTest {

    @Inject
    QuarkusUserTaskJPAContext context;

    @Inject
    JPAUserTaskInstances userTaskInstances;

    @Inject
    UserTaskInstanceRepository userTaskInstanceRepository;

    @Inject
    AttachmentRepository attachmentRepository;

    @Inject
    CommentRepository commentRepository;

    private Function<UserTaskInstance, UserTaskInstance> connect;
    private Function<UserTaskInstance, UserTaskInstance> disconnect;

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
                .isInstanceOf(EntityExistsException.class);
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
