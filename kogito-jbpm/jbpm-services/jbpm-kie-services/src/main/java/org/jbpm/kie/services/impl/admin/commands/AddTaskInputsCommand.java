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

package org.jbpm.kie.services.impl.admin.commands;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.services.task.commands.TaskContext;
import org.jbpm.services.task.commands.UserGroupCallbackTaskCommand;
import org.jbpm.services.task.events.TaskEventSupport;
import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.utils.ContentMarshallerHelper;
import org.kie.api.runtime.Context;
import org.kie.api.task.model.Content;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.ContentMarshallerContext;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.TaskPersistenceContext;
import org.kie.internal.task.api.model.ContentData;
import org.kie.internal.task.api.model.InternalContent;
import org.kie.internal.task.api.model.InternalTaskData;


public class AddTaskInputsCommand extends UserGroupCallbackTaskCommand<Void> {

    private static final long serialVersionUID = -1856489382099976731L;

    
    private Map<String, Object> values;

    public AddTaskInputsCommand(String userId, long taskId, Map<String, Object> values) {
        super();
        setUserId(userId);
        setTaskId(taskId);
        this.values = values;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Void execute(Context cntxt) {
        TaskContext context = (TaskContext) cntxt;
        TaskEventSupport taskEventSupport = context.getTaskEventSupport();
        TaskPersistenceContext persistenceContext = context.getPersistenceContext();
        
        Task task = persistenceContext.findTask(taskId);
        // security check
        if (!isBusinessAdmin(userId, task.getPeopleAssignments().getBusinessAdministrators(), context)) {
            throw new PermissionDeniedException("User " + userId + " is not business admin of task " + taskId);
        }
        
        long inputContentId = task.getTaskData().getDocumentContentId();
        Content outputContent = persistenceContext.findContent(inputContentId);
        
          
        Map<String, Object> initialContent = new HashMap<String, Object>();
        Map<String, Object> mergedContent = values;
        
        if (outputContent == null) { 
            
            ContentMarshallerContext mcontext = context.getTaskContentService().getMarshallerContext(task);
            ContentData outputContentData = ContentMarshallerHelper.marshal(task, values, mcontext.getEnvironment());
            Content content = TaskModelProvider.getFactory().newContent();
            ((InternalContent) content).setContent(outputContentData.getContent());
            persistenceContext.persistContent(content);
            
            ((InternalTaskData) task.getTaskData()).setOutput(content.getId(), outputContentData);
        } else {
            ContentMarshallerContext mcontext = context.getTaskContentService().getMarshallerContext(task);
            Object unmarshalledObject = ContentMarshallerHelper.unmarshall(outputContent.getContent(), mcontext.getEnvironment(), mcontext.getClassloader());
            if(unmarshalledObject != null && unmarshalledObject instanceof Map){
                // set initial content with data from storage before being altered by this values
                initialContent.putAll((Map<String, Object>)unmarshalledObject);
                
                ((Map<String, Object>)unmarshalledObject).putAll(values);
                mergedContent = ((Map<String, Object>)unmarshalledObject);
            }
            
            ContentData outputContentData = ContentMarshallerHelper.marshal(task, unmarshalledObject, mcontext.getEnvironment());
            ((InternalContent)outputContent).setContent(outputContentData.getContent());
            persistenceContext.persistContent(outputContent);
        }
        taskEventSupport.fireBeforeTaskInputVariablesChanged(task, context, initialContent);
        
        ((InternalTaskData)task.getTaskData()).setTaskInputVariables(mergedContent);
        taskEventSupport.fireAfterTaskInputVariablesChanged(task, context, mergedContent);       
                
        
        return null;
    }

}
