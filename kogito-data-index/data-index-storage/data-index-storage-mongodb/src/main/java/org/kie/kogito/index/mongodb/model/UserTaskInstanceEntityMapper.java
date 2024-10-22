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
package org.kie.kogito.index.mongodb.model;

import java.util.Optional;

import org.kie.kogito.index.model.Attachment;
import org.kie.kogito.index.model.Comment;
import org.kie.kogito.index.model.UserTaskInstance;
import org.kie.kogito.persistence.mongodb.model.ModelUtils;
import org.kie.kogito.persistence.mongodb.model.MongoEntityMapper;

import static java.util.stream.Collectors.toList;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.MONGO_ID;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.documentToJsonNode;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.instantToZonedDateTime;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.jsonNodeToDocument;
import static org.kie.kogito.persistence.mongodb.model.ModelUtils.zonedDateTimeToInstant;

public class UserTaskInstanceEntityMapper implements MongoEntityMapper<UserTaskInstance, UserTaskInstanceEntity> {

    static final String COMMENTS_ID_ATTRIBUTE = "comments.id";

    static final String MONGO_COMMENTS_ID_ATTRIBUTE = "comments." + MONGO_ID;

    static final String ATTACHMENTS_ID_ATTRIBUTE = "attachments.id";

    static final String MONGO_ATTACHMENTS_ID_ATTRIBUTE = "attachments." + MONGO_ID;

    @Override
    public Class<UserTaskInstanceEntity> getEntityClass() {
        return UserTaskInstanceEntity.class;
    }

    @Override
    public UserTaskInstanceEntity mapToEntity(String key, UserTaskInstance instance) {
        if (instance == null) {
            return null;
        }

        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();
        entity.setId(instance.getId());
        entity.setDescription(instance.getDescription());
        entity.setName(instance.getName());
        entity.setPriority(instance.getPriority());
        entity.setProcessInstanceId(instance.getProcessInstanceId());
        entity.setState(instance.getState());
        entity.setActualOwner(instance.getActualOwner());
        entity.setAdminGroups(instance.getAdminGroups());
        entity.setAdminUsers(instance.getAdminUsers());
        entity.setCompleted(zonedDateTimeToInstant(instance.getCompleted()));
        entity.setStarted(zonedDateTimeToInstant(instance.getStarted()));
        entity.setExcludedUsers(instance.getExcludedUsers());
        entity.setPotentialGroups(instance.getPotentialGroups());
        entity.setPotentialUsers(instance.getPotentialUsers());
        entity.setReferenceName(instance.getReferenceName());
        entity.setLastUpdate(zonedDateTimeToInstant(instance.getLastUpdate()));
        entity.setProcessId(instance.getProcessId());
        entity.setRootProcessId(instance.getRootProcessId());
        entity.setRootProcessInstanceId(instance.getRootProcessInstanceId());
        entity.setInputs(jsonNodeToDocument(instance.getInputs()));
        entity.setOutputs(jsonNodeToDocument(instance.getOutputs()));
        entity.setEndpoint(instance.getEndpoint());
        entity.setComments(Optional.ofNullable(instance.getComments()).map(comments -> comments.stream().map(this::fromComment).collect(toList())).orElse(null));
        entity.setAttachments(Optional.ofNullable(instance.getAttachments()).map(attachments -> attachments.stream().map(this::fromAttachment).collect(toList())).orElse(null));
        entity.setExternalReferenceId(instance.getExternalReferenceId());
        return entity;
    }

