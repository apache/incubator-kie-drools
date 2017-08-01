/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.remote.ejb.test.task;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.core.impl.EnvironmentFactory;

import org.jboss.qa.bpms.remote.ejb.domain.Person;
import org.jbpm.remote.ejb.test.ProcessDefinitions;
import org.jbpm.remote.ejb.test.RemoteEjbTest;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.junit.Test;

import org.kie.api.runtime.Environment;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.TaskSummary;

public class ETaskAttachmentTest extends RemoteEjbTest {

    private static final String STRING_ATTACHMENT = "String attachment";
    private static final String STRING_ATTACHMENT_NAME = "Attachment";

    @Test
    public void testAttachment() {
        ejb.startProcess(ProcessDefinitions.HUMAN_TASK);

        List<TaskSummary> taskSummaryList = ejb.getTasksAssignedAsPotentialOwner(userId);
        Assertions.assertThat(taskSummaryList.size()).isEqualTo(1);

        // Add a attachment
        Long taskId = taskSummaryList.get(0).getId();
        ejb.addAttachment(taskId, userId, STRING_ATTACHMENT_NAME, STRING_ATTACHMENT);

        // Get the attachment
        Attachment attachment = getAttachment(taskId);
        Assertions.assertThat(attachment.getName()).isEqualTo(STRING_ATTACHMENT_NAME);
        Assertions.assertThat(attachment.getAttachedBy().getId()).isEqualTo(userId);
        Assertions.assertThat(attachment.getContentType()).isEqualTo(STRING_ATTACHMENT.getClass().getName());
        Assertions.assertThat(attachment.getSize()).isEqualTo(persist(STRING_ATTACHMENT).length);

        Object content = getContent(taskId, attachment.getId());
        Assertions.assertThat(content).isEqualTo(STRING_ATTACHMENT);

        // Delete the attachment
        ejb.deleteAttachment(taskId, attachment.getId());
        attachment = getAttachment(taskId);
        Assertions.assertThat(attachment).isNull();
    }

    @Test
    public void testCustomAttachment() throws IOException {
        ejb.startProcess(ProcessDefinitions.HUMAN_TASK);

        List<TaskSummary> taskSummaryList = ejb.getTasksAssignedAsPotentialOwner(userId);
        Long taskId = taskSummaryList.get(0).getId();

        // Add attachment
        Person lisa = new Person("Lisa Simpson", 7);
        ejb.addAttachment(taskId, userId, "Lisa", lisa);

        // Get the attachment
        Attachment attachment = getAttachment(taskId);
        Assertions.assertThat(attachment.getContentType()).isEqualTo(lisa.getClass().getName());
        Assertions.assertThat(attachment.getSize()).isEqualTo(persist(lisa).length);

        // Get the attachment content
        Object content = getContent(taskId, attachment.getId());
        Assertions.assertThat(content).isEqualTo(lisa);
    }

    private List<Attachment> getAttachments(Long taskId) {
        List<Attachment> attachmentList = ejb.getAttachmentsByTaskId(taskId);
        Assertions.assertThat(attachmentList).isNotNull();

        return attachmentList;
    }

    private Attachment getAttachment(Long taskId) {
        List<Attachment> attachmentList = getAttachments(taskId);
        Attachment attachment = null;
        if (!attachmentList.isEmpty()) {
            attachment = attachmentList.get(0);
        }
        return attachment;
    }

    private Object getContent(Long taskId, Long attachmentId) {
        return ejb.getUserTaskService().getAttachmentContentById(taskId, attachmentId);
    }

    private byte[] persist(Serializable object) {
        Environment env = EnvironmentFactory.newEnvironment();

        return ContentMarshallerHelper.marshal(object, env).getContent();
    }

}
