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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpm.usertask.jpa.model.CommentEntity;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.CommentRepository;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.model.Comment;

import static java.util.stream.Collectors.toCollection;

public class CommentsEntityMapper implements EntityMapper {

    private final CommentRepository repository;

    public CommentsEntityMapper(CommentRepository repository) {
        this.repository = repository;
    }

    @Override
    public void mapInstanceToEntity(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskInstanceEntity) {
        Collection<CommentEntity> toRemove = userTaskInstanceEntity.getComments()
                .stream()
                .filter(entity -> userTaskInstance.getComments().stream().noneMatch(comment -> comment.getId().equals(entity.getId())))
                .toList();

        toRemove.forEach(comment -> {
            repository.remove(comment);
            userTaskInstanceEntity.removeComment(comment);
        });

        userTaskInstance.getComments().forEach(comment -> {
            CommentEntity commentEntity = userTaskInstanceEntity.getComments().stream().filter(entity -> entity.getId().equals(comment.getId())).findFirst().orElseGet(() -> {
                CommentEntity entity = new CommentEntity();
                userTaskInstanceEntity.addComment(entity);
                return entity;
            });
            commentEntity.setId(comment.getId());
            commentEntity.setUpdatedBy(comment.getUpdatedBy());
            commentEntity.setComment(comment.getContent());
            commentEntity.setUpdatedAt(comment.getUpdatedAt());
        });
    }

    @Override
    public void mapEntityToInstance(UserTaskInstanceEntity userTaskInstanceEntity, UserTaskInstance userTaskInstance) {
        List<Comment> comments = userTaskInstanceEntity.getComments().stream().map(commentEntity -> {
            Comment comment = new Comment(commentEntity.getId(), commentEntity.getUpdatedBy());
            comment.setId(commentEntity.getId());
            comment.setContent(commentEntity.getComment());
            comment.setUpdatedAt(commentEntity.getUpdatedAt());
            return comment;
        }).collect(toCollection(ArrayList::new));

        ((DefaultUserTaskInstance) userTaskInstance).setComments(comments);
    }
}
