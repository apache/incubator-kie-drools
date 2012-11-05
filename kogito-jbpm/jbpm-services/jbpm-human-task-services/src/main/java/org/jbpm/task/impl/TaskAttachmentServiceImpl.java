/*
 * Copyright 2012 JBoss by Red Hat.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.task.impl;

import java.util.List;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Attachment;
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.api.TaskAttachmentService;

/**
 *
 */
@Transactional
@ApplicationScoped
public class TaskAttachmentServiceImpl implements TaskAttachmentService {

    @Inject 
    private EntityManager em;

    public TaskAttachmentServiceImpl() {
    }
    
    

    public long addAttachment(long taskId, Attachment attachment, Content content) {
        //@TODO: The attachment is not being persisted! 
        Task task = em.find(Task.class, taskId);
        // doCallbackOperationForAttachment(attachment); -> This should go in a decorator
        em.persist(content);
        attachment.setContent(content);
        task.getTaskData().addAttachment(attachment);
        return content.getId();
    }

    public void deleteAttachment(long taskId, long attachmentId) {
       Task task = em.find(Task.class, taskId);
       task.getTaskData().removeAttachment(attachmentId);
       //TODO: should I remove the content?
       
    }

    public List<Attachment> getAllAttachmentsByTaskId(long taskId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Attachment getAttachmentById(long attachId) {
        return em.find(Attachment.class, attachId);
    }
}
