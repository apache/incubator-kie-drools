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

package org.jbpm.services.task;

import static org.junit.Assert.assertEquals;

import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.AccessType;
import org.kie.internal.task.api.model.InternalAttachment;
import org.kie.internal.task.api.model.InternalContent;

public abstract class TaskAttachmentBaseTest extends HumanTaskServicesBaseTest {
    
    @Test
    public void testAttachmentWithStringInlineContent() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [new User('Bobba Fet')], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator("Bobba Fet", "en-UK");
        
        
        assertEquals(1, tasks.size());
        TaskSummary taskSum = tasks.get(0);
        Attachment attach = TaskModelProvider.getFactory().newAttachment();
        ((InternalAttachment)attach).setAccessType(AccessType.Inline);
        ((InternalAttachment)attach).setAttachedAt(new Date());
        ((InternalAttachment)attach).setName("My first doc");
        ((InternalAttachment)attach).setContentType("String");
        Content content = TaskModelProvider.getFactory().newContent();
        ((InternalContent)content).setContent(ContentMarshallerHelper.marshallContent(task, "This is my first inline document", null));
        
        long attachId = taskService.addAttachment(taskSum.getId(), attach, content);
        Assert.assertNotEquals(0, attachId);
        
        Attachment attachmentById = taskService.getAttachmentById(attachId);
        Assert.assertNotNull(attachmentById);
        
        Attachment attach2 = TaskModelProvider.getFactory().newAttachment();
        ((InternalAttachment)attach2).setAccessType(AccessType.Inline);
        ((InternalAttachment)attach2).setAttachedAt(new Date());
        ((InternalAttachment)attach2).setName("My second doc");
        ((InternalAttachment)attach2).setContentType("String");
        Content content2 = TaskModelProvider.getFactory().newContent();
        ((InternalContent)content2).setContent(ContentMarshallerHelper.marshallContent(task, "This is my second inline document", null));
        
        attachId = taskService.addAttachment(taskSum.getId(), attach2, content2);
        Assert.assertNotEquals(0, attachId);
        attachmentById = taskService.getAttachmentById(attachId);
        Assert.assertNotNull(attachmentById);
        List<Attachment> allAttachmentsByTaskId = taskService.getAllAttachmentsByTaskId(taskSum.getId());
        
        assertEquals(2, allAttachmentsByTaskId.size());
        
        Attachment firstAttach = allAttachmentsByTaskId.get(0);
        long firstAttachContentId = firstAttach.getAttachmentContentId();
        Content firstAttachContent = taskService.getContentById(firstAttachContentId);
        String firstDocString = (String)ContentMarshallerHelper.unmarshall(firstAttachContent.getContent(), null);
        assertEquals("This is my first inline document", firstDocString);
        
        Attachment secondAttach = allAttachmentsByTaskId.get(1);
        long secondAttachContentId = secondAttach.getAttachmentContentId();
        Content secondAttachContent = taskService.getContentById(secondAttachContentId);
        String secondDocString = (String)ContentMarshallerHelper.unmarshall(secondAttachContent.getContent(), null);
        assertEquals("This is my second inline document", secondDocString);
        
    }
    
}
