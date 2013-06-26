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
package org.jbpm.services.task.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.seam.transaction.Transactional;
import org.jbpm.services.task.impl.model.ContentDataImpl;
import org.jbpm.services.task.impl.model.ContentImpl;
import org.jbpm.services.task.impl.model.TaskImpl;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.jbpm.shared.services.api.JbpmServicesPersistenceManager;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.TaskContentService;
import org.kie.internal.task.api.model.InternalTaskData;

/**
 *
 */
@Transactional
@ApplicationScoped
public class TaskContentServiceImpl implements TaskContentService {

    @Inject
    private JbpmServicesPersistenceManager pm;

    private ConcurrentHashMap<String, ContentMarshallerContext> marhsalContexts = new ConcurrentHashMap<String, ContentMarshallerContext>();
    

    public TaskContentServiceImpl() {
    }

    public void setPm(JbpmServicesPersistenceManager pm) {
        this.pm = pm;
    }
    
    public long addContent(long taskId, Map<String, Object> params) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        long outputContentId = task.getTaskData().getOutputContentId();
        ContentImpl outputContent = pm.find(ContentImpl.class, outputContentId);
        
        long contentId = -1;
        if (outputContent == null) {
            ContentDataImpl outputContentData = ContentMarshallerHelper.marshal(params, null);
            ContentImpl content = new ContentImpl(outputContentData.getContent());
            pm.persist(content);
            
            ((InternalTaskData) task.getTaskData()).setOutput(content.getId(), outputContentData);
            contentId = content.getId();
        } else {
            // I need to merge it if it already exist
            ContentMarshallerContext context = getMarshallerContext(task);
            Object unmarshalledObject = ContentMarshallerHelper.unmarshall(outputContent.getContent(), context.getEnvironment(), context.getClassloader());
            if(unmarshalledObject != null && unmarshalledObject instanceof Map){
                ((Map<String, Object>)unmarshalledObject).putAll(params);
            }
            ContentDataImpl outputContentData = ContentMarshallerHelper.marshal(unmarshalledObject, context.getEnvironment());
            outputContent.setContent(outputContentData.getContent());
            pm.persist(outputContent);
            contentId = outputContentId;
        }
        return contentId;
    }

    public long addContent(long taskId, Content content) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        pm.persist(content);
        ((InternalTaskData) task.getTaskData()).setDocumentContentId(content.getId());
        return content.getId();
    }

    public void deleteContent(long taskId, long contentId) {
        TaskImpl task = pm.find(TaskImpl.class, taskId);
        ((InternalTaskData) task.getTaskData()).setDocumentContentId(-1);
        ContentImpl content = pm.find(ContentImpl.class, contentId);
        pm.remove(content);

    }

    public List<Content> getAllContentByTaskId(long taskId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ContentImpl getContentById(long contentId) {
        return pm.find(ContentImpl.class, contentId);
    }
    
    @Override
    public void addMarshallerContext(String ownerId, ContentMarshallerContext context) {
        this.marhsalContexts.put(ownerId, context);
    }

    @Override
    public void removeMarshallerContext(String ownerId) {
        this.marhsalContexts.remove(ownerId);
    }   

    public ContentMarshallerContext getMarshallerContext(Task task) {
        if (task.getTaskData().getDeploymentId() != null && this.marhsalContexts.containsKey(task.getTaskData().getDeploymentId())) {
            return this.marhsalContexts.get(task.getTaskData().getDeploymentId());
        }
        
        return new ContentMarshallerContext();
    }
}
