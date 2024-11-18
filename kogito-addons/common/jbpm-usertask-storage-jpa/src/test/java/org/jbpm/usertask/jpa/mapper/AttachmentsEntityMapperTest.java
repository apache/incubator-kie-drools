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
import java.util.Date;

import org.assertj.core.api.Assertions;
import org.jbpm.usertask.jpa.mapper.utils.TestUtils;
import org.jbpm.usertask.jpa.model.AttachmentEntity;
import org.jbpm.usertask.jpa.model.UserTaskInstanceEntity;
import org.jbpm.usertask.jpa.repository.AttachmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.usertask.UserTaskInstance;
import org.kie.kogito.usertask.model.Attachment;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AttachmentsEntityMapperTest {

    @Mock
    private AttachmentRepository repository;
    private EntityMapper mapper;

    @BeforeEach
    public void setup() {
        mapper = new AttachmentsEntityMapper(repository);
    }

    @Test
    public void testMapAttachmentsFromInstanceToEntity() {
        UserTaskInstance instance = TestUtils.createUserTaskInstance();
        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();

        Attachment attachment = new Attachment("1", "John");
        attachment.setName("attachment 1");
        attachment.setContent(URI.create("http://localhost:8080/my-attachment.txt"));
        attachment.setUpdatedAt(new Date());

        Attachment attachment2 = new Attachment("2", "Ned");
        attachment2.setName("attachment 2");
        attachment2.setContent(URI.create("http://localhost:8080/my-attachment2.txt"));
        attachment2.setUpdatedAt(new Date());

        instance.addAttachment(attachment);
        instance.addAttachment(attachment2);

        mapper.mapInstanceToEntity(instance, entity);

        Assertions.assertThat(entity.getAttachments())
                .hasSize(2);
        verify(repository, never())
                .remove(any());
        TestUtils.assertUserTaskEntityAttachments(entity.getAttachments(), instance.getAttachments());

        instance.removeAttachment(attachment);
        mapper.mapInstanceToEntity(instance, entity);

        verify(repository, times(1))
                .remove(any());
        Assertions.assertThat(entity.getAttachments())
                .hasSize(1);
        TestUtils.assertUserTaskEntityAttachments(entity.getAttachments(), instance.getAttachments());

        instance.removeAttachment(attachment2);
        mapper.mapInstanceToEntity(instance, entity);

        verify(repository, times(2))
                .remove(any());
        Assertions.assertThat(entity.getAttachments())
                .hasSize(0);
    }

    @Test
    public void testMapAttachmentsFromEntityToInstance() {

        UserTaskInstanceEntity entity = new UserTaskInstanceEntity();

        UserTaskInstance instance = TestUtils.createUserTaskInstance();

        AttachmentEntity attachment = new AttachmentEntity();
        attachment.setId("1");
        attachment.setUpdatedBy("John");
        attachment.setName("attachment 1");
        attachment.setUrl("http://localhost:8080/my-attachment.txt");
        attachment.setUpdatedAt(new Date());

        AttachmentEntity attachment2 = new AttachmentEntity();
        attachment2.setId("2");
        attachment2.setUpdatedBy("Ned");
        attachment2.setName("attachment 2");
        attachment2.setUrl("http://localhost:8080/my-attachment_2.txt");
        attachment2.setUpdatedAt(new Date());

        entity.addAttachment(attachment);
        entity.addAttachment(attachment2);

        mapper.mapEntityToInstance(entity, instance);

        Assertions.assertThat(instance.getAttachments())
                .hasSize(2);
        verify(repository, never())
                .remove(any());
        TestUtils.assertUserTaskInstanceAttachments(instance.getAttachments(), entity.getAttachments());

        entity.removeAttachment(attachment);
        mapper.mapEntityToInstance(entity, instance);

        Assertions.assertThat(entity.getAttachments())
                .hasSize(1);
        TestUtils.assertUserTaskInstanceAttachments(instance.getAttachments(), entity.getAttachments());
    }
}
