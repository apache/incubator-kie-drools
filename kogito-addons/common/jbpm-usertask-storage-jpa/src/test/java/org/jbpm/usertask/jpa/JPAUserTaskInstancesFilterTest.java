/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file to
 * you under the Apache License, Version 2.0 (the
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jbpm.usertask.jpa.mapper.UserTaskInstanceEntityMapper;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.UserTaskInstanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.auth.IdentityProvider;
import org.kie.kogito.usertask.UserTaskFilter;
import org.kie.kogito.usertask.UserTaskInstance;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test class for JPAUserTaskInstances.findByIdentity() method with filter parameter.
 * This test verifies that the method correctly delegates to the repository's
 * overloaded findByIdentity(IdentityProvider, UserTaskFilter) method
 * and maps the results using the entity mapper.
 */
@ExtendWith(MockitoExtension.class)
public class JPAUserTaskInstancesFilterTest {

    @Mock
    private UserTaskInstanceRepository repository;

    @Mock
    private UserTaskInstanceEntityMapper mapper;

    @Mock
    private IdentityProvider identity;

    @Mock
    private UserTaskInstance userTaskInstance1;

    @Mock
    private UserTaskInstance userTaskInstance2;

    private JPAUserTaskInstances jpaUserTaskInstances;

    @BeforeEach
    public void setup() {
        jpaUserTaskInstances = new JPAUserTaskInstances(repository, mapper);
        // Set reconnect function to identity function for testing
        jpaUserTaskInstances.setReconnectUserTaskInstance(task -> task);
    }

    @Test
    public void testFindByIdentityWithFilterDelegatesToRepository() {
        // Given
        UserTaskFilter filter = UserTaskFilter.builder()
                .processId("hiring")
                .build();

        UserTaskInstanceEntity entity1 = new UserTaskInstanceEntity();
        entity1.setId("entity1");
        UserTaskInstanceEntity entity2 = new UserTaskInstanceEntity();
        entity2.setId("entity2");
        List<UserTaskInstanceEntity> entities = Arrays.asList(entity1, entity2);

        when(repository.findByIdentity(eq(identity), eq(filter)))
                .thenReturn(entities);
        when(mapper.mapTaskEntityToInstance(any(UserTaskInstanceEntity.class)))
                .thenAnswer(invocation -> {
                    UserTaskInstanceEntity entity = invocation.getArgument(0);
                    return entity.getId().equals("entity1") ? userTaskInstance1 : userTaskInstance2;
                });

        // When
        List<UserTaskInstance> result = jpaUserTaskInstances.findByIdentity(identity, filter);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).containsExactly(userTaskInstance1, userTaskInstance2);
        verify(repository).findByIdentity(identity, filter);
    }

    @Test
    public void testFindByIdentityWithFilterWithEmptyResult() {
        // Given
        UserTaskFilter filter = UserTaskFilter.builder().build();
        List<UserTaskInstanceEntity> entities = Collections.emptyList();

        when(repository.findByIdentity(eq(identity), eq(filter)))
                .thenReturn(entities);

        // When
        List<UserTaskInstance> result = jpaUserTaskInstances.findByIdentity(identity, filter);

        // Then
        assertThat(result).isEmpty();
        verify(repository).findByIdentity(identity, filter);
    }

    @Test
    public void testFindByIdentityWithFilterWithAllFilters() {
        // Given
        UserTaskFilter filter = UserTaskFilter.builder()
                .processId("hiring")
                .processInstanceId("instance123")
                .statuses(List.of("Reserved"))
                .taskName("hr_interview")
                .build();

        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();
        when(repository.findByIdentity(eq(identity), eq(filter)))
                .thenReturn(Collections.singletonList(entity));
        when(mapper.mapTaskEntityToInstance(entity)).thenReturn(userTaskInstance1);

        // When
        List<UserTaskInstance> result = jpaUserTaskInstances.findByIdentity(identity, filter);

        // Then
        assertThat(result).hasSize(1);
        verify(repository).findByIdentity(identity, filter);
        verify(mapper).mapTaskEntityToInstance(entity);
    }

    @Test
    public void testFindByIdentityWithFilterWithMultipleStatuses() {
        // Given
        UserTaskFilter filter = UserTaskFilter.builder()
                .statuses(Arrays.asList(
                        "Reserved",
                        "InProgress",
                        "Completed"))
                .build();

        UserTaskInstanceEntity entity1 = new UserTaskInstanceEntity();
        entity1.setId("entity1");
        entity1.setStatus("Reserved");

        UserTaskInstanceEntity entity2 = new UserTaskInstanceEntity();
        entity2.setId("entity2");
        entity2.setStatus("InProgress");

        UserTaskInstanceEntity entity3 = new UserTaskInstanceEntity();
        entity3.setId("entity3");
        entity3.setStatus("Completed");

        List<UserTaskInstanceEntity> entities = Arrays.asList(entity1, entity2, entity3);

        when(repository.findByIdentity(eq(identity), eq(filter)))
                .thenReturn(entities);
        when(mapper.mapTaskEntityToInstance(any(UserTaskInstanceEntity.class)))
                .thenAnswer(invocation -> {
                    UserTaskInstanceEntity entity = invocation.getArgument(0);
                    if (entity.getId().equals("entity1"))
                        return userTaskInstance1;
                    if (entity.getId().equals("entity2"))
                        return userTaskInstance2;
                    return userTaskInstance1; // fallback
                });

        // When
        List<UserTaskInstance> result = jpaUserTaskInstances.findByIdentity(identity, filter);

        // Then
        assertThat(result).hasSize(3);
        verify(repository).findByIdentity(identity, filter);
    }

    @Test
    public void testFindByIdentityWithFilterWithSingleStatus() {
        // Given
        UserTaskFilter filter = UserTaskFilter.builder()
                .statuses(Collections.singletonList("Reserved"))
                .build();

        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();
        entity.setId("entity1");
        entity.setStatus("Reserved");

        when(repository.findByIdentity(eq(identity), eq(filter)))
                .thenReturn(Collections.singletonList(entity));
        when(mapper.mapTaskEntityToInstance(entity)).thenReturn(userTaskInstance1);

        // When
        List<UserTaskInstance> result = jpaUserTaskInstances.findByIdentity(identity, filter);

        // Then
        assertThat(result).hasSize(1);
        verify(repository).findByIdentity(identity, filter);
    }
}
