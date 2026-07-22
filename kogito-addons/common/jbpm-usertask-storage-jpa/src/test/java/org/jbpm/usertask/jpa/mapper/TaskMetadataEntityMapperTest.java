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

package org.jbpm.usertask.jpa.mapper;

import org.assertj.core.api.Assertions;
import org.jbpm.usertask.jpa.mapper.utils.TestUtils;
import org.jbpm.usertask.jpa.model.TaskMetadataEntity;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.TaskMetadataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskMetadataEntityMapperTest {

    @Mock
    private TaskMetadataRepository repository;
    private TaskMetadataEntityMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new TaskMetadataEntityMapper(repository);
    }

    @Test
    public void testMapMetadataFromInstanceToEntity() {
        UserTaskInstance instance = TestUtils.createCompletedUserTaskInstance();
        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();

        mapper.mapInstanceToEntity(instance, entity);

        Assertions.assertThat(entity.getMetadata())
                .hasSize(6);
        verify(repository, never())
                .remove(any());
        TestUtils.assertUserTaskEntityMetadata(entity, instance);

        instance.getMetadata().remove("ProcessId");
        instance.getMetadata().remove("ProcessType");

        mapper.mapInstanceToEntity(instance, entity);

        Assertions.assertThat(entity.getMetadata())
                .hasSize(4);
        verify(repository, times(2))
                .remove(any());

        TestUtils.assertUserTaskEntityMetadata(entity, instance);

        instance.getMetadata().clear();

        mapper.mapInstanceToEntity(instance, entity);

        Assertions.assertThat(entity.getMetadata())
                .hasSize(0);
        verify(repository, times(6))
                .remove(any());
    }

    @Test
    public void testMapMetadataFromEntityToInstance() {

        UserTaskInstance instance = TestUtils.createCompletedUserTaskInstance();
        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();

        TaskMetadataEntity metadata = new TaskMetadataEntity();
        metadata.setName("ProcessId");
        metadata.setValue("1234");

        TaskMetadataEntity metadata2 = new TaskMetadataEntity();
        metadata2.setName("CustomMetadata");
        metadata2.setValue("This is the metadata value");

        entity.addMetadata(metadata);
        entity.addMetadata(metadata2);

        mapper.mapEntityToInstance(entity, instance);

        Assertions.assertThat(instance.getMetadata())
                .hasSize(2);

        TestUtils.assertUserTaskInstanceMetadata(instance, entity);

        entity.getMetadata().clear();

        mapper.mapEntityToInstance(entity, instance);

        Assertions.assertThat(instance.getMetadata())
                .hasSize(0);
    }

    @Test
    public void testFullMappingCircle() {

        UserTaskInstance instance = TestUtils.createCompletedUserTaskInstance();
        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();

        mapper.mapInstanceToEntity(instance, entity);

        Assertions.assertThat(entity.getMetadata())
                .hasSize(6);
        verify(repository, never())
                .remove(any());

        TestUtils.assertUserTaskEntityMetadata(entity, instance);

        DefaultUserTaskInstance instance2 = new DefaultUserTaskInstance();

        mapper.mapEntityToInstance(entity, instance2);

        Assertions.assertThat(instance2.getMetadata())
                .hasSize(6);

        TestUtils.assertUserTaskInstanceMetadata(instance2, entity);

        Assertions.assertThat(instance2.getMetadata())
                .usingRecursiveComparison()
                .isEqualTo(instance.getMetadata());
    }
}
