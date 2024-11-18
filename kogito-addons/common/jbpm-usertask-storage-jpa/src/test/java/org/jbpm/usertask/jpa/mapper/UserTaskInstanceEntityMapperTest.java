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

import java.util.List;

import org.jbpm.usertask.jpa.mapper.utils.TestUtils;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.usertask.UserTaskInstance;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserTaskInstanceEntityMapperTest {

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

    @BeforeEach
    public void setUp() {
        this.userTaskInstanceEntityMapper = new UserTaskInstanceEntityMapper(List.of(attachmentsEntityMapper, commentsEntityMapper, metadataEntityMapper, inputsEntityMapper, outputsEntityMapper));
    }

    @Test
    public void testUserTaskInstanceToUserTaskEntityMapper() {
        UserTaskInstance userTaskInstance = TestUtils.createUserTaskInstance();

        UserTaskInstanceEntity userTaskInstanceEntity = new UserTaskInstanceEntity();

        userTaskInstanceEntityMapper.mapTaskInstanceToEntity(userTaskInstance, userTaskInstanceEntity);

        verify(attachmentsEntityMapper, times(1))
                .mapInstanceToEntity(same(userTaskInstance), same(userTaskInstanceEntity));
        verify(commentsEntityMapper, times(1))
                .mapInstanceToEntity(same(userTaskInstance), same(userTaskInstanceEntity));
        verify(metadataEntityMapper, times(1))
                .mapInstanceToEntity(same(userTaskInstance), same(userTaskInstanceEntity));
        verify(inputsEntityMapper, times(1))
                .mapInstanceToEntity(same(userTaskInstance), same(userTaskInstanceEntity));
        verify(outputsEntityMapper, times(1))
                .mapInstanceToEntity(same(userTaskInstance), same(userTaskInstanceEntity));

        TestUtils.assertUserTaskEntityData(userTaskInstanceEntity, userTaskInstance);
        TestUtils.assertUserTaskEntityPotentialUserAndGroups(userTaskInstanceEntity, userTaskInstance);
        TestUtils.assertUserTaskEntityAdminUserAndGroups(userTaskInstanceEntity, userTaskInstance);
        TestUtils.assertUserTaskEntityExcludedUsers(userTaskInstanceEntity, userTaskInstance);
    }

    @Test
    public void testUserTaskEntityToUserTaskInstanceMapper() {
        UserTaskInstanceEntity userTaskInstanceEntity = TestUtils.createUserTaskInstanceEntity();

        UserTaskInstance userTaskInstance = userTaskInstanceEntityMapper.mapTaskEntityToInstance(userTaskInstanceEntity);

        verify(attachmentsEntityMapper, times(1))
                .mapEntityToInstance(same(userTaskInstanceEntity), same(userTaskInstance));
        verify(commentsEntityMapper, times(1))
                .mapEntityToInstance(same(userTaskInstanceEntity), same(userTaskInstance));
        verify(metadataEntityMapper, times(1))
                .mapEntityToInstance(same(userTaskInstanceEntity), same(userTaskInstance));
        verify(inputsEntityMapper, times(1))
                .mapEntityToInstance(same(userTaskInstanceEntity), same(userTaskInstance));
        verify(outputsEntityMapper, times(1))
                .mapEntityToInstance(same(userTaskInstanceEntity), same(userTaskInstance));

        TestUtils.assertUserTaskInstanceData(userTaskInstance, userTaskInstanceEntity);
        TestUtils.assertUserTaskInstancePotentialUserAndGroups(userTaskInstance, userTaskInstanceEntity);
        TestUtils.assertUserTaskInstanceAdminUserAndGroups(userTaskInstance, userTaskInstanceEntity);
        TestUtils.assertUserTaskInstanceExcludedUsers(userTaskInstance, userTaskInstanceEntity);
    }
}
