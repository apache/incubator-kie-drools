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
import org.jbpm.usertask.jpa.mapper.utils.TestUtils;
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
        UserTaskInstance instance = createUserTaskInstance();

        Assertions.assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();

        Assertions.assertThat(userTaskInstances.findById(instance.getId()))
                .isNotNull()
                .isEmpty();

        userTaskInstances.create(instance);

        verify(connect, times(1)).apply(any(UserTaskInstance.class));

        Optional<UserTaskInstanceEntity> entityOptional = userTaskInstanceRepository.findById(instance.getId());

        Assertions.assertThat(entityOptional)
                .isNotNull()
                .isPresent();

        UserTaskInstanceEntity entity = entityOptional.get();

        assertEntityAndInstance(entity, instance);

        Optional<UserTaskInstance> persistedInstanceOptional = userTaskInstances.findById(instance.getId());

        Assertions.assertThat(persistedInstanceOptional)
                .isNotNull()
                .isPresent();

        assertEntityAndInstance(entity, persistedInstanceOptional.get());
    }

    @Test
    public void testCreateExistingTask() {
        UserTaskInstance instance = createUserTaskInstance();

        userTaskInstances.create(instance);

        Assertions.assertThatThrownBy(() -> userTaskInstances.create(instance))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Task Already exists.");

        userTaskInstances.remove(instance);

        Assertions.assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testEditTaskInputOutputs() {

        UserTaskInstance instance = createUserTaskInstance();

        userTaskInstances.create(instance);

        Optional<UserTaskInstanceEntity> entityOptional = userTaskInstanceRepository.findById(instance.getId());

        Assertions.assertThat(entityOptional)
                .isNotNull()
                .isPresent();

        UserTaskInstanceEntity entity = entityOptional.get();

        Assertions.assertThat(entity.getInputs())
                .hasSize(instance.getInputs().size());

        Assertions.assertThat(entity.getOutputs())
                .hasSize(instance.getOutputs().size());

        instance.getInputs().clear();
        instance.setInput("new_input", "this is a new input");

        instance.getOutputs().clear();
        instance.setOutput("new_output", "this is a new output");

        userTaskInstances.update(instance);

        entity = userTaskInstanceRepository.findById(instance.getId()).get();

        Assertions.assertThat(entity.getInputs())
                .hasSize(1);

        Assertions.assertThat(entity.getOutputs())
                .hasSize(1);

        TestUtils.assertUserTaskEntityInputs(entity, instance);
        TestUtils.assertUserTaskEntityOutputs(entity, instance);

        userTaskInstances.remove(instance);

        Assertions.assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();

    }

    @Test
    public void testFindByIdentityByActualOwner() {

        UserTaskInstance instance = createUserTaskInstance();

        userTaskInstances.create(instance);

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Homer", "Group"));

        Assertions.assertThat(result)
                .hasSize(1);

        verify(connect, times(2)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(instance);

        Assertions.assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByPotentialOwners() {
        UserTaskInstance instance = createUserTaskInstance();

        userTaskInstances.create(instance);

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Liza", "Group"));

        Assertions.assertThat(result)
                .hasSize(1);

        verify(connect, times(2)).apply(any(UserTaskInstance.class));

        List<UserTaskInstance> result2 = userTaskInstances.findByIdentity(IdentityProviders.of("Bart", "Simpson"));

        Assertions.assertThat(result2)
                .hasSize(1);

        verify(connect, times(3)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(instance);

        Assertions.assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByPotentialGroups() {
        UserTaskInstance instance = createUserTaskInstance();

        userTaskInstances.create(instance);

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Abraham", "Admin", "Simpson"));

        Assertions.assertThat(result)
                .hasSize(1);

        verify(connect, times(2)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(instance);

        Assertions.assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByAdminUsers() {

        UserTaskInstance instance = createUserTaskInstance();

        userTaskInstances.create(instance);

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Seymour", "Group"));

        Assertions.assertThat(result)
                .hasSize(1);

        verify(connect, times(2)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(instance);

        Assertions.assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByAdminGroups() {
        UserTaskInstance instance = createUserTaskInstance();

        userTaskInstances.create(instance);

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Abraham", "Administrator", "Managers"));

        Assertions.assertThat(result)
                .hasSize(1);

        verify(connect, times(2)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(instance);

        Assertions.assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByExcludedUser() {
        UserTaskInstance instance = createUserTaskInstance();

        userTaskInstances.create(instance);

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Ned"));

        Assertions.assertThat(result)
                .hasSize(0);

        verify(connect, times(1)).apply(any(UserTaskInstance.class));

        result = userTaskInstances.findByIdentity(IdentityProviders.of("Bart"));

        Assertions.assertThat(result)
                .hasSize(0);

        verify(connect, times(1)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(instance);

        Assertions.assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testFindByIdentityByUnknownUser() {
        UserTaskInstance instance = createUserTaskInstance();

        userTaskInstances.create(instance);

        List<UserTaskInstance> result = userTaskInstances.findByIdentity(IdentityProviders.of("Someone", "Group"));

        Assertions.assertThat(result)
                .hasSize(0);

        verify(connect, times(1)).apply(any(UserTaskInstance.class));

        userTaskInstances.remove(instance);

        Assertions.assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testAttachments() throws URISyntaxException {
        UserTaskInstance instance = createUserTaskInstance();

        userTaskInstances.create(instance);

        Optional<UserTaskInstanceEntity> entityOptional = userTaskInstanceRepository.findById(instance.getId());

        Assertions.assertThat(entityOptional)
                .isNotNull()
                .isPresent();

        assertEntityAndInstance(entityOptional.get(), instance);

        Attachment attachment = new Attachment("1", "Admin");
        attachment.setName("attachment 1");
        attachment.setContent(new URI("http://url.com/to/my/attachment"));
        attachment.setUpdatedAt(new Date());

        instance.addAttachment(attachment);

        entityOptional = userTaskInstanceRepository.findById(instance.getId());

        TestUtils.assertUserTaskEntityAttachments(entityOptional.get().getAttachments(), instance.getAttachments());

        Attachment attachment2 = new Attachment("2", "Admin");
        attachment2.setName("attachment 2");
        attachment2.setContent(new URI("http://url.com/to/my/attachment2"));
        attachment2.setUpdatedAt(new Date());

        instance.addAttachment(attachment2);

        entityOptional = userTaskInstanceRepository.findById(instance.getId());
        TestUtils.assertUserTaskEntityAttachments(entityOptional.get().getAttachments(), instance.getAttachments());

        instance.removeAttachment(attachment);
        instance.removeAttachment(attachment2);

        userTaskInstances.update(instance);

        entityOptional = userTaskInstanceRepository.findById(instance.getId());

        Assertions.assertThat(entityOptional.get().getAttachments())
                .isEmpty();

        Assertions.assertThat(attachmentRepository.findAll())
                .isEmpty();

        userTaskInstances.remove(instance);

        Assertions.assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    @Test
    public void testComments() {
        UserTaskInstance instance = createUserTaskInstance();

        userTaskInstances.create(instance);

        Optional<UserTaskInstanceEntity> entityOptional = userTaskInstanceRepository.findById(instance.getId());

        Assertions.assertThat(entityOptional)
                .isNotNull()
                .isPresent();

        assertEntityAndInstance(entityOptional.get(), instance);

        Comment comment = new Comment("1", "Admin");
        comment.setContent("This the comment 1");
        comment.setUpdatedAt(new Date());

        instance.addComment(comment);

        entityOptional = userTaskInstanceRepository.findById(instance.getId());

        UserTaskInstanceEntity userTaskInstanceEntity = entityOptional.get();

        Assertions.assertThat(userTaskInstanceEntity.getComments())
                .hasSize(1);

        TestUtils.assertUserTaskEntityComments(entityOptional.get().getComments(), instance.getComments());

        Comment comment2 = new Comment("2", "Admin");
        comment2.setContent("This the comment 2");
        comment2.setUpdatedAt(new Date());

        instance.addComment(comment2);

        entityOptional = userTaskInstanceRepository.findById(instance.getId());

        userTaskInstanceEntity = entityOptional.get();

        Assertions.assertThat(userTaskInstanceEntity.getComments())
                .hasSize(2);

        TestUtils.assertUserTaskEntityComments(userTaskInstanceEntity.getComments(), instance.getComments());

        instance.removeComment(comment);
        instance.removeComment(comment2);

        entityOptional = userTaskInstanceRepository.findById(instance.getId());

        Assertions.assertThat(entityOptional.get().getComments())
                .isEmpty();

        Assertions.assertThat(commentRepository.findAll())
                .isEmpty();

        userTaskInstances.remove(instance);

        Assertions.assertThat(userTaskInstances.exists(instance.getId()))
                .isFalse();
    }

    private void assertEntityAndInstance(UserTaskInstanceEntity entity, UserTaskInstance instance) {
        TestUtils.assertUserTaskEntityData(entity, instance);

        TestUtils.assertUserTaskEntityPotentialUserAndGroups(entity, instance);
        TestUtils.assertUserTaskEntityAdminUserAndGroups(entity, instance);
        TestUtils.assertUserTaskEntityExcludedUsers(entity, instance);

        TestUtils.assertUserTaskEntityInputs(entity, instance);
        TestUtils.assertUserTaskEntityOutputs(entity, instance);

        TestUtils.assertUserTaskEntityAttachments(entity.getAttachments(), instance.getAttachments());
        TestUtils.assertUserTaskEntityComments(entity.getComments(), instance.getComments());
        TestUtils.assertUserTaskEntityMetadata(entity, instance);
    }

    private UserTaskInstance createUserTaskInstance() {
        DefaultUserTaskInstance instance = TestUtils.createUserTaskInstance();

        instance.setInstances(userTaskInstances);

        return instance;
    }
}
