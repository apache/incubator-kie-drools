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

import java.nio.charset.StandardCharsets;

import org.assertj.core.api.Assertions;
import org.jbpm.usertask.jpa.mapper.utils.TestUtils;
import org.jbpm.usertask.jpa.model.TaskInputEntity;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.TaskInputRepository;
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
public class TaskInputsEntityMapperTest {

    @Mock
    private TaskInputRepository repository;
    private TaskInputsEntityMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new TaskInputsEntityMapper(repository);
    }

    @Test
    public void testMapInputsFromInstanceToEntity() {
        UserTaskInstance instance = TestUtils.createCompletedUserTaskInstance();
        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();

        mapper.mapInstanceToEntity(instance, entity);

        Assertions.assertThat(entity.getInputs())
                .hasSize(8);
        verify(repository, never())
                .remove(any());
        TestUtils.assertUserTaskEntityInputs(entity, instance);

        instance.getInputs().remove("in_string");
        instance.getInputs().remove("in_integer");
        instance.getInputs().remove("in_null");

        mapper.mapInstanceToEntity(instance, entity);

        Assertions.assertThat(entity.getInputs())
                .hasSize(5);
        verify(repository, times(3))
                .remove(any());

        TestUtils.assertUserTaskEntityInputs(entity, instance);

        instance.getInputs().clear();

        mapper.mapInstanceToEntity(instance, entity);

        Assertions.assertThat(entity.getInputs())
                .hasSize(0);
        verify(repository, times(8))
                .remove(any());
    }

    @Test
    public void testMapInputsFromEntityToInstance() {
        final String stringValue = "This is the input value";

        UserTaskInstance instance = TestUtils.createCompletedUserTaskInstance();
        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();

        TaskInputEntity input = new TaskInputEntity();
        input.setName("in_string");
        input.setValue(stringValue.getBytes(StandardCharsets.UTF_8));

        entity.addInput(input);

        mapper.mapEntityToInstance(entity, instance);

        Assertions.assertThat(instance.getInputs())
                .hasSize(1);

        TestUtils.assertUserTaskInstanceInputs(instance, entity);

        entity.getInputs().clear();

        mapper.mapEntityToInstance(entity, instance);

        Assertions.assertThat(instance.getInputs())
                .hasSize(0);
    }

    @Test
    public void testMappingRoundCircle() {

        UserTaskInstance instance = TestUtils.createCompletedUserTaskInstance();
        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();

        mapper.mapInstanceToEntity(instance, entity);

        Assertions.assertThat(entity.getInputs())
                .hasSize(8);
        verify(repository, never())
                .remove(any());

        TestUtils.assertUserTaskEntityInputs(entity, instance);

        DefaultUserTaskInstance instance2 = new DefaultUserTaskInstance();

        mapper.mapEntityToInstance(entity, instance2);

        Assertions.assertThat(instance2.getInputs())
                .hasSize(8);

        TestUtils.assertUserTaskInstanceInputs(instance2, entity);

        Assertions.assertThat(instance2.getInputs())
                .usingRecursiveComparison()
                .isEqualTo(instance.getInputs());
    }
}