    @Override
    public UserTaskInstance mapToModel(UserTaskInstanceEntity entity) {
        if (entity == null) {
            return null;
        }

        UserTaskInstance instance = new UserTaskInstance();
        instance.setId(entity.getId());
        instance.setDescription(entity.getDescription());
        instance.setName(entity.getName());
        instance.setPriority(entity.getPriority());
        instance.setProcessInstanceId(entity.getProcessInstanceId());
        instance.setState(entity.getState());
        instance.setActualOwner(entity.getActualOwner());
        instance.setAdminGroups(entity.getAdminGroups());
        instance.setAdminUsers(entity.getAdminUsers());
        instance.setCompleted(instantToZonedDateTime(entity.getCompleted()));
        instance.setStarted(instantToZonedDateTime(entity.getStarted()));
        instance.setExcludedUsers(entity.getExcludedUsers());
        instance.setPotentialGroups(entity.getPotentialGroups());
        instance.setPotentialUsers(entity.getPotentialUsers());
        instance.setReferenceName(entity.getReferenceName());
        instance.setLastUpdate(instantToZonedDateTime(entity.getLastUpdate()));
        instance.setProcessId(entity.getProcessId());
        instance.setRootProcessId(entity.getRootProcessId());
        instance.setRootProcessInstanceId(entity.getRootProcessInstanceId());
        instance.setInputs(documentToJsonNode(entity.getInputs()));
        instance.setOutputs(documentToJsonNode(entity.getOutputs()));
        instance.setEndpoint(entity.getEndpoint());
        instance.setComments(Optional.ofNullable(entity.getComments()).map(comments -> comments.stream().map(this::toComment).collect(toList())).orElse(null));
        instance.setAttachments(Optional.ofNullable(entity.getAttachments()).map(attachments -> attachments.stream().map(this::toAttachment).collect(toList())).orElse(null));
        instance.setExternalReferenceId(entity.getExternalReferenceId());
        return instance;
    }

    @Override
    public String convertToMongoAttribute(String attribute) {
        if (COMMENTS_ID_ATTRIBUTE.equals(attribute)) {
            return MONGO_COMMENTS_ID_ATTRIBUTE;
        }
        if (ATTACHMENTS_ID_ATTRIBUTE.equals(attribute)) {
            return MONGO_ATTACHMENTS_ID_ATTRIBUTE;
        }
        return MongoEntityMapper.super.convertToMongoAttribute(attribute);
    }

    @Override
    public String convertToModelAttribute(String attribute) {
        if (MONGO_COMMENTS_ID_ATTRIBUTE.equals(attribute)) {
            return ModelUtils.ID;
        }
        if (MONGO_ATTACHMENTS_ID_ATTRIBUTE.equals(attribute)) {
            return ModelUtils.ID;
        }
        return MongoEntityMapper.super.convertToModelAttribute(attribute);
    }

    Comment toComment(UserTaskInstanceEntity.CommentEntity entity) {
        if (entity == null) {
            return null;
        }

        Comment comment = new Comment();
        comment.setId(entity.getId());
        comment.setContent(entity.getContent());
        comment.setUpdatedBy(entity.getUpdatedBy());
        comment.setUpdatedAt(instantToZonedDateTime(entity.getUpdatedAt()));
        return comment;
    }

    UserTaskInstanceEntity.CommentEntity fromComment(Comment comment) {
        if (comment == null) {
            return null;
        }

        UserTaskInstanceEntity.CommentEntity entity = new UserTaskInstanceEntity.CommentEntity();
        entity.setId(comment.getId());
        entity.setContent(comment.getContent());
        entity.setUpdatedAt(zonedDateTimeToInstant(comment.getUpdatedAt()));
        entity.setUpdatedBy(comment.getUpdatedBy());
        return entity;
    }

    Attachment toAttachment(UserTaskInstanceEntity.AttachmentEntity entity) {
        if (entity == null) {
            return null;
        }

        Attachment attachment = new Attachment();
        attachment.setId(entity.getId());
        attachment.setContent(entity.getContent());
        attachment.setName(entity.getName());
        attachment.setUpdatedBy(entity.getUpdatedBy());
        attachment.setUpdatedAt(instantToZonedDateTime(entity.getUpdatedAt()));
        return attachment;
    }

    UserTaskInstanceEntity.AttachmentEntity fromAttachment(Attachment attachment) {
        if (attachment == null) {
            return null;
        }

        UserTaskInstanceEntity.AttachmentEntity entity = new UserTaskInstanceEntity.AttachmentEntity();
        entity.setId(attachment.getId());
        entity.setContent(attachment.getContent());
        entity.setName(attachment.getName());
        entity.setUpdatedAt(zonedDateTimeToInstant(attachment.getUpdatedAt()));
        entity.setUpdatedBy(attachment.getUpdatedBy());
        return entity;
    }
}
