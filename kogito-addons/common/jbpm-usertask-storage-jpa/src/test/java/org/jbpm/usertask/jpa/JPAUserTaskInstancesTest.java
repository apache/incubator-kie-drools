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

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.assertj.core.api.Assertions;
import org.jbpm.usertask.jpa.mapper.*;
import org.jbpm.usertask.jpa.mapper.utils.TestUtils;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.UserTaskInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.usertask.UserTaskInstance;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JPAUserTaskInstancesTest {

    @Mock
    private UserTaskInstanceRepository userTaskInstanceRepository;

    @Mock
    private AttachmentsEntityMapper attachmentsEntityMapper;
    @Mock
    private CommentsEntityMapper commentsEntityMapper;
    @Mock
    private TaskMetadataEntityMapper metadataEntityMapper;
    @Mock
    private TaskInputsEntityMapper inputsEntityMapper;
    @Mock
    private TaskOutputsEntityMapper outputsEntityMapper;

    private UserTaskInstanceEntityMapper userTaskInstanceEntityMapper;
    @Mock
    private Function<UserTaskInstance, UserTaskInstance> reconnectUserTaskInstance;
    @Mock
    private Function<UserTaskInstance, UserTaskInstance> disconnectUserTaskInstance;

    private JPAUserTaskInstances jpaUserTaskInstances;

    @BeforeEach
    public void setup() {
        userTaskInstanceEntityMapper = spy(new UserTaskInstanceEntityMapper(attachmentsEntityMapper, commentsEntityMapper, metadataEntityMapper, inputsEntityMapper, outputsEntityMapper));
        jpaUserTaskInstances = new JPAUserTaskInstances(userTaskInstanceRepository, userTaskInstanceEntityMapper);
        jpaUserTaskInstances.setReconnectUserTaskInstance(reconnectUserTaskInstance);
        jpaUserTaskInstances.setDisconnectUserTaskInstance(disconnectUserTaskInstance);
    }

    @Test
    public void testSuccessfulFindById() {
        Optional<UserTaskInstanceEntity> result = Optional.of(TestUtils.createUserTaskInstanceEntity());

        when(userTaskInstanceRepository.findById(any())).thenReturn(result);

        jpaUserTaskInstances.findById("1234");

        verify(userTaskInstanceEntityMapper, times(1)).mapTaskEntityToInstance(any());
        verify(reconnectUserTaskInstance, times(1)).apply(any());
    }

    @Test
    public void testUnSuccessfulFindById() {
        when(userTaskInstanceRepository.findById(any())).thenReturn(Optional.empty());

        jpaUserTaskInstances.findById("1234");

        verify(userTaskInstanceEntityMapper, never()).mapTaskEntityToInstance(any());
        verify(reconnectUserTaskInstance, never()).apply(any());
    }

    @Test
    public void testSuccessfulExists() {
        Optional<UserTaskInstanceEntity> result = Optional.of(TestUtils.createUserTaskInstanceEntity());

        when(userTaskInstanceRepository.findById(any())).thenReturn(result);

        Assertions.assertThat(jpaUserTaskInstances.exists("1234"))
                .isTrue();

        verify(userTaskInstanceEntityMapper, never()).mapTaskEntityToInstance(any());
        verify(reconnectUserTaskInstance, never()).apply(any());
    }

    @Test
    public void testUnSuccessfulExists() {
        when(userTaskInstanceRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThat(jpaUserTaskInstances.exists("1234"))
                .isFalse();

        verify(userTaskInstanceEntityMapper, never()).mapTaskEntityToInstance(any());
        verify(reconnectUserTaskInstance, never()).apply(any());
    }

    @Test
    public void testSuccessfulFindByIdentity() {
        List<UserTaskInstanceEntity> result = List.of(TestUtils.createUserTaskInstanceEntity(), TestUtils.createUserTaskInstanceEntity());

        when(userTaskInstanceRepository.findByIdentity(any())).thenReturn(result);

        List<UserTaskInstance> instances = jpaUserTaskInstances.findByIdentity(IdentityProviders.of("user", "group"));

        Assertions.assertThat(instances)
                .hasSize(2);

        verify(userTaskInstanceEntityMapper, times(2)).mapTaskEntityToInstance(any());
        verify(reconnectUserTaskInstance, times(2)).apply(any());

    }

    @Test
    public void testUnSuccessfulFindByIdentity() {
        when(userTaskInstanceRepository.findByIdentity(any())).thenReturn(List.of());

        List<UserTaskInstance> instances = jpaUserTaskInstances.findByIdentity(IdentityProviders.of("user", "group"));

        Assertions.assertThat(instances)
                .isEmpty();

        verify(userTaskInstanceEntityMapper, never()).mapTaskEntityToInstance(any());
        verify(reconnectUserTaskInstance, never()).apply(any());
    }

    @Test
    public void testSuccessfulCreate() {
        when(userTaskInstanceRepository.findById(any())).thenReturn(Optional.empty());

        jpaUserTaskInstances.create(TestUtils.createUserTaskInstance());

        verify(userTaskInstanceRepository, times(1)).persist(any());
        verify(userTaskInstanceEntityMapper, times(1)).mapTaskInstanceToEntity(any(), any());
        verify(reconnectUserTaskInstance, times(1)).apply(any());
    }

    @Test
    public void testUnSuccessfulCreate() {
        Optional<UserTaskInstanceEntity> result = Optional.of(TestUtils.createUserTaskInstanceEntity());
        when(userTaskInstanceRepository.findById(any())).thenReturn(result);

        Assertions.assertThatThrownBy(() -> {
            jpaUserTaskInstances.create(TestUtils.createUserTaskInstance());
        }).hasMessageContaining("Task Already exists.");

        verify(userTaskInstanceRepository, never()).persist(any());
        verify(userTaskInstanceEntityMapper, never()).mapTaskInstanceToEntity(any(), any());
        verify(reconnectUserTaskInstance, never()).apply(any());
    }

    @Test
    public void testSuccessfulUpdate() {
        Optional<UserTaskInstanceEntity> result = Optional.of(TestUtils.createUserTaskInstanceEntity());
        when(userTaskInstanceRepository.findById(any())).thenReturn(result);

        jpaUserTaskInstances.update(TestUtils.createUserTaskInstance());

        verify(userTaskInstanceRepository, times(1)).update(any());
        verify(userTaskInstanceEntityMapper, times(1)).mapTaskInstanceToEntity(any(), any());
    }

    @Test
    public void testUnSuccessfulUpdate() {
        when(userTaskInstanceRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> {
            jpaUserTaskInstances.update(TestUtils.createUserTaskInstance());
        }).hasMessageContaining("Could not find userTaskInstance with id ");

        verify(userTaskInstanceRepository, never()).persist(any());
        verify(userTaskInstanceEntityMapper, never()).mapTaskInstanceToEntity(any(), any());
    }

    @Test
    public void testSuccessfulRemove() {
        Optional<UserTaskInstanceEntity> result = Optional.of(TestUtils.createUserTaskInstanceEntity());
        when(userTaskInstanceRepository.findById(any())).thenReturn(result);

        jpaUserTaskInstances.remove(TestUtils.createUserTaskInstance());

        verify(userTaskInstanceRepository, times(1)).remove(any());
        verify(disconnectUserTaskInstance, times(1)).apply(any());
    }

    @Test
    public void testUnSuccessfulRemove() {
        when(userTaskInstanceRepository.findById(any())).thenReturn(Optional.empty());

        Assertions.assertThatThrownBy(() -> {
            jpaUserTaskInstances.remove(TestUtils.createUserTaskInstance());
        }).hasMessageContaining("Could not remove userTaskInstance with id");

        verify(userTaskInstanceRepository, never()).persist(any());
        verify(disconnectUserTaskInstance, never()).apply(any());
    }
}
