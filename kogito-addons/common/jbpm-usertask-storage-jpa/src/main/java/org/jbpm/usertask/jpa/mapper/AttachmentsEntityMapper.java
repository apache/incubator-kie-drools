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

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jbpm.usertask.jpa.model.AttachmentEntity;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.AttachmentRepository;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.impl.DefaultUserTaskInstance;
import org.kie.kogito.usertask.model.Attachment;

import static java.util.stream.Collectors.toCollection;

public class AttachmentsEntityMapper {
    private final AttachmentRepository repository;

    public AttachmentsEntityMapper(AttachmentRepository repository) {
        this.repository = repository;
    }

    public void mapInstanceToEntity(UserTaskInstance userTaskInstance, UserTaskInstanceEntity userTaskInstanceEntity) {
        Collection<AttachmentEntity> toRemove = userTaskInstanceEntity.getAttachments()
                .stream()
                .filter(entity -> userTaskInstance.getAttachments().stream().noneMatch(attachment -> attachment.getId().equals(entity.getId())))
                .toList();

        toRemove.forEach(attachment -> {
            repository.remove(attachment);
            userTaskInstanceEntity.removeAttachment(attachment);
        });

        userTaskInstance.getAttachments().forEach(attachment -> {
            AttachmentEntity attachmentEntity = userTaskInstanceEntity.getAttachments().stream().filter(entity -> entity.getId().equals(attachment.getId())).findFirst().orElseGet(() -> {
                AttachmentEntity entity = new AttachmentEntity();
                userTaskInstanceEntity.addAttachment(entity);
                return entity;
            });
            attachmentEntity.setId(attachment.getId());
            attachmentEntity.setUpdatedBy(attachment.getUpdatedBy());
            attachmentEntity.setName(attachment.getName());
            attachmentEntity.setUrl(attachment.getContent().toString());
            attachmentEntity.setUpdatedAt(attachment.getUpdatedAt());
        });
    }

    public void mapEntityToInstance(UserTaskInstanceEntity userTaskInstanceEntity, UserTaskInstance userTaskInstance) {

        List<Attachment> attachments = userTaskInstanceEntity.getAttachments().stream().map(attachmentEntity -> {
            Attachment attachment = new Attachment(attachmentEntity.getId(), attachmentEntity.getUpdatedBy());
            attachment.setId(attachmentEntity.getId());
            attachment.setName(attachmentEntity.getName());
            attachment.setContent(URI.create(attachmentEntity.getUrl()));
            attachment.setUpdatedAt(attachmentEntity.getUpdatedAt());
            return attachment;
        }).collect(toCollection(ArrayList::new));

        ((DefaultUserTaskInstance) userTaskInstance).setAttachments(attachments);
    }
}
