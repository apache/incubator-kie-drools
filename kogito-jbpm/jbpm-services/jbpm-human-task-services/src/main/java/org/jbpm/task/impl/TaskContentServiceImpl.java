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
import java.util.Map;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.jboss.seam.transaction.Transactional;
import org.jbpm.task.Content;
import org.jbpm.task.ContentData;
import org.jbpm.task.Task;
import org.jbpm.task.api.TaskContentService;
import org.jbpm.task.utils.ContentMarshallerHelper;

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

    public long addContent(long taskId, Map<String, Object> params) {
        Task task = em.find(Task.class, taskId);
        long outputContentId = task.getTaskData().getOutputContentId();
        Content outputContent = em.find(Content.class, outputContentId);
        
        long contentId = -1;
        if (outputContent == null) {
            ContentData outputContentData = ContentMarshallerHelper.marshal(params, null);
            Content content = new Content(outputContentData.getContent());
            em.persist(content);
            
            task.getTaskData().setOutput(content.getId(), outputContentData);
            contentId = content.getId();
        } else {
            // I need to merge it if it already exist
            Object unmarshalledObject = ContentMarshallerHelper.unmarshall(outputContent.getContent(), null);
            if(unmarshalledObject != null && unmarshalledObject instanceof Map){
                ((Map<String, Object>)unmarshalledObject).putAll(params);
            }
            ContentData outputContentData = ContentMarshallerHelper.marshal(unmarshalledObject, null);
            outputContent.setContent(outputContentData.getContent());
            contentId = outputContentId;
        }
        return contentId;
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
