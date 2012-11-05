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
import org.jbpm.task.Content;
import org.jbpm.task.Task;
import org.jbpm.task.api.TaskContentService;

/**
 *
 */
@Transactional
@ApplicationScoped
public class TaskContentServiceImpl implements TaskContentService {

    @Inject 
    private EntityManager em;

    public TaskContentServiceImpl() {
    }

    
    
    public long addContent(long taskId, Content content) {
        Task task = em.find(Task.class, taskId);
        em.persist(content);
        task.getTaskData().setDocumentContentId(content.getId());
        return content.getId();
    }

    public void deleteContent(long taskId, long contentId) {
        Task task = em.find(Task.class, taskId);
        task.getTaskData().setDocumentContentId(-1);
        Content content = em.find(Content.class, contentId);
        em.remove(content);
        
    }

    public List<Content> getAllContentByTaskId(long taskId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Content getContentById(long contentId) {
        return em.find(Content.class, contentId);
    }
}
