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
package org.jbpm.services.task.impl;

import java.util.List;
import org.jbpm.services.task.utils.ClassUtil;

import org.kie.api.task.model.Attachment;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskAttachmentService;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.InternalAttachment;

/**
 *
 */
public class TaskAttachmentServiceImpl implements TaskAttachmentService {
 
    private TaskPersistenceContext persistenceContext;

    public TaskAttachmentServiceImpl() {
    }
    
    public TaskAttachmentServiceImpl(TaskPersistenceContext persistenceContext) {
    	this.persistenceContext = persistenceContext;
    }
 
    public void setPersistenceContext(TaskPersistenceContext persistenceContext) {
		this.persistenceContext = persistenceContext;
	}

	public long addAttachment(long taskId, Attachment attachment, Content content) {
        Task task = persistenceContext.findTask(taskId);
        persistenceContext.persistAttachment(attachment);
        persistenceContext.persistContent(content);
        ((InternalAttachment) attachment).setContent(content);
        persistenceContext.addAttachmentToTask(attachment, task);
        return attachment.getId();
    }

    public void deleteAttachment(long taskId, long attachmentId) {
       Task task = persistenceContext.findTask(taskId);
       Attachment attachment = persistenceContext.removeAttachmentFromTask(task, attachmentId);
       Content content = persistenceContext.findContent(attachment.getAttachmentContentId());
       persistenceContext.removeContent(content);
    }

    public List<Attachment> getAllAttachmentsByTaskId(long taskId) {
         return (List<Attachment>) persistenceContext.queryWithParametersInTransaction("AttachmentsByTaskId", 
        		persistenceContext.addParametersToMap("taskId", taskId),
                ClassUtil.<List<Attachment>>castClass(List.class));
    }

    public Attachment getAttachmentById(long attachId) {
        return persistenceContext.findAttachment(attachId);
    }
}
