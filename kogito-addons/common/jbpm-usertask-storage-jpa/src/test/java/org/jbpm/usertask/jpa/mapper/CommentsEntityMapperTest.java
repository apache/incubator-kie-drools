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

import java.util.Date;

import org.assertj.core.api.Assertions;
import org.jbpm.usertask.jpa.mapper.utils.TestUtils;
import org.jbpm.usertask.jpa.model.CommentEntity;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.CommentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.model.Comment;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentsEntityMapperTest {

    @Mock
    private CommentRepository repository;
    private CommentsEntityMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new CommentsEntityMapper(repository);
    }

    @Test
    public void testMapCommentsFromInstanceToEntity() {
        UserTaskInstance instance = TestUtils.createCompletedUserTaskInstance();
        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();

        Comment comment = new Comment("1", "John");
        comment.setContent("This is comment 1");
        comment.setUpdatedAt(new Date());

        Comment comment2 = new Comment("2", "Ned");
        comment2.setContent("This is comment 2");
        comment2.setUpdatedAt(new Date());

        instance.addComment(comment);
        instance.addComment(comment2);

        mapper.mapInstanceToEntity(instance, entity);

        Assertions.assertThat(entity.getComments())
                .hasSize(2);
        verify(repository, never())
                .remove(any());

        TestUtils.assertUserTaskEntityComments(entity.getComments(), instance.getComments());

        instance.removeComment(comment);
        mapper.mapInstanceToEntity(instance, entity);

        verify(repository, times(1))
                .remove(any());
        Assertions.assertThat(entity.getComments())
                .hasSize(1);
        TestUtils.assertUserTaskEntityComments(entity.getComments(), instance.getComments());

        instance.removeComment(comment2);
        mapper.mapInstanceToEntity(instance, entity);

        verify(repository, times(2))
                .remove(any());
        Assertions.assertThat(entity.getComments())
                .hasSize(0);
    }

    @Test
    public void testMapCommentsFromEntityToInstance() {

        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();

        UserTaskInstance instance = TestUtils.createCompletedUserTaskInstance();

        CommentEntity comment = new CommentEntity();
        comment.setId("1");
        comment.setUpdatedBy("John");
        comment.setComment("This is comment 1");
        comment.setUpdatedAt(new Date());

        CommentEntity comment2 = new CommentEntity();
        comment2.setId("2");
        comment2.setUpdatedBy("Ned");
        comment2.setComment("This is comment 2");
        comment2.setUpdatedAt(new Date());

        entity.addComment(comment);
        entity.addComment(comment2);

        mapper.mapEntityToInstance(entity, instance);

        Assertions.assertThat(instance.getComments())
                .hasSize(2);
        verify(repository, never())
                .remove(any());

        TestUtils.assertUserTaskInstanceComments(instance.getComments(), entity.getComments());

        entity.removeComment(comment);
        mapper.mapEntityToInstance(entity, instance);

        Assertions.assertThat(entity.getComments())
                .hasSize(1);
        TestUtils.assertUserTaskInstanceComments(instance.getComments(), entity.getComments());

        entity.removeComment(comment);
        mapper.mapEntityToInstance(entity, instance);

        Assertions.assertThat(entity.getComments())
                .hasSize(1);
        TestUtils.assertUserTaskInstanceComments(instance.getComments(), entity.getComments());

        entity.removeComment(comment2);
        mapper.mapEntityToInstance(entity, instance);

        Assertions.assertThat(entity.getComments())
                .hasSize(0);
    }
}
